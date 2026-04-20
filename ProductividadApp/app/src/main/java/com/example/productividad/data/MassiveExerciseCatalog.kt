package com.example.productividad.data

import android.content.Context
import org.json.JSONArray
import java.text.Normalizer
import java.util.Locale

private var importedExerciseCatalogCache: List<ExerciseCatalogItem>? = null

fun exerciseCatalog(context: Context): List<ExerciseCatalogItem> {
    val cached = importedExerciseCatalogCache
    if (cached != null) return cached

    val loaded = loadExactExerciseCatalog(context)
    importedExerciseCatalogCache = loaded
    return loaded
}

fun translateExerciseNameToSpanish(raw: String): String {
    val sanitized = sanitizeRawExerciseName(raw)
    if (sanitized.isBlank()) return raw
    exactSpanishOverrides[sanitized]?.let { return toSentenceCaseSpanish(it) }

    var remaining = sanitized
    val suffixes = mutableListOf<String>()

    equipmentPrefixSuffixes.forEach { (prefix, suffix) ->
        if (remaining == prefix || remaining.startsWith("$prefix ")) {
            remaining = remaining.removePrefix(prefix).trim()
            suffixes += suffix
        }
    }

    val translated = applyOrderedReplacements(remaining, phraseReplacements)
        .let { applyOrderedReplacements(it, tokenReplacements) }
        .replace(Regex("\\s+"), " ")
        .trim()
        .trim('-', ',', '/', ' ')

    val result = buildString {
        append(translated.ifBlank { raw.lowercase(Locale.getDefault()) })
        if (suffixes.isNotEmpty()) {
            append(' ')
            append(suffixes.joinToString(" "))
        }
    }
        .replace(" cuerda en polea ", " en polea con cuerda ")
        .replace(" barra ez con barra ez ", " con barra EZ ")
        .replace(" en máquina smith en máquina smith ", " en máquina Smith ")
        .replace(Regex("\\s+"), " ")
        .trim()

    return toSentenceCaseSpanish(result)
}

private fun loadExactExerciseCatalog(context: Context): List<ExerciseCatalogItem> =
    runCatching {
        val raw = context.assets.open("exact_relevant_exercises.json").bufferedReader().use { it.readText() }
        val array = JSONArray(raw)
        val availableGifs = context.assets.list("exercise_gifs")?.toSet().orEmpty()

        buildList(array.length()) {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                val gifFile = item.optString("gifUrl").substringAfterLast('/').trim()
                if (gifFile.isBlank() || gifFile !in availableGifs) return@repeat

                val bodyParts = item.optJSONArray("bodyParts").toStringList()
                val targetMuscles = item.optJSONArray("targetMuscles").toStringList()
                val equipments = item.optJSONArray("equipments").toStringList()
                val muscle = resolveMuscle(bodyParts, targetMuscles) ?: return@repeat
                val equipment = resolveEquipmentCategory(equipments)

                val rawName = item.optString("name").trim()
                val translatedName = translateExerciseNameToSpanish(rawName)
                if (translatedName.isBlank()) return@repeat

                add(
                    ExerciseCatalogItem(
                        name = translatedName,
                        muscle = muscle,
                        equipment = equipment,
                        rawName = rawName,
                        aliases = buildSearchAliases(rawName, translatedName, muscle, targetMuscles, equipments),
                        tutorial = ExerciseTutorial(
                            description = buildImportedDescription(muscle, targetMuscles, equipments),
                            steps = buildSpanishTutorialSteps(rawName, muscle),
                            gifUrl = bundledGifUri(gifFile)
                        )
                    )
                )
            }
        }
            .distinctBy { it.tutorial.gifUrl ?: "${it.rawName}|${it.name}|${it.muscle}" }
            .let(::disambiguateCatalogItems)
            .sortedWith(compareBy<ExerciseCatalogItem>({ it.muscle }, { it.name }))
    }.getOrDefault(emptyList())

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return buildList(length()) {
        repeat(length()) { index ->
            val value = optString(index).trim()
            if (value.isNotBlank()) add(value)
        }
    }
}

