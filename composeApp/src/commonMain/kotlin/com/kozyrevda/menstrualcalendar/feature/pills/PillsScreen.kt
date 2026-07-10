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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.core.logic.PillLogic
import com.kozyrevda.menstrualcalendar.core.logic.daysWord
import com.kozyrevda.menstrualcalendar.core.logic.rus
import com.kozyrevda.menstrualcalendar.core.logic.today
import com.kozyrevda.menstrualcalendar.core.model.PillCourse
import com.kozyrevda.menstrualcalendar.core.model.PillDayInfo
import com.kozyrevda.menstrualcalendar.core.model.PillScheme
import com.kozyrevda.menstrualcalendar.core.model.PillStatus
import com.kozyrevda.menstrualcalendar.feature.common.BackChevron
import com.kozyrevda.menstrualcalendar.feature.common.Cta
import com.kozyrevda.menstrualcalendar.feature.common.DatePickerCalendar
import com.kozyrevda.menstrualcalendar.feature.common.LunaCard
import com.kozyrevda.menstrualcalendar.feature.common.RadioRow
import com.kozyrevda.menstrualcalendar.feature.common.noRippleClick
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes
import kotlinx.datetime.LocalDate

private const val MEDICAL_WARNING =
    "Приложение только напоминает о приёме. " +
        "Следуйте назначению врача и инструкции к препарату."

@Composable
fun PillsScreen(onBack: () -> Unit) {
    val t = today()
    val course = AppStateHolder.pillCourse
    var editing by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        // ── шапка ──
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BackChevron(onBack)
            Text(
                "Таблетки", style = MaterialTheme.typography.headlineMedium,
                color = AppColors.ink, modifier = Modifier.weight(1f),
            )
            if (course != null && !editing) {
                val streak = PillLogic.streak(course, AppStateHolder.pillsTaken, t)
                Box(Modifier.clip(AppShapes.pill).background(AppColors.roseLight).padding(horizontal = 14.dp, vertical = 7.dp)) {
                    Text(
                        "Серия: $streak ${daysWord(streak)}",
                        style = MaterialTheme.typography.labelMedium, color = AppColors.roseDark,
                    )
                }
            }
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp).padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (course == null || editing) {
                SetupCourse(initial = course) { editing = false }
            } else {
                CourseContent(course, t) { editing = true }
            }
            MedicalWarning()
        }
    }
}

/* ─────────────── настройка курса ─────────────── */
@Composable
private fun SetupCourse(initial: PillCourse?, onDone: () -> Unit) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var scheme by remember {
        mutableStateOf(
            PillScheme.entries.firstOrNull {
                it.active == initial?.activePills && it.breakDays == initial?.breakDays
            } ?: PillScheme.Classic21,
        )
    }
    var startDate by remember { mutableStateOf(initial?.startDate) }
    var time by remember { mutableStateOf(initial?.reminderTime ?: "21:00") }

    Text(
        "Настройте курс: схема приёма, начало упаковки и время напоминания.",
        style = MaterialTheme.typography.bodyMedium, color = AppColors.sub,
    )

    SectionTitle("Название препарата")
    BasicTextField(
        name, { name = it }, singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.ink),
        modifier = Modifier.fillMaxWidth().clip(AppShapes.pill).background(AppColors.surface)
            .border(1.5.dp, AppColors.border, AppShapes.pill)
            .padding(horizontal = 20.dp, vertical = 15.dp),
        decorationBox = { inner ->
            if (name.isEmpty()) Text(
                "Например, «Джес» (необязательно)",
                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.subLight,
            )
            inner()
        },
    )

    SectionTitle("Схема приёма")
    PillScheme.entries.forEach { s ->
        RadioRow(s.label, scheme == s) { scheme = s }
    }

    SectionTitle("Дата начала упаковки")
    DatePickerCalendar(selected = startDate, rangeLen = 1) { startDate = it }

    ReminderTimeCard(time) { time = it }

    Cta(if (initial == null) "Начать курс" else "Сохранить изменения", enabled = startDate != null) {
        AppStateHolder.savePillCourse(
            PillCourse(
                startDate = startDate!!,
                reminderTime = time,
                activePills = scheme.active,
                breakDays = scheme.breakDays,
                name = name.trim(),
            )
        )
        onDone()
    }
}

