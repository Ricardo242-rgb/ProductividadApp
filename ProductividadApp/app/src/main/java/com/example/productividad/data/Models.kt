package com.example.productividad.data

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

enum class GymTab(val es: String, val en: String) { 
    HOME("Inicio", "Home"), 
    ROUTINES("Rutinas", "Routines"), 
    TRAIN("Entrenar", "Train"),
    NOTES("Notas", "Notes"),
    CARDIO("Cardio", "Cardio"),
    PROFILE("Perfil", "Profile") 
}
enum class AppLanguage { ES, EN }

data class WorkoutRoutine(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val exercises: List<RoutineExercise>
)

data class RoutineExercise(
    val name: String,
    val muscle: String,
    val targetSets: Int,
    val targetReps: String
)

data class ActiveExercise(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val muscle: String,
    val sets: List<WorkoutSet>,
    var completed: Boolean = false,
    val template: RoutineExercise? = null
)

data class WorkoutSet(
    var weight: String = "",
    var reps: String = "",
    var done: Boolean = false
)

data class CompletedWorkout(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sessionId: String = java.util.UUID.randomUUID().toString(),
    val routineName: String,
    val timestamp: Long,
    val totalSets: Int,
    val totalVolume: Int,
    val durationMinutes: Int
)

data class WorkoutNote(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sessionId: String = java.util.UUID.randomUUID().toString(),
    val routineName: String,
    val timestamp: Long,
    val durationMinutes: Int,
    val totalSets: Int,
    val totalVolume: Int,
    val exercises: List<WorkoutExerciseNote>,
    val annotation: String = ""
)

data class WorkoutExerciseNote(
    val name: String,
    val muscle: String,
    val completedSets: Int,
    val plannedSets: Int,
    val sets: List<WorkoutNoteSet>
)

data class WorkoutNoteSet(
    val setNumber: Int,
    val weight: String,
    val reps: String
)

data class ExerciseCatalogItem(
    val name: String,
    val muscle: String,
    val equipment: String = "Otro",
    val rawName: String = "",
    val aliases: List<String> = emptyList(),
    val tutorial: ExerciseTutorial
)

data class ExerciseTutorial(
    val description: String,
    val steps: List<String>,
    val gifUrl: String? = null
)

fun bundledGifUri(remoteUrl: String): String =
    "file:///android_asset/exercise_gifs/${remoteUrl.substringAfterLast('/')}"

object AppColors {
    var BG = Color(0xFF0B1118)
    var BG_SOFT = Color(0xFF101923)
    var CARD = Color(0xFF111B26)
    var CARD_SOFT = Color(0xFF182433)
    var ACCENT = Color(0xFF0F766E)
    var ACCENT_VARIANT = Color(0xFF14B8A6)
    var SUCCESS = Color(0xFF16A34A)
    var TEXT = Color(0xFFF8FAFC)
    var TEXT_SECONDARY = Color(0xFFD7E0EA)
    var MUTED = Color(0xFF8EA3B7)
    var BORDER = Color(0xFF253345)

    fun applyPalette(isDark: Boolean) {
        if (isDark) {
            BG = Color(0xFF0B1118)
            BG_SOFT = Color(0xFF101923)
            CARD = Color(0xFF111B26)
            CARD_SOFT = Color(0xFF182433)
            ACCENT = Color(0xFF0F766E)
            ACCENT_VARIANT = Color(0xFF14B8A6)
            SUCCESS = Color(0xFF16A34A)
            TEXT = Color(0xFFF8FAFC)
            TEXT_SECONDARY = Color(0xFFD7E0EA)
            MUTED = Color(0xFF8EA3B7)
            BORDER = Color(0xFF253345)
        } else {
            BG = Color(0xFFF3F7FA)
            BG_SOFT = Color(0xFFE8EEF4)
            CARD = Color(0xFFFFFFFF)
            CARD_SOFT = Color(0xFFF6F9FC)
            ACCENT = Color(0xFF0F766E)
            ACCENT_VARIANT = Color(0xFF0EA5A4)
            SUCCESS = Color(0xFF15803D)
            TEXT = Color(0xFF0F172A)
            TEXT_SECONDARY = Color(0xFF334155)
            MUTED = Color(0xFF64748B)
            BORDER = Color(0xFFD7E1EA)
        }
    }
}

object AppUtils {
    private var _loader: coil.ImageLoader? = null

    fun getLoader(context: android.content.Context): coil.ImageLoader {
        return _loader ?: coil.ImageLoader.Builder(context)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(coil.decode.ImageDecoderDecoder.Factory())
                } else {
                    add(coil.decode.GifDecoder.Factory())
                }
            }
            .okHttpClient {
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                            .header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                            .build()
                        chain.proceed(request)
                    }
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            }
            .diskCache {
                coil.disk.DiskCache.Builder()
                    .directory(context.filesDir.resolve("exercise_gifs_cache"))
                    .maxSizeBytes(1024 * 1024 * 500) // 500MB for offline accessibility
                    .build()
            }
            .crossfade(true)
            .allowHardware(false)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
            .build().also { _loader = it }
    }

    @Composable
    fun gifLoader(): coil.ImageLoader {
        val context = androidx.compose.ui.platform.LocalContext.current.applicationContext
        return remember { getLoader(context) }
    }
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
