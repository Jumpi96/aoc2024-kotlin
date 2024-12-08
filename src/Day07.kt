import java.io.File

fun main() {
    var accumulator = 0L
    File("src/resources/day07/input.txt").useLines { lines ->
        lines.forEach { line ->
            val parts = line.trim().split(":")
            val result = parts[0].trim().toLong()
            val tokens = parts[1].trim().split(" ").map { it.toInt() }

             if (isEquationCalibratable(tokens, result)) {
                 accumulator += result
             }
        }
    }
    println("Total calibration result: $accumulator")
}

// Recursive and exponential function ~ O(3^n)
// Memoization may help but not clear how much
fun isEquationCalibratable(tokens: List<Int>, result: Long, carrier: Long = 0): Boolean {
    if (tokens.size == 1) {
        return carrier * tokens.first() == result
                || carrier + tokens.first() == result
                || (carrier.toString() + tokens.first().toString()).toLong() == result
    } else if (tokens.size > 1) {
        val slicedTokens = tokens.slice(1..< tokens.size)
        val multiplicationCarrier = if (carrier != 0L) carrier else 1
        val sumCarrier = if (carrier != 0L) carrier else 0
        return isEquationCalibratable(slicedTokens, result, sumCarrier+tokens[0])
                || isEquationCalibratable(slicedTokens, result, multiplicationCarrier*tokens[0])
                || (carrier != 0L && isEquationCalibratable(slicedTokens, result, (carrier.toString() + tokens[0].toString()).toLong()))
    }
    return false
}