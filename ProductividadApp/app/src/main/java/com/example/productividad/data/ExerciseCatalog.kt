package com.example.productividad.data

fun exerciseCatalog(): List<ExerciseCatalogItem> {
    fun tut(desc: String, gif: String? = null, vararg points: String) = 
        ExerciseTutorial(desc, points.toList(), gifUrl = gif?.let(::bundledGifUri))
    
    fun ex(name: String, muscle: String, description: String = "", gif: String? = null, vararg points: String) =
        ExerciseCatalogItem(name = name, muscle = muscle, rawName = name, tutorial = tut(description, gif, *points))

    val benchBar = "https://static.exercisedb.dev/media/EIeI8Vf.gif"
    val inclineDb = "https://static.exercisedb.dev/media/ns0SIbU.gif"
    val benchDb = "https://static.exercisedb.dev/media/SpYC0Kp.gif"
    val cableFly = "https://static.exercisedb.dev/media/w4dLzSx.gif"
    val inclineFly = "https://static.exercisedb.dev/media/ESOd5Pl.gif"
    val declineBench = "https://static.exercisedb.dev/media/GrO65fd.gif"
    val pushUp = "https://static.exercisedb.dev/media/I4hDWkc.gif"
    val dips = "https://static.exercisedb.dev/media/X6C6i5Y.gif"
    val pecDeck = "https://static.exercisedb.dev/media/v3xmPAR.gif"

    val deadlift = "https://static.exercisedb.dev/media/ila4NZS.gif"
    val pullUp = "https://static.exercisedb.dev/media/lBDjFxJ.gif"
    val latPulldown = "https://static.exercisedb.dev/media/qdRxqCj.gif"
    val vPulldown = "https://static.exercisedb.dev/media/4c9BhzB.gif"
    val barbellRow = "https://static.exercisedb.dev/media/eZyBC3j.gif"
    val tBarRow = "https://static.exercisedb.dev/media/aaXr7ld.gif"
    val cableRow = "https://static.exercisedb.dev/media/fUBheHs.gif"
    val dumbbellRow = "https://static.exercisedb.dev/media/C0MA9bC.gif"
    val straightArmPulldown = "https://static.exercisedb.dev/media/x69MAlq.gif"
    val shrug = "https://static.exercisedb.dev/media/dG7tG5y.gif"

    val militaryPress = "https://static.exercisedb.dev/media/Kyd9Rz5.gif"
    val dumbbellShoulderPress = "https://static.exercisedb.dev/media/znQUdHY.gif"
    val lateralRaise = "https://static.exercisedb.dev/media/DsgkuIt.gif"
    val frontRaise = "https://static.exercisedb.dev/media/3eGE2JC.gif"
    val uprightRow = "https://static.exercisedb.dev/media/UDlhcO8.gif"
    val rearDelt = "https://static.exercisedb.dev/media/v1qBec9.gif"
    val facePull = "https://static.exercisedb.dev/media/ZfyAGhK.gif"
    val arnoldPress = "https://static.exercisedb.dev/media/Xy4jlWA.gif"

    val backSquat = "https://static.exercisedb.dev/media/qXTaZnJ.gif"
    val legPress = "https://static.exercisedb.dev/media/10Z2DXU.gif"
    val legExtension = "https://static.exercisedb.dev/media/my33uHU.gif"
    val hackSquat = "https://static.exercisedb.dev/media/Qa55kX1.gif"
    val gobletSquat = "https://static.exercisedb.dev/media/yn8yg1r.gif"
    val sissySquat = "https://static.exercisedb.dev/media/xdYPUtE.gif"
    val lunges = "https://static.exercisedb.dev/media/RRWFUcw.gif"
    val bulgarianSplitSquat = "https://static.exercisedb.dev/media/qx4fgX7.gif"

    val romanianDeadlift = "https://static.exercisedb.dev/media/wQ2c4XD.gif"
    val goodMorning = "https://static.exercisedb.dev/media/XlZ4lAC.gif"
    val lyingLegCurl = "https://static.exercisedb.dev/media/17lJ1kr.gif"
    val seatedLegCurl = "https://static.exercisedb.dev/media/Zg3XY7P.gif"
    val hipThrust = "https://static.exercisedb.dev/media/qg2PGl6.gif"
    val cableKickback = "https://static.exercisedb.dev/media/Kpajagk.gif"

    val ezCurl = "https://static.exercisedb.dev/media/25GPyDY.gif"
    val alternatingCurl = "https://static.exercisedb.dev/media/xiA6lRr.gif"
    val hammerCurl = "https://static.exercisedb.dev/media/slDvUAU.gif"
    val preacherCurl = "https://static.exercisedb.dev/media/qOgPVf6.gif"
    val concentrationCurl = "https://static.exercisedb.dev/media/gvsWLQw.gif"
    val spiderCurl = "https://static.exercisedb.dev/media/2kattbR.gif"
    val cableCurl = "https://static.exercisedb.dev/media/otqIxU4.gif"
    val wristCurl = "https://static.exercisedb.dev/media/82LxxkW.gif"
    val reverseWristCurl = "https://static.exercisedb.dev/media/LsZkfU6.gif"

    val pushdownBar = "https://static.exercisedb.dev/media/gAwDzB3.gif"
    val ropePushdown = "https://static.exercisedb.dev/media/dU605di.gif"
    val singleArmExtension = "https://static.exercisedb.dev/media/qRZ5S1N.gif"
    val skullcrusher = "https://static.exercisedb.dev/media/h8LFzo9.gif"
    val overheadExtension = "https://static.exercisedb.dev/media/1xHyxys.gif"
    val diamondPushUp = "https://static.exercisedb.dev/media/x6KpKpq.gif"

    val plank = "https://static.exercisedb.dev/media/hCjGsRQ.gif"
    val abWheel = "https://static.exercisedb.dev/media/NAgVB3t.gif"
    val hangingLegRaise = "https://static.exercisedb.dev/media/I3tsCnC.gif"
    val russianTwist = "https://static.exercisedb.dev/media/XVDdcoj.gif"
    val mountainClimber = "https://static.exercisedb.dev/media/RJgzwny.gif"
    val deadBug = "https://static.exercisedb.dev/media/iny3m5y.gif"
    val cableCrunch = "https://static.exercisedb.dev/media/8xUv4J7.gif"

    val standingCalfRaise = "https://static.exercisedb.dev/media/bJYHBIN.gif"
    val seatedCalfRaise = "https://static.exercisedb.dev/media/bOOdeyc.gif"
    val calfPress = "https://static.exercisedb.dev/media/ykHcWme.gif"

    fun chest(name: String, description: String = "", gif: String = benchBar, vararg points: String) =
        ex(name, "Pecho", description, gif, *points)
    fun back(name: String, description: String = "", gif: String = barbellRow, vararg points: String) =
        ex(name, "Espalda", description, gif, *points)
    fun shoulder(name: String, description: String = "", gif: String = dumbbellShoulderPress, vararg points: String) =
        ex(name, "Hombro", description, gif, *points)
    fun quad(name: String, description: String = "", gif: String = backSquat, vararg points: String) =
        ex(name, "Cuadriceps", description, gif, *points)
    fun ham(name: String, description: String = "", gif: String = romanianDeadlift, vararg points: String) =
        ex(name, "Isquios", description, gif, *points)
    fun glute(name: String, description: String = "", gif: String = hipThrust, vararg points: String) =
        ex(name, "Glúteo", description, gif, *points)
    fun biceps(name: String, description: String = "", gif: String = ezCurl, vararg points: String) =
        ex(name, "Biceps", description, gif, *points)
    fun forearm(name: String, description: String = "", gif: String = wristCurl, vararg points: String) =
        ex(name, "Antebrazo", description, gif, *points)
    fun triceps(name: String, description: String = "", gif: String = pushdownBar, vararg points: String) =
        ex(name, "Triceps", description, gif, *points)
    fun abs(name: String, description: String = "", gif: String = plank, vararg points: String) =
        ex(name, "Abdomen", description, gif, *points)
    fun calf(name: String, description: String = "", gif: String = standingCalfRaise, vararg points: String) =
        ex(name, "Pantorrilla", description, gif, *points)

    return buildList {
        addAll(
            listOf(
                chest("Press de Banca con Barra", "El clásico para ganar fuerza y masa.", benchBar, "Baja la barra hasta el pecho", "Mantén codos estables", "Empuja con control"),
                chest("Press Inclinado con Barra", "Más enfoque en pecho superior.", benchBar, "Banco a 30-45 grados", "No rebotes la barra"),
                chest("Press Declinado con Barra", "Parte baja del pecho.", declineBench),
                chest("Press Superior con Mancuernas", "Enfocado en la parte alta del pecho.", inclineDb, "Inclina el banco 30-45 grados", "Junta las mancuernas arriba", "Controla el descenso"),
                chest("Press de Banca con Mancuernas", "Mayor rango de movimiento que la barra.", benchDb, "Mantén los pies firmes en el suelo", "No choques las mancuernas arriba"),
                chest("Press Declinado con Mancuernas", "Versión libre para pecho inferior.", declineBench),
                chest("Press de Pecho en Máquina", "Ideal para aislar sin perder estabilidad.", benchBar),
                chest("Press Convergente en Máquina", "Empuje guiado con convergencia natural.", benchDb),
                chest("Press en Smith Inclinado", "Variante guiada para pecho superior.", inclineDb),
                chest("Press en Smith Plano", "Empuje estable para progresar carga.", benchBar),
                chest("Flexiones de Pecho (Push Ups)", "Básico de calistenia.", pushUp, "Cuerpo recto como una tabla", "Pecho al suelo"),
                chest("Flexiones Inclinadas", "Útiles para principiantes o series altas.", pushUp),
                chest("Flexiones Declinadas", "Mayor carga sobre el pecho superior.", pushUp),
                chest("Flexiones con Palmada", "Explosividad y potencia.", pushUp),
                chest("Fondos de Pecho (Dips)", "Excelente para la parte inferior.", dips, "Inclina el torso hacia adelante", "Baja hasta que los brazos formen 90 grados"),
                chest("Pec Deck (Contractora)", "Aislamiento total.", pecDeck, "Espalda pegada al respaldo", "Aprieta al centro 1 segundo"),
                chest("Aperturas con Mancuernas en Banco Plano", "Estira y contrae el pecho con control.", benchDb),
                chest("Aperturas Inclinadas con Mancuernas", "Enfoque en pecho superior.", inclineFly),
                chest("Aperturas Declinadas con Mancuernas", "Gran estímulo en pecho inferior.", declineBench),
                chest("Aperturas en Polea (Cables)", "Tensión constante en el pectoral.", cableFly, "Mantén una ligera flexión de codos", "Siente el estiramiento al abrir"),
                chest("Cruce de Poleas Alto a Bajo", "Aprieta el pecho hacia abajo y al centro.", cableFly),
                chest("Cruce de Poleas Bajo a Alto", "Más énfasis en la porción clavicular.", cableFly),
                chest("Cruce de Poleas al Medio", "Aislamiento limpio en rango medio.", cableFly),
                chest("Press Unilateral en Cable", "Corrige desequilibrios y mejora control.", cableFly)
            )
        )

        addAll(
            listOf(
                back("Peso Muerto Convencional", "Ejercicio de fuerza total.", deadlift, "Barra pegada a las espinillas", "Espalda neutra", "Empuja con las piernas"),
                back("Peso Muerto Sumo", "Variante con base amplia y más cadera.", deadlift),
                back("Rack Pull", "Trabajo pesado del bloqueo y trapecio.", deadlift),
                back("Dominadas (Pull Ups)", "El mejor para amplitud.", pullUp, "Agarre más ancho que los hombros", "Lleva la barbilla sobre la barra"),
                back("Dominadas Supinas (Chin Ups)", "Más carga para dorsal y bíceps.", pullUp),
                back("Dominadas Asistidas", "Progresión para ganar volumen y técnica.", pullUp),
                back("Jalón al Pecho", "Alternativa a dominadas.", latPulldown, "Espalda ligeramente inclinada", "Lleva la barra a la parte alta del pecho"),
                back("Jalón con Agarre Neutro", "Recorrido cómodo para dorsales.", vPulldown),
                back("Jalón con Agarre Inverso", "Mayor protagonismo del dorsal bajo.", latPulldown),
                back("Jalón con Agarre en V", "Enfoque en densidad central.", vPulldown),
                back("Remo con Barra", "Para densidad de espalda.", barbellRow, "Torso casi paralelo al suelo", "Lleva la barra al ombligo"),
                back("Remo Yates", "Remo con torso más alto y agarre supino.", barbellRow),
                back("Remo Pendlay", "Desde el suelo en cada repetición.", barbellRow),
                back("Remo en T (T-Bar Row)", "Básico de tracción.", tBarRow),
                back("Remo en Polea Baja", "Remo sentado.", cableRow, "No balancees el torso", "Retrae las escápulas al final"),
                back("Remo Unilateral en Polea", "Más control y recorrido individual.", cableRow),
                back("Remo con Mancuerna", "Trabajo unilateral.", dumbbellRow, "Apóyate en un banco", "Lleva la mancuerna a la cadera"),
                back("Remo con Mancuerna en Banco Inclinado", "Soporte para evitar balanceo.", dumbbellRow),
                back("Remo en Máquina", "Estímulo estable para la parte media.", cableRow),
                back("Remo en Máquina con Pecho Apoyado", "Recorrido limpio sin fatiga lumbar.", dumbbellRow),
                back("Pull Over con Polea Alta", "Aislamiento de dorsales.", straightArmPulldown, "Brazos casi rectos", "Baja la barra hasta los muslos"),
                back("Pull Over con Mancuerna", "Estiramiento y bombeo del dorsal.", straightArmPulldown),
                back("Encogimientos con Barra (Shrugs)", "Para los trapecios.", shrug),
                back("Encogimientos con Mancuernas", "Trapecio con recorrido libre.", shrug)
            )
        )

        addAll(
            listOf(
                shoulder("Press Militar con Barra", "Fuerza de empuje vertical.", militaryPress, "Abdomen apretado", "Barra por encima de la cabeza"),
                shoulder("Press de Hombro con Mancuernas", "Sentado para más estabilidad.", dumbbellShoulderPress, "Espalda apoyada", "Sube en arco hacia el centro"),
                shoulder("Press Arnold", "Variante con rotación.", arnoldPress, "Empieza con palmas hacia ti", "Gira al subir"),
                shoulder("Press en Máquina para Hombro", "Empuje guiado y estable.", dumbbellShoulderPress),
                shoulder("Press en Smith para Hombro", "Gran estabilidad para progresar carga.", militaryPress),
                shoulder("Press Landmine", "Empuje seguro y muy cómodo para el hombro.", militaryPress),
                shoulder("Elevaciones Laterales", "Para hombros anchos.", lateralRaise, "No subas más allá de la horizontal", "Meñiques ligeramente hacia arriba"),
                shoulder("Elevaciones Laterales en Cable", "Tensión constante en el lateral.", lateralRaise),
                shoulder("Elevaciones Laterales Inclinadas", "Más rango en la parte baja.", lateralRaise),
                shoulder("Elevaciones Frontales", "Deltoides anterior.", frontRaise),
                shoulder("Elevaciones Frontales con Barra", "Variante bilateral con carga estable.", frontRaise),
                shoulder("Elevaciones Frontales con Disco", "Muy útil en series controladas.", frontRaise),
                shoulder("Remo al Mentón (Upright Row)", "Hombro lateral y trapecio.", uprightRow),
                shoulder("Remo al Mentón con Polea", "Recorrido suave y constante.", uprightRow),
                shoulder("Pájaros (Elevaciones Posteriores)", "Parte trasera del hombro.", rearDelt, "Inclina el torso", "Separa las mancuernas con fuerza"),
                shoulder("Reverse Pec Deck", "Aislamiento directo del deltoide posterior.", rearDelt),
                shoulder("Face Pull con Polea", "Salud del hombro y deltoides posterior.", facePull, "Lleva la cuerda hacia tu frente", "Separa las manos al final"),
                shoulder("Cuban Press", "Movilidad y trabajo del manguito.", facePull)
            )
        )

        addAll(
            listOf(
                quad("Sentadilla con Barra (Back Squat)", "El rey de las piernas.", backSquat, "Baja hasta romper la paralela", "Talones pegados al suelo"),
                quad("Sentadilla Frontal", "Más énfasis en cuadriceps y core.", backSquat),
                quad("Prensa de Piernas", "Mucho volumen de carga.", legPress, "No bloquees las rodillas arriba", "Baja de forma controlada"),
                quad("Extensiones de Cuadriceps", "Aislamiento puro.", legExtension, "Punta de los pies hacia arriba", "Siente la contracción arriba"),
                quad("Sentadilla Hack", "Excelente para cuádriceps.", hackSquat, "Cuerpo apoyado en la máquina", "Baja profundo"),
                quad("Sentadilla Goblet", "Ideal para principiantes.", gobletSquat),
                quad("Sissy Squat", "Aislamiento extremo frontal.", sissySquat),
                quad("Zancadas Caminando", "Trabajo unilateral y coordinación.", lunges),
                quad("Zancadas Hacia Atrás", "Más control y menos estrés en rodilla.", lunges),
                quad("Zancadas Estáticas", "Gran opción para hipertrofia.", lunges),
                quad("Sentadilla Búlgara", "Mucha intensidad.", bulgarianSplitSquat, "Un pie elevado atrás", "Todo el peso en la pierna delantera"),
                quad("Step Up con Mancuernas", "Transferencia muy útil y estable.", bulgarianSplitSquat),
                quad("Sentadilla en Smith", "Guiada para buscar más volumen.", backSquat),
                quad("Cyclist Squat", "Talones elevados para cargar el cuadriceps.", gobletSquat),
                quad("Sentadilla con Talones Elevados", "Aumenta la flexión de rodilla.", gobletSquat),
                quad("Pistol Squat Asistida", "Control unilateral y fuerza relativa.", gobletSquat),
                quad("Wall Sit", "Isométrico clásico para quemar quadriceps.", sissySquat),
                quad("Split Squat", "Muy útil como base unilateral.", lunges)
            )
        )

        addAll(
            listOf(
                ham("Peso Muerto Rumano", "Estiramiento de isquios.", romanianDeadlift, "Rodillas ligeramente flexionadas", "Baja la barra rozando piernas"),
                ham("Peso Muerto Piernas Rígidas", "Más recorrido posterior y estiramiento.", romanianDeadlift),
                ham("Buenos Días con Barra", "Cadera hacia atrás.", goodMorning),
                ham("Curl Femoral Tumbado", "Aislamiento de la parte posterior.", lyingLegCurl, "Aprieta 1 segundo arriba", "No levantes la cadera"),
                ham("Curl Femoral Sentado", "Más tensión en estiramiento.", seatedLegCurl, "Ajusta bien el rodillo", "Controla la subida"),
                ham("Curl Femoral Unilateral", "Trabajo individual para corregir desequilibrios.", seatedLegCurl),
                ham("Nordic Curl", "Muy demandante para los isquios.", lyingLegCurl),
                ham("Peso Muerto Sumo para Isquios", "Variante posterior con apertura amplia.", deadlift),
                ham("Pull Through con Polea", "Bisagra limpia con foco posterior.", cableKickback),
                ham("Kettlebell Swing", "Potencia de cadera e isquios.", romanianDeadlift),

                glute("Hip Thrust (Empuje de Cadera)", "El mejor para glúteos.", hipThrust, "Barra en la cadera", "Aprieta glúteos arriba fuerte"),
                glute("Glute Bridge con Barra", "Versión más corta del empuje de cadera.", hipThrust),
                glute("Hip Thrust Unilateral", "Más estabilidad y trabajo por lado.", hipThrust),
                glute("Patada de Glúteo en Polea", "Aislamiento máximo.", cableKickback, "Cuerpo inclinado", "Extiende la pierna atrás plenamente"),
                glute("Patada de Glúteo en Máquina", "Muy útil para congestión y control.", cableKickback),
                glute("Frog Pumps", "Bombeo directo y fácil de acumular repeticiones.", hipThrust),
                glute("Back Extension Enfocada en Glúteo", "Bisagra con énfasis en glúteo.", hipThrust),
                glute("Reverse Hyperextension", "Extensión de cadera con rango largo.", cableKickback)
            )
        )

        addAll(
            listOf(
                biceps("Curl con Barra EZ", "Cuidado de muñecas.", ezCurl, "No balancees el cuerpo", "Aprieta al subir"),
                biceps("Curl con Barra Recta", "Clásico pesado para bíceps.", ezCurl),
                biceps("Curl Alterno con Mancuernas", "Rotación de muñeca.", alternatingCurl, "Gira la palma hacia arriba al subir"),
                biceps("Curl Inclinado con Mancuernas", "Gran estiramiento en la parte baja.", alternatingCurl),
                biceps("Curl Martillo", "Para el braquial.", hammerCurl, "Agarre neutro (palmas enfrentadas)"),
                biceps("Curl Martillo Cruzado", "Variante más cerrada para braquial.", hammerCurl),
                biceps("Curl en Banco Scott (Predicador)", "Aislamiento total sin trampas.", preacherCurl, "Brazos totalmente apoyados"),
                biceps("Curl Scott con Mancuerna", "Unilateral y muy controlado.", preacherCurl),
                biceps("Curl Concentrado", "Aislamiento máximo.", concentrationCurl),
                biceps("Spider Curl", "Sin trampas dinámicas.", spiderCurl),
                biceps("Curl de Bíceps en Polea", "Tensión constante.", cableCurl),
                biceps("Curl de Bíceps en Polea Alta", "Gran contracción al centro.", cableCurl),
                biceps("Curl Inverso", "Trabaja bíceps y antebrazo.", ezCurl),
                biceps("Curl Zottman", "Combina supinación y antebrazo.", hammerCurl),
                forearm("Curl de Antebrazo", "Flexores.", wristCurl),
                forearm("Curl de Antebrazo Inverso", "Extensores.", reverseWristCurl)
            )
        )

        addAll(
            listOf(
                triceps("Extensiones en Polea Alta", "Básico de tríceps.", pushdownBar, "Codos pegados al cuerpo", "Extiende los brazos abajo"),
                triceps("Extensiones con Cuerda", "Mayor apertura al final.", ropePushdown),
                triceps("Extensión Unilateral en Polea", "Trabajo concentrado.", singleArmExtension),
                triceps("Extensión en Polea con Agarre Inverso", "Muy buena para control y congestión.", singleArmExtension),
                triceps("Press Francés (Skullcrusher)", "Para masa de tríceps.", skullcrusher, "Lleva la barra hacia la frente"),
                triceps("Skullcrusher con Mancuernas", "Más libertad para las muñecas.", skullcrusher),
                triceps("Extensiones sobre la cabeza", "Enfocado en la cabeza larga.", overheadExtension, "Codos apuntando al techo"),
                triceps("Extensión Sobre Cabeza con Mancuerna", "Muy útil en banco o de pie.", overheadExtension),
                triceps("Fondos en Paralelas", "Cuerpo vertical.", dips, "Codos cerca del cuerpo"),
                triceps("Fondos en Banco", "Variante corporal simple.", dips),
                triceps("Flexiones Diamante", "Básico corporal.", diamondPushUp, "Manos formando un diamante"),
                triceps("Press Cerrado en Banco", "Gran básico pesado para tríceps.", benchBar),
                triceps("Kickback con Mancuerna", "Aislamiento en extensión final.", singleArmExtension),
                triceps("JM Press", "Mixto entre press y extensión.", benchBar),
                triceps("Tate Press", "Aísla bien desde banco plano.", benchDb),
                triceps("Press Francés Sentado", "Más rango para cabeza larga.", overheadExtension)
            )
        )

        addAll(
            listOf(
                abs("Plancha Abdominal (Plank)", "Resistencia isométrica.", plank, "Cuerpo recto", "Aprieta abdomen y glúteos"),
                abs("Plancha Lateral", "Oblicuos y estabilidad lateral.", plank),
                abs("Rueda Abdominal", "Alta intensidad.", abWheel, "Baja con control", "No arquees la espalda"),
                abs("Elevación de Piernas Colgado", "Abdomen inferior.", hangingLegRaise, "No te columpies", "Sube los pies lo más alto posible"),
                abs("Elevación de Rodillas Colgado", "Versión más accesible.", hangingLegRaise),
                abs("Elevación de Piernas en Suelo", "Control total del abdomen bajo.", hangingLegRaise),
                abs("Reverse Crunch", "Lleva la pelvis hacia el pecho.", hangingLegRaise),
                abs("V-Ups", "Abdomen completo y coordinación.", hangingLegRaise),
                abs("Sit Up", "Flexión completa de tronco.", cableCrunch),
                abs("Crunch en Polea", "Abdomen con carga.", cableCrunch, "Arrodillado", "Lleva los codos a las rodillas"),
                abs("Crunch Bicicleta", "Oblicuos y recto abdominal.", russianTwist),
                abs("Giro Ruso (Russian Twist)", "Oblicuos.", russianTwist),
                abs("Mountain Climbers", "Core y cardio.", mountainClimber),
                abs("Dead Bug", "Estabilidad lumbar.", deadBug),
                abs("Hollow Hold", "Tensión isométrica del core.", deadBug),
                abs("Toe Touches", "Recto abdominal con alcance.", cableCrunch),
                abs("Flutter Kicks", "Gran quema para abdomen inferior.", hangingLegRaise),
                abs("Heel Touches", "Oblicuos con control corto.", russianTwist),
                abs("Bird Dog", "Estabilidad lumbar y control motor.", deadBug)
            )
        )

        addAll(
            listOf(
                calf("Elevación de Talones De Pie", "Máquina o escalón.", standingCalfRaise, "Máximo estiramiento abajo", "Contrae arriba fuerte"),
                calf("Elevación de Talones Sentado", "Enfocado en el sóleo.", seatedCalfRaise),
                calf("Prensa de Pantorrilla", "En la prensa de pierna.", calfPress),
                calf("Elevación Unilateral de Pantorrilla", "Más rango y enfoque por lado.", standingCalfRaise)
            )
        )
    }
}

