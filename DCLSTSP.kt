import kotlin.math.roundToInt
import kotlin.math.sqrt
import java.io.File

fun quicksortThreeWay(arr: Array<Double>) {
    quicksortThreeWay(arr, 0, arr.size - 1)
}

fun quicksortThreeWay(arr: Array<Double>, l: Int, r: Int) {
    if (r <= l) return
    
    var i = l - 1
    var j = r
    var p = l - 1
    var q = r
    val v = arr[r]
    
    while (true) {
        while (arr[++i] < v) { 
            // Avanza el índice 'i' hacia la derecha mientras los elementos sean menores que el pivote 'v'.
        }
        while (v < arr[--j]) { 
            // Retrocede el índice 'j' hacia la izquierda mientras los elementos sean mayores que el pivote 'v'.
            if (j == l) break
        }
        if (i >= j) break
        swap(arr, i, j) // Intercambia los elementos en las posiciones 'i' y 'j'.
        
        if (arr[i] == v) { 
            p++
            swap(arr, p, i) // Intercambia los elementos en las posiciones 'p' y 'i'.
        }
        if (v == arr[j]) { 
            q--
            swap(arr, j, q) // Intercambia los elementos en las posiciones 'j' y 'q'.
        }
    }
    
    swap(arr, i, r) // Intercambia el pivote 'v' con el elemento en la posición 'i'.
    
    j = i - 1
    i = i + 1
    
    var k = l
    while (k < p) {
        swap(arr, k, j) // Intercambia los elementos en las posiciones 'k' y 'j'.
        j--
        k++
    }
    
    k = r - 1
    while (k > q) {
        swap(arr, i, k) // Intercambia los elementos en las posiciones 'i' y 'k'.
        i++
        k--
    }
    
    quicksortThreeWay(arr, l, j) // Ordena recursivamente la sublista izquierda.
    quicksortThreeWay(arr, i, r) // Ordena recursivamente la sublista derecha.
}

private fun swap(arr: Array<Double>, i: Int, j: Int) {
    val temp = arr[i]
    arr[i] = arr[j]
    arr[j] = temp
}

fun modificarPuntos(P: Array<Triple<Double, Double, Int>>, pderecha: Array<Pair<Double, Double>>, pizquierda: Array<Pair<Double, Double>>): Pair<Array<Triple<Double, Double, Int>>, Array<Triple<Double, Double, Int>>> {
    val PizquierdaModificada = pizquierda.mapNotNull { punto ->
        val matchingPoint = P.find { tripleta ->
            tripleta.first == punto.first && tripleta.second == punto.second
        }
        matchingPoint?.let { Triple(punto.first, punto.second, it.third) }
    }.toTypedArray()

    val PderechaModificada = pderecha.mapNotNull { punto ->
        val matchingPoint = P.find { tripleta ->
            tripleta.first == punto.first && tripleta.second == punto.second
        }
        matchingPoint?.let { Triple(punto.first, punto.second, it.third) }
    }.toTypedArray()

    return Pair(PderechaModificada, PizquierdaModificada)
}


//Algoritmo 1
fun divideAndConquerTSP(P: Array<Triple<Double,Double,Int>>): Array<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>> {
    val n = P.size

    if (n == 0) {
        return emptyArray()
    } else if (n == 1) {
        return arrayOf(Pair(P[0], P[0])) 
    } else if (n == 2) {
        return arrayOf(Pair(P[0], P[1]), Pair(P[1], P[0]))
    } else if (n == 3) {
        return arrayOf(Pair(P[0], P[1]), Pair(P[1], P[2]), Pair(P[2], P[0]))
    } else {
        var ( pizquierda, pderecha) = obtenerParticiones(P)
        var result = modificarPuntos(P, pderecha, pizquierda)
        val PizquierdaModificada = result.first
        val PderechaModificada = result.second

        val c1 = divideAndConquerTSP(PderechaModificada)
        val c2 = divideAndConquerTSP(PizquierdaModificada)
        var combinar = combinarCiclos(c1, c2)

        return combinar
    }
}


