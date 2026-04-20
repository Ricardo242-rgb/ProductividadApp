package com.example.productividad.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productividad.data.*
import com.example.productividad.ui.components.*
import com.example.productividad.viewmodel.GymViewModel

@Composable
fun RoutinesScreen(vm: GymViewModel, t: (String, String) -> String) {
    var mode by remember { mutableStateOf("create") }
    var showCatalog by remember { mutableStateOf(false) }
    val shouldRenderCatalog = mode == "create" && showCatalog
    val filtered = if (shouldRenderCatalog) {
        remember(vm.customSearch, vm.customMuscleFilter, vm.customEquipmentFilter, vm.exerciseCatalog.size) {
            vm.filteredCatalog()
        }
    } else {
        emptyList()
    }
    val muscles = if (shouldRenderCatalog) {
        remember(vm.exerciseCatalog.size) {
            vm.availableMuscleFilters()
        }
    } else {
        emptyList()
    }
    val equipments = if (shouldRenderCatalog) {
        remember(vm.exerciseCatalog.size) {
            vm.availableEquipmentFilters()
        }
    } else {
        emptyList()
    }

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
                        t("CENTRO DE RUTINAS", "ROUTINE HUB"), 
                        color = AppColors.ACCENT, 
                        style = MaterialTheme.typography.labelMedium, 
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        t("Diseña tu Éxito", "Design Your Success"), 
                        color = AppColors.TEXT, 
                        style = MaterialTheme.typography.headlineMedium, 
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = mode == "create", 
                            onClick = { mode = "create" }, 
                            label = { Text(t("CREAR", "CREATE"), fontWeight = FontWeight.Black) }, 
                            colors = appChipColors()
                        )
                        FilterChip(
                            selected = mode == "explore", 
                            onClick = { mode = "explore" }, 
                            label = { Text(t("EXPLORAR", "EXPLORE"), fontWeight = FontWeight.Black) }, 
                            colors = appChipColors()
                        )
                    }
                }
            }
        }

        if (mode == "create") {
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
                        Text(t("NUEVA RUTINA", "NEW ROUTINE"), color = AppColors.TEXT, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                        OutlinedTextField(
                            value = vm.customRoutineName, 
                            onValueChange = { vm.customRoutineName = it }, 
                            label = { Text(t("Nombre de rutina (ej. Push A)", "Routine Name")) }, 
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.ACCENT)
                        )
                        
                        HorizontalDivider(color = AppColors.BORDER)
                        
                        Button(
                            onClick = { vm.saveCustomRoutine() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = primaryButtonColors()
                        ) { 
                            Text(t("CREAR RUTINA", "CREATE ROUTINE"), fontWeight = FontWeight.Black) 
                        }
                        
                        vm.customMessage?.let { 
                            Text(it, color = AppColors.SUCCESS, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall) 
                        }
                    }
                }
            }
            
            if (vm.customExercises.isNotEmpty()) {
                item { 
                    Text(t("TU SELECCIÓN", "YOUR SELECTION"), color = AppColors.TEXT, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelSmall) 
                }
                itemsIndexed(vm.customExercises, key = { _, ex -> ex.name }) { index, ex ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AppColors.CARD_SOFT), 
                        modifier = Modifier.fillMaxWidth().border(1.dp, AppColors.BORDER, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp), 
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(vm.displayExerciseName(ex.name), color = AppColors.TEXT, fontWeight = FontWeight.Black)
                                Text(ex.muscle.uppercase(), color = AppColors.ACCENT, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            OutlinedTextField(
                                value = ex.targetSets.toString(),
                                onValueChange = { vm.updateCustomExercise(index, sets = it) },
                                label = { Text("SETS") },
                                modifier = Modifier.width(64.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = ex.targetReps,
                                onValueChange = { vm.updateCustomExercise(index, reps = it) },
                                label = { Text("REPS") },
                                modifier = Modifier.width(74.dp),
                                singleLine = true
                            )
                            IconButton(onClick = { vm.removeExerciseFromCustom(index) }) {
                                Text("✕", color = Color.Red.copy(alpha = 0.6f), fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { showCatalog = !showCatalog },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = accentVariantButtonColors(),
                    border = BorderStroke(1.dp, AppColors.BORDER)
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(20.dp), tint = AppColors.ACCENT)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        if (showCatalog) t("OCULTAR CATÁLOGO", "HIDE CATALOG") else t("AGREGAR EJERCICIOS", "ADD EXERCISES"),
                        fontWeight = FontWeight.Black
                    )
                }
            }

            if (showCatalog) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(t("BUSCAR", "SEARCH"), color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = vm.customSearch, 
                            onValueChange = { vm.customSearch = it }, 
                            placeholder = { Text(t("Ej. Bench Press...", "Search...")) }, 
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.ACCENT)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(muscles) { muscle ->
                                FilterChip(
                                    selected = vm.customMuscleFilter == muscle, 
                                    onClick = { vm.customMuscleFilter = muscle }, 
                                    label = { Text(muscle) }, 
                                    colors = appChipColors()
                                )
                            }
                        }
                        Text(t("EQUIPO", "EQUIPMENT"), color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(equipments) { equipment ->
                                FilterChip(
                                    selected = vm.customEquipmentFilter == equipment,
                                    onClick = { vm.customEquipmentFilter = equipment },
                                    label = { Text(equipment) },
                                    colors = appChipColors()
                                )
                            }
                        }
                    }
                }

                items(filtered, key = { "${it.muscle}-${it.name}" }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AppColors.CARD), 
                        modifier = Modifier.fillMaxWidth().border(1.dp, AppColors.BORDER, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { vm.selectedExerciseForTutorial = item },
                                modifier = Modifier.size(40.dp).background(AppColors.BG_SOFT, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = AppColors.MUTED, modifier = Modifier.size(20.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name.uppercase(), color = AppColors.TEXT, fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${item.muscle.uppercase()} • ${item.equipment.uppercase()}",
                                    color = AppColors.MUTED,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            IconButton(
                                onClick = { vm.addExerciseToCustom(item) },
                                modifier = Modifier.size(32.dp).background(AppColors.ACCENT, CircleShape)
                            ) { 
                                Text("+", color = Color.White, fontWeight = FontWeight.Black) 
                            }
                        }
                    }
                }
            }
        } else {
            item { 
                Text(
                    t("TEMPLADOS PRO", "PRO TEMPLATES"), 
                    color = AppColors.TEXT, 
                    style = MaterialTheme.typography.labelLarge, 
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ) 
            }
            items(vm.exploreRoutines, key = { it.id }) { routine ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = AppColors.CARD), 
                    modifier = Modifier.fillMaxWidth().border(1.dp, AppColors.BORDER, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp), 
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(routine.name.uppercase(), color = AppColors.TEXT, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                        Text(t("${routine.exercises.size} EJERCICIOS", "${routine.exercises.size} EXERCISES"), color = AppColors.ACCENT, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        
                        HorizontalDivider(color = AppColors.BORDER, modifier = Modifier.padding(vertical = 4.dp))
                        
                        routine.exercises.take(4).forEach { ex ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(4.dp).background(AppColors.ACCENT, CircleShape))
                                Spacer(Modifier.width(8.dp))
                                Text(vm.displayExerciseName(ex.name), color = AppColors.MUTED, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp)) {
                            Button(
                                onClick = { 
                                    vm.addExploreRoutineToMine(routine)
                                    vm.startWorkout(routine) 
                                },
                                modifier = Modifier.weight(1f),
                                colors = primaryButtonColors(),
                                shape = RoundedCornerShape(10.dp)
                            ) { 
                                Text(t("EMPEZAR", "START"), fontWeight = FontWeight.Black) 
                            }
                            Button(
                                onClick = { vm.addExploreRoutineToMine(routine) },
                                modifier = Modifier.weight(1f),
                                colors = accentVariantButtonColors(),
                                shape = RoundedCornerShape(10.dp)
                            ) { 
                                Text(t("GUARDAR", "SAVE"), fontWeight = FontWeight.Bold) 
                            }
                        }
                    }
                }
            }
        }
    }

    if (vm.selectedExerciseForTutorial != null) {
        ExerciseTutorialDialog(vm.selectedExerciseForTutorial!!, t) {
            vm.selectedExerciseForTutorial = null
        }
    }
}
