package com.kozyrevda.menstrualcalendar.core.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kozyrevda.menstrualcalendar.core.model.CycleSettings

/**
 * Хранилище состояния приложения.
 * MVP: in-memory (пропадает при перезапуске); на этапе БД
 * заменим реализацию на персистентную, интерфейс сохраним.
 */
object AppStateHolder {
    var cycleSettings: CycleSettings? by mutableStateOf(null)

    val isOnboarded: Boolean get() = cycleSettings != null

    fun saveCycleSettings(settings: CycleSettings) {
        cycleSettings = settings
    }
}
