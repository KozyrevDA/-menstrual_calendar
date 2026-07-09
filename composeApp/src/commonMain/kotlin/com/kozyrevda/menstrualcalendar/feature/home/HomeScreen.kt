package com.kozyrevda.menstrualcalendar.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.core.logic.CyclePredictor
import com.kozyrevda.menstrualcalendar.core.logic.MONTHS_SHORT
import com.kozyrevda.menstrualcalendar.core.logic.daysWord
import com.kozyrevda.menstrualcalendar.core.logic.rus
import com.kozyrevda.menstrualcalendar.core.logic.today
import com.kozyrevda.menstrualcalendar.core.logic.weekdayFull
import com.kozyrevda.menstrualcalendar.core.model.CycleDayInfo
import com.kozyrevda.menstrualcalendar.core.model.CyclePhase
import com.kozyrevda.menstrualcalendar.feature.common.LunaCard
import com.kozyrevda.menstrualcalendar.feature.common.noRippleClick
import com.kozyrevda.menstrualcalendar.navigation.Screen
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes

@Composable
fun HomeScreen(onOpen: (Screen) -> Unit) {
    val t = today()
    val settings = AppStateHolder.cycleSettings
    val info = settings?.let { CyclePredictor.getDayInfo(t, it) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text(
                "${t.weekdayFull()}, ${t.rus()}".uppercase(),
                style = MaterialTheme.typography.labelSmall, color = AppColors.subLight,
            )
            Text("Привет", style = MaterialTheme.typography.headlineMedium, color = AppColors.ink)
        }

        // ── кольцо цикла ──
        if (info != null && settings != null) {
            CycleRingCard(info = info, cycleLength = settings.cycleLengthDays)

            val next = CyclePredictor.getNextPeriodStart(settings, t)
            if (!info.isPeriod) {
                LunaCard(corner = AppShapes.cardSmall) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(Modifier.size(46.dp).clip(CircleShape).background(AppColors.roseLight), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${next.dayOfMonth}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AppColors.roseDark)
                                Text(MONTHS_SHORT[next.monthNumber - 1].lowercase(), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.roseDark)
                            }
                        }
                        Column {
                            Text(
                                "Месячные — через ${info.daysUntilNextPeriod} ${daysWord(info.daysUntilNextPeriod)}",
                                style = MaterialTheme.typography.titleSmall, color = AppColors.ink,
                            )
                            Text("Прогноз с точностью ±2 дня", style = MaterialTheme.typography.bodySmall, color = AppColors.sub)
                        }
                    }
                }
            }
        }

        // ── карточки-переходы ──
        NavCard("К", AppColors.roseLight, AppColors.roseDark, "Календарь", "Месячные, овуляция и фертильные дни") { onOpen(Screen.Calendar) }
        NavCard("С", AppColors.peachLight, AppColors.peachInk, "Симптомы", "Отметить самочувствие и настроение") { onOpen(Screen.LogSymptoms()) }
        NavCard("№", AppColors.greenLight, AppColors.green, "Таблетки", "Блистер 21+7 и напоминания") { onOpen(Screen.Pills) }
        NavCard("Л", AppColors.roseLight, AppColors.roseDark, "Луна — чат", "Подружка и психолог · всегда рядом") { onOpen(Screen.LunaChat) }

        // ── CTA premium ──
        Box(
            Modifier.fillMaxWidth().clip(AppShapes.cardSmall).background(AppColors.roseLight)
                .noRippleClick { onOpen(Screen.Paywall) }
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Луна Плюс", style = MaterialTheme.typography.titleLarge, color = AppColors.roseDark)
                Text(
                    "Безлимитный чат с Луной, расширенная статистика и прогнозы",
                    style = MaterialTheme.typography.bodySmall, color = AppColors.roseInk,
                )
                Box(
                    Modifier.padding(top = 8.dp).clip(AppShapes.pill).background(AppColors.rose)
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text("Попробовать бесплатно", fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }
    }
}

/** Карточка с кольцом прогресса цикла, днём и статусом. */
@Composable
private fun CycleRingCard(info: CycleDayInfo, cycleLength: Int) {
    LunaCard(corner = AppShapes.cardLarge) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(Modifier.size(210.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    val stroke = Stroke(strokeWidth, cap = StrokeCap.Round)
                    val inset = strokeWidth
                    val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
                    drawArc(Color(0xFFF8E4DC), 0f, 360f, false, Offset(inset, inset), arcSize, style = stroke)
                    drawArc(
                        AppColors.rose, -90f, 360f * info.cycleDay / cycleLength, false,
                        Offset(inset, inset), arcSize, style = stroke,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("день цикла", style = MaterialTheme.typography.bodySmall, color = AppColors.subLight)
                    Text(
                        "${info.cycleDay}", fontSize = 58.sp, fontWeight = FontWeight.Black,
                        color = AppColors.ink, lineHeight = 62.sp,
                    )
                    Text(
                        info.statusTitle(), style = MaterialTheme.typography.titleMedium,
                        color = AppColors.rose, textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 26.dp),
                    )
                }
            }
            Text(
                info.statusSubtitle(), style = MaterialTheme.typography.bodyMedium,
                color = AppColors.sub, textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun NavCard(glyph: String, glyphBg: Color, glyphFg: Color, title: String, subtitle: String, onClick: () -> Unit) {
    LunaCard(corner = AppShapes.cardSmall) {
        Row(
            Modifier.fillMaxWidth().noRippleClick(onClick).padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(Modifier.size(46.dp).clip(CircleShape).background(glyphBg), contentAlignment = Alignment.Center) {
                Text(glyph, fontSize = 16.sp, fontWeight = FontWeight.Black, color = glyphFg)
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = AppColors.ink)
                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = AppColors.sub)
            }
            Text("›", style = MaterialTheme.typography.titleMedium, color = AppColors.chevron)
        }
    }
}

/** Статус дня: месячные / овуляция / фертильные дни / до месячных N дней. */
private fun CycleDayInfo.statusTitle(): String = when {
    isPeriod -> "Месячные"
    isOvulation -> "Овуляция"
    isFertile -> "Фертильные дни"
    phase == CyclePhase.Follicular -> "Фолликулярная фаза"
    else -> "До месячных $daysUntilNextPeriod ${daysWord(daysUntilNextPeriod)}"
}

private fun CycleDayInfo.statusSubtitle(): String = when {
    isPeriod -> "День $cycleDay месячных — берегите себя"
    isOvulation || isFertile -> "Высокая вероятность зачатия"
    phase == CyclePhase.Follicular -> "Низкая вероятность зачатия"
    else -> "Лютеиновая фаза · месячные через $daysUntilNextPeriod ${daysWord(daysUntilNextPeriod)}"
}
