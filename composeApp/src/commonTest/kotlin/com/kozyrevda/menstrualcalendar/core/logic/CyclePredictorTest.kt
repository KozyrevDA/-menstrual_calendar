package com.kozyrevda.menstrualcalendar.core.logic

import com.kozyrevda.menstrualcalendar.core.model.CyclePhase
import com.kozyrevda.menstrualcalendar.core.model.CycleSettings
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CyclePredictorTest {

    // Цикл 28 дней, месячные 5 дней, лютеиновая 14 → овуляция на 14-й день.
    private val settings = CycleSettings(
        lastPeriodStart = LocalDate(2026, 6, 22),
        cycleLengthDays = 28,
        periodLengthDays = 5,
        lutealPhaseDays = 14,
    )

    @Test
    fun firstDayIsMenstruation() {
        val info = CyclePredictor.getDayInfo(LocalDate(2026, 6, 22), settings)
        assertEquals(1, info.cycleDay)
        assertEquals(CyclePhase.Menstruation, info.phase)
        assertTrue(info.isPeriod)
        assertFalse(info.isOvulation)
        assertEquals(28, info.daysUntilNextPeriod)
    }

    @Test
    fun lastPeriodDayThenFollicular() {
        val day5 = CyclePredictor.getDayInfo(LocalDate(2026, 6, 26), settings)
        assertEquals(CyclePhase.Menstruation, day5.phase)
        val day6 = CyclePredictor.getDayInfo(LocalDate(2026, 6, 27), settings)
        assertEquals(CyclePhase.Follicular, day6.phase)
        assertFalse(day6.isPeriod)
    }

    @Test
    fun ovulationOnDay14() {
        assertEquals(14, CyclePredictor.ovulationCycleDay(settings))
        val info = CyclePredictor.getDayInfo(LocalDate(2026, 7, 5), settings) // 22.06 + 13
        assertEquals(14, info.cycleDay)
        assertEquals(CyclePhase.Ovulation, info.phase)
        assertTrue(info.isOvulation)
        assertTrue(info.isFertile)
    }

    @Test
    fun fertileWindowIsMinus4Plus1() {
        // дни цикла 10..15 → 1 июля .. 6 июля
        assertTrue(CyclePredictor.getDayInfo(LocalDate(2026, 7, 1), settings).isFertile)
        assertTrue(CyclePredictor.getDayInfo(LocalDate(2026, 7, 6), settings).isFertile)
        assertFalse(CyclePredictor.getDayInfo(LocalDate(2026, 6, 30), settings).isFertile)
        assertFalse(CyclePredictor.getDayInfo(LocalDate(2026, 7, 7), settings).isFertile)
    }

    @Test
    fun lutealAfterOvulationAndCountdown() {
        val info = CyclePredictor.getDayInfo(LocalDate(2026, 7, 11), settings) // день 20
        assertEquals(20, info.cycleDay)
        assertEquals(CyclePhase.Luteal, info.phase)
        assertEquals(9, info.daysUntilNextPeriod)
        // 11 июля + 9 дней = 20 июля — начало следующего цикла
        assertEquals(LocalDate(2026, 7, 20), CyclePredictor.getNextPeriodStart(settings, LocalDate(2026, 7, 11)))
    }

    @Test
    fun dateBeforeLastStartWrapsToPreviousCycle() {
        val info = CyclePredictor.getDayInfo(LocalDate(2026, 6, 21), settings)
        assertEquals(28, info.cycleDay)
        assertEquals(CyclePhase.Luteal, info.phase)
    }

    @Test
    fun nextPeriodStartChains() {
        assertEquals(LocalDate(2026, 7, 20), CyclePredictor.getNextPeriodStart(settings, LocalDate(2026, 6, 22)))
        assertEquals(LocalDate(2026, 8, 17), CyclePredictor.getNextPeriodStart(settings, LocalDate(2026, 7, 20)))
    }

    @Test
    fun ovulationDateSearch() {
        assertEquals(LocalDate(2026, 7, 5), CyclePredictor.getOvulationDate(settings, LocalDate(2026, 6, 22)))
        // в сам день овуляции возвращается она же
        assertEquals(LocalDate(2026, 7, 5), CyclePredictor.getOvulationDate(settings, LocalDate(2026, 7, 5)))
        // на следующий день — уже овуляция следующего цикла
        assertEquals(LocalDate(2026, 8, 2), CyclePredictor.getOvulationDate(settings, LocalDate(2026, 7, 6)))
    }

    @Test
    fun fertileWindowAroundOvulation() {
        val w = CyclePredictor.getFertileWindow(settings, LocalDate(2026, 6, 22))
        assertEquals(LocalDate(2026, 7, 1), w.start)
        assertEquals(LocalDate(2026, 7, 6), w.end)
        assertTrue(LocalDate(2026, 7, 5) in w)
        // 6 июля овуляция прошла (5.07), но хвост окна ещё длится — окно то же
        val tail = CyclePredictor.getFertileWindow(settings, LocalDate(2026, 7, 6))
        assertEquals(w, tail)
        // 7 июля — уже окно следующего цикла
        val nextW = CyclePredictor.getFertileWindow(settings, LocalDate(2026, 7, 7))
        assertEquals(LocalDate(2026, 7, 29), nextW.start)
        assertEquals(LocalDate(2026, 8, 3), nextW.end)
    }

    @Test
    fun generateMonthProducesConsecutiveDays() {
        val list = CyclePredictor.generateMonth(LocalDate(2026, 7, 1), 31, settings)
        assertEquals(31, list.size)
        assertEquals(LocalDate(2026, 7, 1), list.first().date)
        assertEquals(LocalDate(2026, 7, 31), list.last().date)
        assertEquals(list.map { it.date }.toSet().size, list.size)
        // 20–24 июля — прогнозные месячные следующего цикла
        assertTrue(list.first { it.date == LocalDate(2026, 7, 20) }.isPeriod)
        assertTrue(list.first { it.date == LocalDate(2026, 7, 24) }.isPeriod)
        assertFalse(list.first { it.date == LocalDate(2026, 7, 25) }.isPeriod)
    }

    @Test
    fun shortCycleSettings() {
        val short = CycleSettings(LocalDate(2026, 6, 22), cycleLengthDays = 21, periodLengthDays = 3, lutealPhaseDays = 12)
        assertEquals(9, CyclePredictor.ovulationCycleDay(short))
        assertEquals(LocalDate(2026, 6, 30), CyclePredictor.getOvulationDate(short, LocalDate(2026, 6, 22)))
    }

    @Test
    fun invalidSettingsRejected() {
        assertFailsWith<IllegalArgumentException> {
            CycleSettings(LocalDate(2026, 1, 1), cycleLengthDays = 5)
        }
    }
}
