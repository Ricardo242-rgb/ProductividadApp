package com.example.productividad.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productividad.data.AppColors
import com.example.productividad.data.GymTab
import com.example.productividad.data.WorkoutExerciseNote
import com.example.productividad.data.WorkoutNote
import com.example.productividad.ui.components.neutralFilledButtonColors
import com.example.productividad.ui.components.primaryButtonColors
import com.example.productividad.viewmodel.GymViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(vm: GymViewModel, t: (String, String) -> String) {
    var selectedNoteId by remember { mutableStateOf<String?>(null) }
    var confirmDeleteNoteId by remember { mutableStateOf<String?>(null) }
    var confirmClearAll by remember { mutableStateOf(false) }
    val selectedNote = vm.workoutNotes.firstOrNull { it.id == selectedNoteId }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            NotesHeaderCard(
                totalNotes = vm.workoutNotes.size,
                annotatedNotes = vm.notesWithAnnotationCount(),
                onClearAll = { confirmClearAll = true },
                hasNotes = vm.workoutNotes.isNotEmpty(),
                t = t
            )
        }

        if (vm.workoutNotes.isEmpty()) {
            item {
                EmptyNotesCard(
                    onTrainClick = { vm.selectedTab = GymTab.TRAIN },
                    t = t
                )
            }
        } else {
            items(vm.workoutNotes, key = { it.id }) { note ->
                WorkoutNoteSummaryCard(
                    note = note,
                    t = t,
                    onOpen = { selectedNoteId = note.id },
                    onDelete = { confirmDeleteNoteId = note.id }
                )
            }
        }
    }

    if (selectedNote != null) {
        WorkoutNoteDetailSheet(
            note = selectedNote,
            t = t,
            onDismiss = { selectedNoteId = null },
            onSave = { annotation ->
                vm.updateWorkoutNoteAnnotation(selectedNote.id, annotation)
                selectedNoteId = null
            },
            onDelete = {
                selectedNoteId = null
                confirmDeleteNoteId = selectedNote.id
            }
        )
    }

    if (confirmDeleteNoteId != null) {
        AlertDialog(
            onDismissRequest = { confirmDeleteNoteId = null },
            title = { Text(t("Borrar registro", "Delete record")) },
            text = {
                Text(
                    t(
                        "Este registro y su sesión asociada se eliminarán del historial.",
                        "This log and its linked session will be removed from history."
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteWorkoutRecord(confirmDeleteNoteId!!)
                        confirmDeleteNoteId = null
                    }
                ) {
                    Text(t("Borrar", "Delete"), color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteNoteId = null }) {
                    Text(t("Cancelar", "Cancel"))
                }
            }
        )
    }

    if (confirmClearAll) {
        AlertDialog(
            onDismissRequest = { confirmClearAll = false },
            title = { Text(t("Borrar todos los registros", "Delete all records")) },
            text = {
                Text(
                    t(
                        "Se eliminarán todas las notas y el historial asociado. Esta acción no se puede deshacer.",
                        "All notes and linked history will be removed. This action cannot be undone."
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.clearWorkoutRecords()
                        selectedNoteId = null
                        confirmClearAll = false
                    }
                ) {
                    Text(t("Borrar todo", "Delete all"), color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClearAll = false }) {
                    Text(t("Cancelar", "Cancel"))
                }
            }
        )
    }
}

@Composable
private fun NotesHeaderCard(
    totalNotes: Int,
    annotatedNotes: Int,
    onClearAll: () -> Unit,
    hasNotes: Boolean,
    t: (String, String) -> String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.BORDER, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        t("BITÁCORA", "LOGBOOK"),
                        color = AppColors.ACCENT,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        t("Tus registros de entrenamiento", "Your workout records"),
                        color = AppColors.TEXT,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                if (hasNotes) {
                    TextButton(onClick = onClearAll) {
                        Text(t("Borrar todo", "Delete all"), color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NotesMetric(
                    modifier = Modifier.weight(1f),
                    label = t("Registros", "Records"),
                    value = totalNotes.toString()
                )
                NotesMetric(
                    modifier = Modifier.weight(1f),
                    label = t("Con nota", "With note"),
                    value = annotatedNotes.toString()
                )
            }
        }
    }
}