private fun resolveMuscle(bodyParts: List<String>, targetMuscles: List<String>): String? {
    val body = normalizeCatalogText(bodyParts.firstOrNull().orEmpty())
    val targets = normalizeCatalogText(targetMuscles.joinToString(" "))
    return when (body) {
        "chest" -> "Pecho"
        "back" -> "Espalda"
        "shoulders" -> "Hombro"
        "lower arms" -> "Antebrazo"
        "lower legs" -> "Pantorrilla"
        "waist" -> "Abdomen"
        "upper arms" -> if ("triceps" in targets) "Triceps" else "Biceps"
        "upper legs" -> when {
            "hamstrings" in targets -> "Isquios"
            "glutes" in targets -> "Glúteo"
            else -> "Cuadriceps"
        }
        else -> null
    }
}

private fun buildImportedDescription(
    muscle: String,
    targetMuscles: List<String>,
    equipments: List<String>
): String {
    val target = translateTarget(targetMuscles.firstOrNull().orEmpty()).ifBlank { muscle }
    val equipment = resolveEquipmentCategory(equipments)
    return "Ejercicio de $muscle con foco en $target usando $equipment."
}

private fun buildSearchAliases(
    rawName: String,
    translatedName: String,
    muscle: String,
    targetMuscles: List<String>,
    equipments: List<String>
): List<String> {
    val raw = sanitizeRawExerciseName(rawName)
    val equipment = resolveEquipmentCategory(equipments)
    val target = translateTarget(targetMuscles.firstOrNull().orEmpty())
    val aliases = linkedSetOf<String>()
    aliases += rawName
    aliases += raw
    aliases += translatedName

    val translatedRaw = applyOrderedReplacements(raw, phraseReplacements)
        .let { applyOrderedReplacements(it, tokenReplacements) }
        .replace(Regex("\\s+"), " ")
        .trim()
    if (translatedRaw.isNotBlank()) aliases += toSentenceCaseSpanish(translatedRaw)

    if (equipment != "Otro") {
        aliases += "$translatedName $equipment"
        aliases += "$muscle $equipment"
    }
    if (target.isNotBlank()) aliases += "$translatedName $target"

    when (equipment) {
        "Polea" -> aliases += listOf("$translatedName en polea", "$translatedName polea")
        "Mancuernas" -> aliases += listOf("$translatedName con mancuerna", "$translatedName con mancuernas")
        "Barra" -> aliases += listOf("$translatedName con barra", "$translatedName barra")
        "Peso corporal" -> aliases += listOf("$translatedName peso corporal", "$translatedName sin equipo")
        "Máquina" -> aliases += listOf("$translatedName en máquina", "$translatedName máquina")
        "Banda" -> aliases += listOf("$translatedName con banda", "$translatedName banda")
    }

    if ("rope" in raw) aliases += listOf("$translatedName con cuerda", "$translatedName cuerda")
    if ("pullover" in raw) aliases += listOf("pullover", "pull over", "$translatedName pull over")
    if ("pulldown" in raw) aliases += listOf("jalon", "jalón", "pulldown")
    if ("row" in raw) aliases += listOf("remo", "$translatedName remo")
    if ("pushdown" in raw) aliases += listOf("jalón de tríceps", "pushdown")
    if ("skullcrusher" in raw) aliases += listOf("rompecráneos", "skullcrusher")
    if ("lying triceps extension" in raw || "french press" in raw) aliases += listOf("rompecráneos", "skullcrusher")
    if ("rear delt row" in raw && ("rope" in raw || "cable" in raw)) aliases += listOf("face pull", "jalón a la cara")
    if ("hack squat" in raw) aliases += listOf("hack squat", "sentadilla hack")
    if (raw.contains("seated fly") && muscle == "Pecho" && equipment == "Máquina") aliases += listOf("pec deck", "apertura en pec deck")
    if ("face pull" in raw) aliases += listOf("face pull", "jalón a la cara")
    if ("pec" in raw && "deck" in raw) aliases += listOf("pec deck", "apertura en pec deck")

    return aliases
        .map { it.replace(Regex("\\s+"), " ").trim() }
        .filter { it.isNotBlank() }
        .distinct()
}