//algoritmo 2
fun obtenerParticiones(P: Array<Triple<Double, Double, Int>>): Pair<Array<Pair<Double, Double>>, Array<Pair<Double, Double>>> {
    val rectangulo = obtenerRectangulo(P)

    val (Xdim, Ydim) = obtenerDimensionesLados(rectangulo)
    var ejeDeCorte = if (Xdim > Ydim) 'X' else 'Y'
    var xc: Double
    var yc: Double
    var puntoDeCorte = obtenerPuntoDeCorte(P, ejeDeCorte)
    xc = puntoDeCorte.first
    yc = puntoDeCorte.second
    var rectanguloCortado = aplicarCorte(ejeDeCorte, Pair(xc, yc), rectangulo)
    var rectanguloIzq = rectanguloCortado.first
    var rectanguloDer = rectanguloCortado.second
    var particionIzq = obtenerPuntosRectangulo(P, rectanguloIzq)
    var particionDer = obtenerPuntosRectangulo(P, rectanguloDer)

    if ((particionIzq.isEmpty() && particionDer.size > 3) || (particionIzq.size > 3 && particionDer.isEmpty())) {
        ejeDeCorte = if (ejeDeCorte == 'X') 'Y' else 'X'
        puntoDeCorte = obtenerPuntoDeCorte(P, ejeDeCorte)
        xc = puntoDeCorte.first
        yc = puntoDeCorte.second
        rectanguloCortado = aplicarCorte(ejeDeCorte, Pair(xc, yc), rectangulo)
        rectanguloIzq = rectanguloCortado.first
        rectanguloDer = rectanguloCortado.second
        particionIzq = obtenerPuntosRectangulo(P, rectanguloIzq)
        particionDer = obtenerPuntosRectangulo(P, rectanguloDer)

        if ((particionIzq.isEmpty() && particionDer.size > 3) || (particionIzq.size > 3 && particionDer.isEmpty())) {
            puntoDeCorte = obtenerPuntoDeCorteMitad(rectangulo, ejeDeCorte)
            xc = puntoDeCorte.first
            yc = puntoDeCorte.second
            rectanguloCortado = aplicarCorte(ejeDeCorte, Pair(xc, yc), rectangulo)
            rectanguloIzq = rectanguloCortado.first
            rectanguloDer = rectanguloCortado.second
            particionIzq = obtenerPuntosRectangulo(P, rectanguloIzq)
            particionDer = obtenerPuntosRectangulo(P, rectanguloDer)
        }
    }
    // Verificar y eliminar puntos repetidos en ambas particiones
    val uniquePoints = mutableSetOf<Pair<Double, Double>>()
    val finalPartitionIzq = mutableListOf<Pair<Double, Double>>()
    val finalPartitionDer = mutableListOf<Pair<Double, Double>>()

    for (punto in particionIzq) {
        if (!uniquePoints.contains(punto)) {
            uniquePoints.add(punto)
            finalPartitionIzq.add(punto)
        }
    }

    for (punto in particionDer) {
        if (!uniquePoints.contains(punto)) {
            uniquePoints.add(punto)
            finalPartitionDer.add(punto)
        }
    }
    //return Pair(particionIzq, particionDer)
    return Pair(finalPartitionIzq.toTypedArray(), finalPartitionDer.toTypedArray())

}




fun obtenerRectangulo(P: Array<Triple<Double, Double, Int>>): Array<Pair<Double, Double>> {
    val xmin = P.minByOrNull { it.first }?.first ?: throw IllegalArgumentException("Invalid points")
    val ymin = P.minByOrNull { it.second }?.second ?: throw IllegalArgumentException("Invalid points")
    val xmax = P.maxByOrNull { it.first }?.first ?: throw IllegalArgumentException("Invalid points")
    val ymax = P.maxByOrNull { it.second }?.second ?: throw IllegalArgumentException("Invalid points")

    return arrayOf(Pair(xmin, ymin), Pair(xmax, ymin), Pair(xmin, ymax), Pair(xmax, ymax))
}

