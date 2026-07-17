package com.example.taskapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colores personalizados
val DarkBg = Color(0xFF0D0D1A)
val CardBg = Color(0xFF1A1A2E)
val Purple = Color(0xFF7B2FBE)
val Blue = Color(0xFF2196F3)
val LightPurple = Color(0xFFBB86FC)
val TextWhite = Color(0xFFE8E8F0)
val TextGray = Color(0xFF8888AA)

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = DarkBg,
                    surface = CardBg,
                    primary = Purple,
                    onPrimary = TextWhite,
                    onBackground = TextWhite,
                    onSurface = TextWhite
                )
            ) {
                TaskScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.allTasks.collectAsState()
    var newTaskTitle by remember { mutableStateOf("") }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var editedTitle by remember { mutableStateOf("") }

    val completedCount = tasks.count { it.completed }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Purple, DarkBg)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Mis Tareas",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "$completedCount de ${tasks.size} completadas",
                        fontSize = 14.sp,
                        color = TextWhite.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Barra de progreso
                    if (tasks.isNotEmpty()) {
                        LinearProgressIndicator(
                            progress = { completedCount.toFloat() / tasks.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = LightPurple,
                            trackColor = TextWhite.copy(alpha = 0.2f)
                        )
                    }
                }
            }

            // Campo para agregar tarea
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    placeholder = { Text("Nueva tarea...", color = TextGray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = TextGray.copy(alpha = 0.3f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = LightPurple,
                        focusedContainerColor = CardBg,
                        unfocusedContainerColor = CardBg
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        viewModel.addTask(newTaskTitle)
                        newTaskTitle = ""
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Agregar", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }

            // Lista de tareas
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tasks) { task ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut()
                    ) {
                        TaskItem(
                            task = task,
                            onToggle = { viewModel.toggleCompleted(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            onEdit = {
                                taskToEdit = task
                                editedTitle = task.title
                            }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    // Diálogo de edición
    if (taskToEdit != null) {
        AlertDialog(
            onDismissRequest = { taskToEdit = null },
            containerColor = CardBg,
            title = { Text("Editar tarea", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text("Título", color = TextGray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = TextGray.copy(alpha = 0.3f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = LightPurple,
                        focusedContainerColor = CardBg,
                        unfocusedContainerColor = CardBg
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateTask(taskToEdit!!, editedTitle)
                    taskToEdit = null
                }) {
                    Text("Guardar", color = LightPurple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToEdit = null }) {
                    Text("Cancelar", color = TextGray)
                }
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox con color morado
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Purple,
                    uncheckedColor = TextGray,
                    checkmarkColor = TextWhite
                )
            )

            // Título
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                color = if (task.completed) TextGray else TextWhite,
                fontWeight = if (task.completed) FontWeight.Normal else FontWeight.Medium,
                textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                fontSize = 16.sp
            )

            // Botón editar
            TextButton(
                onClick = onEdit,
                colors = ButtonDefaults.textButtonColors(contentColor = Blue)
            ) {
                Text("Editar", fontSize = 13.sp)
            }

            // Botón eliminar
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF6B6B))
            ) {
                Text("Eliminar", fontSize = 13.sp)
            }
        }
    }
}