package com.kozyrevda.menstrualcalendar.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.logic.today
import com.kozyrevda.menstrualcalendar.core.model.CycleSettings
import com.kozyrevda.menstrualcalendar.feature.common.BackChevron
import com.kozyrevda.menstrualcalendar.feature.common.Cta
import com.kozyrevda.menstrualcalendar.feature.common.DatePickerCalendar
import com.kozyrevda.menstrualcalendar.feature.common.GhostButton
import com.kozyrevda.menstrualcalendar.feature.common.LunaCard
import com.kozyrevda.menstrualcalendar.feature.common.RadioRow
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes
import kotlinx.datetime.LocalDate

private val CYCLE_OPTS = listOf(
    "Меньше 21 дня" to 20, "21–25 дней" to 23, "26–30 дней" to 28,
    "31–35 дней" to 33, "Не знаю — рассчитайте за меня" to 28,
)
private val PERIOD_OPTS = listOf("1–3 дня" to 2, "4–5 дней" to 5, "6–7 дней" to 6, "Не знаю" to 5)

/**
 * Онбординг из 4 шагов:
 * 1. интро «Луна поможет следить за циклом»
 * 2. дата последних месячных
 * 3. длина цикла
 * 4. длина месячных
 */
@Composable
fun OnboardingScreen(onFinish: (CycleSettings) -> Unit) {
    var step by remember { mutableStateOf(0) }
    var lastStart by remember { mutableStateOf<LocalDate?>(null) }
    var cycleLabel by remember { mutableStateOf<String?>(null) }
    var cycleLen by remember { mutableStateOf(28) }
    var periodLabel by remember { mutableStateOf<String?>(null) }
    var periodLen by remember { mutableStateOf(5) }

    val quizSteps = 3   // шаги 1..3 после интро

    fun finish() = onFinish(
        CycleSettings(
            lastPeriodStart = lastStart ?: today(),
            cycleLengthDays = cycleLen,
            periodLengthDays = periodLen,
        )
    )

    Column(Modifier.fillMaxSize()) {
        // прогресс-бар (кроме интро)
        if (step > 0) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                BackChevron { step-- }
                Box(Modifier.weight(1f).height(8.dp).clip(AppShapes.pill).background(AppColors.borderSoft)) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(step / quizSteps.toFloat()).clip(AppShapes.pill).background(AppColors.rose))
                }
                Text("$step / $quizSteps", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.sub)
            }
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (step) {
                0 -> IntroStep()
                1 -> {
                    Question("Когда началась последняя менструация?")
                    Subtitle("Так мы рассчитаем ваш цикл и сразу дадим первый прогноз.")
                    DatePickerCalendar(selected = lastStart, rangeLen = periodLen) { lastStart = it }
                    Text(
                        "Дату всегда можно поменять в настройках",
                        style = MaterialTheme.typography.labelMedium, color = AppColors.subLight,
                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                    )
                }
                2 -> {
                    Question("Сколько обычно длится ваш цикл?")
                    Subtitle("От первого дня месячных до первого дня следующих.")
                    CYCLE_OPTS.forEach { (l, v) ->
                        RadioRow(l, cycleLabel == l) { cycleLabel = l; cycleLen = v }
                    }
                }
                3 -> {
                    Question("Сколько дней идут месячные?")
                    PERIOD_OPTS.forEach { (l, v) ->
                        RadioRow(l, periodLabel == l) { periodLabel = l; periodLen = v }
                    }
                    Box(
                        Modifier.fillMaxWidth().clip(AppShapes.tile).background(AppColors.peachLight)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "Если цикл нерегулярный — не страшно: прогноз будет уточняться с каждым месяцем.",
                            style = MaterialTheme.typography.labelMedium, color = AppColors.peachText, lineHeight = 18.sp,
                        )
                    }
                }
            }
        }

        Column(
            Modifier.padding(horizontal = 24.dp).padding(top = 12.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (step) {
                0 -> Cta("Настроить за 2 минуты") { step = 1 }
                1 -> {
                    Cta("Продолжить", enabled = lastStart != null) { step = 2 }
                    GhostButton("Не помню точную дату") { lastStart = null; step = 2 }
                }
                2 -> Cta("Продолжить", enabled = cycleLabel != null) { step = 3 }
                3 -> Cta("Готово", enabled = periodLabel != null) { finish() }
            }
        }
    }
}

@Composable
private fun Question(text: String) =
    Text(text, style = MaterialTheme.typography.headlineMedium, color = AppColors.ink)

@Composable
private fun Subtitle(text: String) =
    Text(text, style = MaterialTheme.typography.bodyLarge, color = AppColors.sub)

@Composable
private fun IntroStep() {
    Spacer(Modifier.height(24.dp))
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Луна поможет следить за циклом",
            style = MaterialTheme.typography.headlineLarge, color = AppColors.ink, textAlign = TextAlign.Center,
        )
        Text("Забота о вас каждый день цикла", style = MaterialTheme.typography.bodyLarge, color = AppColors.sub)
    }
    Spacer(Modifier.height(6.dp))
    listOf(
        Triple("28", AppColors.roseLight to AppColors.roseDark, "Календарь и прогнозы" to "Месячные, овуляция и фертильные дни"),
        Triple("Л", AppColors.peachLight to AppColors.peachInk, "Луна — подружка и психолог" to "Поддержка в любой день цикла"),
        Triple("✓", AppColors.greenLight to AppColors.green, "Напоминания о таблетках" to "Вовремя и незаметно для посторонних"),
    ).forEach { (glyph, colors, texts) ->
        LunaCard(corner = AppShapes.cardSmall) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    Modifier.size(46.dp).clip(RoundedCornerShape(16.dp)).background(colors.first),
                    contentAlignment = Alignment.Center,
                ) { Text(glyph, fontSize = 15.sp, fontWeight = FontWeight.Black, color = colors.second) }
                Column {
                    Text(texts.first, style = MaterialTheme.typography.titleSmall, color = AppColors.ink)
                    Text(texts.second, style = MaterialTheme.typography.labelMedium, color = AppColors.sub)
                }
            }
        }
    }
}
