package com.kozyrevda.menstrualcalendar.core.logic

import com.kozyrevda.menstrualcalendar.core.model.CycleSettings
import com.kozyrevda.menstrualcalendar.core.model.PillCourse
import kotlinx.datetime.LocalDate

/**
 * Санитизация данных при восстановлении из хранилища и при сохранении.
 * Повреждённое или некорректное поле заменяется безопасным значением,
 * остальные данные сохраняются (никаких полных сбросов состояния).
 */

/** HH:mm, 00–23 / 00–59. */
private val TIME_RE = Regex("""^([01]\d|2[0-3]):[0-5]\d$""")

fun sanitizeReminderTime(time: String, fallback: String = "21:00"): String =
    if (TIME_RE.matches(time.trim())) time.trim() else fallback

fun CycleSettings.sanitized(today: LocalDate): CycleSettings {
    val cycle = if (cycleLengthDays in 15..90) cycleLengthDays else 28
    val period = (if (periodLengthDays in 1..14) periodLengthDays else 5)
        .coerceAtMost(cycle - 1)
    var luteal = if (lutealPhaseDays in 7..20) lutealPhaseDays else 14
    if (luteal >= cycle) luteal = (cycle - 1).coerceIn(7, 20)
    val start = if (lastPeriodStart > today) today else lastPeriodStart
    return CycleSettings(
        lastPeriodStart = start,
        cycleLengthDays = cycle,
        periodLengthDays = period,
        lutealPhaseDays = luteal,
    )
}

fun PillCourse.sanitized(today: LocalDate): PillCourse {
    val validScheme = activePills in 1..28 && breakDays in 0..14
    return PillCourse(
        startDate = if (startDate > today) today else startDate,
        reminderTime = sanitizeReminderTime(reminderTime),
        activePills = if (validScheme) activePills else 21,
        breakDays = if (validScheme) breakDays else 7,
        name = name.trim(),
    )
}
