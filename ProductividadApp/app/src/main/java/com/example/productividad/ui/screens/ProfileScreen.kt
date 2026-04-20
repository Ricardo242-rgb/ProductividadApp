package com.example.productividad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productividad.data.*
import com.example.productividad.ui.components.appChipColors
import com.example.productividad.ui.components.primaryButtonColors
import com.example.productividad.viewmodel.GymViewModel

@Composable
fun ProfileScreen(vm: GymViewModel, t: (String, String) -> String) {
    var nameInput by remember(vm.preferredName) { mutableStateOf(vm.preferredName) }
    var weightInput by remember(vm.bodyWeight) { mutableStateOf(vm.bodyWeight) }
    var unitInput by remember(vm.weightUnit) { mutableStateOf(vm.weightUnit) }
    var ageInput by remember(vm.userAge) { mutableStateOf(vm.userAge) }
    var heightInput by remember(vm.userHeight) { mutableStateOf(vm.userHeight) }
    val achievements = achievementGoals(vm, t)
    val unlockedAchievements = achievements.count { it.achieved }
    val achievementGroups = achievements.groupBy { it.category }.toList()

    LazyColumn(
        modifier = Modifier.fillMaxSize(), 
        contentPadding = PaddingValues(16.dp), 
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD), 
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp), 
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        t("Ajustes de Cuenta", "Account Settings"), 
                        color = AppColors.ACCENT, 
                        style = MaterialTheme.typography.labelMedium, 
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                vm.displayName(),
                                color = AppColors.TEXT,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                t("Tu perfil RIFIT y tus datos base.", "Your RIFIT profile and your base data."),
                                color = AppColors.MUTED,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        val sInfo = vm.streakInfo()
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AppColors.BG_SOFT),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.border(1.dp, AppColors.ACCENT.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🛡️", fontSize = 16.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(sInfo.shields.toString(), color = AppColors.TEXT, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD), 
                modifier = Modifier.fillMaxWidth().border(1.dp, AppColors.BORDER, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp), 
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(t("PREFERENCIAS", "PREFERENCES"), color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(t("Tema oscuro", "Dark theme"), color = AppColors.TEXT, fontWeight = FontWeight.Bold)
                            Text(t("Cambia entre una paleta clara y otra más sobria.", "Switch between the light palette and a more restrained dark one."), color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall)
                        }
                        Switch(
                            checked = vm.isDarkTheme,
                            onCheckedChange = { vm.changeTheme(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = AppColors.ACCENT)
                        )
                    }

                    HorizontalDivider(color = AppColors.BORDER)

                    Text(t("IDIOMA", "LANGUAGE"), color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = vm.language == AppLanguage.ES, 
                            onClick = { vm.changeLanguage(AppLanguage.ES) }, 
                            label = { Text("ESPAÑOL", fontWeight = FontWeight.Bold) }, 
                            colors = appChipColors()
                        )
                        FilterChip(
                            selected = vm.language == AppLanguage.EN, 
                            onClick = { vm.changeLanguage(AppLanguage.EN) }, 
                            label = { Text("ENGLISH", fontWeight = FontWeight.Bold) }, 
                            colors = appChipColors()
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD), 
                modifier = Modifier.fillMaxWidth().border(1.dp, AppColors.BORDER, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp), 
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(t("Cómo quieres que te llamemos", "How should we call you")) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.ACCENT)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = weightInput, 
                            onValueChange = { weightInput = it }, 
                            label = { Text(t("Peso", "Weight")) }, 
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.ACCENT)
                        )
                        Row {
                            FilterChip(
                                selected = unitInput == "kg", 
                                onClick = { unitInput = "kg" }, 
                                label = { Text("KG") }, 
                                colors = appChipColors()
                            )
                            Spacer(Modifier.width(8.dp))
                            FilterChip(
                                selected = unitInput == "lb", 
                                onClick = { unitInput = "lb" }, 
                                label = { Text("LB") }, 
                                colors = appChipColors()
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                         OutlinedTextField(
                            value = heightInput, 
                            onValueChange = { heightInput = it }, 
                            label = { Text(t("Estatura (cm)", "Height")) }, 
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.ACCENT)
                        )
                        OutlinedTextField(
                            value = ageInput, 
                            onValueChange = { ageInput = it }, 
                            label = { Text(t("Edad", "Age")) }, 
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.ACCENT)
                        )
                    }
                    
                    Button(
                        onClick = { 
                            vm.updatePersonalProfile(
                                name = nameInput,
                                weight = weightInput,
                                unit = unitInput,
                                age = ageInput,
                                height = heightInput
                            )
                        }, 
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = primaryButtonColors()
                    ) {
                        Text(t("ACTUALIZAR PERFIL", "UPDATE PROFILE"), fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Charts Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD), 
                modifier = Modifier.fillMaxWidth().border(1.dp, AppColors.BORDER, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(t("HISTORIAL DE RACHAS", "STREAK HISTORY"), color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(AppColors.BG_SOFT, RoundedCornerShape(12.dp)).padding(16.dp)) {
                        StreakHistoryChart(vm.streakInfo().history)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LegendItem(AppColors.ACCENT, t("Entreno", "Workout"))
                        LegendItem(AppColors.ACCENT_VARIANT, t("Escudo usado", "Shield used"))
                        LegendItem(AppColors.MUTED.copy(alpha = 0.2f), t("Descanso", "Rest"))
                    }
                }
            }
        }

        item {
            AchievementSummaryCard(
                unlocked = unlockedAchievements,
                total = achievements.size,
                t = t
            )
        }

        items(achievementGroups, key = { it.first }) { (category, goals) ->
            AchievementCategoryCard(category = category, goals = goals, t = t)
        }
    }
}