private fun disambiguateCatalogItems(items: List<ExerciseCatalogItem>): List<ExerciseCatalogItem> {
    val groups = items.groupBy {
        "${normalizeCatalogText(it.muscle)}|${normalizeCatalogText(it.equipment)}|${normalizeCatalogText(it.name)}"
    }
    return items.map { item ->
        val key = "${normalizeCatalogText(item.muscle)}|${normalizeCatalogText(item.equipment)}|${normalizeCatalogText(item.name)}"
        val duplicates = groups[key].orEmpty()
        if (duplicates.size <= 1) {
            item
        } else {
            val index = duplicates.indexOfFirst { it.rawName == item.rawName }.let { if (it >= 0) it + 1 else 1 }
            val qualifier = buildVariantQualifier(item.rawName).ifBlank { "variante $index" }
            item.copy(
                name = "${item.name} (${toSentenceCaseSpanish(qualifier)})",
                aliases = (item.aliases + item.name).distinct()
            )
        }
    }
}

private fun buildVariantQualifier(rawName: String): String {
    val raw = sanitizeRawExerciseName(rawName)
    val qualifiers = linkedSetOf<String>()
    variantQualifierMappings.forEach { (pattern, label) ->
        if (pattern in raw) qualifiers += label
    }
    return qualifiers.take(3).joinToString(" ")
}

private fun buildSpanishTutorialSteps(rawName: String, muscle: String): List<String> {
    val normalized = normalizeCatalogText(rawName)
    return when {
        listOf("press", "push up", "dip").any { it in normalized } -> listOf(
            "Mantén el pecho abierto y los hombros estables durante todo el recorrido.",
            "Empuja con control sin rebotar en la parte baja del movimiento.",
            "Baja lento y termina cada repetición con buena alineación."
        )
        listOf("row", "pulldown", "pull up", "chin up").any { it in normalized } -> listOf(
            "Inicia el tirón con la espalda y evita balancearte para sacar la repetición.",
            "Lleva los codos hacia atrás o hacia abajo según el ángulo del ejercicio.",
            "Controla el regreso hasta el estiramiento sin perder tensión."
        )
        listOf("curl").any { it in normalized } -> listOf(
            "Mantén el codo estable y evita impulsarte con el torso.",
            "Aprieta fuerte en la parte alta sin cerrar de golpe la muñeca.",
            "Baja lento para aprovechar toda la tensión del recorrido."
        )
        listOf("extension", "pushdown", "kickback").any { it in normalized } -> listOf(
            "Fija el hombro y deja que el codo marque el movimiento principal.",
            "Extiende completo sin perder la postura ni abrir de más los brazos.",
            "Controla la vuelta para no soltar la carga de golpe."
        )
        listOf("squat", "lunge", "leg press", "deadlift", "hip thrust", "bridge").any { it in normalized } -> listOf(
            "Mantén el abdomen firme y la espalda neutra en cada repetición.",
            "Controla rodillas y cadera para que el movimiento salga limpio y estable.",
            "Sube fuerte, baja con control y respeta todo el rango útil."
        )
        listOf("plank", "crunch", "leg raise").any { it in normalized } || muscle == "Abdomen" -> listOf(
            "Aprieta el core desde el inicio y evita arquear la zona lumbar.",
            "Mueve solo lo necesario para que el abdomen haga el trabajo principal.",
            "Respira con control y no aceleres el recorrido."
        )
        listOf("calf raise").any { it in normalized } || muscle == "Pantorrilla" -> listOf(
            "Busca subir hasta la máxima contracción de la pantorrilla.",
            "Haz una pausa corta arriba y baja lento hasta sentir estiramiento.",
            "Evita rebotar para que cada repetición cuente completa."
        )
        else -> listOf(
            "Mantén una postura estable y el abdomen activo durante el ejercicio.",
            "Haz el recorrido con control, sin usar impulso innecesario.",
            "Regresa lento a la posición inicial y cuida la técnica en cada repetición."
        )
    }
}

