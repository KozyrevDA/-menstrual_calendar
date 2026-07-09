package com.kozyrevda.menstrualcalendar.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.logic.MONTHS
import com.kozyrevda.menstrualcalendar.core.logic.WEEKDAYS
import com.kozyrevda.menstrualcalendar.core.logic.daysInMonth
import com.kozyrevda.menstrualcalendar.core.logic.mondayFirstIndex
import com.kozyrevda.menstrualcalendar.core.logic.today
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

/** Радио-строка из прототипа: точка-кольцо + подпись, розовая при выборе. */
@Composable
fun RadioRow(label: String, on: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (on) AppColors.roseLight else AppColors.surface)
            .border(if (on) 2.dp else 1.5.dp, if (on) AppColors.rose else AppColors.border, RoundedCornerShape(20.dp))
            .noRippleClick(onClick)
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(22.dp).clip(CircleShape).background(Color.White)
                .border(if (on) 7.dp else 2.dp, if (on) AppColors.rose else Color(0xFFDFC8C3), CircleShape)
        )
        Text(
            label, fontSize = 15.sp,
            fontWeight = if (on) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (on) AppColors.roseDark else AppColors.ink,
        )
    }
}

/** Круглая кнопка навигации по месяцам. */
@Composable
fun MonthNavButton(label: String, onClick: () -> Unit) {
    Box(
        Modifier.size(36.dp).shadow(4.dp, CircleShape, spotColor = Color(0x2EC48E84))
            .clip(CircleShape).background(AppColors.surface).noRippleClick(onClick),
        contentAlignment = Alignment.Center,
    ) { Text(label, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.roseDark) }
}

@Composable
fun WeekdayHeader() {
    Row(Modifier.fillMaxWidth()) {
        WEEKDAYS.forEach {
            Text(
                it.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                color = AppColors.subLight, textAlign = TextAlign.Center, modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Мини-календарь выбора одной даты (не позже сегодня).
 * [rangeLen] дней от выбранной даты подсвечиваются как месячные.
 */
@Composable
fun DatePickerCalendar(selected: LocalDate?, rangeLen: Int, onSelect: (LocalDate) -> Unit) {
    val t = today()
    var year by remember { mutableStateOf(t.year) }
    var month by remember { mutableStateOf(t.monthNumber) }

    LunaCard {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                MonthNavButton("‹") { if (month == 1) { month = 12; year-- } else month-- }
                Text(
                    "${MONTHS[month - 1]} $year", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
                    color = AppColors.ink, textAlign = TextAlign.Center, modifier = Modifier.weight(1f),
                )
                MonthNavButton("›") { if (month == 12) { month = 1; year++ } else month++ }
            }
            WeekdayHeader()
            val offset = LocalDate(year, month, 1).dayOfWeek.mondayFirstIndex
            val dim = daysInMonth(year, month)
            val cells: List<LocalDate?> = List(offset) { null } + (1..dim).map { LocalDate(year, month, it) }
            cells.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    week.forEach { d ->
                        Box(Modifier.weight(1f).height(40.dp), contentAlignment = Alignment.Center) {
                            if (d != null) {
                                val future = d > t
                                val isSel = d == selected
                                val inRange = selected != null && d >= selected && selected.daysUntil(d) < rangeLen
                                Box(
                                    Modifier.fillMaxSize().clip(AppShapes.day)
                                        .background(if (isSel) AppColors.rose else if (inRange) AppColors.roseLight else Color.Transparent)
                                        .noRippleClick { if (!future) onSelect(d) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        "${d.dayOfMonth}", fontSize = 15.sp,
                                        fontWeight = if (isSel || inRange) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        color = when {
                                            isSel -> Color.White
                                            inRange -> AppColors.roseDark
                                            future -> AppColors.ghost
                                            else -> AppColors.ink
                                        },
                                    )
                                }
                            }
                        }
                    }
                    repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

/** Чип-переключатель из прототипа. */
@Composable
fun Chip(label: String, on: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(AppShapes.pill)
            .background(if (on) AppColors.roseLight else AppColors.surface)
            .border(1.5.dp, if (on) AppColors.rose else AppColors.border, AppShapes.pill)
            .noRippleClick(onClick)
            .padding(horizontal = 15.dp, vertical = 9.dp)
    ) {
        Text(
            label, fontSize = 13.5.sp,
            fontWeight = if (on) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (on) AppColors.roseDark else AppColors.inkSoft,
        )
    }
}

/** Набор чипов с переносом по [perRow] в строке. */
@Composable
fun FlowChips(items: List<String>, selected: Set<String>, perRow: Int = 3, onToggle: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(perRow).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { Chip(it, it in selected) { onToggle(it) } }
            }
        }
    }
}
