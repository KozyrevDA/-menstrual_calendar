package com.kozyrevda.menstrualcalendar.core.logic

import com.kozyrevda.menstrualcalendar.core.model.CycleSettings
import com.kozyrevda.menstrualcalendar.core.model.PillCourse
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class SanitizeTest {

    private val today = LocalDate(2026, 7, 10)

    @Test
    fun cycleLengthOutOfRangeFallsBackTo28() {
        assertEquals(28, CycleSettings(today, cycleLengthDays = 5).sanitized(today).cycleLengthDays)
        assertEquals(28, CycleSettings(today, cycleLengthDays = 120).sanitized(today).cycleLengthDays)
        assertEquals(90, CycleSettings(today, cycleLengthDays = 90).sanitized(today).cycleLengthDays)
        assertEquals(15, CycleSettings(today, cycleLengthDays = 15).sanitized(today).cycleLengthDays)
    }

    @Test
    fun periodLengthOutOfRangeFallsBackTo5() {
        assertEquals(5, CycleSettings(today, periodLengthDays = 0).sanitized(today).periodLengthDays)
        assertEquals(5, CycleSettings(today, periodLengthDays = 30).sanitized(today).periodLengthDays)
        assertEquals(14, CycleSettings(today, periodLengthDays = 14).sanitized(today).periodLengthDays)
    }

    @Test
    fun futureLastPeriodStartClampedToToday() {
        val s = CycleSettings(lastPeriodStart = LocalDate(2027, 1, 1)).sanitized(today)
        assertEquals(today, s.lastPeriodStart)
        // прошлое не трогаем
        val past = LocalDate(2026, 6, 20)
        assertEquals(past, CycleSettings(lastPeriodStart = past).sanitized(today).lastPeriodStart)
    }

    @Test
    fun lutealPhaseNeverExceedsCycle() {
        val s = CycleSettings(today, cycleLengthDays = 15, lutealPhaseDays = 20).sanitized(today)
        assertEquals(14, s.lutealPhaseDays)   // clamp до cycle-1
        assertEquals(14, CycleSettings(today, lutealPhaseDays = 99).sanitized(today).lutealPhaseDays)
    }

    @Test
    fun pillCourseSanitized() {
        val broken = PillCourse(
            startDate = LocalDate(2027, 3, 1),   // будущее
            reminderTime = "25:99",              // некорректное время
            activePills = 99, breakDays = 99,    // битая схема
            name = "  Джес  ",
        ).sanitized(today)
        assertEquals(today, broken.startDate)
        assertEquals("21:00", broken.reminderTime)
        assertEquals(21, broken.activePills)
        assertEquals(7, broken.breakDays)
        assertEquals("Джес", broken.name)        // пустоты по краям убраны
    }

    @Test
    fun validPillCourseUntouched() {
        val ok = PillCourse(LocalDate(2026, 7, 1), "09:30", 24, 4, "Ярина").sanitized(today)
        assertEquals("09:30", ok.reminderTime)
        assertEquals(24, ok.activePills)
        assertEquals(4, ok.breakDays)
    }

    @Test
    fun reminderTimeFormats() {
        assertEquals("21:00", sanitizeReminderTime(""))
        assertEquals("21:00", sanitizeReminderTime("9:5"))
        assertEquals("21:00", sanitizeReminderTime("24:00"))
        assertEquals("00:00", sanitizeReminderTime("00:00"))
        assertEquals("23:59", sanitizeReminderTime(" 23:59 "))
    }
}
