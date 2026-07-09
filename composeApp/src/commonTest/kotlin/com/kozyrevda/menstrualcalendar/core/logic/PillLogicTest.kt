package com.kozyrevda.menstrualcalendar.core.logic

import com.kozyrevda.menstrualcalendar.core.model.PillCourse
import com.kozyrevda.menstrualcalendar.core.model.PillStatus
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PillLogicTest {

    private val course = PillCourse(startDate = LocalDate(2026, 6, 15))   // 21+7

    @Test
    fun packDayAndBreak() {
        assertEquals(1, PillLogic.packDayOf(LocalDate(2026, 6, 15), course))
        assertEquals(21, PillLogic.packDayOf(LocalDate(2026, 7, 5), course))
        assertEquals(22, PillLogic.packDayOf(LocalDate(2026, 7, 6), course))     // начало паузы
        assertTrue(PillLogic.isBreakDay(LocalDate(2026, 7, 6), course))
        assertEquals(28, PillLogic.packDayOf(LocalDate(2026, 7, 12), course))
        assertEquals(1, PillLogic.packDayOf(LocalDate(2026, 7, 13), course))     // новая упаковка
    }

    @Test
    fun statuses() {
        val today = LocalDate(2026, 6, 18)   // день 4
        val taken = setOf(LocalDate(2026, 6, 15), LocalDate(2026, 6, 17))
        assertEquals(PillStatus.Taken, PillLogic.statusOf(LocalDate(2026, 6, 15), course, taken, today))
        assertEquals(PillStatus.Missed, PillLogic.statusOf(LocalDate(2026, 6, 16), course, taken, today))
        assertEquals(PillStatus.Today, PillLogic.statusOf(today, course, taken, today))
        assertEquals(PillStatus.Upcoming, PillLogic.statusOf(LocalDate(2026, 6, 19), course, taken, today))
        assertEquals(PillStatus.Break, PillLogic.statusOf(LocalDate(2026, 7, 8), course, taken, today))
    }

    @Test
    fun generatePackReturnsWholePack() {
        val today = LocalDate(2026, 6, 18)
        val pack = PillLogic.generatePack(course, emptySet(), today)
        assertEquals(28, pack.size)
        assertEquals(LocalDate(2026, 6, 15), pack.first().date)
        assertEquals(21, pack.count { it.pillNumber != null })
        assertEquals(7, pack.count { it.status == PillStatus.Break })
        assertTrue(pack.first { it.isToday }.packDay == 4)
    }

    @Test
    fun streakCountsConsecutiveAndSkipsBreak() {
        // приняты дни 19,20,21 упаковки (3–5 июля), сегодня — 2-й день паузы
        val taken = setOf(LocalDate(2026, 7, 3), LocalDate(2026, 7, 4), LocalDate(2026, 7, 5))
        val today = LocalDate(2026, 7, 7)
        assertEquals(3, PillLogic.streak(course, taken, today))
        // пропуск рвёт серию
        val withGap = taken - LocalDate(2026, 7, 4)
        assertEquals(1, PillLogic.streak(course, withGap, today))
    }
}