private data class AchievementGoal(
    val category: String,
    val title: String,
    val description: String,
    val hint: String,
    val current: Int,
    val target: Int
) {
    val achieved: Boolean get() = current >= target
    val progress: Float get() = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
}

private fun achievementGoals(vm: GymViewModel, t: (String, String) -> String): List<AchievementGoal> {
    return listOf(
        AchievementGoal(
            category = t("Arranque", "Kickoff"),
            title = t("Primer paso", "First step"),
            description = t("Completa tu primer entrenamiento.", "Complete your first workout."),
            hint = t("Empieza con cualquier rutina guardada.", "Start with any saved routine."),
            current = vm.totalWorkouts(),
            target = 1
        ),
        AchievementGoal(
            category = t("Arranque", "Kickoff"),
            title = t("Constructor de rutinas", "Routine builder"),
            description = t("Guarda tu primera rutina personalizada.", "Save your first custom routine."),
            hint = t("Ve a Rutinas y arma una sesión propia.", "Go to Routines and build your own session."),
            current = vm.myRoutines.size,
            target = 1
        ),
        AchievementGoal(
            category = t("Constancia", "Consistency"),
            title = t("Guerrero semanal", "Weekly warrior"),
            description = t("Entrena 3 veces en una semana.", "Train 3 times in one week."),
            hint = t("Mantén 3 sesiones dentro de los últimos 7 días.", "Keep 3 sessions within the last 7 days."),
            current = vm.weeklyWorkouts(),
            target = 3
        ),
        AchievementGoal(
            category = t("Constancia", "Consistency"),
            title = t("Motor encendido", "Engine on"),
            description = t("Llega a 5 entrenamientos totales.", "Reach 5 total workouts."),
            hint = t("Suma sesiones, no importa la rutina.", "Any finished workout counts."),
            current = vm.totalWorkouts(),
            target = 5
        ),
        AchievementGoal(
            category = t("Constancia", "Consistency"),
            title = t("Disciplina real", "Real discipline"),
            description = t("Llega a 15 entrenamientos totales.", "Reach 15 total workouts."),
            hint = t("Una constancia corta pero firme ya lo desbloquea.", "A short but solid streak unlocks it."),
            current = vm.totalWorkouts(),
            target = 15
        ),
        AchievementGoal(
            category = t("Constancia", "Consistency"),
            title = t("Racha de acero", "Steel streak"),
            description = t("Mantén 7 días de racha.", "Hold a 7-day streak."),
            hint = t("Tus escudos te ayudan a no romper el ritmo.", "Your shields help protect the streak."),
            current = vm.estimatedStreakDays(),
            target = 7
        ),
        AchievementGoal(
            category = t("Constancia", "Consistency"),
            title = t("Inquebrantable", "Unbreakable"),
            description = t("Mantén 14 días de racha.", "Hold a 14-day streak."),
            hint = t("La clave es no dejar pasar días seguidos sin entrenar.", "Avoid letting back-to-back rest days pile up."),
            current = vm.estimatedStreakDays(),
            target = 14
        ),
        AchievementGoal(
            category = t("Volumen", "Volume"),
            title = t("Titán inicial", "Starter titan"),
            description = t("Mueve 5,000 kg acumulados.", "Move 5,000 kg total."),
            hint = t("Marca tus cargas en las series para contar el volumen.", "Log your weights in each set to count volume."),
            current = vm.totalVolumeAll(),
            target = 5000
        ),
        AchievementGoal(
            category = t("Volumen", "Volume"),
            title = t("Acero pesado", "Heavy steel"),
            description = t("Mueve 20,000 kg acumulados.", "Move 20,000 kg total."),
            hint = t("El volumen sube rápido cuando completas todas tus series.", "Volume climbs fast when you finish all your sets."),
            current = vm.totalVolumeAll(),
            target = 20000
        ),
        AchievementGoal(
            category = t("Volumen", "Volume"),
            title = t("Bestia del gimnasio", "Gym beast"),
            description = t("Mueve 50,000 kg acumulados.", "Move 50,000 kg total."),
            hint = t("Consistencia + cargas registradas = progreso visible.", "Consistency plus logged weights makes progress visible."),
            current = vm.totalVolumeAll(),
            target = 50000
        ),
        AchievementGoal(
            category = t("Series", "Sets"),
            title = t("Primer bloque", "First block"),
            description = t("Completa 20 series en total.", "Complete 20 sets total."),
            hint = t("Finaliza tus series con el botón de hecho.", "Mark finished sets with the done button."),
            current = vm.totalSetsAll(),
            target = 20
        ),
        AchievementGoal(
            category = t("Series", "Sets"),
            title = t("Volumen serio", "Serious volume"),
            description = t("Completa 100 series en total.", "Complete 100 sets total."),
            hint = t("Las rutinas largas empujan mucho este contador.", "Longer routines move this counter fast."),
            current = vm.totalSetsAll(),
            target = 100
        ),
        AchievementGoal(
            category = t("Series", "Sets"),
            title = t("Maratón de hierro", "Iron marathon"),
            description = t("Completa 250 series en total.", "Complete 250 sets total."),
            hint = t("Ve registrando todo; aquí se nota el trabajo acumulado.", "Keep logging everything; this is where accumulation shows."),
            current = vm.totalSetsAll(),
            target = 250
        ),
        AchievementGoal(
            category = t("Registro", "Logging"),
            title = t("Primera bitácora", "First log"),
            description = t("Genera tu primera nota automática.", "Generate your first automatic note."),
            hint = t("Solo necesitas finalizar una rutina.", "Just finish one workout."),
            current = vm.workoutNotes.size,
            target = 1
        ),
        AchievementGoal(
            category = t("Registro", "Logging"),
            title = t("Atleta consciente", "Aware athlete"),
            description = t("Escribe anotaciones en 3 notas.", "Write notes in 3 workout logs."),
            hint = t("Anota mejoras, pesos o sensaciones al terminar.", "Write down improvements, weights, or sensations after training."),
            current = vm.notesWithAnnotationCount(),
            target = 3
        ),
        AchievementGoal(
            category = t("Registro", "Logging"),
            title = t("Analista de progreso", "Progress analyst"),
            description = t("Escribe anotaciones en 10 notas.", "Write notes in 10 workout logs."),
            hint = t("Tus notas te van a servir para subir cargas con criterio.", "Your notes help you increase loads with intention."),
            current = vm.notesWithAnnotationCount(),
            target = 10
        ),
        AchievementGoal(
            category = t("Planificación", "Planning"),
            title = t("Biblioteca propia", "Own library"),
            description = t("Guarda 5 rutinas.", "Save 5 routines."),
            hint = t("Puedes mezclar plantillas y rutinas creadas por ti.", "You can mix templates and your own routines."),
            current = vm.myRoutines.size,
            target = 5
        ),
        AchievementGoal(
            category = t("Planificación", "Planning"),
            title = t("Día completo", "Full day"),
            description = t("Haz 10 series en un solo día.", "Complete 10 sets in one day."),
            hint = t("Una sola sesión bien cerrada puede bastar.", "One solid session can be enough."),
            current = vm.totalSetsToday(),
            target = 10
        )
    )
}

