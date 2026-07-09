package com.kozyrevda.menstrualcalendar.feature.paywall

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.feature.common.BackChevron
import com.kozyrevda.menstrualcalendar.feature.common.Cta
import com.kozyrevda.menstrualcalendar.feature.common.GhostButton
import com.kozyrevda.menstrualcalendar.feature.common.LunaCard
import com.kozyrevda.menstrualcalendar.feature.common.noRippleClick
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes

private enum class Plan(val title: String, val price: String, val note: String) {
    Month("Месяц", "299 ₽ / месяц", "Гибко, можно отменить в любой момент"),
    Year("Год", "2 490 ₽ / год", "≈ 208 ₽ в месяц"),
}

private val BENEFITS = listOf(
    "ИИ-поддержка без ограничений",
    "Расширенная аналитика цикла",
    "Напоминания о таблетках",
    "Приватный режим",
    "Backup — резервная копия данных",
)

@Composable
fun PaywallScreen(onBack: () -> Unit) {
    var plan by remember { mutableStateOf(Plan.Year) }
    var purchased by remember { mutableStateOf(AppStateHolder.isPremium) }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) { BackChevron(onBack) }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (purchased) {
                SuccessContent(onBack)
                return@Column
            }

            // ── заголовок ──
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    Modifier.size(64.dp).clip(CircleShape).background(AppColors.roseLight),
                    contentAlignment = Alignment.Center,
                ) { Text("✦", fontSize = 26.sp, color = AppColors.roseDark) }
                Text("Луна Premium", style = MaterialTheme.typography.headlineLarge, color = AppColors.ink, textAlign = TextAlign.Center)
                Text(
                    "Максимум заботы о вас и вашем цикле",
                    style = MaterialTheme.typography.bodyLarge, color = AppColors.sub, textAlign = TextAlign.Center,
                )
            }

            // ── преимущества ──
            LunaCard {
                Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    BENEFITS.forEach { benefit ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                Modifier.size(26.dp).clip(CircleShape).background(AppColors.roseLight),
                                contentAlignment = Alignment.Center,
                            ) { Text("✓", fontSize = 13.sp, fontWeight = FontWeight.Black, color = AppColors.roseDark) }
                            Text(benefit, style = MaterialTheme.typography.bodyMedium, color = AppColors.inkSoft)
                        }
                    }
                }
            }

            // ── тарифы ──
            Plan.entries.forEach { p ->
                PlanCard(
                    plan = p,
                    selected = plan == p,
                    badge = if (p == Plan.Year) "Выгодно −31%" else null,
                ) { plan = p }
            }

            // ── пробный период ──
            Box(
                Modifier.fillMaxWidth().clip(AppShapes.tile).background(AppColors.greenLight)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    "Пробный период: 3 дня за 1 ₽ — полный доступ ко всему",
                    style = MaterialTheme.typography.labelMedium, color = AppColors.green,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(2.dp))
            Cta("Попробовать 3 дня за 1 ₽") {
                AppStateHolder.activatePremiumStub()   // заглушка вместо billing
                purchased = true
            }
            Text(
                "Затем ${plan.price.lowercase()}. Отмена в любой момент в настройках подписки.",
                style = MaterialTheme.typography.labelMedium, color = AppColors.subLight,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
            )
            GhostButton("Восстановить покупки") { /* заглушка без billing */ }
        }
    }
}

@Composable
private fun PlanCard(plan: Plan, selected: Boolean, badge: String?, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) AppColors.roseLight else AppColors.surface)
            .border(if (selected) 2.dp else 1.5.dp, if (selected) AppColors.rose else AppColors.border, RoundedCornerShape(20.dp))
            .noRippleClick(onClick)
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(22.dp).clip(CircleShape).background(Color.White)
                .border(if (selected) 7.dp else 2.dp, if (selected) AppColors.rose else Color(0xFFDFC8C3), CircleShape)
        )
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    plan.price, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
                    color = if (selected) AppColors.roseDark else AppColors.ink,
                )
                if (badge != null) {
                    Box(Modifier.clip(AppShapes.pill).background(AppColors.rose).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(badge, fontSize = 10.5.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }
            Text(plan.note, style = MaterialTheme.typography.labelMedium, color = AppColors.sub)
        }
    }
}

@Composable
private fun SuccessContent(onBack: () -> Unit) {
    Spacer(Modifier.height(40.dp))
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier.size(72.dp).clip(CircleShape).background(AppColors.greenLight),
            contentAlignment = Alignment.Center,
        ) { Text("✓", fontSize = 30.sp, fontWeight = FontWeight.Black, color = AppColors.green) }
        Text("Premium активен", style = MaterialTheme.typography.headlineMedium, color = AppColors.ink)
        Text(
            "Спасибо, что вы с Луной! Все возможности открыты — пользуйтесь с удовольствием.",
            style = MaterialTheme.typography.bodyLarge, color = AppColors.sub, textAlign = TextAlign.Center,
        )
    }
    Spacer(Modifier.height(10.dp))
    Cta("Отлично") { onBack() }
}
