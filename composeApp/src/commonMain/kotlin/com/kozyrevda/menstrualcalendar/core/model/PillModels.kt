package com.kozyrevda.menstrualcalendar.core.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/** Курс контрацептивов: 21 активная таблетка + 7 дней перерыва. */
@Serializable
data class PillCourse(
    @Serializable(with = LocalDateIsoSerializer::class)
    val startDate: LocalDate,               // день таблетки №1 текущей схемы
    val reminderTime: String = "21:00",     // HH:mm
    val activePills: Int = 21,
    val breakDays: Int = 7,
    val name: String = "",                  // название препарата (необязательно)
) {
    val packLength: Int get() = activePills + breakDays

    /** «21 + 7», «24 + 4», «28 без перерыва». */
    val schemeLabel: String
        get() = if (breakDays == 0) "$activePills без перерыва" else "$activePills + $breakDays"

    init {
        require(activePills in 1..28) { "activePills вне диапазона" }
        require(breakDays in 0..14) { "breakDays вне диапазона" }
    }
}

/** Стандартные схемы приёма КОК. */
enum class PillScheme(val active: Int, val breakDays: Int, val label: String) {
    Classic21(21, 7, "21 + 7"),
    Modern24(24, 4, "24 + 4"),
    Continuous28(28, 0, "28 без перерыва"),
}

/** Статус дня в блистере. */
enum class PillStatus {
    Taken,      // принято
    Missed,     // пропущено
    Break,      // пауза (7 дней перерыва)
    Today,      // сегодняшняя таблетка, ещё не отмечена
    Upcoming,   // впереди
}

/** Расчётная информация о дне текущей упаковки. */
data class PillDayInfo(
    val date: LocalDate,
    val packDay: Int,           // 1..packLength
    val pillNumber: Int?,       // 1..activePills, null в перерыве
    val status: PillStatus,
    val isToday: Boolean,
)
