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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Хранилище состояния приложения.
 * MVP: in-memory (пропадает при перезапуске); на этапе БД
 * заменим реализацию на персистентную, интерфейс сохраним.
 */
object AppStateHolder {
    var cycleSettings: CycleSettings? by mutableStateOf(null)

    /** Журнал самочувствия по датам. */
    val dayLogs = mutableStateMapOf<LocalDate, DayLog>()

    private val chatGreeting = ChatMessage(
        ChatMessage.Role.Luna,
        "Привет! Это Луна. Как ты себя чувствуешь сегодня? Расскажи про настроение, тревогу, ПМС — что угодно, я рядом.",
    )

    /** История чата с Луной. */
    var chatMessages: List<ChatMessage> by mutableStateOf(listOf(chatGreeting))

    /** Курс таблеток и отметки приёма. */
    var pillCourse: PillCourse? by mutableStateOf(null)
    var pillsTaken: Set<LocalDate> by mutableStateOf(emptySet())

    /** Premium: пока UI-заглушка без реального billing. */
    var isPremium: Boolean by mutableStateOf(false)

    /** Напоминания (UI-настройки; сами уведомления — этап нотификаций). */
    var remindPeriod: Boolean by mutableStateOf(true)
    var remindOvulation: Boolean by mutableStateOf(true)
    var remindPills: Boolean by mutableStateOf(true)

    /** Приватный режим (PIN-заглушка). */
    var privateMode: Boolean by mutableStateOf(false)

    val isOnboarded: Boolean get() = cycleSettings != null

    fun activatePremiumStub() {
        isPremium = true
    }

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

    /* ── экспорт и удаление данных ── */

    @Serializable
    private data class ExportSnapshot(
        val cycleSettings: com.kozyrevda.menstrualcalendar.core.model.CycleSettings?,
        val dayLogs: Map<String, DayLog>,
        val pillCourse: PillCourse?,
        val pillsTaken: List<String>,
        val isPremium: Boolean,
    )

    /** Все данные пользовательницы одним JSON (для экспорта/бэкапа). */
    fun exportJson(): String {
        val json = Json { prettyPrint = true; encodeDefaults = true }
        return json.encodeToString(
            ExportSnapshot.serializer(),
            ExportSnapshot(
                cycleSettings = cycleSettings,
                dayLogs = dayLogs.entries.associate { it.key.toString() to it.value },
                pillCourse = pillCourse,
                pillsTaken = pillsTaken.map { it.toString() }.sorted(),
                isPremium = isPremium,
            ),
        )
    }

    /** Полное удаление данных: состояние как при первом запуске. */
    fun clearAll() {
        cycleSettings = null
        dayLogs.clear()
        pillCourse = null
        pillsTaken = emptySet()
        isPremium = false
        remindPeriod = true
        remindOvulation = true
        remindPills = true
        privateMode = false
        chatMessages = listOf(chatGreeting)
    }
}
