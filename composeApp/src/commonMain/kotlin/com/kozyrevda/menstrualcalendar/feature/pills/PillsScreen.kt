package com.kozyrevda.menstrualcalendar.feature.pills

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.core.logic.PillLogic
import com.kozyrevda.menstrualcalendar.core.logic.daysWord
import com.kozyrevda.menstrualcalendar.core.logic.rus
import com.kozyrevda.menstrualcalendar.core.logic.today
import com.kozyrevda.menstrualcalendar.core.model.PillCourse
import com.kozyrevda.menstrualcalendar.core.model.PillStatus
import com.kozyrevda.menstrualcalendar.feature.common.BackChevron
import com.kozyrevda.menstrualcalendar.feature.common.Cta
import com.kozyrevda.menstrualcalendar.feature.common.DatePickerCalendar
import com.kozyrevda.menstrualcalendar.feature.common.LunaCard
import com.kozyrevda.menstrualcalendar.feature.common.noRippleClick
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes
import kotlinx.datetime.LocalDate

@Composable
fun PillsScreen(onBack: () -> Unit) {
    val t = today()
    val course = AppStateHolder.pillCourse

    Column(Modifier.fillMaxSize()) {
        // ── шапка ──
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BackChevron(onBack)
            Text("Таблетки", style = MaterialTheme.typography.headlineMedium, color = AppColors.ink, modifier = Modifier.weight(1f))
            if (course != null) {
                val streak = PillLogic.streak(course, AppStateHolder.pillsTaken, t)
                Box(Modifier.clip(AppShapes.pill).background(AppColors.roseLight).padding(horizontal = 14.dp, vertical = 7.dp)) {
                    Text("Серия: $streak ${daysWord(streak)}", style = MaterialTheme.typography.labelMedium, color = AppColors.roseDark)
                }
            }
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp).padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (course == null) SetupCourse() else CourseContent(course, t)
        }
    }
}

/* ─────────────── настройка курса ─────────────── */
@Composable
private fun SetupCourse() {
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var time by remember { mutableStateOf("21:00") }

    Text(
        "Настройте курс 21+7: три недели активных таблеток и семь дней перерыва.",
        style = MaterialTheme.typography.bodyMedium, color = AppColors.sub,
    )
    Text("Дата начала упаковки", style = MaterialTheme.typography.titleSmall, color = AppColors.ink)
    DatePickerCalendar(selected = startDate, rangeLen = 1) { startDate = it }
    ReminderTimeCard(time, onChange = { time = it })
    Cta("Начать курс 21+7", enabled = startDate != null) {
        AppStateHolder.savePillCourse(PillCourse(startDate = startDate!!, reminderTime = time))
    }
}

