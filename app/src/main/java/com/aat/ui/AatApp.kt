package com.aat.ui

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aat.data.ChatMessage
import com.aat.data.ReminderItem
import com.aat.data.StudyPromptParser
import com.aat.data.StudyRepository

@Composable
fun AatApp() {
    val context = LocalContext.current
    val repo = remember { StudyRepository(context) }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val reminders = remember { mutableStateListOf<ReminderItem>() }
    val listState = rememberLazyListState()
    var input by rememberSaveable { mutableStateOf("") }
    var activeTab by rememberSaveable { mutableStateOf("IA") }

    LaunchedEffect(Unit) {
        messages.clear()
        messages.addAll(repo.loadMessages())
        reminders.clear()
        reminders.addAll(repo.loadReminders())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AAT • AI Academic Tutor") }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.systemBars),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HeroCard()
            }

            item {
                TabRow(activeTab = activeTab, onTabSelected = { activeTab = it })
            }

            item {
                QuickActions(
                    onAction = { prompt ->
                        input = prompt
                    }
                )
            }

            item {
                when (activeTab) {
                    "IA" -> AiPanel(
                        context = context,
                        repo = repo,
                        messages = messages,
                        reminders = reminders,
                        input = input,
                        onInputChange = { input = it },
                        onSend = {
                            if (input.isBlank()) return@AiPanel
                            repo.sendUserMessage(context, input)
                            messages.clear()
                            messages.addAll(repo.loadMessages())
                            reminders.clear()
                            reminders.addAll(repo.loadReminders())
                            input = ""
                            activeTab = "IA"
                        }
                    )
                    "Planner" -> PlannerPanel(reminders = reminders)
                    else -> LabsPanel()
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HeroCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF24124D),
                            Color(0xFF121225),
                            Color(0xFF1E2348)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Estudia con IA, recordatorios y estudio guiado",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Inspirada en apps educativas modernas, pero con identidad propia. Chat, quizzes, flashcards, cámara, voz y GPS listo para conectar APIs públicas.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun TabRow(activeTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf("IA", "Planner", "Labs").forEach { tab ->
            FilledTonalButton(
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f)
            ) {
                Text(tab)
            }
        }
    }
}

@Composable
private fun QuickActions(onAction: (String) -> Unit) {
    val actions = listOf(
        "Hazme un quiz de biología",
        "Crea flashcards de historia",
        "Resume este tema",
        "Genera una imagen educativa",
        "Tengo examen mañana a las 4:40 de biología, recuérdame"
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Acciones rápidas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        actions.forEach { action ->
            AssistChip(
                onClick = { onAction(action) },
                label = { Text(action) }
            )
        }
    }
}

@Composable
private fun AiPanel(
    context: Context,
    repo: StudyRepository,
    messages: List<ChatMessage>,
    reminders: List<ReminderItem>,
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Chat IA", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                messages.takeLast(8).forEach { message ->
                    MessageBubble(message)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe tu pregunta o recordatorio...") },
                    minLines = 2
                )

                Button(
                    onClick = onSend,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Enviar")
                }
            }
        }

        if (reminders.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Recordatorios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    reminders.takeLast(5).forEach { reminder ->
                        ReminderRow(reminder)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Surface(
        color = if (isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (isUser) "Tú" else "AAT",
                fontWeight = FontWeight.Bold,
                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(message.content)
        }
    }
}

@Composable
private fun ReminderRow(item: ReminderItem) {
    val stateText = if (item.done) "Hecho" else "Pendiente"
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(item.title, fontWeight = FontWeight.SemiBold)
        Text(
            text = "Programado: ${StudyPromptParser.humanTime(item.triggerAt)}",
            style = MaterialTheme.typography.bodySmall
        )
        Text("Estado: $stateText", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun PlannerPanel(reminders: List<ReminderItem>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Planner escolar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Aquí luego conectas calendario, exámenes, tareas, materias y horarios.")
            }
        }
        reminders.forEach { reminder ->
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(reminder.title, fontWeight = FontWeight.Bold)
                    Text("Estado: ${if (reminder.done) "Hecho" else "Pendiente"}")
                }
            }
        }
    }
}

@Composable
private fun LabsPanel() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Cámara", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Lista para OCR de apuntes, fotos y libros.")
            }
        }
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("GPS escolar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Aquí conectas Maps + Places para elegir colegio o encontrar uno cerca.")
            }
        }
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Generación de imágenes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("La UI ya está lista para conectarse a una API pública de generación.")
            }
        }
    }
}
