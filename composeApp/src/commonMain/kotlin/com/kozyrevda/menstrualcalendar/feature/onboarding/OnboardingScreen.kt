package com.kozyrevda.menstrualcalendar.feature.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kozyrevda.menstrualcalendar.feature.common.Cta
import com.kozyrevda.menstrualcalendar.feature.common.StubScaffold
import com.kozyrevda.menstrualcalendar.theme.AppColors

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    StubScaffold(
        title = "Луна — календарь цикла",
        subtitle = "Забота о вас каждый день цикла",
    ) {
        Text(
            "Квиз онбординга появится на следующем этапе",
            style = MaterialTheme.typography.labelMedium, color = AppColors.subLight,
            textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp),
        )
        Cta("Настроить за 2 минуты") { onFinish() }
    }
}