/* ─────────────── активный курс ─────────────── */
@Composable
private fun CourseContent(course: PillCourse, t: LocalDate, onEdit: () -> Unit) {
    val taken = AppStateHolder.pillsTaken
    val todayInfo = PillLogic.dayInfo(t, course, taken, t)
    val inBreak = todayInfo.status == PillStatus.Break
    val takenToday = t in taken
    val pack = PillLogic.generatePack(course, taken, t)
    val missedCount = pack.count { it.status == PillStatus.Missed }

    // ── карточка «сегодня» ──
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
                    if (course.name.isNotBlank()) {
                        Text(
                            course.name.uppercase(),
                            style = MaterialTheme.typography.labelSmall, color = AppColors.subLight,
                        )
                    }
                    Text(
                        when {
                            inBreak -> "Перерыв ${course.breakDays} ${daysWord(course.breakDays)}"
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
                            else -> {
                                val left = course.activePills - todayInfo.packDay + 1
                                "Напомним в ${course.reminderTime} · осталось $left в упаковке"
                            }
                        },
                        style = MaterialTheme.typography.bodySmall, color = AppColors.sub,
                    )
                }
            }
            if (missedCount > 0) {
                Box(
                    Modifier.fillMaxWidth().clip(AppShapes.tile).background(AppColors.roseLight)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        "Пропущено в этой упаковке: $missedCount. " +
                            "Нажмите на пропущенный день в блистере, если приняли таблетку.",
                        style = MaterialTheme.typography.labelMedium, color = AppColors.roseDark,
                        lineHeight = 17.sp,
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

    // ── блистер ──
    LunaCard {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Text(
                    "Текущая упаковка", style = MaterialTheme.typography.titleMedium,
                    color = AppColors.ink, modifier = Modifier.weight(1f),
                )
                Text(
                    "${course.schemeLabel} · день ${todayInfo.packDay}",
                    style = MaterialTheme.typography.bodySmall, color = AppColors.sub,
                )
            }
            pack.chunked(7).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { day ->
                        BlisterCell(day) {
                            // прошедшие и сегодняшние активные дни можно отметить/снять задним числом
                            if (day.pillNumber != null && day.date <= t) {
                                AppStateHolder.togglePillTaken(day.date)
                            }
                        }
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            ) {
                Legend("Принята") { Box(Modifier.size(10.dp).clip(CircleShape).background(AppColors.rose)) }
                Legend("Сегодня") { Box(Modifier.size(10.dp).clip(CircleShape).border(2.5.dp, AppColors.rose, CircleShape)) }
                Legend("Пропущено") { Box(Modifier.size(10.dp).clip(CircleShape).border(1.5.dp, AppColors.rose, CircleShape)) }
                if (course.breakDays > 0) {
                    Legend("Пауза") { Box(Modifier.size(10.dp).clip(CircleShape).background(AppColors.divider)) }
                }
            }
        }
    }

    // ── параметры курса ──
    ReminderTimeCard(course.reminderTime) { newTime ->
        AppStateHolder.savePillCourse(course.copy(reminderTime = newTime))
    }
    LunaCard(corner = AppShapes.cardSmall) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    buildString {
                        if (course.name.isNotBlank()) append(course.name).append(" · ")
                        append(course.schemeLabel)
                    },
                    fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.ink,
                )
                Text(
                    "Начало упаковки — ${course.startDate.rus()}",
                    style = MaterialTheme.typography.labelMedium, color = AppColors.sub,
                )
            }
            Box(
                Modifier.clip(AppShapes.pill).background(AppColors.roseLight)
                    .noRippleClick(onEdit)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Изменить", style = MaterialTheme.typography.labelMedium, color = AppColors.roseDark)
            }
        }
    }
}

/* ─────────────── ячейка блистера ─────────────── */
@Composable
private fun BlisterCell(day: PillDayInfo, onClick: () -> Unit) {
    val base = Modifier.size(34.dp).clip(CircleShape)
    val cell = when (day.status) {
        PillStatus.Taken -> base.background(AppColors.rose)
        PillStatus.Today -> base.background(AppColors.roseLight).border(2.5.dp, AppColors.rose, CircleShape)
        PillStatus.Missed -> base.background(AppColors.surface).border(1.5.dp, AppColors.rose, CircleShape)
        PillStatus.Break -> base.background(AppColors.divider)
        PillStatus.Upcoming -> base.background(Color(0xFFF7E9E4))
    }
    Box(cell.noRippleClick(onClick), contentAlignment = Alignment.Center) {
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

@Composable
private fun SectionTitle(text: String) =
    Text(text, style = MaterialTheme.typography.titleSmall, color = AppColors.ink)

@Composable
private fun MedicalWarning() {
    Box(
        Modifier.fillMaxWidth().clip(AppShapes.tile).background(AppColors.peachLight)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            MEDICAL_WARNING,
            style = MaterialTheme.typography.labelMedium, color = AppColors.peachText,
            lineHeight = 18.sp,
        )
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
                Text("Время приёма", fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.ink)
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