private fun sanitizeRawExerciseName(raw: String): String {
    var value = raw
        .replace("в°", "°")
        .replace("&", " and ")
        .replace("(with rope attachment)", " rope attachment ", ignoreCase = true)
        .replace("(with rope)", " rope ", ignoreCase = true)
        .replace("(pro lat bar)", " pro lat bar ", ignoreCase = true)
        .replace("(v-bar)", " v bar ", ignoreCase = true)
        .replace("(on stability ball)", " on stability ball ", ignoreCase = true)
        .replace("(on exercise ball)", " on exercise ball ", ignoreCase = true)
        .replace("(on bench)", " on bench ", ignoreCase = true)
        .replace("(on box)", " on box ", ignoreCase = true)
        .replace("(between benches)", " between benches ", ignoreCase = true)
        .replace("(wall)", " against wall ", ignoreCase = true)
        .replace("(kneeling)", " kneeling ", ignoreCase = true)
        .replace("/", " ")
        .replace("-", " ")
        .replace(Regex("\\(male\\)|\\(female\\)|\\bv\\.\\s*\\d+\\b", RegexOption.IGNORE_CASE), " ")
        .lowercase(Locale.getDefault())

    noisePatterns.forEach { pattern ->
        value = value.replace(pattern, " ")
    }
    noiseTokens.forEach { token ->
        value = value.replace(" $token ", " ")
    }
    value = value.replace(Regex("[()]"), " ")

    return value.replace(Regex("\\s+"), " ").trim()
}

private fun applyOrderedReplacements(text: String, replacements: List<Pair<String, String>>): String {
    var result = " $text "
    replacements.forEach { (from, to) ->
        result = result.replace(" $from ", " $to ")
    }
    return result.replace(Regex("\\s+"), " ").trim()
}

private fun translateTarget(raw: String): String =
    when (normalizeCatalogText(raw)) {
        "pectorals" -> "pectoral"
        "upper back" -> "espalda alta"
        "lats" -> "dorsal"
        "delts" -> "deltoides"
        "biceps" -> "bíceps"
        "triceps" -> "tríceps"
        "glutes" -> "glúteo"
        "quads" -> "cuádriceps"
        "hamstrings" -> "isquios"
        "calves" -> "pantorrilla"
        "abs" -> "abdomen"
        "forearms" -> "antebrazo"
        "traps" -> "trapecio"
        "spine" -> "core"
        else -> raw.lowercase(Locale.getDefault())
    }

private fun translateEquipment(raw: String): String =
    when (normalizeCatalogText(raw)) {
        "body weight" -> "peso corporal"
        "barbell" -> "barra"
        "dumbbell" -> "mancuernas"
        "cable" -> "polea"
        "rope" -> "cuerda"
        "smith machine" -> "máquina smith"
        "hammer" -> "máquina hammer"
        "leverage machine" -> "máquina de palanca"
        "band" -> "banda"
        "resistance band" -> "banda de resistencia"
        "kettlebell" -> "kettlebell"
        "bosu ball" -> "bosu"
        "stability ball" -> "balón de estabilidad"
        "medicine ball" -> "balón medicinal"
        "ez barbell" -> "barra EZ"
        "sled machine" -> "máquina de trineo"
        "roller", "wheel roller" -> "rodillo"
        "tire" -> "llanta"
        "weighted" -> "lastre"
        "assisted" -> "asistencia"
        else -> raw.lowercase(Locale.getDefault())
    }

private fun resolveEquipmentCategory(equipments: List<String>): String {
    val normalized = normalizeCatalogText(equipments.firstOrNull().orEmpty())
    return when (normalized) {
        "body weight" -> "Peso corporal"
        "dumbbell" -> "Mancuernas"
        "barbell", "olympic barbell", "trap bar", "ez barbell" -> "Barra"
        "cable" -> "Polea"
        "rope" -> "Cuerda"
        "smith machine", "leverage machine", "sled machine", "upper body ergometer", "skierg machine", "hammer" -> "Máquina"
        "band", "resistance band" -> "Banda"
        "kettlebell" -> "Kettlebell"
        "bosu ball" -> "Bosu"
        "stability ball" -> "Fitball"
        "medicine ball" -> "Balón medicinal"
        "roller", "wheel roller" -> "Rodillo"
        "tire" -> "Llanta"
        "weighted" -> "Lastre"
        "assisted" -> "Asistida"
        else -> "Otro"
    }
}

