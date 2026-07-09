package com.kozyrevda.menstrualcalendar.core.data

import com.kozyrevda.menstrualcalendar.core.model.ChatMessage
import com.kozyrevda.menstrualcalendar.core.model.CycleSettings
import com.kozyrevda.menstrualcalendar.core.model.DayLog
import com.kozyrevda.menstrualcalendar.core.model.PillCourse
import com.russhwolf.settings.Settings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Локальное хранение: SharedPreferences на Android, NSUserDefaults на iOS
 * (multiplatform-settings, no-arg). Всё состояние — одним JSON-снапшотом.
 *
 * Слой намеренно отделён от [AppStateHolder]: если данных станет много,
 * за этим же контрактом появится SQLDelight — экраны не изменятся.
 */
class PersistentStore {

    @Serializable
    data class Snapshot(
        val onboardingCompleted: Boolean = false,
        val cycleSettings: CycleSettings? = null,
        val dayLogs: Map<String, DayLog> = emptyMap(),
        val pillCourse: PillCourse? = null,
        val pillsTaken: List<String> = emptyList(),
        val chatMessages: List<ChatMessage> = emptyList(),
        val isPremium: Boolean = false,
        val remindPeriod: Boolean = true,
        val remindOvulation: Boolean = true,
        val remindPills: Boolean = true,
        val privateMode: Boolean = false,
    )

    private val settings = Settings()
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun load(): Snapshot =
        settings.getStringOrNull(KEY)?.let {
            runCatching { json.decodeFromString<Snapshot>(it) }.getOrNull()
        } ?: Snapshot()

    fun save(snapshot: Snapshot) {
        settings.putString(KEY, json.encodeToString(Snapshot.serializer(), snapshot))
    }

    fun clear() {
        settings.remove(KEY)
    }

    private companion object { const val KEY = "luna-state-v1" }
}