@Composable
private fun WorkoutNoteSummaryCard(
    note: WorkoutNote,
    t: (String, String) -> String,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val dateLabel = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("es", "MX")).format(Date(note.timestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.BORDER, RoundedCornerShape(20.dp))
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(note.routineName, color = AppColors.TEXT, fontWeight = FontWeight.ExtraBold)
                    Text(dateLabel, color = AppColors.MUTED, style = MaterialTheme.typography.bodySmall)
                }
                TextButton(onClick = onDelete) {
                    Text(t("Borrar", "Delete"), color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryChip(modifier = Modifier.weight(1f), label = t("Duración", "Duration"), value = "${note.durationMinutes}m")
                SummaryChip(modifier = Modifier.weight(1f), label = t("Series", "Sets"), value = note.totalSets.toString())
                SummaryChip(modifier = Modifier.weight(1f), label = t("Volumen", "Volume"), value = note.totalVolume.toString())
            }

            Text(
                if (note.annotation.isBlank()) {
                    t("Sin comentario guardado.", "No saved comment.")
                } else {
                    note.annotation
                },
                color = if (note.annotation.isBlank()) AppColors.MUTED else AppColors.TEXT_SECONDARY,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutNoteDetailSheet(
    note: WorkoutNote,
    t: (String, String) -> String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onDelete: () -> Unit
) {
    var annotationDraft by remember(note.id) { mutableStateOf(note.annotation) }

    LaunchedEffect(note.annotation) {
        annotationDraft = note.annotation
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppColors.CARD
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        note.routineName.uppercase(),
                        color = AppColors.TEXT,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("es", "MX")).format(Date(note.timestamp)),
                        color = AppColors.MUTED
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryChip(modifier = Modifier.weight(1f), label = t("Duración", "Duration"), value = "${note.durationMinutes}m")
                    SummaryChip(modifier = Modifier.weight(1f), label = t("Series", "Sets"), value = note.totalSets.toString())
                    SummaryChip(modifier = Modifier.weight(1f), label = t("Volumen", "Volume"), value = note.totalVolume.toString())
                }
            }

            items(note.exercises) { exercise ->
                ExerciseLogCard(exercise = exercise, t = t)
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AppColors.BG_SOFT),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppColors.BORDER, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            t("COMENTARIO", "COMMENT"),
                            color = AppColors.ACCENT,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black
                        )
                        OutlinedTextField(
                            value = annotationDraft,
                            onValueChange = { annotationDraft = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            placeholder = {
                                Text(
                                    t(
                                        "Ej. subir peso, mejorar técnica, ajustar descanso...",
                                        "Example: increase load, improve technique, adjust rest..."
                                    )
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.ACCENT,
                                unfocusedBorderColor = AppColors.BORDER,
                                focusedContainerColor = AppColors.CARD,
                                unfocusedContainerColor = AppColors.CARD,
                                focusedTextColor = AppColors.TEXT,
                                unfocusedTextColor = AppColors.TEXT
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onSave(annotationDraft.trim()) },
                                modifier = Modifier.weight(1f),
                                colors = primaryButtonColors()
                            ) {
                                Text(t("Guardar", "Save"), fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                colors = neutralFilledButtonColors()
                            ) {
                                Text(t("Cerrar", "Close"), fontWeight = FontWeight.Bold)
                            }
                        }
                        TextButton(
                            onClick = onDelete,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(t("Borrar registro", "Delete record"), color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseLogCard(exercise: WorkoutExerciseNote, t: (String, String) -> String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.BG_SOFT),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.BORDER, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(exercise.name, color = AppColors.TEXT, fontWeight = FontWeight.Bold)
            Text(
                t(
                    "${exercise.completedSets}/${exercise.plannedSets} series completadas",
                    "${exercise.completedSets}/${exercise.plannedSets} completed sets"
                ),
                color = AppColors.MUTED,
                style = MaterialTheme.typography.labelSmall
            )
            if (exercise.sets.isEmpty()) {
                Text(
                    t("Sin series marcadas.", "No sets marked."),
                    color = AppColors.MUTED,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                exercise.sets.forEach { set ->
                    Surface(
                        color = AppColors.CARD,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            t(
                                "Serie ${set.setNumber}: ${set.weight} kg x ${set.reps}",
                                "Set ${set.setNumber}: ${set.weight} kg x ${set.reps}"
                            ),
                            color = AppColors.TEXT_SECONDARY,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyNotesCard(onTrainClick: () -> Unit, t: (String, String) -> String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.BORDER, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(t("Todavía no tienes registros", "No workout logs yet"), color = AppColors.TEXT, fontWeight = FontWeight.Bold)
            Text(
                t(
                    "Cuando termines una rutina, aquí aparecerá resumida para que puedas revisarla o borrarla después.",
                    "When you finish a workout, it will show up here so you can review or delete it later."
                ),
                color = AppColors.MUTED
            )
            Button(onClick = onTrainClick, colors = primaryButtonColors()) {
                Text(t("Ir a entrenar", "Go to train"), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NotesMetric(modifier: Modifier = Modifier, label: String, value: String) {
    Surface(
        color = AppColors.BG_SOFT,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.border(1.dp, AppColors.BORDER, RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall)
            Text(value, color = AppColors.TEXT, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun SummaryChip(modifier: Modifier = Modifier, label: String, value: String) {
    Surface(
        color = AppColors.BG_SOFT,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.border(1.dp, AppColors.BORDER, RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall)
            Text(value, color = AppColors.TEXT, fontWeight = FontWeight.Bold)
        }
    }
}
