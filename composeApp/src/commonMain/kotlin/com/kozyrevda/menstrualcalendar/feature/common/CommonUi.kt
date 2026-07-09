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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

/** Переключатель в стиле прототипа. */
@Composable
fun LunaToggle(on: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(width = 46.dp, height = 27.dp)
            .clip(AppShapes.pill)
            .background(if (on) AppColors.rose else Color(0xFFEBD9D5))
            .noRippleClick(onClick)
            .padding(3.dp),
        contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(Modifier.size(21.dp).shadow(2.dp, CircleShape).clip(CircleShape).background(Color.White))
    }
}