@Composable
private fun AchievementSummaryCard(unlocked: Int, total: Int, t: (String, String) -> String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.BORDER, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                t("LOGROS RIFIT", "RIFIT ACHIEVEMENTS"),
                color = AppColors.ACCENT,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = AppColors.ACCENT.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🏆", fontSize = 24.sp)
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        t("Llevas $unlocked de $total desbloqueados", "You have unlocked $unlocked of $total"),
                        color = AppColors.TEXT,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        t(
                            "Cada logro muestra cuánto te falta y qué acción te conviene tomar para desbloquearlo.",
                            "Each achievement shows what is missing and the best action to unlock it."
                        ),
                        color = AppColors.MUTED
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementCategoryCard(
    category: String,
    goals: List<AchievementGoal>,
    t: (String, String) -> String
) {
    val unlocked = goals.count { it.achieved }

    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.BORDER, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(category, color = AppColors.TEXT, fontWeight = FontWeight.ExtraBold)
                Text(
                    t("$unlocked/${goals.size} listos", "$unlocked/${goals.size} unlocked"),
                    color = AppColors.ACCENT,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            goals.forEach { goal ->
                AchievementItem(goal = goal, t = t)
            }
        }
    }
}

@Composable
private fun AchievementItem(goal: AchievementGoal, t: (String, String) -> String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (goal.achieved) AppColors.BG_SOFT else AppColors.CARD_SOFT),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (goal.achieved) AppColors.ACCENT.copy(alpha = 0.45f) else AppColors.BORDER,
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = if (goal.achieved) AppColors.ACCENT.copy(alpha = 0.18f) else AppColors.BG
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (goal.achieved) "🏆" else "🎯", fontSize = 18.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(goal.title, color = AppColors.TEXT, fontWeight = FontWeight.Bold)
                    Text(goal.description, color = AppColors.TEXT_SECONDARY, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    if (goal.achieved) t("LISTO", "DONE") else "${goal.current}/${goal.target}",
                    color = if (goal.achieved) AppColors.ACCENT else AppColors.MUTED,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(AppColors.BORDER, RoundedCornerShape(999.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(goal.progress)
                        .fillMaxHeight()
                        .background(AppColors.ACCENT, RoundedCornerShape(999.dp))
                )
            }

            Text(
                t("Cómo desbloquearlo: ${goal.hint}", "How to unlock it: ${goal.hint}"),
                color = AppColors.MUTED,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Text(label, color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun StreakHistoryChart(history: List<GymViewModel.StreakDay>) {
    val lastDays = history.takeLast(14) // Show last 2 weeks
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        lastDays.forEach { day ->
            val color = when {
                day.workedOut -> AppColors.ACCENT
                day.shieldUsed -> AppColors.ACCENT_VARIANT
                else -> AppColors.MUTED.copy(alpha = 0.2f)
            }
            
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (day.shieldUsed) {
                    Text("🛡️", fontSize = 10.sp)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (day.workedOut || day.shieldUsed) 60.dp else 20.dp)
                        .background(color, RoundedCornerShape(4.dp))
                )
                Text(
                    day.date.takeLast(2), // Just the day number
                    color = AppColors.MUTED,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp
                )
            }
        }
    }
}
