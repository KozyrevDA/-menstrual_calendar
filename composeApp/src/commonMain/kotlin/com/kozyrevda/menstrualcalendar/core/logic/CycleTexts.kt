package com.kozyrevda.menstrualcalendar.core.logic

import com.kozyrevda.menstrualcalendar.core.model.CycleDayInfo
import com.kozyrevda.menstrualcalendar.core.model.CyclePhase

/** Русские названия фаз и статусов дня. */
fun CyclePhase.rusTitle(): String = when (this) {
    CyclePhase.Menstruation -> "Месячные"
    CyclePhase.Follicular -> "Фолликулярная фаза"
    CyclePhase.Ovulation -> "Овуляция"
    CyclePhase.Luteal -> "Лютеиновая фаза"
}

fun CycleDayInfo.statusTitle(): String = when {
    isPeriod -> "Месячные"
    isOvulation -> "Овуляция"
    isFertile -> "Фертильные дни"
    phase == CyclePhase.Follicular -> "Фолликулярная фаза"
    else -> "До месячных $daysUntilNextPeriod ${daysWord(daysUntilNextPeriod)}"
}

fun CycleDayInfo.statusSubtitle(): String = when {
    isPeriod -> "День $cycleDay месячных — берегите себя"
    isOvulation || isFertile -> "Высокая вероятность зачатия"
    phase == CyclePhase.Follicular -> "Низкая вероятность зачатия"
    else -> "Лютеиновая фаза · месячные через $daysUntilNextPeriod ${daysWord(daysUntilNextPeriod)}"
}