fun obtenerDimensionesLados(rectangulo: Array<Pair<Double, Double>>): Pair<Double, Double> {
    val Xdim = rectangulo[1].first - rectangulo[0].first
    val Ydim = rectangulo[3].second - rectangulo[0].second
    return Pair(Xdim, Ydim)
}

fun obtenerPuntoDeCorte(P: Array<Triple<Double, Double, Int>>, ejeDeCorte: Char): Pair<Double, Double> {
    val n = P.size
    val pos = n / 2 - 1

    val sortedPoints = when (ejeDeCorte) {
        'X' -> {
            val sortedX = P.map { it.first }.toTypedArray()
            quicksortThreeWay(sortedX)
            P.sortedBy { sortedX.indexOf(it.first) }
        }
        'Y' -> {
            val sortedY = P.map { it.second }.toTypedArray()
            quicksortThreeWay(sortedY)
            P.sortedBy { sortedY.indexOf(it.second) }
        }
        else -> throw IllegalArgumentException("Invalid axis")
    }

    return Pair(sortedPoints[pos].first, sortedPoints[pos].second)
}



fun obtenerPuntoDeCorteMitad(rectangulo: Array<Pair<Double, Double>>, eje: Char): Pair<Double, Double> {
    // Obtener el punto de corte en la mitad del lado más largo del rectángulo
    val xDim = rectangulo[1].first - rectangulo[0].first
    val yDim = rectangulo[1].second - rectangulo[0].second
    
    return when (eje) {
        'X' -> Pair(rectangulo[0].first + xDim / 2, rectangulo[0].second)
        'Y' -> Pair(rectangulo[0].first, rectangulo[0].second + yDim / 2)
        else -> rectangulo[0] // En caso de un eje inválido, se devuelve la esquina inferior izquierda del rectángulo
    }
}


fun aplicarCorte(ejeDeCorte: Char, puntoDeCorte: Pair<Double, Double>, rectangulo: Array<Pair<Double,Double>>): Pair<Array<Pair<Double,   Double>>, Array<Pair<Double, Double>>> {
    val rectanguloIzq = Array(4) { rectangulo[it].copy() }
    val rectanguloDer = Array(4) { rectangulo[it].copy() }

    if (ejeDeCorte == 'X') {
        rectanguloIzq[1] = puntoDeCorte
        rectanguloIzq[3] = Pair(puntoDeCorte.first, rectangulo[3].second)

        rectanguloDer[0] = puntoDeCorte
        rectanguloDer[2] = Pair(puntoDeCorte.first, rectangulo[3].second)

    } else {
        rectanguloIzq[1] = puntoDeCorte
        rectanguloIzq[3] = Pair(rectangulo[1].first, puntoDeCorte.second)

        rectanguloDer[0] = puntoDeCorte
        rectanguloDer[2] = Pair(rectangulo[1].first, puntoDeCorte.second)
    }

    return Pair(rectanguloIzq, rectanguloDer)
}

fun obtenerPuntosRectangulo(P: Array<Triple<Double, Double, Int>>, rectangulo: Array<Pair<Double, Double>>): Array<Pair<Double, Double>> {
    val particion = arrayOfNulls<Pair<Double, Double>>(P.size)
    var j = 0 
    for (p in P) {
        if ((rectangulo[0].first <= p.first && p.first <= rectangulo[1].first) && (rectangulo[0].second <= p.second && p.second <= rectangulo[2].second)){
            particion[j] = Pair(p.first, p.second)
            j++
        }
    }
    
    return particion.filterNotNull().toTypedArray()
}



