package com.kozyrevda.menstrualcalendar.core.logic

import com.kozyrevda.menstrualcalendar.core.model.PillCourse
import com.kozyrevda.menstrualcalendar.core.model.PillDayInfo
import com.kozyrevda.menstrualcalendar.core.model.PillStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

/** Доменная логика курса 21+7. */
object PillLogic {

    /** День упаковки (1..packLength) для даты; работает и до startDate (заворачивает цикл). */
    fun packDayOf(date: LocalDate, course: PillCourse): Int {
        val len = course.packLength
        val diff = course.startDate.daysUntil(date)
        return ((diff % len) + len) % len + 1
    }

    fun isBreakDay(date: LocalDate, course: PillCourse): Boolean =
        packDayOf(date, course) > course.activePills

    /** Первый день текущей упаковки для даты. */
    fun packStart(date: LocalDate, course: PillCourse): LocalDate =
        date.plusDays(-(packDayOf(date, course) - 1))

    /** Статус конкретного дня с учётом отметок приёма. */
    fun statusOf(date: LocalDate, course: PillCourse, taken: Set<LocalDate>, today: LocalDate): PillStatus {
        val packDay = packDayOf(date, course)
        return when {
            packDay > course.activePills -> PillStatus.Break
            date in taken -> PillStatus.Taken
            date < today -> PillStatus.Missed
            date == today -> PillStatus.Today
            else -> PillStatus.Upcoming
        }
    }

    fun dayInfo(date: LocalDate, course: PillCourse, taken: Set<LocalDate>, today: LocalDate): PillDayInfo {
        val packDay = packDayOf(date, course)
        return PillDayInfo(
            date = date,
            packDay = packDay,
            pillNumber = packDay.takeIf { it <= course.activePills },
            status = statusOf(date, course, taken, today),
            isToday = date == today,
        )
    }

    /** Текущая упаковка целиком: packLength последовательных дней. */
    fun generatePack(course: PillCourse, taken: Set<LocalDate>, today: LocalDate): List<PillDayInfo> {
        val start = packStart(today, course)
        return (0 until course.packLength).map { dayInfo(start.plusDays(it), course, taken, today) }
    }

    /**
     * Серия приёма: сколько активных дней подряд отмечено, заканчивая сегодня
     * (или вчера, если сегодняшняя ещё не принята). Дни перерыва серию не рвут.
     */
    fun streak(course: PillCourse, taken: Set<LocalDate>, today: LocalDate): Int {
        var count = 0
        var cursor = if (today in taken || isBreakDay(today, course)) today else today.plusDays(-1)
        while (true) {
            when {
                isBreakDay(cursor, course) -> { /* пауза — пропускаем */ }
                cursor in taken -> count++
                else -> return count
            }
            cursor = cursor.plusDays(-1)
            if (count > 400) return count   // страховка от бесконечного цикла
        }
    }
}