private fun toSentenceCaseSpanish(text: String): String =
    text.split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            when (word.lowercase(Locale.getDefault())) {
                "de", "del", "con", "en", "a", "y", "al", "la", "el", "tras", "sobre", "contra", "entre", "sin", "para" -> word.lowercase(Locale.getDefault())
                "ez" -> "EZ"
                else -> word.replaceFirstChar { ch ->
                    if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
                }
            }
        }

private fun catalogKey(name: String, muscle: String): String =
    "${normalizeCatalogText(muscle)}|${normalizeCatalogText(name)}"

private fun normalizeCatalogText(value: String): String {
    val normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase(Locale.getDefault())
        .replace("ñ", "n")
    return normalized.replace("[^a-z0-9\\s]".toRegex(), " ").trim().replace("\\s+".toRegex(), " ")
}

private val equipmentPrefixSuffixes = listOf(
    "smith machine" to "en máquina Smith",
    "leverage machine" to "en máquina de palanca",
    "ez barbell" to "con barra EZ",
    "olympic barbell" to "con barra olímpica",
    "body weight" to "con peso corporal",
    "resistance band" to "con banda de resistencia",
    "stability ball" to "con balón de estabilidad",
    "medicine ball" to "con balón medicinal",
    "sled machine" to "en máquina de trineo",
    "upper body ergometer" to "en ergómetro superior",
    "skierg machine" to "en SkiErg",
    "trap bar" to "con barra trap",
    "barbell" to "con barra",
    "dumbbell" to "con mancuernas",
    "cable" to "en polea",
    "band" to "con banda",
    "kettlebell" to "con kettlebell",
    "weighted" to "lastrada",
    "assisted" to "asistida",
    "rope" to "con cuerda",
    "roller" to "con rodillo",
    "wheel roller" to "con rueda"
)

private val phraseReplacements = listOf(
    "lying extension pullover" to "pullover tumbado",
    "straight arm pulldown" to "jalón de brazos rectos",
    "straight arm pullover" to "pullover de brazos rectos",
    "rope attachment" to "con cuerda",
    "close grip bench press" to "press de banca con agarre cerrado",
    "reverse grip incline bench press" to "press inclinado de banca con agarre inverso",
    "reverse grip bench press" to "press de banca con agarre inverso",
    "reverse grip decline bench press" to "press declinado de banca con agarre inverso",
    "wide grip decline bench press" to "press declinado de banca con agarre amplio",
    "wide grip bench press" to "press de banca con agarre amplio",
    "incline bench press" to "press inclinado de banca",
    "decline bench press" to "press declinado de banca",
    "bench press" to "press de banca",
    "military press" to "press militar",
    "shoulder press" to "press de hombro",
    "back extension" to "extensión de espalda",
    "hip extension" to "extensión de cadera",
    "wrist curl" to "curl de muñeca",
    "wrist extension" to "extensión de muñeca",
    "upright row" to "remo al mentón",
    "bent over row" to "remo inclinado",
    "high row" to "remo alto",
    "low row" to "remo bajo",
    "seated row" to "remo sentado",
    "renegade row" to "remo renegado",
    "cross over" to "cruce",
    "crossover" to "cruce",
    "lat pulldown" to "jalón al pecho",
    "pull up" to "dominada",
    "chin up" to "dominada supina",
    "push up" to "flexión",
    "sit up" to "abdominal",
    "glute bridge" to "puente de glúteo",
    "hip thrust" to "empuje de cadera",
    "romanian deadlift" to "peso muerto rumano",
    "deadlift" to "peso muerto",
    "leg press" to "prensa de pierna",
    "leg extension" to "extensión de pierna",
    "leg curl" to "curl femoral",
    "calf raise" to "elevación de pantorrilla",
    "lateral raise" to "elevación lateral",
    "front raise" to "elevación frontal",
    "face pull" to "face pull",
    "rear fly" to "apertura posterior",
    "rear delt row" to "remo posterior",
    "rear delt" to "deltoide posterior",
    "biceps curl" to "curl de bíceps",
    "hammer curl" to "curl martillo",
    "preacher curl" to "curl predicador",
    "spider curl" to "curl araña",
    "triceps extension" to "extensión de tríceps",
    "pushdown" to "jalón de tríceps",
    "kickback" to "patada",
    "french press" to "press francés",
    "pallof press" to "press Pallof",
    "turkish get up" to "levantamiento turco",
    "oblique knee raise" to "elevación oblicua de rodillas",
    "leg hip raise" to "elevación de cadera y piernas",
    "exercise ball" to "fitball",
    "stability ball" to "fitball",
    "medicine ball" to "balón medicinal",
    "muscle up" to "muscle up",
    "pelvic tilt" to "inclinación pélvica",
    "lower back stretch" to "estiramiento de espalda baja",
    "back stretch" to "estiramiento de espalda",
    "chest stretch" to "estiramiento de pecho",
    "hyperextension" to "hiperextensión",
    "pull over" to "pullover",
    "pullover" to "pullover",
    "shrug" to "encogimiento",
    "row" to "remo",
    "squat" to "sentadilla",
    "lunge" to "zancada",
    "fly" to "apertura",
    "plank" to "plancha",
    "crunch" to "crunch",
    "leg raise" to "elevación de piernas",
    "raise" to "elevación",
    "extension" to "extensión",
    "curl" to "curl",
    "press" to "press",
    "dip" to "fondo"
)

