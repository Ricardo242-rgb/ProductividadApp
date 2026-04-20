package com.example.productividad.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import com.example.productividad.data.AppColors
import com.example.productividad.ui.components.appChipColors
import com.example.productividad.ui.components.neutralFilledButtonColors
import com.example.productividad.ui.components.primaryButtonColors
import com.example.productividad.viewmodel.GymViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardioScreen(vm: GymViewModel, t: (String, String) -> String) {
    LaunchedEffect(vm.cardioTimerRunning) {
        while (vm.cardioTimerRunning) {
            delay(1000)
            vm.tickCardioTimer()
        }
    }

    val modes = listOf(
        CardioMode(
            id = "walk",
            label = t("Caminata", "Walk"),
            headline = t("Recuperación activa y pasos con intención", "Active recovery with purposeful steps"),
            description = t(
                "Perfecta para soltar piernas, sumar actividad y mantenerte constante sin vaciarte.",
                "Perfect to loosen up, add movement, and stay consistent without draining yourself."
            ),
            focus = t("Ritmo conversacional, hombros relajados y respiración nasal.", "Keep a conversational pace, relaxed shoulders, and nasal breathing."),
            feel = t("Sensación ligera pero sostenida.", "Light but steady effort."),
            quickTargets = listOf(10, 20, 35),
            cues = listOf(
                t("Empieza 2 minutos suave y deja que el cuerpo entre en calor.", "Start with 2 easy minutes and let the body warm up."),
                t("Sube el paso a mitad de sesión para activar sin perder control.", "Pick up the pace halfway through without losing control."),
                t("Cierra con 1 minuto más tranquilo para bajar pulsaciones.", "Finish with 1 calmer minute to bring your heart rate down.")
            )
        ),
        CardioMode(
            id = "bike",
            label = t("Bici", "Bike"),
            headline = t("Cadencia limpia para acumular tiempo de calidad", "Smooth cadence for quality time"),
            description = t(
                "Ideal para sesiones estables con menos impacto y buena continuidad.",
                "Ideal for steady sessions with lower impact and solid continuity."
            ),
            focus = t("Mantén una cadencia pareja y evita apretar de más al inicio.", "Hold a smooth cadence and avoid pushing too hard too early."),
            feel = t("Trabajo medio, continuo y cómodo.", "Moderate, continuous, comfortable work."),
            quickTargets = listOf(15, 25, 40),
            cues = listOf(
                t("Usa los primeros minutos para encontrar una resistencia que puedas sostener.", "Use the first minutes to find a resistance you can sustain."),
                t("Cada 5 minutos revisa postura, respiración y ritmo.", "Every 5 minutes check posture, breathing, and rhythm."),
                t("Si aún te sientes fuerte al final, añade 3 minutos extra de cierre.", "If you still feel strong at the end, add 3 extra closing minutes.")
            )
        ),
        CardioMode(
            id = "run",
            label = t("Trote", "Jog"),
            headline = t("Base aeróbica con ritmo uniforme", "Aerobic base with even pacing"),
            description = t(
                "Para construir resistencia sin convertirlo en una carrera a tope.",
                "Great for building endurance without turning it into an all-out run."
            ),
            focus = t("Pisada ligera, tronco alto y zancada natural.", "Light footstrike, tall torso, and natural stride."),
            feel = t("Constante, con margen para hablar en frases cortas.", "Steady, with enough room to talk in short phrases."),
            quickTargets = listOf(12, 20, 30),
            cues = listOf(
                t("Haz el arranque un poco más suave de lo que te pide la emoción.", "Start a little easier than your motivation wants."),
                t("A mitad de sesión busca el ritmo que puedas sostener sin mirar el reloj.", "Mid-session, find a pace you can sustain without chasing the clock."),
                t("Termina fuerte sólo en el último minuto si todavía te sientes sólido.", "Finish strong only in the last minute if you still feel solid.")
            )
        ),
        CardioMode(
            id = "hiit",
            label = t("Intervalos", "Intervals"),
            headline = t("Bloques cortos para encender la sesión", "Short blocks to light up the session"),
            description = t(
                "Cuando quieres algo más agresivo y dinámico sin dedicarle demasiado tiempo.",
                "For days when you want something sharper and more dynamic without a long session."
            ),
            focus = t("Aprieta en los bloques duros y recupera de verdad en los suaves.", "Push on hard blocks and genuinely recover on the easy ones."),
            feel = t("Intenso, corto y muy enfocado.", "Intense, short, and highly focused."),
            quickTargets = listOf(8, 12, 20),
            cues = listOf(
                t("Dedica 2 minutos al calentamiento antes del primer bloque fuerte.", "Give yourself 2 minutes of warm-up before the first hard block."),
                t("Piensa en ráfagas de calidad, no en aguantar a medias toda la sesión.", "Think quality bursts, not dragging medium effort through the whole session."),
                t("Termina con respiración lenta y un minuto suave para cerrar bien.", "Finish with slow breathing and one easy minute to close well.")
            )
        )
    )
    var selectedModeId by rememberSaveable { mutableStateOf(modes.first().id) }
    val selectedMode = modes.firstOrNull { it.id == selectedModeId } ?: modes.first()
    val elapsed = vm.formattedDuration(vm.cardioElapsedSeconds)
    val goal = vm.formattedDuration(vm.cardioGoalSeconds())
    val progress = vm.cardioGoalProgress()
    val checkpoints = listOf(0.25f, 0.5f, 0.75f, 1f)
    val nextCheckpoint = checkpoints.firstOrNull { progress < it }
    val nextCheckpointSeconds = nextCheckpoint
        ?.let { ((vm.cardioGoalSeconds() * it) - vm.cardioElapsedSeconds).toInt().coerceAtLeast(0) }
        ?: 0

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
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        t("CARDIO MODE", "CARDIO MODE"),
                        color = AppColors.ACCENT,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        t("Cardio con más intención y mejor ritmo", "Cardio with more intent and better rhythm"),
                        color = AppColors.TEXT,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        t(
                            "Elige el tipo de sesión, ajusta tu meta y deja que la pantalla te vaya guiando mientras sumas tiempo.",
                            "Pick the session style, adjust your goal, and let the screen guide you while you rack up minutes."
                        ),
                        color = AppColors.TEXT_SECONDARY,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            color = AppColors.ACCENT,
                            trackColor = AppColors.BORDER,
                            strokeWidth = 10.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                elapsed,
                                color = AppColors.TEXT,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                t("Meta actual $goal", "Current goal $goal"),
                                color = AppColors.MUTED,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { vm.toggleCardioTimer() },
                            modifier = Modifier.weight(1f),
                            colors = primaryButtonColors(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                if (vm.cardioTimerRunning) t("PAUSAR", "PAUSE") else t("INICIAR", "START"),
                                fontWeight = FontWeight.Black
                            )
                        }
                        Button(
                            onClick = { vm.resetCardioTimer() },
                            modifier = Modifier.weight(1f),
                            colors = neutralFilledButtonColors(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(t("REINICIAR", "RESET"), fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        t("MODO DE HOY", "TODAY'S MODE"),
                        color = AppColors.MUTED,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        modes.forEach { mode ->
                            FilterChip(
                                selected = selectedModeId == mode.id,
                                onClick = { selectedModeId = mode.id },
                                label = { Text(mode.label) },
                                colors = appChipColors()
                            )
                        }
                    }

                    Surface(
                        color = AppColors.ACCENT.copy(alpha = if (vm.isDarkTheme) 0.14f else 0.10f),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppColors.ACCENT.copy(alpha = 0.18f), RoundedCornerShape(18.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                selectedMode.headline,
                                color = AppColors.TEXT,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                selectedMode.description,
                                color = AppColors.TEXT_SECONDARY,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                t("Enfoque", "Focus") + ": " + selectedMode.focus,
                                color = AppColors.TEXT,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                t("Sensación", "Feel") + ": " + selectedMode.feel,
                                color = AppColors.TEXT_SECONDARY,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        t("METAS RÁPIDAS", "QUICK GOALS"),
                        color = AppColors.MUTED,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedMode.quickTargets.forEach { minutes ->
                            FilterChip(
                                selected = vm.cardioGoalMinutes == minutes,
                                onClick = { vm.updateCardioGoalMinutes(minutes) },
                                label = { Text("$minutes ${t("min", "min")}") },
                                colors = appChipColors()
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CardioMetric(
                    modifier = Modifier.weight(1f),
                    label = t("Estado", "Status"),
                    value = if (vm.cardioTimerRunning) t("Activo", "Active") else t("Pausado", "Paused")
                )
                CardioMetric(
                    modifier = Modifier.weight(1f),
                    label = t("Progreso", "Progress"),
                    value = "${(progress * 100).toInt()}%"
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        t("RUTA DE LA SESIÓN", "SESSION PATH"),
                        color = AppColors.ACCENT,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        checkpoints.forEach { checkpoint ->
                            val checkpointReached = progress >= checkpoint
                            val checkpointSeconds = ((vm.cardioGoalSeconds() * checkpoint) - vm.cardioElapsedSeconds)
                                .toInt()
                                .coerceAtLeast(0)
                            CardioCheckpoint(
                                modifier = Modifier.weight(1f),
                                label = "${(checkpoint * 100).toInt()}%",
                                state = if (checkpointReached) {
                                    t("Listo", "Done")
                                } else {
                                    vm.formattedDuration(checkpointSeconds)
                                },
                                active = checkpointReached
                            )
                        }
                    }
                    Text(
                        if (nextCheckpoint == null) {
                            t(
                                "Meta cumplida. Si te sientes bien, alarga unos minutos extra para cerrar la sesión con calidad.",
                                "Goal done. If you feel good, extend a few extra minutes to finish the session strong."
                            )
                        } else {
                            t(
                                "Te faltan ${vm.formattedDuration(nextCheckpointSeconds)} para tocar el ${(nextCheckpoint * 100).toInt()}% de tu meta.",
                                "You are ${vm.formattedDuration(nextCheckpointSeconds)} away from hitting ${(nextCheckpoint * 100).toInt()}% of your goal."
                            )
                        },
                        color = AppColors.TEXT_SECONDARY,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        t("GUÍA EXPRESS", "EXPRESS GUIDE"),
                        color = AppColors.ACCENT,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black
                    )
                    selectedMode.cues.forEach { cue ->
                        Text(
                            cue,
                            color = AppColors.TEXT_SECONDARY,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

private data class CardioMode(
    val id: String,
    val label: String,
    val headline: String,
    val description: String,
    val focus: String,
    val feel: String,
    val quickTargets: List<Int>,
    val cues: List<String>
)

@Composable
private fun CardioCheckpoint(
    modifier: Modifier = Modifier,
    label: String,
    state: String,
    active: Boolean
) {
    Surface(
        color = if (active) AppColors.ACCENT.copy(alpha = 0.14f) else AppColors.BG_SOFT,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.border(
            1.dp,
            if (active) AppColors.ACCENT.copy(alpha = 0.32f) else AppColors.BORDER,
            RoundedCornerShape(16.dp)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                label,
                color = if (active) AppColors.ACCENT else AppColors.MUTED,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                state,
                color = AppColors.TEXT,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CardioMetric(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Surface(
        color = AppColors.CARD,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.border(1.dp, AppColors.BORDER, RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                label,
                color = AppColors.MUTED,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                value,
                color = AppColors.TEXT,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}
