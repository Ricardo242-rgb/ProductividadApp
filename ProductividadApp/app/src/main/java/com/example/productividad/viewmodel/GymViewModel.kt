package com.example.productividad.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.productividad.data.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

class GymViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("gym_prefs", Context.MODE_PRIVATE)

    // Removal of old hardcoded names as we now have a massive catalog

    var language by mutableStateOf(AppLanguage.ES)
    var isDarkTheme by mutableStateOf(true)
    var selectedTab by mutableStateOf(GymTab.HOME)

    val exploreRoutines = templateRoutines()
    val myRoutines = mutableStateListOf<WorkoutRoutine>()
    val history = mutableStateListOf<CompletedWorkout>()
    val workoutNotes = mutableStateListOf<WorkoutNote>()
    val exerciseCatalog = exerciseCatalog(application.applicationContext)
    private data class IndexedExercise(
        val item: ExerciseCatalogItem,
        val normalizedName: String,
        val searchableText: String,
        val words: Set<String>
    )
    private val indexedExerciseCatalog = exerciseCatalog.map { item ->
        val normalizedName = normalize(item.name)
        val searchableText = normalize(
            listOf(
                item.name,
                item.muscle,
                item.equipment,
                item.rawName,
                item.aliases.joinToString(" "),
                item.tutorial.description,
                item.tutorial.steps.joinToString(" ")
            ).joinToString(" ")
        )
        IndexedExercise(
            item = item,
            normalizedName = normalizedName,
            searchableText = searchableText,
            words = searchableText.split("\\s+".toRegex()).filter { it.isNotBlank() }.toSet()
        )
    }
    private val catalogByNormalizedName = linkedMapOf<String, ExerciseCatalogItem>().apply {
        indexedExerciseCatalog.forEach { indexed ->
            listOf(indexed.item.name, indexed.item.rawName)
                .plus(indexed.item.aliases)
                .map(::normalize)
                .filter { it.isNotBlank() }
                .forEach { putIfAbsent(it, indexed.item) }
        }
    }
    private val translatedNameCache = mutableMapOf<String, String>()
    private val muscleFiltersCache = listOf("Todos") + exerciseCatalog.map { it.muscle }.distinct().sorted()
    private val equipmentFiltersCache = listOf("Todos") + exerciseCatalog.map { it.equipment }.distinct().sorted()

    var preferredName by mutableStateOf("")
    var hasCompletedOnboarding by mutableStateOf(false)
    var bodyWeight by mutableStateOf("")
    var weightUnit by mutableStateOf("kg")
    var userAge by mutableStateOf("")
    var userHeight by mutableStateOf("")

    var activeRoutineId by mutableStateOf<String?>(null)
    var activeRoutineName by mutableStateOf<String?>(null)
    val activeExercises = mutableStateListOf<ActiveExercise>()
    
    var selectedExerciseForTutorial by mutableStateOf<ExerciseCatalogItem?>(null)

    var workoutElapsedSeconds by mutableStateOf(0)
    var workoutTimerRunning by mutableStateOf(false)
    var restTimerSecondsLeft by mutableStateOf(90)
    var restTimerRunning by mutableStateOf(false)
    var cardioElapsedSeconds by mutableStateOf(0)
    var cardioTimerRunning by mutableStateOf(false)
    var cardioGoalMinutes by mutableStateOf(20)

    var customRoutineName by mutableStateOf("")
    var customSearch by mutableStateOf("")
    var customMuscleFilter by mutableStateOf("Todos")
    var customEquipmentFilter by mutableStateOf("Todos")
    val customExercises = mutableStateListOf<RoutineExercise>()
    var customMessage by mutableStateOf<String?>(null)

    init {
        loadState()
        AppColors.applyPalette(isDarkTheme)
    }

    fun tr(es: String, en: String): String = if (language == AppLanguage.ES) es else en

    fun displayName(): String = preferredName.trim().ifBlank { tr("Atleta", "Athlete") }

    fun updatePersonalProfile(
        name: String,
        weight: String,
        unit: String,
        age: String,
        height: String,
        completeOnboarding: Boolean = hasCompletedOnboarding
    ) {
        preferredName = name.trim()
        bodyWeight = weight.trim()
        weightUnit = unit
        userAge = age.trim()
        userHeight = height.trim()
        hasCompletedOnboarding = completeOnboarding
        saveState()
    }

    fun completeOnboarding(
        name: String,
        weight: String,
        unit: String,
        age: String,
        height: String
    ) {
        updatePersonalProfile(
            name = name,
            weight = weight,
            unit = unit,
            age = age,
            height = height,
            completeOnboarding = true
        )
    }

    private fun normalize(s: String): String {
        val translated = s.lowercase(Locale.getDefault())
            .replace("pull-over", "pullover")
            .replace("pull over", "pullover")
            .replace("push-up", "push up")
            .replace("pull-up", "pull up")
            .replace("chin-up", "chin up")
            .replace("sit-up", "sit up")
            .replace("peso muerto", "deadlift")
            .replace("mancuernas", "dumbbell")
            .replace("mancuerna", "dumbbell")
            .replace("barra", "barbell")
            .replace("polea", "cable")
            .replace("cuerda", "rope")
            .replace("banca", "bench")
            .replace("press superior", "incline press")
            .replace("superior", "incline")
            .replace("inclinado", "incline")
            .replace("declinado", "decline")
            .replace("jalón", "pulldown")
            .replace("jalon", "pulldown")
            .replace("remo", "row")
            .replace("sentadilla", "squat")
            .replace("zancadas", "lunge")
            .replace("zancada", "lunge")
            .replace("prensa", "press")
            .replace("piernas", "leg")
            .replace("pierna", "leg")
            .replace("femoral", "hamstrings")
            .replace("isquios", "hamstrings")
            .replace("isquiotibiales", "hamstrings")
            .replace("glúteo", "glutes")
            .replace("gluteo", "glutes")
            .replace("pantorrilla", "calves")
            .replace("pantorrillas", "calves")
            .replace("abdomen", "abs")
            .replace("abdominal", "abs")
            .replace("abdominales", "abs")
            .replace("plancha", "plank")
            .replace("elevaciones", "raise")
            .replace("elevación", "raise")
            .replace("elevacion", "raise")
            .replace("laterales", "lateral")
            .replace("aperturas", "fly")
            .replace("fondos", "dip")
            .replace("dominadas", "pull up")
            .replace("dominada", "pull up")
            .replace("talones", "calf raise")
        val n = java.text.Normalizer.normalize(translated, java.text.Normalizer.Form.NFD)
        return n.replace(Regex("\\p{Mn}+"), "") // Remove all accents/marks
            .lowercase(Locale.getDefault())
            .replace("ñ", "n")
            .replace("[^a-z0-9\\s]".toRegex(), "") // Fixed typo 0-0 to 0-9
            .trim()
    }

    fun findExerciseInCatalog(name: String): ExerciseCatalogItem? {
        val target = normalize(name).ifBlank { return null }
        val targetWords = target.split("\\s+".toRegex()).toSet()
        
        // 1. Exact or contains match (fast path)
        catalogByNormalizedName[target]?.let { return it }
        indexedExerciseCatalog.firstOrNull { it.normalizedName.contains(target) || target.contains(it.normalizedName) }?.item?.let { return it }

        // 2. Word-set intersection (fuzzy path)
        return indexedExerciseCatalog.map { indexed ->
            val intersect = targetWords.intersect(indexed.words).size
            val ratio = intersect.toDouble() / minOf(targetWords.size, indexed.words.size.coerceAtLeast(1))
            indexed.item to ratio
        }.filter { it.second >= 0.4 } // Lowered to 40% for ultra-fuzzy matching
         .sortedWith(compareByDescending<Pair<ExerciseCatalogItem, Double>> { it.second }
             .thenBy { it.first.name.length }) // Shorter name as tie-breaker
         .firstOrNull()?.first
    }

    fun displayExerciseName(name: String): String =
        translatedNameCache.getOrPut(name) {
            val normalized = normalize(name)
            catalogByNormalizedName[normalized]?.name
                ?: indexedExerciseCatalog.firstOrNull {
                    it.normalizedName.contains(normalized) || normalized.contains(it.normalizedName)
                }?.item?.name
                ?: translateExerciseNameToSpanish(name)
        }

    fun exerciseImageUrl(name: String): String = "https://source.unsplash.com/700x380/?${Uri.encode("$name gym exercise")}" 

    fun exerciseGifSearchUrl(name: String): String = "https://www.google.com/search?tbm=isch&q=${Uri.encode("$name exercise gif")}" 

    fun exerciseVideoUrl(name: String): String = "https://www.youtube.com/results?search_query=${Uri.encode("$name exercise tutorial")}" 

    fun changeLanguage(value: AppLanguage) {
        language = value
        saveState()
    }

    fun changeTheme(isDark: Boolean) {
        isDarkTheme = isDark
        AppColors.applyPalette(isDark)
        saveState()
    }

    fun toggleCardioTimer() {
        cardioTimerRunning = !cardioTimerRunning
        saveState()
    }

    fun pauseCardioTimer() {
        if (!cardioTimerRunning) return
        cardioTimerRunning = false
        saveState()
    }

    fun tickCardioTimer() {
        if (cardioTimerRunning) cardioElapsedSeconds += 1
    }

    fun resetCardioTimer() {
        cardioTimerRunning = false
        cardioElapsedSeconds = 0
        saveState()
    }

    fun updateCardioGoalMinutes(minutes: Int) {
        cardioGoalMinutes = minutes.coerceIn(5, 120)
        saveState()
    }

    fun cardioGoalSeconds(): Int = cardioGoalMinutes * 60

    fun cardioGoalProgress(): Float =
        (cardioElapsedSeconds.toFloat() / cardioGoalSeconds().coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    fun formattedDuration(totalSeconds: Int): String {
        val safeSeconds = totalSeconds.coerceAtLeast(0)
        val hours = safeSeconds / 3600
        val minutes = (safeSeconds % 3600) / 60
        val seconds = safeSeconds % 60
        return if (hours > 0) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }

    fun availableMuscleFilters(): List<String> = muscleFiltersCache

    fun availableEquipmentFilters(): List<String> = equipmentFiltersCache

    fun filteredCatalog(): List<ExerciseCatalogItem> {
        val q = normalize(customSearch)
        val ignoredSearchWords = setOf("de", "del", "con", "en", "al", "la", "el", "y")
        val queryWords = q.split("\\s+".toRegex()).filter { it.length > 1 && it !in ignoredSearchWords }
        return indexedExerciseCatalog.asSequence()
            .filter { indexed ->
                val byMuscle = customMuscleFilter == "Todos" || indexed.item.muscle == customMuscleFilter
                val byEquipment = customEquipmentFilter == "Todos" || indexed.item.equipment == customEquipmentFilter
                val byQuery = q.isEmpty() ||
                    indexed.searchableText.contains(q) ||
                    queryWords.all { indexed.searchableText.contains(it) }
                byMuscle && byEquipment && byQuery
            }
            .map { it.item }
            .toList()
    }

    fun addExerciseToCustom(item: ExerciseCatalogItem) {
        if (customExercises.any { it.name == item.name }) return
        customExercises.add(RoutineExercise(item.name, item.muscle, 3, "8-12"))
    }

    fun removeExerciseFromCustom(index: Int) {
        if (index in customExercises.indices) customExercises.removeAt(index)
    }

    fun updateCustomExercise(index: Int, sets: String? = null, reps: String? = null) {
        val ex = customExercises.getOrNull(index) ?: return
        customExercises[index] = ex.copy(
            targetSets = sets?.toIntOrNull()?.coerceIn(1, 12) ?: ex.targetSets,
            targetReps = reps ?: ex.targetReps
        )
    }

    fun saveCustomRoutine() {
        customMessage = null
        val name = customRoutineName.trim()
        if (name.isEmpty()) {
            customMessage = tr("Ponle nombre a tu rutina", "Add a routine name")
            return
        }
        if (customExercises.isEmpty()) {
            customMessage = tr("Agrega al menos 1 ejercicio", "Add at least 1 exercise")
            return
        }

        myRoutines.add(0, WorkoutRoutine("custom-${System.currentTimeMillis()}", name, customExercises.toList()))
        customRoutineName = ""
        customSearch = ""
        customMuscleFilter = "Todos"
        customEquipmentFilter = "Todos"
        customExercises.clear()
        customMessage = tr("Rutina creada", "Routine created")
        selectedTab = GymTab.TRAIN
        saveState()
    }

    fun addExploreRoutineToMine(routine: WorkoutRoutine) {
        if (myRoutines.any { it.name == routine.name }) return
        myRoutines.add(0, WorkoutRoutine("mine-${routine.id}", routine.name, routine.exercises))
        saveState()
    }

    fun removeMyRoutine(routineId: String) {
        val name = myRoutines.firstOrNull { it.id == routineId }?.name
        val removed = myRoutines.removeAll { it.id == routineId }
        if (!removed) return
        if (activeRoutineName == name) stopWorkout()
        saveState()
    }

    fun startWorkout(routine: WorkoutRoutine) {
        val routineToStart = if (myRoutines.none { it.name == routine.name }) {
            val newRoutine = routine.copy(id = "mine-${System.currentTimeMillis()}")
            myRoutines.add(0, newRoutine)
            newRoutine
        } else {
            routine
        }

        val newExercises = routineToStart.exercises.map { ex ->
            val catalogItem = findExerciseInCatalog(ex.name)
            ActiveExercise(
                name = catalogItem?.name ?: displayExerciseName(ex.name),
                muscle = catalogItem?.muscle ?: ex.muscle,
                sets = List(ex.targetSets) { WorkoutSet("", ex.targetReps, false) },
                completed = false,
                template = ex
            )
        }

        // 1. Clear everything first
        activeRoutineName = null
        activeRoutineId = null
        activeExercises.clear()
        
        // 2. Populate data
        activeExercises.addAll(newExercises)
        activeRoutineId = routineToStart.id
        
        // 3. Reset timers (Rest timer remains 90 but stopped)
        workoutElapsedSeconds = 0
        workoutTimerRunning = true
        restTimerSecondsLeft = 90
        restTimerRunning = false
        
        // 4. Finally activate UI
        activeRoutineName = routineToStart.name
        selectedTab = GymTab.TRAIN
        saveState()
    }

    fun pauseWorkoutTimer() { workoutTimerRunning = false }
    fun resumeWorkoutTimer() { if (activeRoutineName != null) workoutTimerRunning = true }
    fun tickWorkoutTimer() { if (workoutTimerRunning) workoutElapsedSeconds += 1 }

    fun startRestTimer(seconds: Int) {
        restTimerSecondsLeft = seconds.coerceIn(10, 600)
        restTimerRunning = true
    }

    fun pauseRestTimer() { restTimerRunning = false }
    fun resetRestTimer(seconds: Int = 90) {
        restTimerRunning = false
        restTimerSecondsLeft = seconds.coerceIn(10, 600)
    }

    fun tickRestTimer() {
        if (!restTimerRunning) return
        if (restTimerSecondsLeft > 0) restTimerSecondsLeft -= 1 else restTimerRunning = false
    }

    fun stopWorkout() {
        activeRoutineId = null
        activeRoutineName = null
        activeExercises.clear()
        workoutElapsedSeconds = 0
        workoutTimerRunning = false
        restTimerSecondsLeft = 90
        restTimerRunning = false
        saveState()
    }

    fun finishWorkout() {
        val routineName = activeRoutineName ?: return
        val snapshot = activeExercises.toList()
        val completedSets = snapshot.flatMap { it.sets }.filter { it.done }
        val totalSets = completedSets.size
        val volume = completedSets.sumOf { s ->
            val w = s.weight.toIntOrNull() ?: 0
            val reps = s.reps.substringBefore("-").toIntOrNull() ?: s.reps.toIntOrNull() ?: 0
            w * reps
        }
        val durationMinutes = (workoutElapsedSeconds / 60.0).roundToInt().coerceAtLeast(1)
        val sessionTimestamp = System.currentTimeMillis()
        val sessionId = sessionTimestamp.toString()
        workoutNotes.add(
            0,
            WorkoutNote(
                id = sessionId,
                sessionId = sessionId,
                routineName = routineName,
                timestamp = sessionTimestamp,
                durationMinutes = durationMinutes,
                totalSets = totalSets,
                totalVolume = volume,
                exercises = snapshot.map { exercise ->
                    val completedExerciseSets = exercise.sets.mapIndexedNotNull { index, set ->
                        if (!set.done) return@mapIndexedNotNull null
                        WorkoutNoteSet(
                            setNumber = index + 1,
                            weight = set.weight.ifBlank { "0" },
                            reps = set.reps.ifBlank { "-" }
                        )
                    }
                    WorkoutExerciseNote(
                        name = exercise.name,
                        muscle = exercise.muscle,
                        completedSets = completedExerciseSets.size,
                        plannedSets = exercise.sets.size,
                        sets = completedExerciseSets
                    )
                }
            )
        )
        history.add(
            0,
            CompletedWorkout(
                id = sessionId,
                sessionId = sessionId,
                routineName = routineName,
                timestamp = sessionTimestamp,
                totalSets = totalSets,
                totalVolume = volume,
                durationMinutes = durationMinutes
            )
        )
        stopWorkout()
        selectedTab = GymTab.NOTES
        saveState()
    }

    fun addSet(exIndex: Int) {
        val ex = activeExercises.getOrNull(exIndex) ?: return
        val defaultReps = ex.template?.targetReps ?: ex.sets.lastOrNull()?.reps ?: "8-12"
        val newSets = ex.sets + WorkoutSet("", defaultReps, false)
        activeExercises[exIndex] = ex.copy(
            sets = newSets,
            completed = newSets.isNotEmpty() && newSets.all { it.done }
        )
    }

    fun updateSet(exIndex: Int, setIndex: Int, weight: String? = null, reps: String? = null) {
        val ex = activeExercises.getOrNull(exIndex) ?: return
        val set = ex.sets.getOrNull(setIndex) ?: return
        val newSets = ex.sets.toMutableList()
        newSets[setIndex] = set.copy(weight = weight ?: set.weight, reps = reps ?: set.reps)
        activeExercises[exIndex] = ex.copy(
            sets = newSets,
            completed = newSets.isNotEmpty() && newSets.all { it.done }
        )
    }

    fun toggleSetDone(exIndex: Int, setIndex: Int) {
        val ex = activeExercises.getOrNull(exIndex) ?: return
        val set = ex.sets.getOrNull(setIndex) ?: return
        val newSets = ex.sets.toMutableList()
        newSets[setIndex] = set.copy(done = !set.done)
        activeExercises[exIndex] = ex.copy(
            sets = newSets,
            completed = newSets.isNotEmpty() && newSets.all { it.done }
        )
    }

    fun toggleExerciseDone(exIndex: Int) {
        val ex = activeExercises.getOrNull(exIndex) ?: return
        val markDone = !ex.completed
        activeExercises[exIndex] = ex.copy(
            completed = markDone,
            sets = ex.sets.map { it.copy(done = markDone) }
        )
    }

    fun completedExercisesCount(): Int = activeExercises.count { it.completed }
    fun remainingExercisesCount(): Int = activeExercises.count { !it.completed }

    fun totalWorkouts(): Int = history.size
    fun notesWithAnnotationCount(): Int = workoutNotes.count { it.annotation.isNotBlank() }
    fun weeklyWorkouts(): Int {
        val weekAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        return history.count { it.timestamp >= weekAgo }
    }

    data class StreakDay(val date: String, val workedOut: Boolean, val shieldUsed: Boolean)
    data class StreakInfo(val days: Int, val shields: Int, val history: List<StreakDay>)

    fun streakInfo(): StreakInfo {
        if (history.isEmpty()) return StreakInfo(0, 0, emptyList())
        
        val days = history.map { dayKey(it.timestamp) }.distinct().sorted()
        if (days.isEmpty()) return StreakInfo(0, 0, emptyList())

        var currentStreak = 0
        var shields = 0
        var consecutiveWorkoutDays = 0
        val historyList = mutableListOf<StreakDay>()
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val firstDayStr = days.first()
        val todayStr = dayKey(System.currentTimeMillis())
        
        val firstDayTs = sdf.parse(firstDayStr)?.time ?: return StreakInfo(0, 0, emptyList())
        val todayTs = sdf.parse(todayStr)?.time ?: return StreakInfo(0, 0, emptyList())
        
        var iterTs = firstDayTs
        val oneDayMs = 24L * 60 * 60 * 1000
        
        val daySet = days.toSet()

        while (iterTs <= todayTs) {
            val key = dayKey(iterTs)
            if (daySet.contains(key)) {
                currentStreak++
                consecutiveWorkoutDays++
                if (consecutiveWorkoutDays == 2) {
                    shields++
                    consecutiveWorkoutDays = 0
                }
                historyList.add(StreakDay(key, workedOut = true, shieldUsed = false))
            } else {
                if (shields > 0) {
                    shields--
                    currentStreak++
                    consecutiveWorkoutDays = 0
                    historyList.add(StreakDay(key, workedOut = false, shieldUsed = true))
                } else {
                    currentStreak = 0
                    consecutiveWorkoutDays = 0
                    historyList.add(StreakDay(key, workedOut = false, shieldUsed = false))
                }
            }
            iterTs += oneDayMs
        }
        
        return StreakInfo(currentStreak, shields, historyList)
    }

    fun estimatedStreakDays(): Int = streakInfo().days

    fun totalVolumeAll(): Int = history.sumOf { it.totalVolume }
    fun totalSetsAll(): Int = history.sumOf { it.totalSets }
    fun totalSetsToday(): Int {
        val today = dayKey(System.currentTimeMillis())
        return history.filter { dayKey(it.timestamp) == today }.sumOf { it.totalSets }
    }

    // AI Chat logic
    var chatMessages = mutableStateListOf<ChatMessage>()
    var aiIsLoading by mutableStateOf(false)
    var n8nWebhookUrl by mutableStateOf("")

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        chatMessages.add(ChatMessage(text = text, isFromUser = true))
        aiIsLoading = true
        
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://localhost/") // Base URL is ignored when @Url is used, but required.
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                
                val api = retrofit.create(N8NApi::class.java)
                val payload = mapOf(
                    "message" to text,
                    "userContext" to mapOf(
                        "name" to preferredName,
                        "weight" to bodyWeight,
                        "unit" to weightUnit,
                        "age" to userAge,
                        "height" to userHeight,
                        "goal" to "general_fitness",
                        "language" to language.name
                    )
                )
                
                val response = api.sendToWebhook(n8nWebhookUrl, payload)
                val aiResponse = response["output"]?.toString() ?: "Lo siento, hubo un error procesando tu solicitud."
                chatMessages.add(ChatMessage(text = aiResponse, isFromUser = false))
            } catch (e: Exception) {
                chatMessages.add(ChatMessage(text = "Error: No se pudo conectar con el asistente. Verifica la URL de n8n.", isFromUser = false))
            } finally {
                aiIsLoading = false
            }
        }
    }

    fun saveProfile(weight: String, unit: String) {
        updatePersonalProfile(
            name = preferredName,
            weight = weight,
            unit = unit,
            age = userAge,
            height = userHeight
        )
    }

    fun updateWorkoutNoteAnnotation(noteId: String, annotation: String) {
        val index = workoutNotes.indexOfFirst { it.id == noteId }
        if (index == -1) return
        workoutNotes[index] = workoutNotes[index].copy(annotation = annotation)
        saveState()
    }

    fun deleteWorkoutRecord(noteId: String) {
        val noteIndex = workoutNotes.indexOfFirst { it.id == noteId }
        if (noteIndex == -1) return
        val note = workoutNotes.removeAt(noteIndex)
        val removedBySession = history.removeAll { it.sessionId == note.sessionId }
        if (!removedBySession) {
            history.removeAll { record ->
                record.routineName == note.routineName &&
                    record.totalSets == note.totalSets &&
                    record.totalVolume == note.totalVolume &&
                    abs(record.timestamp - note.timestamp) <= 5 * 60 * 1000
            }
        }
        saveState()
    }

    fun clearWorkoutRecords() {
        workoutNotes.clear()
        history.clear()
        saveState()
    }

    private fun loadState() {
        language = when (prefs.getString("language", AppLanguage.ES.name)) {
            AppLanguage.EN.name -> AppLanguage.EN
            else -> AppLanguage.ES
        }
        isDarkTheme = prefs.getBoolean("dark_theme", true)
        preferredName = prefs.getString("preferred_name", "") ?: ""
        hasCompletedOnboarding = prefs.getBoolean("has_completed_onboarding", false)
        bodyWeight = prefs.getString("body_weight", "") ?: ""
        weightUnit = prefs.getString("weight_unit", "kg") ?: "kg"
        userAge = prefs.getString("user_age", "") ?: ""
        userHeight = prefs.getString("user_height", "") ?: ""
        cardioElapsedSeconds = prefs.getInt("cardio_elapsed_seconds", 0).coerceAtLeast(0)
        cardioGoalMinutes = prefs.getInt("cardio_goal_minutes", 20).coerceIn(5, 120)
        cardioTimerRunning = false

        val historyJson = prefs.getString("history", "[]") ?: "[]"
        runCatching {
            val arr = JSONArray(historyJson)
            repeat(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                val fallbackId = o.opt("id")?.toString() ?: System.currentTimeMillis().toString()
                history.add(
                    CompletedWorkout(
                        id = fallbackId,
                        sessionId = o.optString("sessionId", fallbackId),
                        routineName = o.getString("routineName"),
                        timestamp = o.getLong("timestamp"),
                        totalSets = o.getInt("totalSets"),
                        totalVolume = o.getInt("totalVolume"),
                        durationMinutes = o.getInt("durationMinutes")
                    )
                )
            }
        }

        val routinesJson = prefs.getString("routines", "[]") ?: "[]"
        runCatching {
            val arr = JSONArray(routinesJson)
            repeat(arr.length()) { i ->
                val r = arr.getJSONObject(i)
                val id = r.getString("id")
                if (!(id.startsWith("custom-") || id.startsWith("mine-"))) return@repeat
                val exArr = r.getJSONArray("exercises")
                val exs = mutableListOf<RoutineExercise>()
                repeat(exArr.length()) { j ->
                    val e = exArr.getJSONObject(j)
                    exs.add(
                        RoutineExercise(
                            displayExerciseName(e.getString("name")),
                            e.getString("muscle"),
                            e.getInt("targetSets"),
                            e.getString("targetReps")
                        )
                    )
                }
                myRoutines.add(WorkoutRoutine(id, r.getString("name"), exs))
            }
        }

        val notesJson = prefs.getString("workout_notes", "[]") ?: "[]"
        runCatching {
            val arr = JSONArray(notesJson)
            repeat(arr.length()) { i ->
                val noteObj = arr.getJSONObject(i)
                val fallbackId = noteObj.optString("id", noteObj.optLong("timestamp").toString())
                val exercisesArr = noteObj.getJSONArray("exercises")
                val exercises = mutableListOf<WorkoutExerciseNote>()
                repeat(exercisesArr.length()) { j ->
                    val exerciseObj = exercisesArr.getJSONObject(j)
                    val setsArr = exerciseObj.getJSONArray("sets")
                    val sets = mutableListOf<WorkoutNoteSet>()
                    repeat(setsArr.length()) { k ->
                        val setObj = setsArr.getJSONObject(k)
                        sets.add(
                            WorkoutNoteSet(
                                setNumber = setObj.getInt("setNumber"),
                                weight = setObj.getString("weight"),
                                reps = setObj.getString("reps")
                            )
                        )
                    }
                    exercises.add(
                        WorkoutExerciseNote(
                            name = displayExerciseName(exerciseObj.getString("name")),
                            muscle = exerciseObj.getString("muscle"),
                            completedSets = exerciseObj.getInt("completedSets"),
                            plannedSets = exerciseObj.getInt("plannedSets"),
                            sets = sets
                        )
                    )
                }
                workoutNotes.add(
                    WorkoutNote(
                        id = fallbackId,
                        sessionId = noteObj.optString("sessionId", fallbackId),
                        routineName = noteObj.getString("routineName"),
                        timestamp = noteObj.getLong("timestamp"),
                        durationMinutes = noteObj.getInt("durationMinutes"),
                        totalSets = noteObj.getInt("totalSets"),
                        totalVolume = noteObj.getInt("totalVolume"),
                        exercises = exercises,
                        annotation = noteObj.optString("annotation", "")
                    )
                )
            }
        }
    }

    fun saveState() {
        val historyArr = JSONArray()
        history.forEach { w ->
            historyArr.put(
                JSONObject()
                    .put("id", w.id)
                    .put("sessionId", w.sessionId)
                    .put("routineName", w.routineName)
                    .put("timestamp", w.timestamp)
                    .put("totalSets", w.totalSets)
                    .put("totalVolume", w.totalVolume)
                    .put("durationMinutes", w.durationMinutes)
            )
        }

        val routinesArr = JSONArray()
        myRoutines.forEach { r ->
            val exArr = JSONArray()
            r.exercises.forEach { e ->
                exArr.put(
                    JSONObject()
                        .put("name", e.name)
                        .put("muscle", e.muscle)
                        .put("targetSets", e.targetSets)
                        .put("targetReps", e.targetReps)
                )
            }
            routinesArr.put(JSONObject().put("id", r.id).put("name", r.name).put("exercises", exArr))
        }

        val notesArr = JSONArray()
        workoutNotes.forEach { note ->
            val exercisesArr = JSONArray()
            note.exercises.forEach { exercise ->
                val setsArr = JSONArray()
                exercise.sets.forEach { set ->
                    setsArr.put(
                        JSONObject()
                            .put("setNumber", set.setNumber)
                            .put("weight", set.weight)
                            .put("reps", set.reps)
                    )
                }
                exercisesArr.put(
                    JSONObject()
                        .put("name", exercise.name)
                        .put("muscle", exercise.muscle)
                        .put("completedSets", exercise.completedSets)
                        .put("plannedSets", exercise.plannedSets)
                        .put("sets", setsArr)
                )
            }
            notesArr.put(
                JSONObject()
                    .put("id", note.id)
                    .put("sessionId", note.sessionId)
                    .put("routineName", note.routineName)
                    .put("timestamp", note.timestamp)
                    .put("durationMinutes", note.durationMinutes)
                    .put("totalSets", note.totalSets)
                    .put("totalVolume", note.totalVolume)
                    .put("annotation", note.annotation)
                    .put("exercises", exercisesArr)
            )
        }

        prefs.edit()
            .putString("language", language.name)
            .putBoolean("dark_theme", isDarkTheme)
            .putString("preferred_name", preferredName)
            .putBoolean("has_completed_onboarding", hasCompletedOnboarding)
            .putString("body_weight", bodyWeight)
            .putString("weight_unit", weightUnit)
            .putString("user_age", userAge)
            .putString("user_height", userHeight)
            .putInt("cardio_elapsed_seconds", cardioElapsedSeconds)
            .putInt("cardio_goal_minutes", cardioGoalMinutes)
            .remove("diet_goal")
            .remove("diet_style")
            .remove("meals_per_day")
            .remove("diet_plan_seed")
            .remove("allergies")
            .putString("history", historyArr.toString())
            .putString("routines", routinesArr.toString())
            .putString("workout_notes", notesArr.toString())
            .apply()
    }

    fun dayKey(ts: Long): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(ts))
}

interface N8NApi {
    @POST
    suspend fun sendToWebhook(@Url url: String, @Body body: Map<String, Any>): Map<String, Any>
}