private val tokenReplacements = listOf(
    "pull-up" to "dominada",
    "chin-up" to "dominada supina",
    "push-up" to "flexión",
    "sit-up" to "abdominal",
    "reverse grip" to "agarre inverso",
    "close grip" to "agarre cerrado",
    "wide grip" to "agarre amplio",
    "close-grip" to "agarre cerrado",
    "wide-grip" to "agarre amplio",
    "reverse-grip" to "agarre inverso",
    "underhand" to "supino",
    "overhand" to "prono",
    "neutral grip" to "agarre neutro",
    "single arm" to "a una mano",
    "one arm" to "a una mano",
    "one hand" to "a una mano",
    "two arm" to "a dos manos",
    "single leg" to "a una pierna",
    "one leg" to "a una pierna",
    "two leg" to "a dos piernas",
    "alternating" to "alterno",
    "alternate" to "alterno",
    "seated" to "sentado",
    "standing" to "de pie",
    "lying" to "tumbado",
    "hanging" to "colgado",
    "kneeling" to "arrodillado",
    "incline" to "inclinado",
    "decline" to "declinado",
    "bent arms" to "con brazos flexionados",
    "bent arm" to "con brazos flexionados",
    "straight arm" to "de brazos rectos",
    "bent over" to "inclinado",
    "behind head" to "tras nuca",
    "front" to "frontal",
    "side to side" to "de lado a lado",
    "side" to "lateral",
    "diagonal" to "diagonal",
    "twisting" to "con giro",
    "twist" to "giro",
    "against wall" to "contra pared",
    "on floor" to "en suelo",
    "on bench" to "en banca",
    "over bench" to "sobre banca",
    "on box" to "en cajón",
    "between benches" to "entre bancos",
    "on exercise ball" to "en fitball",
    "over exercise ball" to "sobre fitball",
    "on stability ball" to "en fitball",
    "exercise ball" to "fitball",
    "stability ball" to "fitball",
    "medicine ball" to "balón medicinal",
    "v bar" to "barra V",
    "pro lat bar" to "barra pro lat",
    "overhead" to "sobre la cabeza",
    "machine" to "máquina",
    "smith" to "smith",
    "cable" to "polea",
    "barbell" to "barra",
    "dumbbell" to "mancuerna",
    "band" to "banda",
    "kettlebell" to "kettlebell",
    "lever" to "máquina",
    "rear" to "posterior",
    "shoulder" to "hombro",
    "chest" to "pecho",
    "back" to "espalda",
    "bodyweight" to "peso corporal",
    "stretch" to "estiramiento",
    "rotational" to "rotacional",
    "towel" to "toalla",
    "palm" to "palma",
    "prone" to "prono",
    "supine" to "supino",
    "catch" to "recepción",
    "throw" to "lanzamiento",
    "around" to "circular",
    "sphinx" to "esfinge",
    "wrist" to "muñeca",
    "hip" to "cadera",
    "calf" to "pantorrilla",
    "tricep" to "tríceps",
    "bicep" to "bíceps",
    "with" to "con",
    "on" to "en",
    "over" to "sobre",
    "between" to "entre",
    "and" to "y",
    "weighted" to "lastrada",
    "assisted" to "asistida",
    "rope" to "cuerda",
    "attachment" to ""
)