/* ─────────────── активный курс ─────────────── */
@Composable
private fun CourseContent(course: PillCourse, t: LocalDate) {
    val taken = AppStateHolder.pillsTaken
    val todayInfo = PillLogic.dayInfo(t, course, taken, t)
    val inBreak = todayInfo.status == PillStatus.Break
    val takenToday = t in taken

    // ── карточка «сегодня» + кнопка отметки ──
    LunaCard {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    Modifier.size(50.dp).clip(CircleShape)
                        .background(if (takenToday) AppColors.greenLight else AppColors.roseLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        when {
                            takenToday -> "✓"
                            inBreak -> "—"
                            else -> "№${todayInfo.pillNumber}"
                        },
                        fontSize = if (takenToday) 24.sp else 14.sp, fontWeight = FontWeight.Black,
                        color = if (takenToday) AppColors.green else AppColors.roseDark,
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        when {
                            inBreak -> "Перерыв 7 дней"
                            takenToday -> "Таблетка принята"
                            else -> "Таблетка №${todayInfo.pillNumber} — сегодня"
                        },
                        style = MaterialTheme.typography.titleMedium, color = AppColors.ink,
                    )
                    Text(
                        when {
                            inBreak -> {
                                val left = course.packLength - todayInfo.packDay + 1
                                "Новая упаковка через $left ${daysWord(left)}"
                            }
                            takenToday -> "Следующая — завтра в ${course.reminderTime}"
                            else -> "Напомним в ${course.reminderTime} · осталось ${course.activePills - todayInfo.packDay + 1} в упаковке"
                        },
                        style = MaterialTheme.typography.bodySmall, color = AppColors.sub,
                    )
                }
            }
            if (!inBreak) {
                Box(
                    Modifier.fillMaxWidth().clip(AppShapes.pill)
                        .then(
                            if (takenToday) Modifier.border(2.dp, AppColors.border, AppShapes.pill)
                            else Modifier.background(AppColors.rose)
                        )
                        .noRippleClick { AppStateHolder.togglePillTaken(t) }
                        .padding(13.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (takenToday) "Отменить отметку" else "Отметить приём",
                        fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
                        color = if (takenToday) AppColors.roseDark else Color.White,
                    )
                }
            }
        }
    }

    // ── блистер 28 дней ──
    LunaCard {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Text("Текущая упаковка", style = MaterialTheme.typography.titleMedium, color = AppColors.ink, modifier = Modifier.weight(1f))
                Text("${course.activePills} + ${course.breakDays} · день ${todayInfo.packDay}", style = MaterialTheme.typography.bodySmall, color = AppColors.sub)
            }
            val pack = PillLogic.generatePack(course, taken, t)
            pack.chunked(7).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { day -> BlisterCell(day) }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            ) {
                Legend("Принята") { Box(Modifier.size(10.dp).clip(CircleShape).background(AppColors.rose)) }
                Legend("Сегодня") { Box(Modifier.size(10.dp).clip(CircleShape).border(2.5.dp, AppColors.rose, CircleShape)) }
                Legend("Пропущена") { Box(Modifier.size(10.dp).clip(CircleShape).border(1.5.dp, AppColors.rose, CircleShape)) }
                Legend("Пауза") { Box(Modifier.size(10.dp).clip(CircleShape).background(AppColors.divider)) }
            }
        }
    }

    // ── настройки курса ──
    ReminderTimeCard(course.reminderTime) { newTime ->
        AppStateHolder.savePillCourse(course.copy(reminderTime = newTime))
    }
    LunaCard(corner = AppShapes.cardSmall) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Дата начала упаковки", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.5.sp), color = AppColors.ink)
                Text(course.startDate.rus(), style = MaterialTheme.typography.labelMedium, color = AppColors.sub)
            }
            Box(
                Modifier.clip(AppShapes.pill).background(AppColors.roseLight)
                    .noRippleClick { AppStateHolder.savePillCourse(null) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Изменить", style = MaterialTheme.typography.labelMedium, color = AppColors.roseDark)
            }
        }
    }
    Text(
        "Push-уведомления подключим на следующем этапе — время уже сохраняется.",
        style = MaterialTheme.typography.labelMedium, color = AppColors.subLight,
        modifier = Modifier.padding(horizontal = 6.dp),
    )
}

/* ─────────────── ячейка блистера ─────────────── */
@Composable
private fun BlisterCell(day: com.kozyrevda.menstrualcalendar.core.model.PillDayInfo) {
    val base = Modifier.size(34.dp).clip(CircleShape)
    val cell = when (day.status) {
        PillStatus.Taken -> base.background(AppColors.rose)
        PillStatus.Today -> base.background(AppColors.roseLight).border(2.5.dp, AppColors.rose, CircleShape)
        PillStatus.Missed -> base.background(AppColors.surface).border(1.5.dp, AppColors.rose, CircleShape)
        PillStatus.Break -> base.background(AppColors.divider)
        PillStatus.Upcoming -> base.background(Color(0xFFF7E9E4))
    }
    Box(cell, contentAlignment = Alignment.Center) {
        Text(
            when (day.status) {
                PillStatus.Taken -> "✓"
                PillStatus.Break -> "·"
                else -> "${day.pillNumber}"
            },
            fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
            color = when (day.status) {
                PillStatus.Taken -> Color.White
                PillStatus.Today, PillStatus.Missed -> AppColors.roseDark
                PillStatus.Break -> AppColors.subLight
                PillStatus.Upcoming -> Color(0xFFC9ABA6)
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

/* ─────────────── время напоминания ─────────────── */
@Composable
private fun ReminderTimeCard(time: String, onChange: (String) -> Unit) {
    LunaCard(corner = AppShapes.cardSmall) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Время напоминания", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.5.sp), color = AppColors.ink)
                Text("Каждый день приёма", style = MaterialTheme.typography.labelMedium, color = AppColors.sub)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TimeStepBtn("−") { onChange(shiftTime(time, -30)) }
                Box(Modifier.clip(AppShapes.pill).background(AppColors.roseLight).padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Text(time, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.roseDark)
                }
                TimeStepBtn("+") { onChange(shiftTime(time, 30)) }
            }
        }
    }
}

@Composable
private fun TimeStepBtn(label: String, onClick: () -> Unit) {
    Box(
        Modifier.size(28.dp).clip(CircleShape).background(AppColors.roseLight).noRippleClick(onClick),
        contentAlignment = Alignment.Center,
    ) { Text(label, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.roseDark) }
}

private fun shiftTime(time: String, minutes: Int): String {
    val parts = time.split(":")
    val total = ((parts.getOrNull(0)?.toIntOrNull() ?: 21) * 60 +
        (parts.getOrNull(1)?.toIntOrNull() ?: 0) + minutes + 1440) % 1440
    val h = (total / 60).toString().padStart(2, '0')
    val m = (total % 60).toString().padStart(2, '0')
    return "$h:$m"
}
