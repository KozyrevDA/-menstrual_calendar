package com.kozyrevda.menstrualcalendar.core.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/** Фаза менструального цикла. */
enum class CyclePhase {
    Menstruation,   // месячные
    Follicular,     // фолликулярная фаза
    Ovulation,      // овуляция
    Luteal,         // лютеиновая фаза
}

/** Настройки цикла пользовательницы. */
@Serializable
data class CycleSettings(
    @Serializable(with = LocalDateIsoSerializer::class)
    val lastPeriodStart: LocalDate,
    val cycleLengthDays: Int = 28,
    val periodLengthDays: Int = 5,
    val lutealPhaseDays: Int = 14,
) {
    init {
        require(cycleLengthDays in 15..60) { "cycleLengthDays вне разумного диапазона" }
        require(periodLengthDays in 1..14) { "periodLengthDays вне разумного диапазона" }
        require(lutealPhaseDays in 7..20) { "lutealPhaseDays вне разумного диапазона" }
    }
}

/** Расчётная информация об одном дне цикла. */
data class CycleDayInfo(
    val date: LocalDate,
    val cycleDay: Int,              // 1..cycleLengthDays
    val phase: CyclePhase,
    val isPeriod: Boolean,          // прогнозируемые дни месячных
    val isOvulation: Boolean,       // день овуляции
    val isFertile: Boolean,         // фертильное окно (овуляция −4…+1)
    val daysUntilNextPeriod: Int,   // дней до начала следующих месячных (в день накануне = 1)
)

/** Фертильное окно: от [start] до [end] включительно. */
data class FertileWindow(val start: LocalDate, val end: LocalDate) {
    operator fun contains(date: LocalDate): Boolean = date in start..end
}