fun templateRoutines(): List<WorkoutRoutine> {
    fun r(id: String, name: String, exercises: List<Pair<String, String>>) = 
        WorkoutRoutine(id, name, exercises.map { RoutineExercise(it.first, "", 4, it.second) })

    return listOf(
        WorkoutRoutine("tpl-full-body", "Full Body Iniciante", listOf(
            RoutineExercise("Sentadilla con Barra (Back Squat)", "Pierna", 3, "8-10"),
            RoutineExercise("Press de Banca con Barra", "Pecho", 3, "8-10"),
            RoutineExercise("Jalón al Pecho", "Espalda", 3, "10-12"),
            RoutineExercise("Press Militar con Barra", "Hombro", 2, "10-12"),
            RoutineExercise("Curl con Barra EZ", "Biceps", 2, "12"),
            RoutineExercise("Extensiones en Polea Alta", "Triceps", 2, "12")
        )),
        WorkoutRoutine("tpl-push", "Empuje (Push) PRO", listOf(
            RoutineExercise("Press de Banca con Barra", "Pecho", 4, "6-8"),
            RoutineExercise("Press Superior con Mancuernas", "Pecho", 3, "10-12"),
            RoutineExercise("Press Militar con Barra", "Hombro", 3, "8-10"),
            RoutineExercise("Elevaciones Laterales", "Hombro", 4, "15-20"),
            RoutineExercise("Extensiones sobre la cabeza", "Triceps", 3, "12-15"),
            RoutineExercise("Fondos en Paralelas", "Triceps", 3, "Fallo")
        )),
        WorkoutRoutine("tpl-pull", "Tracción (Pull) PRO", listOf(
            RoutineExercise("Peso Muerto Convencional", "Espalda", 3, "5"),
            RoutineExercise("Remo con Barra", "Espalda", 4, "8-10"),
            RoutineExercise("Jalón al Pecho", "Espalda", 3, "10-12"),
            RoutineExercise("Face Pull con Polea", "Hombro", 3, "15"),
            RoutineExercise("Curl Alterno con Mancuernas", "Biceps", 3, "12"),
            RoutineExercise("Curl Martillo", "Biceps", 3, "12")
        )),
        WorkoutRoutine("tpl-legs", "Pierna Completa (Elite)", listOf(
            RoutineExercise("Sentadilla con Barra (Back Squat)", "Cuadriceps", 4, "6-8"),
            RoutineExercise("Prensa de Piernas", "Cuadriceps", 3, "12-15"),
            RoutineExercise("Peso Muerto Rumano", "Isquios", 4, "8-10"),
            RoutineExercise("Curl Femoral Tumbado", "Isquios", 3, "12-15"),
            RoutineExercise("Hip Thrust (Empuje de Cadera)", "Glúteo", 4, "10-12"),
            RoutineExercise("Elevación de Talones De Pie", "Pantorrilla", 4, "15-20")
        )),
        WorkoutRoutine("tpl-upper", "Torso (Upper Body)", listOf(
            RoutineExercise("Press de Banca con Mancuernas", "Pecho", 4, "8-10"),
            RoutineExercise("Remo con Mancuerna", "Espalda", 4, "10-12"),
            RoutineExercise("Press de Hombro con Mancuernas", "Hombro", 3, "10-12"),
            RoutineExercise("Dominadas (Pull Ups)", "Espalda", 3, "Fallo"),
            RoutineExercise("Aperturas en Polea (Cables)", "Pecho", 3, "15")
        )),
        WorkoutRoutine("tpl-pecho-triceps", "Pecho, Tríceps y Hombro", listOf(
            RoutineExercise("Press de Banca con Barra", "Pecho", 4, "8"),
            RoutineExercise("Press Superior con Mancuernas", "Pecho", 3, "12"),
            RoutineExercise("Elevaciones Laterales", "Hombro", 4, "15"),
            RoutineExercise("Extensiones en Polea Alta", "Triceps", 3, "12"),
            RoutineExercise("Fondos en Paralelas", "Triceps", 3, "Fallo")
        )),
        WorkoutRoutine("tpl-espalda-biceps", "Espalda y Bíceps (Tracción)", listOf(
            RoutineExercise("Jalón al Pecho", "Espalda", 4, "10"),
            RoutineExercise("Remo en Polea Baja", "Espalda", 3, "12"),
            RoutineExercise("Pull Over con Polea Alta", "Espalda", 3, "15"),
            RoutineExercise("Curl con Barra EZ", "Biceps", 4, "12"),
            RoutineExercise("Curl Martillo", "Biceps", 3, "12")
        )),
        WorkoutRoutine("tpl-lower-quads", "Pierna (Enfoque Cuádriceps)", listOf(
            RoutineExercise("Sentadilla Hack", "Cuadriceps", 4, "10-12"),
            RoutineExercise("Zancadas (Lunges)", "Cuadriceps", 3, "12 pasos"),
            RoutineExercise("Extensiones de Cuadriceps", "Cuadriceps", 4, "15"),
            RoutineExercise("Prensa de Piernas", "Cuadriceps", 3, "20"),
            RoutineExercise("Elevación de Talones De Pie", "Pantorrilla", 4, "15")
        )),
        WorkoutRoutine("tpl-lower-glutes", "Glúteo e Isquios", listOf(
            RoutineExercise("Hip Thrust (Empuje de Cadera)", "Glúteo", 5, "8-10"),
            RoutineExercise("Peso Muerto Rumano", "Isquios", 4, "10-12"),
            RoutineExercise("Sentadilla Búlgara", "Pierna", 3, "12"),
            RoutineExercise("Patada de Glúteo en Polea", "Glúteo", 3, "15"),
            RoutineExercise("Curl Femoral Sentado", "Isquios", 4, "12")
        ))
    )
}
