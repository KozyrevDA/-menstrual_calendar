package com.kozyrevda.menstrualcalendar.core.logic

import com.kozyrevda.menstrualcalendar.core.model.CycleDayInfo
import com.kozyrevda.menstrualcalendar.core.model.CyclePhase
import com.kozyrevda.menstrualcalendar.core.model.CycleSettings
import com.kozyrevda.menstrualcalendar.core.model.FertileWindow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus

internal fun LocalDate.plusDays(n: Int): LocalDate = plus(n, DateTimeUnit.DAY)

/**
 * Прогноз цикла по правилам прототипа «Луна»:
 *  • день овуляции = cycleLength − lutealPhase (обычно −14);
 *  • фертильное окно = овуляция −4 … овуляция +1;
 *  • фазы: месячные → фолликулярная → овуляция → лютеиновая.
 *
 * Дни считаются от [CycleSettings.lastPeriodStart] и циклически
 * продолжаются в прошлое и будущее (простая календарная модель MVP).
 */
object CyclePredictor {

    /** Номер дня цикла (1..cycleLengthDays) для произвольной даты, включая даты до lastPeriodStart. */
    fun cycleDayOf(date: LocalDate, settings: CycleSettings): Int {
        val len = settings.cycleLengthDays
        val diff = settings.lastPeriodStart.daysUntil(date)   // может быть отрицательным
        return ((diff % len) + len) % len + 1
    }

    /** День овуляции как номер дня цикла (1-based). */
    fun ovulationCycleDay(settings: CycleSettings): Int =
        settings.cycleLengthDays - settings.lutealPhaseDays

    /** Полная информация об одном дне. */
    fun getDayInfo(date: LocalDate, settings: CycleSettings): CycleDayInfo {
        val len = settings.cycleLengthDays
        val day = cycleDayOf(date, settings)
        val ovDay = ovulationCycleDay(settings)

        val isPeriod = day <= settings.periodLengthDays
        val isOvulation = day == ovDay
        val isFertile = day in (ovDay - 4)..(ovDay + 1)
        val phase = when {
            isPeriod -> CyclePhase.Menstruation
            isOvulation -> CyclePhase.Ovulation
            day < ovDay -> CyclePhase.Follicular
            else -> CyclePhase.Luteal
        }
        return CycleDayInfo(
            date = date,
            cycleDay = day,
            phase = phase,
            isPeriod = isPeriod,
            isOvulation = isOvulation,
            isFertile = isFertile,
            daysUntilNextPeriod = len - day + 1,
        )
    }

    /** Информация по [days] последовательным дням, начиная с [startDate]. */
    fun generateMonth(startDate: LocalDate, days: Int, settings: CycleSettings): List<CycleDayInfo> =
        (0 until days).map { getDayInfo(startDate.plusDays(it), settings) }

    /** Начало следующих месячных строго после даты [after]. */
    fun getNextPeriodStart(settings: CycleSettings, after: LocalDate): LocalDate {
        val day = cycleDayOf(after, settings)
        return after.plusDays(settings.cycleLengthDays - day + 1)
    }

    /** Ближайшая овуляция в дату [after] или позже. */
    fun getOvulationDate(settings: CycleSettings, after: LocalDate): LocalDate {
        val ovDay = ovulationCycleDay(settings)
        val day = cycleDayOf(after, settings)
        val shift = ((ovDay - day) % settings.cycleLengthDays + settings.cycleLengthDays) % settings.cycleLengthDays
        return after.plusDays(shift)
    }

    /** Фертильное окно (овуляция −4…+1), ближайшее к дате [after] (конец окна ≥ after). */
    fun getFertileWindow(settings: CycleSettings, after: LocalDate): FertileWindow {
        var ov = getOvulationDate(settings, after)
        // если овуляция уже прошла, но хвост окна (+1) ещё актуален — берём прошлую
        val prevOv = ov.plusDays(-settings.cycleLengthDays)
        if (prevOv.plusDays(1) >= after) ov = prevOv
        return FertileWindow(start = ov.plusDays(-4), end = ov.plusDays(1))
    }
}
