package com.kozyrevda.menstrualcalendar.core.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kozyrevda.menstrualcalendar.core.model.CycleSettings
import com.kozyrevda.menstrualcalendar.core.model.DayLog
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

    val isOnboarded: Boolean get() = cycleSettings != null

    fun saveCycleSettings(settings: CycleSettings) {
        cycleSettings = settings
    }

    fun logFor(date: LocalDate): DayLog? = dayLogs[date]

    fun saveDayLog(date: LocalDate, log: DayLog) {
        if (log.isEmpty) dayLogs.remove(date) else dayLogs[date] = log
    }
}
