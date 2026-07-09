package com.kozyrevda.menstrualcalendar.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes

/** Клик без ripple — как в прототипе. */
fun Modifier.noRippleClick(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(indication = null, interactionSource = MutableInteractionSource(), onClick = onClick)
)

/** Белая карточка «Луны» с мягкой тенью. */
@Composable
fun LunaCard(
    modifier: Modifier = Modifier,
    corner: androidx.compose.foundation.shape.RoundedCornerShape = AppShapes.card,
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .shadow(6.dp, corner, ambientColor = AppColors.cardShadow, spotColor = AppColors.cardShadow)
            .clip(corner)
            .background(AppColors.surface)
    ) { content() }
}

/** Розовая pill-кнопка (CTA). */
@Composable
fun Cta(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .then(if (enabled) Modifier.shadow(10.dp, AppShapes.pill, spotColor = AppColors.roseShadow) else Modifier)
            .clip(AppShapes.pill)
            .background(if (enabled) AppColors.rose else AppColors.roseDisabled)
            .noRippleClick { if (enabled) onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White) }
}

/** Текстовая «призрачная» кнопка. */
@Composable
fun GhostButton(label: String, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().noRippleClick(onClick).padding(6.dp), contentAlignment = Alignment.Center) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.roseDark)
    }
}

/** Стрелка «назад». */
@Composable
fun BackChevron(onClick: () -> Unit) {
    Text(
        "‹", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.roseDark,
        modifier = Modifier.noRippleClick(onClick).padding(horizontal = 6.dp),
    )
}

/** Каркас экрана-заглушки: заголовок, описание, контент. */
@Composable
fun StubScaffold(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    content: ColumnScopeContent = {},
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 14.dp)) {
        if (onBack != null) {
            Row(verticalAlignment = Alignment.CenterVertically) { BackChevron(onBack) }
        }
        Column(
            Modifier.fillMaxSize().padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium, color = AppColors.ink, textAlign = TextAlign.Center)
            Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = AppColors.sub, textAlign = TextAlign.Center)
            content()
        }
    }
}
typealias ColumnScopeContent = @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit

/** Нижняя навигация в стиле прототипа: активная вкладка — розовая pill. */
@Composable
fun LunaBottomBar(
    labels: List<Pair<String, Boolean>>,   // подпись → активна?
    onSelect: (index: Int) -> Unit,
) {
    Column {
        Box(Modifier.fillMaxWidth().height(1.dp).background(AppColors.borderSoft))
        Row(
            Modifier.fillMaxWidth().background(AppColors.surface).padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            labels.forEachIndexed { i, (label, active) ->
                Box(
                    Modifier
                        .clip(AppShapes.pill)
                        .background(if (active) AppColors.roseLight else Color.Transparent)
                        .noRippleClick { onSelect(i) }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(
                        label, fontSize = 13.sp,
                        fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Bold,
                        color = if (active) AppColors.roseDark else AppColors.sub,
                    )
                }
            }
        }
    }
}

/** Радио-строка квиза: розовая подложка и кольцо-индикатор при выборе. */
@Composable
fun RadioRow(label: String, on: Boolean, onClick: () -> Unit) {
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (on) AppColors.roseLight else AppColors.surface)
            .border(
                width = if (on) 2.dp else 1.5.dp,
                color = if (on) AppColors.rose else AppColors.border,
                shape = shape,
            )
            .noRippleClick(onClick)
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(AppColors.surface)
                .border(
                    width = if (on) 7.dp else 2.dp,
                    color = if (on) AppColors.rose else AppColors.ghost,
                    shape = CircleShape,
                )
        )
        Text(
            label, fontSize = 15.sp,
            fontWeight = if (on) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (on) AppColors.roseDark else AppColors.ink,
        )
    }
}

/** Прозрачная текстовая кнопка. */
