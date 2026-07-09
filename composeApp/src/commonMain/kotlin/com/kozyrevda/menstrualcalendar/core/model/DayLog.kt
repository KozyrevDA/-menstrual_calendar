package com.kozyrevda.menstrualcalendar.core.model

import kotlinx.serialization.Serializable

/** Запись самочувствия за день. */
@Serializable
data class DayLog(
    val mood: List<String> = emptyList(),
    val symptoms: List<String> = emptyList(),
    val note: String = "",
) {
    val isEmpty: Boolean get() = mood.isEmpty() && symptoms.isEmpty() && note.isBlank()
}

/** Справочники для экрана логирования (MVP). */
object LogOptions {
    val moods = listOf(
        "😊 Радость", "😔 Грусть", "😠 Злость", "😰 Тревога", "😴 Усталость",
    )
    val symptoms = listOf(
        "Боль", "Грудь", "Акне", "Тяга к сладкому", "Вздутие", "Головная боль",
    )
}
