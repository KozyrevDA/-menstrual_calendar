package com.kozyrevda.menstrualcalendar.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.core.data.createLunaChatRepository
import com.kozyrevda.menstrualcalendar.core.logic.CyclePredictor
import com.kozyrevda.menstrualcalendar.core.logic.rusTitle
import com.kozyrevda.menstrualcalendar.core.logic.today
import com.kozyrevda.menstrualcalendar.core.model.ChatMessage
import com.kozyrevda.menstrualcalendar.feature.common.BackChevron
import com.kozyrevda.menstrualcalendar.feature.common.noRippleClick
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes
import kotlinx.coroutines.launch

private val QUICK_TOPICS = listOf("Тревожно без причины", "ПМС и раздражительность", "Плохо сплю")

@Composable
fun LunaChatScreen(onBack: () -> Unit) {
    val repository = remember { createLunaChatRepository() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var draft by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    val messages = AppStateHolder.chatMessages

    fun cycleContext(): String? {
        val settings = AppStateHolder.cycleSettings ?: return null
        val info = CyclePredictor.getDayInfo(today(), settings)
        return "${info.cycleDay}-й день цикла, ${info.phase.rusTitle().lowercase()}"
    }

    fun send(text: String) {
        val t = text.trim()
        if (t.isEmpty() || busy) return
        AppStateHolder.appendChatMessage(ChatMessage(ChatMessage.Role.User, t))
        draft = ""
        busy = true
        scope.launch {
            val reply = runCatching { repository.reply(AppStateHolder.chatMessages, cycleContext()) }
                .getOrDefault("Кажется, связь прервалась. Попробуй ещё раз чуть позже.")
            AppStateHolder.appendChatMessage(ChatMessage(ChatMessage.Role.Luna, reply))
            busy = false
        }
    }

    LaunchedEffect(messages.size, busy) {
        val target = messages.size - 1 + if (busy) 1 else 0
        if (target >= 0) listState.animateScrollToItem(target)
    }

    Column(Modifier.fillMaxSize()) {
        // ── шапка ──
        Column {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BackChevron(onBack)
                Box {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(AppColors.peachLight),
                        contentAlignment = Alignment.Center,
                    ) { Text("Л", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AppColors.peachInk) }
                    Box(
                        Modifier.align(Alignment.BottomEnd).size(11.dp).clip(CircleShape)
                            .background(Color(0xFF7BC49A)).border(2.dp, AppColors.bg, CircleShape)
                    )
                }
                Column {
                    Text("Луна", style = MaterialTheme.typography.titleMedium, color = AppColors.ink)
                    Text("подружка · всегда рядом", style = MaterialTheme.typography.labelMedium, color = AppColors.sub)
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(AppColors.borderSoft))
        }

        // ── дисклеймер ──
        Box(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(14.dp)).background(AppColors.peachLight)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                "Луна поддержит, но не заменит врача или психотерапевта",
                fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AppColors.peachText,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
            )
        }

        // ── сообщения ──
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
        ) {
            items(messages) { MessageBubble(it) }
            if (busy) item { TypingIndicator() }
        }

        // ── быстрые темы ──
        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QUICK_TOPICS.take(2).forEach { topic -> QuickChip(topic) { send(topic) } }
            }
            Row { QuickChip(QUICK_TOPICS[2]) { send(QUICK_TOPICS[2]) } }
        }

        // ── ввод ──
        Column {
            Box(Modifier.fillMaxWidth().height(1.dp).background(AppColors.borderSoft))
            Row(
                Modifier.fillMaxWidth().background(AppColors.surface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BasicTextField(
                    draft, { draft = it }, singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.ink),
                    modifier = Modifier.weight(1f).height(46.dp).clip(AppShapes.pill)
                        .background(AppColors.bg).border(1.5.dp, AppColors.border, AppShapes.pill),
                    decorationBox = { inner ->
                        Box(Modifier.fillMaxSize().padding(horizontal = 18.dp), contentAlignment = Alignment.CenterStart) {
                            if (draft.isEmpty()) Text(
                                "Напишите, что чувствуете…",
                                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.subLight,
                            )
                            inner()
                        }
                    },
                )
                Box(
                    Modifier.size(46.dp)
                        .then(if (!busy) Modifier.shadow(8.dp, CircleShape, spotColor = AppColors.roseShadow) else Modifier)
                        .clip(CircleShape)
                        .background(if (busy) AppColors.roseDisabled else AppColors.rose)
                        .noRippleClick { send(draft) },
                    contentAlignment = Alignment.Center,
                ) { Text("➤", fontSize = 17.sp, color = Color.White) }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == ChatMessage.Role.User
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Box(
            Modifier.widthIn(max = 300.dp)
                .clip(if (isUser) AppShapes.bubbleUser else AppShapes.bubbleLuna)
                .background(if (isUser) AppColors.rose else AppColors.surface)
                .padding(horizontal = 15.dp, vertical = 11.dp)
        ) {
            Text(
                message.text, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = if (isUser) Color.White else AppColors.ink, lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        Modifier.clip(AppShapes.bubbleLuna).background(AppColors.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(1f, 0.6f, 0.3f).forEach { alpha ->
            Box(Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFE4A5B2).copy(alpha = alpha)))
        }
    }
}

@Composable
private fun QuickChip(label: String, onClick: () -> Unit) {
    Box(
        Modifier.clip(AppShapes.pill).background(AppColors.surface)
            .border(1.5.dp, AppColors.border, AppShapes.pill)
            .noRippleClick(onClick).padding(horizontal = 14.dp, vertical = 8.dp)
    ) { Text(label, fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.roseDark) }
}
