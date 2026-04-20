package com.example.productividad.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productividad.data.ActiveExercise
import com.example.productividad.data.AppColors
import com.example.productividad.data.GymTab
import com.example.productividad.ui.components.ExerciseTutorialDialog
import com.example.productividad.ui.components.accentVariantButtonColors
import com.example.productividad.ui.components.neutralFilledButtonColors
import com.example.productividad.ui.components.primaryButtonColors
import com.example.productividad.viewmodel.GymViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrainScreen(vm: GymViewModel, t: (String, String) -> String) {
    LaunchedEffect(vm.workoutTimerRunning) {
        while (vm.workoutTimerRunning) {
            delay(1000)
            vm.tickWorkoutTimer()
        }
    }
    LaunchedEffect(vm.restTimerRunning) {
        while (vm.restTimerRunning) {
            delay(1000)
            vm.tickRestTimer()
        }
    }

    if (vm.activeRoutineName == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            t("Mis rutinas de entrenamiento", "My workout routines"),
                            color = AppColors.TEXT,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            t("Selecciona una rutina para comenzar tu sesión.", "Select a routine to start your session."),
                            color = AppColors.MUTED
                        )
                    }
                }
            }
            items(vm.myRoutines, key = { it.id }) { routine ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(routine.name, color = AppColors.TEXT, fontWeight = FontWeight.Bold)
                        Text(
                            t("${routine.exercises.size} ejercicios", "${routine.exercises.size} exercises"),
                            color = AppColors.MUTED
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { vm.startWorkout(routine) },
                                colors = primaryButtonColors()
                            ) {
                                Text(t("Iniciar entreno", "Start workout"), fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { vm.removeMyRoutine(routine.id) }) {
                                Text(t("Eliminar", "Delete"), color = Color.Red.copy(alpha = 0.75f))
                            }
                        }
                    }
                }
            }
            if (vm.myRoutines.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(t("Todavía no tienes rutinas", "No routines yet"), color = AppColors.TEXT)
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { vm.selectedTab = GymTab.ROUTINES },
                                colors = primaryButtonColors()
                            ) {
                                Text(t("Crear mi primera rutina", "Create my first routine"), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        return
    }

    val totalMin = vm.workoutElapsedSeconds / 60
    val totalSec = vm.workoutElapsedSeconds % 60
    val restMin = vm.restTimerSecondsLeft / 60
    val restSec = vm.restTimerSecondsLeft % 60
    val done = vm.completedExercisesCount()
    val total = vm.activeExercises.size.coerceAtLeast(1)
    val progress = done / total.toFloat()

    var expandedExerciseId by rememberSaveable(vm.activeRoutineId) {
        mutableStateOf(vm.activeExercises.firstOrNull()?.id)
    }
    var timerPanelVisible by rememberSaveable(vm.activeRoutineId) { mutableStateOf(true) }

    LaunchedEffect(vm.activeExercises.map { it.id to it.completed }) {
        val firstIncompleteId = vm.activeExercises.firstOrNull { !it.completed }?.id
        val expandedExercise = vm.activeExercises.firstOrNull { it.id == expandedExerciseId }
        expandedExerciseId = when {
            vm.activeExercises.isEmpty() -> null
            expandedExercise == null -> firstIncompleteId ?: vm.activeExercises.first().id
            expandedExercise.completed -> {
                val currentIndex = vm.activeExercises.indexOfFirst { it.id == expandedExercise.id }
                vm.activeExercises
                    .drop(currentIndex + 1)
                    .firstOrNull { !it.completed }
                    ?.id ?: firstIncompleteId
            }
            else -> expandedExerciseId
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vm.activeExercises, key = { it.id }) { exercise ->
                val idx = vm.activeExercises.indexOfFirst { it.id == exercise.id }
                if (idx == -1) return@items

                ActiveExerciseCard(
                    ex = exercise,
                    exerciseNumber = idx + 1,
                    totalExercises = vm.activeExercises.size,
                    displayName = vm.displayExerciseName(exercise.name),
                    isExpanded = expandedExerciseId == exercise.id,
                    doneSets = exercise.sets.count { it.done },
                    onToggleExpand = {
                        expandedExerciseId = if (expandedExerciseId == exercise.id) null else exercise.id
                    },
                    onOpenTutorial = {
                        vm.findExerciseInCatalog(exercise.name)?.let { vm.selectedExerciseForTutorial = it }
                    },
                    onWeightChange = { setIndex, value -> vm.updateSet(idx, setIndex, weight = value) },
                    onRepsChange = { setIndex, value -> vm.updateSet(idx, setIndex, reps = value) },
                    onToggleSet = { setIndex, wasDone ->
                        vm.toggleSetDone(idx, setIndex)
                        if (!wasDone) vm.startRestTimer(90)
                    },
                    onAddSet = { vm.addSet(idx) },
                    t = t
                )
            }
        }

        FloatingTimerDock(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            visible = timerPanelVisible,
            routineName = vm.activeRoutineName.orEmpty(),
            workoutLabel = "${totalMin.toString().padStart(2, '0')}:${totalSec.toString().padStart(2, '0')}",
            restLabel = "${restMin.toString().padStart(2, '0')}:${restSec.toString().padStart(2, '0')}",
            progress = progress,
            done = done,
            total = total,
            workoutRunning = vm.workoutTimerRunning,
            restRunning = vm.restTimerRunning,
            restSecondsLeft = vm.restTimerSecondsLeft,
            onToggleVisibility = { timerPanelVisible = !timerPanelVisible },
            onToggleWorkout = {
                if (vm.workoutTimerRunning) vm.pauseWorkoutTimer() else vm.resumeWorkoutTimer()
            },
            onPauseRest = vm::pauseRestTimer,
            onRestPreset = vm::startRestTimer,
            onFinishWorkout = vm::finishWorkout,
            t = t
        )
    }

    if (vm.selectedExerciseForTutorial != null) {
        ExerciseTutorialDialog(vm.selectedExerciseForTutorial!!, t) {
            vm.selectedExerciseForTutorial = null
        }
    }
}

