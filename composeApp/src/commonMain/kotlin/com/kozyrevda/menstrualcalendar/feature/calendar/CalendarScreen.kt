package com.kozyrevda.menstrualcalendar.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.core.logic.CyclePredictor
import com.kozyrevda.menstrualcalendar.core.logic.MONTHS
import com.kozyrevda.menstrualcalendar.core.logic.daysInMonth
import com.kozyrevda.menstrualcalendar.core.logic.mondayFirstIndex
import com.kozyrevda.menstrualcalendar.core.logic.rus
import com.kozyrevda.menstrualcalendar.core.logic.statusSubtitle
import com.kozyrevda.menstrualcalendar.core.logic.statusTitle
import com.kozyrevda.menstrualcalendar.core.logic.today
import com.kozyrevda.menstrualcalendar.core.logic.weekdayFull
import com.kozyrevda.menstrualcalendar.core.model.CycleDayInfo
import com.kozyrevda.menstrualcalendar.feature.common.LunaCard
import com.kozyrevda.menstrualcalendar.feature.common.MonthNavButton
import com.kozyrevda.menstrualcalendar.feature.common.WeekdayHeader
import com.kozyrevda.menstrualcalendar.feature.common.noRippleClick
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes
import kotlinx.datetime.LocalDate

@Composable
fun CalendarScreen() {
    val t = today()
    val settings = AppStateHolder.cycleSettings
    var year by remember { mutableStateOf(t.year) }
    var month by remember { mutableStateOf(t.monthNumber) }
    var selected by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // ── переключение месяцев ──
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MonthNavButton("‹") { if (month == 1) { month = 12; year-- } else month--; selected = null }
            Text(
                "${MONTHS[month - 1]} $year",
                style = MaterialTheme.typography.titleLarge, color = AppColors.ink,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f),
            )
            MonthNavButton("›") { if (month == 12) { month = 1; year++ } else month++; selected = null }
        }

        // ── сетка 7×6 ──
        LunaCard {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                WeekdayHeader()
                val offset = LocalDate(year, month, 1).dayOfWeek.mondayFirstIndex
                val dim = daysInMonth(year, month)
                val prevY = if (month == 1) year - 1 else year
                val prevM = if (month == 1) 12 else month - 1
                val prevDim = daysInMonth(prevY, prevM)
                val nextY = if (month == 12) year + 1 else year
                val nextM = if (month == 12) 1 else month + 1

                // всегда 42 ячейки: хвост прошлого месяца + текущий + голова следующего
                val cells: List<Pair<LocalDate, Boolean>> = buildList {
                    for (i in offset downTo 1) add(LocalDate(prevY, prevM, prevDim - i + 1) to false)
                    for (d in 1..dim) add(LocalDate(year, month, d) to true)
                    var d = 1
                    while (size < 42) add(LocalDate(nextY, nextM, d++) to false)
                }

                cells.chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        week.forEach { (date, inMonth) ->
                            DayCell(
                                date = date,
                                inMonth = inMonth,
                                info = settings?.let { CyclePredictor.getDayInfo(date, it) },
                                isToday = date == t,
                                isSelected = date == selected,
                                modifier = Modifier.weight(1f),
                            ) { selected = if (selected == date) null else date }
                        }
                    }
                }

                // ── легенда ──
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                ) {
                    Legend("Месячные") { Box(Modifier.size(10.dp).clip(CircleShape).background(AppColors.rose)) }
                    Legend("Фертильные") { Box(Modifier.size(10.dp).clip(CircleShape).background(AppColors.peachSoft)) }
                    Legend("Овуляция") { Box(Modifier.size(10.dp).clip(CircleShape).border(2.5.dp, AppColors.peach, CircleShape)) }
                    Legend("Сегодня") { Box(Modifier.size(10.dp).clip(CircleShape).border(2.dp, AppColors.roseDark, CircleShape)) }
                }
            }
        }

        // ── краткая информация по выбранному дню ──
        val sel = selected
        if (sel != null && settings != null) {
            val info = CyclePredictor.getDayInfo(sel, settings)
            DayInfoCard(sel, info)
        } else {
            Text(
                "Нажмите на день, чтобы увидеть информацию о нём",
                style = MaterialTheme.typography.labelMedium, color = AppColors.subLight,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    inMonth: Boolean,
    info: CycleDayInfo?,
    isToday: Boolean,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val isPeriod = info?.isPeriod == true
    val isOv = info?.isOvulation == true
    val isFertile = info?.isFertile == true && !isOv

    var m = modifier.height(42.dp).clip(AppShapes.day)
    if (inMonth) {
        if (isFertile) m = m.background(AppColors.peachLight)
        if (isOv) m = m.border(2.5.dp, AppColors.peach, AppShapes.day)
        if (isPeriod) m = m.background(AppColors.rose)
    }
    if (isToday) m = m.border(2.dp, AppColors.roseDark, AppShapes.day)
    if (isSelected && !isToday) m = m.border(2.dp, AppColors.subLight, AppShapes.day)

    Box(m.noRippleClick(onClick), contentAlignment = Alignment.Center) {
        Text(
            "${date.dayOfMonth}",
            fontSize = 15.sp,
            fontWeight = if (isPeriod || isOv || isToday || isSelected) FontWeight.ExtraBold
            else if (isFertile) FontWeight.Bold else FontWeight.SemiBold,
            color = when {
                !inMonth -> AppColors.ghost
                isPeriod -> Color.White
                isOv || isFertile -> AppColors.peachInk
                else -> AppColors.ink
            },
        )
    }
}

@Composable
private fun Legend(label: String, dot: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        dot()
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.sub)
    }
}

@Composable
private fun DayInfoCard(date: LocalDate, info: CycleDayInfo) {
    Column(
        Modifier.fillMaxWidth().clip(AppShapes.cardSmall).background(AppColors.roseLight)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            "${date.weekdayFull()}, ${date.rus()} · день цикла ${info.cycleDay}".uppercase(),
            style = MaterialTheme.typography.labelSmall, color = AppColors.roseDark,
        )
        Text(info.statusTitle(), style = MaterialTheme.typography.titleLarge, color = AppColors.ink)
        Text(info.statusSubtitle(), style = MaterialTheme.typography.bodyMedium, color = AppColors.roseInk)
    }
}
