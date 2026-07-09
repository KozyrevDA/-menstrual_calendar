package com.kozyrevda.menstrualcalendar.core.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kozyrevda.menstrualcalendar.core.model.ChatMessage
import com.kozyrevda.menstrualcalendar.core.model.CycleSettings
import com.kozyrevda.menstrualcalendar.core.model.DayLog
import com.kozyrevda.menstrualcalendar.core.model.PillCourse
import kotlinx.datetime.LocalDate

/**
 * Хранилище состояния приложения.
 * MVP: in-memory (пропадает при перезапуске); на этапе БД
 * заменим реализацию на персистентную, интерфейс сохраним.
 */
object AppStateHolder {
    var cycleSettings: CycleSettings? by mutableStateOf(null)

    /** Журнал самочувствия по датам. */
    val dayLogs = mutableStateMapOf<LocalDate, DayLog>()

    /** История чата с Луной. */
    var chatMessages: List<ChatMessage> by mutableStateOf(
        listOf(
            ChatMessage(
                ChatMessage.Role.Luna,
                "Привет! Это Луна. Как ты себя чувствуешь сегодня? Расскажи про настроение, тревогу, ПМС — что угодно, я рядом.",
            )
        )
    )

    /** Курс таблеток и отметки приёма. */
    var pillCourse: PillCourse? by mutableStateOf(null)
    var pillsTaken: Set<LocalDate> by mutableStateOf(emptySet())

    val isOnboarded: Boolean get() = cycleSettings != null

    fun saveCycleSettings(settings: CycleSettings) {
        cycleSettings = settings
    }

    fun logFor(date: LocalDate): DayLog? = dayLogs[date]

    fun saveDayLog(date: LocalDate, log: DayLog) {
        if (log.isEmpty) dayLogs.remove(date) else dayLogs[date] = log
    }

    fun savePillCourse(course: PillCourse?) {
        pillCourse = course
    }

    fun appendChatMessage(message: ChatMessage) {
        chatMessages = chatMessages + message
    }

    fun togglePillTaken(date: LocalDate) {
        pillsTaken = if (date in pillsTaken) pillsTaken - date else pillsTaken + date
    }
}