@Composable
private fun ActiveExerciseCard(
    ex: ActiveExercise,
    exerciseNumber: Int,
    totalExercises: Int,
    displayName: String,
    isExpanded: Boolean,
    doneSets: Int,
    onToggleExpand: () -> Unit,
    onOpenTutorial: () -> Unit,
    onWeightChange: (Int, String) -> Unit,
    onRepsChange: (Int, String) -> Unit,
    onToggleSet: (Int, Boolean) -> Unit,
    onAddSet: () -> Unit,
    t: (String, String) -> String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.BORDER, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp)
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
                        t("Ejercicio $exerciseNumber de $totalExercises", "Exercise $exerciseNumber of $totalExercises"),
                        color = AppColors.MUTED,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        displayName.uppercase(),
                        color = AppColors.TEXT,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.7.sp
                    )
                    Text(
                        ex.muscle.uppercase(),
                        color = AppColors.ACCENT,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    color = if (ex.completed) AppColors.ACCENT.copy(alpha = 0.14f) else AppColors.BG_SOFT,
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(
                        1.dp,
                        if (ex.completed) AppColors.ACCENT.copy(alpha = 0.45f) else AppColors.BORDER
                    )
                ) {
                    Text(
                        if (ex.completed) t("Completado", "Completed") else t("Pendiente", "Pending"),
                        color = if (ex.completed) AppColors.ACCENT else AppColors.TEXT_SECONDARY,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(8.dp))
                Surface(
                    color = AppColors.CARD_SOFT,
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier
                        .clickable(onClick = onToggleExpand)
                        .border(1.dp, AppColors.BORDER, RoundedCornerShape(999.dp))
                ) {
                    Text(
                        if (isExpanded) t("Ocultar", "Hide") else t("Abrir", "Open"),
                        color = AppColors.TEXT,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                color = AppColors.BG_SOFT,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            t("Series", "Sets"),
                            color = AppColors.MUTED,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$doneSets/${ex.sets.size}",
                            color = AppColors.TEXT,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Button(
                        onClick = onOpenTutorial,
                        colors = neutralFilledButtonColors(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(t("Tutorial", "Tutorial"), fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ex.sets.forEachIndexed { setIndex, set ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (set.done) AppColors.ACCENT.copy(alpha = 0.45f) else AppColors.BORDER,
                                    RoundedCornerShape(14.dp)
                                )
                                .background(
                                    if (set.done) AppColors.ACCENT.copy(alpha = 0.08f) else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${setIndex + 1}",
                                color = if (set.done) AppColors.ACCENT else AppColors.MUTED,
                                modifier = Modifier.width(18.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Black
                            )
                            OutlinedTextField(
                                value = set.weight,
                                onValueChange = { onWeightChange(setIndex, it) },
                                label = { Text(t("KG", "KG"), style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppColors.ACCENT,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = AppColors.CARD_SOFT,
                                    unfocusedContainerColor = AppColors.CARD_SOFT,
                                    focusedTextColor = AppColors.TEXT,
                                    unfocusedTextColor = AppColors.TEXT
                                )
                            )
                            OutlinedTextField(
                                value = set.reps,
                                onValueChange = { onRepsChange(setIndex, it) },
                                label = { Text("REPS", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppColors.ACCENT,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = AppColors.CARD_SOFT,
                                    unfocusedContainerColor = AppColors.CARD_SOFT,
                                    focusedTextColor = AppColors.TEXT,
                                    unfocusedTextColor = AppColors.TEXT
                                )
                            )
                            Button(
                                onClick = { onToggleSet(setIndex, set.done) },
                                modifier = Modifier.size(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = if (set.done) primaryButtonColors() else accentVariantButtonColors(),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                if (set.done) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                } else {
                                    Text("+", fontWeight = FontWeight.Black, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onAddSet,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = accentVariantButtonColors(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(t("+ Añadir serie", "+ Add set"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    if (ex.completed) {
                        t(
                            "Ejercicio completado. El siguiente ya quedó listo para abrirse.",
                            "Exercise completed. The next one is ready to open."
                        )
                    } else {
                        t(
                            "Ejercicio oculto para mantener el enfoque. Ábrelo cuando quieras registrar la siguiente serie.",
                            "Exercise collapsed to keep the session focused. Open it when you want to log the next set."
                        )
                    },
                    color = AppColors.MUTED,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun FloatingTimerDock(
    modifier: Modifier = Modifier,
    visible: Boolean,
    routineName: String,
    workoutLabel: String,
    restLabel: String,
    progress: Float,
    done: Int,
    total: Int,
    workoutRunning: Boolean,
    restRunning: Boolean,
    restSecondsLeft: Int,
    onToggleVisibility: () -> Unit,
    onToggleWorkout: () -> Unit,
    onPauseRest: () -> Unit,
    onRestPreset: (Int) -> Unit,
    onFinishWorkout: () -> Unit,
    t: (String, String) -> String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (visible) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            t("Temporizadores", "Timers"),
                            color = AppColors.TEXT,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onToggleVisibility) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = AppColors.TEXT)
                        }
                    }

                    Surface(
                        color = AppColors.BG_SOFT,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppColors.BORDER, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                t("Rutina actual", "Current routine"),
                                color = AppColors.MUTED,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                routineName,
                                color = AppColors.TEXT,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    t("$done de $total ejercicios", "$done of $total exercises"),
                                    color = AppColors.TEXT_SECONDARY,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "${(progress * 100).toInt()}%",
                                    color = AppColors.ACCENT,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(AppColors.BORDER)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                                        .height(4.dp)
                                        .background(AppColors.ACCENT)
                                )
                            }
                        }
                    }

                    TimerMetric(
                        label = t("Sesión", "Session"),
                        value = workoutLabel,
                        valueColor = AppColors.TEXT
                    )
                    TimerMetric(
                        label = t("Descanso", "Rest"),
                        value = restLabel,
                        valueColor = if (restRunning && restSecondsLeft < 15) Color(0xFFEF4444) else AppColors.ACCENT
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onToggleWorkout,
                            modifier = Modifier.weight(1f),
                            colors = if (workoutRunning) accentVariantButtonColors() else primaryButtonColors(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                if (workoutRunning) t("Pausar", "Pause") else t("Reanudar", "Resume"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = onPauseRest,
                            modifier = Modifier.weight(1f),
                            colors = neutralFilledButtonColors(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(t("Detener", "Stop"), fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(60, 90, 120).forEach { seconds ->
                            Button(
                                onClick = { onRestPreset(seconds) },
                                modifier = Modifier.weight(1f),
                                colors = neutralFilledButtonColors(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
                            ) {
                                Text("${seconds}s", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Button(
                        onClick = onFinishWorkout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.SUCCESS,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(t("Finalizar entreno", "Finish workout"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            FloatingActionButton(
                onClick = onToggleVisibility,
                containerColor = AppColors.ACCENT,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Timer, contentDescription = t("Mostrar temporizador", "Show timer"))
            }
        }
    }
}

@Composable
private fun TimerMetric(
    label: String,
    value: String,
    valueColor: Color
) {
    Surface(
        color = AppColors.BG_SOFT,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.BORDER, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                color = AppColors.MUTED,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                value,
                color = valueColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}
