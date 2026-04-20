package com.example.productividad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productividad.data.*
import com.example.productividad.ui.components.KPI
import com.example.productividad.ui.components.RifitLogo
import com.example.productividad.ui.components.accentVariantButtonColors
import com.example.productividad.ui.components.primaryButtonColors
import com.example.productividad.viewmodel.GymViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(vm: GymViewModel, t: (String, String) -> String) {
    val displayName = vm.displayName()
    LazyColumn(
        modifier = Modifier.fillMaxSize(), 
        contentPadding = PaddingValues(16.dp), 
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD), 
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RifitLogo(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            t("Hola, $displayName", "Hi, $displayName"),
                            color = AppColors.ACCENT,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            t("Domina tu entrenamiento", "Master your training"), 
                            color = AppColors.TEXT, 
                            style = MaterialTheme.typography.headlineMedium, 
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            t("Optimiza cada serie, registra tu progreso y mantén el ritmo con una app más limpia y enfocada.", "Optimize every set, track your progress, and keep momentum with a cleaner focused app."), 
                            color = AppColors.MUTED,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { vm.selectedTab = GymTab.TRAIN },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = primaryButtonColors()
                            ) { 
                                Text(t("ENTRENAR", "TRAIN"), fontWeight = FontWeight.Bold) 
                            }
                            Button(
                                onClick = { vm.selectedTab = GymTab.ROUTINES },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = accentVariantButtonColors()
                            ) { 
                                Text(t("RUTINAS", "ROUTINES"), fontWeight = FontWeight.Bold) 
                            }
                        }
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KPI(title = t("Mis rutinas", "My routines"), value = vm.myRoutines.size.toString(), modifier = Modifier.weight(1f))
                KPI(title = t("Entrenos semana", "Week workouts"), value = vm.weeklyWorkouts().toString(), modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val sInfo = vm.streakInfo()
                KPI(title = t("Racha", "Streak"), value = t("${sInfo.days} días", "${sInfo.days} days") + if (sInfo.shields > 0) " 🛡️${sInfo.shields}" else "", modifier = Modifier.weight(1f))
                KPI(title = t("Series hoy", "Sets today"), value = vm.totalSetsToday().toString(), modifier = Modifier.weight(1f))
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD), 
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp), 
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(t("Actividad semanal", "Weekly activity"), color = AppColors.TEXT, fontWeight = FontWeight.Bold)
                val sInfo = vm.streakInfo()
                val last7Days = sInfo.history.takeLast(7)
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), 
                    verticalAlignment = Alignment.Bottom, 
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                ) {
                    last7Days.forEach { day ->
                        val color = when {
                            day.workedOut -> AppColors.ACCENT
                            day.shieldUsed -> AppColors.ACCENT_VARIANT
                            else -> AppColors.CARD_SOFT
                        }
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally, 
                            modifier = Modifier.weight(1f)
                        ) {
                            if (day.shieldUsed) {
                                Text("🛡️", fontSize = 12.sp)
                                Spacer(Modifier.height(4.dp))
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(if (day.workedOut || day.shieldUsed) 100.dp else 20.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(color)
                            )
                            Spacer(Modifier.height(8.dp))
                            // Format date to day name (e.g. Lun, Mar)
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            val dateObj = sdf.parse(day.date)
                            val label = if (dateObj != null) {
                                SimpleDateFormat("EE", Locale("es", "ES")).format(dateObj).replaceFirstChar { it.uppercase() }
                            } else day.date.takeLast(2)
                            
                            Text(label, color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                }
            }
        }
    }
}
