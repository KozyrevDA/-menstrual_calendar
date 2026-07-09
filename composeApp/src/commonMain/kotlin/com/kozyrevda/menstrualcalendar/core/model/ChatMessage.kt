package com.kozyrevda.menstrualcalendar.core.model

import kotlinx.serialization.Serializable

/** Сообщение в чате с Луной. */
@Serializable
data class ChatMessage(
    val role: Role,
    val text: String,
) {
    @Serializable
    enum class Role { User, Luna }
}
