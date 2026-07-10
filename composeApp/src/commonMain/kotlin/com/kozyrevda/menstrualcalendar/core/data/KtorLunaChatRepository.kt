package com.kozyrevda.menstrualcalendar.core.data

import com.kozyrevda.menstrualcalendar.core.model.ChatMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Реальный API Луны через Ktor (Anthropic-совместимый формат messages).
 *
 * ВАЖНО ДЛЯ ПРОДАКШЕНА: не храните API-ключ в клиенте — его извлекут из APK/IPA.
 * Правильно: тонкий прокси-бэкенд (ключ на сервере), сюда — только PROXY_URL.
 * Пустые константы ниже ⇒ [isConfigured] == false ⇒ приложение использует mock.
 */
class KtorLunaChatRepository : LunaChatRepository {

    companion object {
        private const val API_KEY = ""       // только для локальной отладки!
        private const val PROXY_URL = ""     // напр. "https://api.example.com/luna-chat"
        private const val ANTHROPIC_URL = "https://api.anthropic.com/v1/messages"
        private const val MODEL = "claude-sonnet-4-6"

        val isConfigured: Boolean get() = API_KEY.isNotBlank() || PROXY_URL.isNotBlank()
    }

    @Serializable private data class ApiMessage(val role: String, val content: String)
    @Serializable private data class ApiRequest(
        val model: String,
        @SerialName("max_tokens") val maxTokens: Int,
        val system: String,
        val messages: List<ApiMessage>,
    )
    @Serializable private data class ContentBlock(val type: String = "", val text: String = "")
    @Serializable private data class ApiResponse(val content: List<ContentBlock> = emptyList())

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
        }
    }

    override suspend fun reply(history: List<ChatMessage>, cycleContext: String?): String {
        val system = buildString {
            append(LUNA_SYSTEM_PROMPT)
            append(" Отвечай по-русски, тепло и на «ты», коротко: 2–4 предложения.")
            append(
                " Строгие правила безопасности: никогда не ставь диагнозы и не предполагай заболевания; " +
                    "никогда не советуй и не называй лекарства или дозировки — направляй к врачу. " +
                    "При признаках кризиса (мысли о самоповреждении или суициде) отвечай поддержкой " +
                    "и настоятельно рекомендуй немедленно обратиться к специалисту или на линию экстренной помощи."
            )
            if (cycleContext != null) append(" Контекст: у собеседницы $cycleContext.")
        }
        val url = PROXY_URL.ifBlank { ANTHROPIC_URL }
        val response: ApiResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            if (PROXY_URL.isBlank()) {
                header("x-api-key", API_KEY)
                header("anthropic-version", "2023-06-01")
            }
            setBody(
                ApiRequest(
                    model = MODEL,
                    maxTokens = 1000,
                    system = system,
                    messages = history.map {
                        ApiMessage(if (it.role == ChatMessage.Role.User) "user" else "assistant", it.text)
                    },
                )
            )
        }.body()
        return response.content.filter { it.type == "text" }
            .joinToString("\n") { it.text }
            .trim()
            .ifBlank { "Кажется, связь прервалась. Попробуй ещё раз чуть позже." }
    }
}
