import java.io.File

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Usage: VerificadorTSP.kt <instancia> <solución>")
        return
    }

    val instanciaFile = File(args[0])
    val solucionFile = File(args[1])

    val (dimension, instanciaDistance) = leerInstancia(instanciaFile)
    val (solucion, solucionDistance) = leerSolucion(solucionFile)

    val problemas = verificarSolucion(solucion, dimension, instanciaDistance, solucionDistance)

    if (problemas.isEmpty()) {
        println("La solución es válida.")
    } else {
        println("La solución tiene los siguientes problemas:")
        problemas.forEach { println("- $it") }
    }
}

fun leerInstancia(file: File): Pair<Int, Double> {
    var dimension = 0
    var distance = 0.0

    file.forEachLine { line ->
        if (line.startsWith("DIMENSION")) {
            val parts = line.trim().split(":")
            dimension = parts[1].trim().toInt()
        } else if (line.startsWith("COMMENT")) {
            val parts = line.trim().split(":")
            val distanceText = parts[1].trim().substringAfter("Length").trim()
            distance = distanceText.toDouble()
        }
    }

    return Pair(dimension, distance)
}

fun leerSolucion(file: File): Pair<IntArray, Double> {
    val solucion = mutableListOf<Int>()
    var distance = 0.0

    var leerSolucion = false
    file.forEachLine { line ->
        if (!leerSolucion) {
            if (line.startsWith("COMMENT")) {
                val parts = line.trim().split(":")
                val distanceText = parts[1].trim().substringAfter("Length").trim()
                distance = distanceText.toDouble()
            }
        } else if (line != "-1" && line != "EOF") {
            val city = line.trim().toInt()
            solucion.add(city)
        }
        if (line.startsWith("TOUR_SECTION")) {
            leerSolucion = true
        }
    }
    
    return Pair(solucion.toIntArray(), distance)
}

fun verificarSolucion(
    solucion: IntArray,
    dimension: Int,
    instanciaDistance: Double,
    solucionDistance: Double
): List<String> {
    val problemas = mutableListOf<String>()

    // Verificar que todas las ciudades sean visitadas exactamente una vez
    val ciudadesVisitadas = mutableSetOf<Int>()
    for (i in solucion) {
        if (i !in ciudadesVisitadas) {
            ciudadesVisitadas.add(i)
        } else {
            problemas.add("La ciudad $i es visitada más de una vez.")
        }
    }
    for (i in 1..dimension) {
        if (i !in ciudadesVisitadas) {
            problemas.add("La ciudad $i no es visitada.")
        }
    }

    // Verificar la longitud de la solución
    if (solucion.size != dimension) {
        problemas.add("La solución tiene una longitud incorrecta. Se esperaban ${solucion.size} ciudades pero se encontraron $dimension.")
    }

    // Verificar la distancia de la solución
    if (instanciaDistance != solucionDistance) {
        problemas.add("La distancia de la solución es incorrecta. Se esperaba $solucionDistance pero se encontró $instanciaDistance .")
    }

    return problemas
}