//algoritmo 3
fun combinarCiclos(
    Ciclo1: Array<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>>,
    Ciclo2: Array<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>>
): Array<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>> {
    if (Ciclo1.isEmpty()) {
        return Ciclo2.clone()
    } else if (Ciclo2.isEmpty()) {
        return Ciclo1.clone()
    }

    var minG = Double.POSITIVE_INFINITY
    var ladosAgregarC1 = emptyArray<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>>()
    var ladosAgregarC2 = emptyArray<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>>()
    var ladosEliminarC1 = emptyArray<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>>()
    var ladosEliminarC2 = emptyArray<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>>()


    for ((a, b) in Ciclo1) {
        val dOLD1 = distancia(Pair(a.first, a.second), Pair(b.first, b.second))
        for ((c, d) in Ciclo2) {
            val dOLD2 = distancia(Pair(c.first, c.second), Pair(d.first, d.second))
            val dNEW1 = distancia(Pair(a.first, a.second), Pair(c.first, c.second))
            val dNEW2 = distancia(Pair(b.first, b.second), Pair(d.first, d.second))
            val dNEW3 = distancia(Pair(a.first, a.second), Pair(d.first, d.second))
            val dNEW4 = distancia(Pair(b.first, b.second), Pair(c.first, c.second))

            val g1 = distanciaGanada(dOLD1, dOLD2, dNEW1, dNEW2)
            val g2 = distanciaGanada(dOLD1, dOLD2, dNEW3, dNEW4)
            val ganancia = minOf(g1, g2)

            if (ganancia < minG) {
                minG = ganancia.toDouble()
                if (g1 < g2) {
                    ladosAgregarC1 = arrayOf(Pair(a, c))
                    ladosAgregarC2 = arrayOf(Pair(b, d))
                } else {
                    ladosAgregarC1 = arrayOf(Pair(a, d))
                    ladosAgregarC2 = arrayOf(Pair(b, c))
                }
                ladosEliminarC1 = arrayOf(Pair(a, b))
                ladosEliminarC2 = arrayOf(Pair(c, d))
            }
        }
    }

    var Ciclo3 = arrayOf<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>>()
    Ciclo3 += ladosAgregarC1
    Ciclo3 += ladosAgregarC2
    Ciclo3 += Ciclo1.filterNot { it in ladosEliminarC1 }
    Ciclo3 += Ciclo2.filterNot { it in ladosEliminarC2 }
    
    return Ciclo3
}




fun distanciaGanada(dOLD1: Int, dOLD2: Int, dNEW1: Int, dNEW2: Int): Int {
    return (dNEW1 + dNEW2) - (dOLD1 + dOLD2)
}

fun distancia(p1: Pair<Double, Double> , p2: Pair<Double, Double>): Int {
    val xd =  p2.first - p1.first
    val yd =  p2.second - p1.second
    val distance = Math.sqrt((xd * xd + yd * yd).toDouble()).roundToInt()
    return distance
}

fun twoOptSwap(c: Array<Pair<Triple<Double, Double, Int>,Triple<Double, Double, Int>>>, i: Int, j: Int): Array<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>> {
    val newC = c.copyOf()
    var start = i
    var end = j

    while (start < end) {
        val temp = newC[start]
        newC[start] = newC[end]
        newC[end] = temp
        start++
        end--
    }

    return newC
}


fun calcularDistancia(c: Array<Pair<Pair<Double, Double>, Pair<Double, Double>>>): Int {
    var distance = 0

    for (i in c.indices) {
        val p1 = c[i].first
        val p2 = c[i].second
        distance += distancia(p1, p2)
    }
    return distance
}


