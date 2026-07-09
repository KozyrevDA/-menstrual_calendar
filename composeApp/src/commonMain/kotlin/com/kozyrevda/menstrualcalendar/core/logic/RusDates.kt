package com.kozyrevda.menstrualcalendar.core.logic

import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.todayIn

/** Русские названия и форматирование дат. */
val MONTHS = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь",
)
val MONTHS_GEN = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)
val MONTHS_SHORT = listOf(
    "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
    "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек",
)
val WEEKDAYS = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

fun LocalDate.rus(): String = "$dayOfMonth ${MONTHS_GEN[monthNumber - 1]}"

fun LocalDate.weekdayFull(): String = when (dayOfWeek) {
    DayOfWeek.MONDAY -> "Понедельник"; DayOfWeek.TUESDAY -> "Вторник"; DayOfWeek.WEDNESDAY -> "Среда"
    DayOfWeek.THURSDAY -> "Четверг"; DayOfWeek.FRIDAY -> "Пятница"; DayOfWeek.SATURDAY -> "Суббота"
    else -> "Воскресенье"
}

val DayOfWeek.mondayFirstIndex: Int get() = isoDayNumber - 1   // Пн=0 … Вс=6

fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    else -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
}

fun plural(n: Int, one: String, few: String, many: String): String {
    val m10 = n % 10; val m100 = n % 100
    return when {
        m10 == 1 && m100 != 11 -> one
        m10 in 2..4 && m100 !in 12..14 -> few
        else -> many
    }
}
fun daysWord(n: Int) = plural(n, "день", "дня", "дней")
