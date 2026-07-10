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
import kotlinx.serialization.json.Json

/**
 * Единая точка состояния приложения. Каждая мутация сохраняется
 * в [PersistentStore] — данные переживают перезапуск на Android и iOS.
 */
object AppStateHolder {

    private val store = PersistentStore()

    private val chatGreeting = ChatMessage(
        ChatMessage.Role.Luna,
        "Привет! Это Луна. Как ты себя чувствуешь сегодня? " +
            "Расскажи про настроение, тревогу, ПМС — что угодно, я рядом.",
    )

    /** Пройден ли онбординг (явный персистентный флаг). */
    var onboardingCompleted: Boolean by mutableStateOf(false)
        private set

    /** Принят ли дисклеймер чата с Луной. */
    var lunaDisclaimerAccepted: Boolean by mutableStateOf(false)
        private set

    var cycleSettings: CycleSettings? by mutableStateOf(null)
        private set

    /** Журнал самочувствия по датам. */
    val dayLogs = mutableStateMapOf<LocalDate, DayLog>()

    /** История чата с Луной. */
    var chatMessages: List<ChatMessage> by mutableStateOf(listOf(chatGreeting))
        private set

    /** Курс таблеток и отметки приёма. */
    var pillCourse: PillCourse? by mutableStateOf(null)
        private set
    var pillsTaken: Set<LocalDate> by mutableStateOf(emptySet())
        private set

    /** Premium: пока UI-заглушка без реального billing. */
    var isPremium: Boolean by mutableStateOf(false)
        private set

    /** Напоминания (UI-настройки; сами уведомления — этап нотификаций). */
    var remindPeriod: Boolean by mutableStateOf(true)
        private set
    var remindOvulation: Boolean by mutableStateOf(true)
        private set
    var remindPills: Boolean by mutableStateOf(true)
        private set

    /** Приватный режим (PIN-заглушка). */
    var privateMode: Boolean by mutableStateOf(false)
        private set

    val isOnboarded: Boolean get() = onboardingCompleted

    init {
        restore()
    }

    /* ── мутации (каждая сохраняется) ── */

    fun acceptLunaDisclaimer() {
        lunaDisclaimerAccepted = true
        persist()
    }

    /** Завершение онбординга: сохраняет настройки и поднимает флаг. */
    fun completeOnboarding(settings: CycleSettings) {
        cycleSettings = settings
        onboardingCompleted = true
        persist()
    }

    /** Правка параметров цикла из настроек (флаг онбординга не трогает). */
    fun saveCycleSettings(settings: CycleSettings) {
        cycleSettings = settings
        persist()
    }

    fun logFor(date: LocalDate): DayLog? = dayLogs[date]

    fun saveDayLog(date: LocalDate, log: DayLog) {
        if (log.isEmpty) dayLogs.remove(date) else dayLogs[date] = log
        persist()
    }

    fun appendChatMessage(message: ChatMessage) {
        chatMessages = chatMessages + message
        persist()
    }

    fun savePillCourse(course: PillCourse?) {
        pillCourse = course
        persist()
    }

    fun togglePillTaken(date: LocalDate) {
        pillsTaken = if (date in pillsTaken) pillsTaken - date else pillsTaken + date
        persist()
    }

    fun activatePremiumStub() {
        isPremium = true
        persist()
    }

    fun toggleRemindPeriod() { remindPeriod = !remindPeriod; persist() }
    fun toggleRemindOvulation() { remindOvulation = !remindOvulation; persist() }
    fun toggleRemindPills() { remindPills = !remindPills; persist() }
    fun togglePrivateMode() { privateMode = !privateMode; persist() }

    /** Полное удаление данных: состояние как при первом запуске. */
    fun clearAll() {
        onboardingCompleted = false
        lunaDisclaimerAccepted = false
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
        store.clear()
    }

    /* ── экспорт ── */

    /** Все данные пользовательницы одним JSON (для экспорта/бэкапа). */
    fun exportJson(): String {
        val json = Json { prettyPrint = true; encodeDefaults = true }
        return json.encodeToString(PersistentStore.Snapshot.serializer(), snapshot())
    }

    /* ── персистентность ── */

    private fun snapshot() = PersistentStore.Snapshot(
        onboardingCompleted = onboardingCompleted,
        lunaDisclaimerAccepted = lunaDisclaimerAccepted,
        cycleSettings = cycleSettings,
        dayLogs = dayLogs.entries.associate { it.key.toString() to it.value },
        pillCourse = pillCourse,
        pillsTaken = pillsTaken.map { it.toString() }.sorted(),
        chatMessages = chatMessages,
        isPremium = isPremium,
        remindPeriod = remindPeriod,
        remindOvulation = remindOvulation,
        remindPills = remindPills,
        privateMode = privateMode,
    )

    private fun persist() = store.save(snapshot())

    private fun restore() {
        val s = store.load()
        // миграция со старых снапшотов: раньше факт онбординга выводился из наличия настроек
        onboardingCompleted = s.onboardingCompleted || s.cycleSettings != null
        lunaDisclaimerAccepted = s.lunaDisclaimerAccepted
        cycleSettings = s.cycleSettings
        dayLogs.clear()
        s.dayLogs.forEach { (k, v) ->
            runCatching { LocalDate.parse(k) }.getOrNull()?.let { dayLogs[it] = v }
        }
        pillCourse = s.pillCourse
        pillsTaken = s.pillsTaken.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.toSet()
        chatMessages = s.chatMessages.ifEmpty { listOf(chatGreeting) }
        isPremium = s.isPremium
        remindPeriod = s.remindPeriod
        remindOvulation = s.remindOvulation
        remindPills = s.remindPills
        privateMode = s.privateMode
    }
}
