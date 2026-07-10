package com.kozyrevda.menstrualcalendar.core.data

import com.kozyrevda.menstrualcalendar.core.model.ChatMessage

/**
 * Источник ответов Луны. Реализации:
 *  • [MockLunaChatRepository] — работает без бэкенда (MVP);
 *  • [KtorLunaChatRepository] — реальный API, включается конфигурацией.
 */
interface LunaChatRepository {
    /**
     * @param history вся переписка, последним — сообщение пользовательницы
     * @param cycleContext краткий контекст цикла («14-й день, овуляция») или null
     */
    suspend fun reply(history: List<ChatMessage>, cycleContext: String?): String
}

/** Системный промпт Луны (по ТЗ). */
const val LUNA_SYSTEM_PROMPT =
    "Ты Луна — мягкая ИИ-подруга для эмоциональной поддержки. " +
    "Ты не врач, не ставишь диагнозы, не лечишь. " +
    "Поддерживаешь, задаешь бережные вопросы, помогаешь вести дневник чувств."

/** Фабрика: реальный API, если сконфигурирован, иначе mock. */
fun createLunaChatRepository(): LunaChatRepository = SafeLunaChatRepository(
    if (KtorLunaChatRepository.isConfigured) KtorLunaChatRepository() else MockLunaChatRepository()
)