//algoritmo 4
fun divideAndConquerAndLocalSearchTSP(P: Array<Triple<Double, Double, Int>>): Array<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>> {
    val c1 = divideAndConquerTSP(P)
    print("Distancia de la solucion por el algoritmo dividirYConquistar ${c1.size} \n")
    val c2 = busquedaLocalCon2OPT(c1)
    return c2
}

 fun busquedaLocalCon2OPT(c1: Array<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>>): Array<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>> {

    var bestC = c1
    var improved = true

    while (improved) {
        improved = false

        for (i in 1 until bestC.size - 1) {
            for (j in i + 1 until bestC.size) {
                val newC = twoOptSwap(bestC, i, j)

                val currentDistance = calcularDistancia(bestC.map { Pair(it.first.first to it.first.second, it.second.first to it.second.second) }.toTypedArray())
                val newDistance = calcularDistancia(newC.map { Pair(it.first.first to it.first.second, it.second.first to it.second.second) }.toTypedArray())
                


                if (newDistance < currentDistance) {
                    bestC = newC
                    improved = true
                }
            }
        }
    }

    return bestC
}



fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Uso: runDCLSTSP.sh archivo_entrada archivo_salida")
        return
    }

    val inputFile = args[0]
    val outputFile = args[1]

    val cities = parseInputFile(inputFile).first
    var instanceName = parseInputFile(inputFile).second
    print("nombre de la instancia a resolver: $instanceName \n")
    val tour = divideAndConquerAndLocalSearchTSP(cities)
    print("distancia del tour final : ${tour.size}\n")

    val distance = calcularDistancia(tour.map { Pair( it.first.first to it.first.second, it.second.first to it.second.second )}.toTypedArray())
    
    writeOutputFile(outputFile, instanceName, tour, distance)
    println("Solución del TSP generada en el archivo: ${instanceName}.out")
}

fun parseInputFile(filename: String): Pair<Array<Triple<Double, Double, Int>>, String> {
    var cities = emptyArray<Pair<Double, Double>>()
    val positions = mutableListOf<Int>()
    var readCoordinates = false
    var citiesCount = 0
    var instanceName: String = ""
    File(filename).useLines { lines ->
        for (line in lines) {
            val tokens = line.trim().split("\\s+".toRegex())

            if (tokens.isNotEmpty()) {
                when (tokens[0]) {
                    "DIMENSION:", "DIMENSION" -> {
                        cities = if (tokens[0] == "DIMENSION:")
                            Array(tokens[1].toInt()) { Pair(0.0, 0.0) }
                        else
                            Array(tokens[2].toInt()) { Pair(0.0, 0.0) }

                    }
                    "NAME:", "NAME" -> {
                        instanceName = if (tokens[0] == "NAME:")
                            tokens.subList(1, tokens.size).joinToString(" ")
                        else
                            tokens.subList(2, tokens.size).joinToString(" ")
                    }
                    "NODE_COORD_SECTION" -> {
                        readCoordinates = true
                    }
                    else -> {
                        if (readCoordinates && tokens.size >= 3) {
                            try {
                                val position = tokens[0].toInt()
                                val x = tokens[1].toDouble()
                                val y = tokens[2].toDouble()
                                cities[position - 1] = Pair(x, y)
                                positions.add(position)
                                citiesCount++
                            } catch (e: NumberFormatException) {
                                // Ignorar líneas que no contienen coordenadas numéricas
                            }
                        }
                    }
                }
            }
        }
    }

    val triplets = Array(citiesCount) { Triple(0.0, 0.0, 0) }
    for (i in positions.indices) {
        triplets[i] = Triple(cities[i].first, cities[i].second, positions[i])
    }

    return Pair(triplets, instanceName)
}




fun writeOutputFile(outPutFile: String, filename: String, tour: Array<Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>>, distance: Int) {

    File(outPutFile).bufferedWriter().use { writer ->
        writer.write("NAME: ${filename}\n")
        writer.write("COMMENT: Length $distance\n")
        writer.write("TYPE: TOUR\n")
        writer.write("DIMENSION: ${tour.size}\n")
        writer.write("TOUR_SECTION\n")

        for ((x) in tour) {
            var z = x.third
            writer.write("$z \n")
        }

        writer.write("-1\n")
        writer.write("EOF\n")
    }
}