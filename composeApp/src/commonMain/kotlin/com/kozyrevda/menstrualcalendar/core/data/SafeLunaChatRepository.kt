package com.kozyrevda.menstrualcalendar.core.data

import com.kozyrevda.menstrualcalendar.core.model.ChatMessage
import kotlinx.coroutines.delay

/** Декоратор безопасности: перехватывает опасные темы до обращения к источнику. */
class SafeLunaChatRepository(private val delegate: LunaChatRepository) : LunaChatRepository {

    override suspend fun reply(history: List<ChatMessage>, cycleContext: String?): String {
        val lastUser = history.lastOrNull { it.role == ChatMessage.Role.User }?.text.orEmpty()
        LunaSafety.interceptedReply(lastUser)?.let {
            delay(500)   // естественная пауза вместо мгновенного ответа
            return it
        }
        return delegate.reply(history, cycleContext)
    }
}