private val exactSpanishOverrides = mapOf(
    "cable lying extension pullover rope attachment" to "pullover tumbado en polea con cuerda",
    "cable straight arm pulldown rope" to "jalón de brazos rectos en polea con cuerda",
    "cable standing pulldown rope" to "jalón de pie en polea con cuerda",
    "cable rope crossover seated row" to "remo sentado en polea con cuerda cruzada",
    "cable rope elevated seated row" to "remo sentado elevado en polea con cuerda",
    "cable pulldown pro lat bar" to "jalón al pecho en polea con barra pro lat",
    "cable lateral pulldown rope attachment" to "jalón lateral en polea con cuerda",
    "cable lateral pulldown rope attachment core" to "jalón lateral en polea con cuerda",
    "cable low seated row controlled" to "remo bajo sentado en polea",
    "pure chin up" to "dominada supina",
    "side cable high row kneeling" to "remo alto lateral en polea arrodillado",
    "cable twisting pull with intensified" to "jalón con giro en polea",
    "band underhand pulldown with classic" to "jalón supino con banda",
    "cable incline bench row elite variation" to "remo inclinado en banca con polea",
    "balance cable incline fly" to "apertura inclinada en polea",
    "flexible cable triceps pushdown v bar" to "jalón de tríceps en polea con barra V"
)

private val noisePatterns = listOf(
    Regex("\\b\\w+ style\\b"),
    Regex("\\b\\w+ variation\\b"),
    Regex("\\bwith (classic|intensified|strength|pure|fast|firm|smooth|static|mega|mini|weak|elevated|compound|intermediate|contracted|intensive|improved|chair|resistance)\\b"),
    Regex("\\bv\\s*\\d+\\b")
)

private val noiseTokens = listOf(
    " macro ",
    " precision ",
    " athletic ",
    " dynamic ",
    " active ",
    " gentle ",
    " firm ",
    " smooth ",
    " static ",
    " mega ",
    " mini ",
    " weak ",
    " elevated ",
    " compound ",
    " intermediate ",
    " contracted ",
    " improved ",
    " intensified ",
    " complete ",
    " targeted ",
    " hold ",
    " endurance ",
    " fierce ",
    " sharp ",
    " blunt ",
    " micro ",
    " expanded ",
    " deep ",
    " classic ",
    " pure ",
    " fast ",
    " rigid ",
    " elite ",
    " intense ",
    " pointed ",
    " squared ",
    " compressed ",
    " controlled ",
    " core ",
    " focused ",
    " linear ",
    " modified ",
    " traditional ",
    " tempo ",
    " resistance ",
    " rough ",
    " light ",
    " quick ",
    " beginner ",
    " partial ",
    " isolation ",
    " horizontal ",
    " extreme ",
    " tough "
)

private val variantQualifierMappings = listOf(
    "one arm" to "a una mano",
    "one hand" to "a una mano",
    "two arm" to "a dos manos",
    "single leg" to "a una pierna",
    "one leg" to "a una pierna",
    "two leg" to "a dos piernas",
    "reverse grip" to "agarre inverso",
    "underhand" to "supino",
    "overhand" to "prono",
    "close grip" to "agarre cerrado",
    "wide grip" to "agarre amplio",
    "incline" to "inclinado",
    "decline" to "declinado",
    "standing" to "de pie",
    "seated" to "sentado",
    "lying" to "tumbado",
    "kneeling" to "arrodillado",
    "rope" to "con cuerda",
    "v bar" to "barra V",
    "pro lat bar" to "barra pro lat",
    "smith machine" to "smith",
    "exercise ball" to "en fitball",
    "stability ball" to "en fitball",
    "on bench" to "en banca",
    "on box" to "en cajón",
    "behind head" to "tras nuca",
    "high" to "alto",
    "low" to "bajo",
    "front" to "frontal",
    "rear" to "posterior"
)
