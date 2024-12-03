import java.io.File

fun main() {
    var accumulator = 0
    File("src/resources/day03/input.txt").useLines { lines ->
        accumulator += runMultiplicationProgram(lines.joinToString("\n"))
    }
    println("Total result: $accumulator")
}

// O(1) time complexity solution.
// Potential improvements: using a State Machine
fun runMultiplicationProgram(input: String, prefix: String = "mul(", separator: Char = ',',
        suffix: String = ")", enabler: String = "do()", disabler: String = "don't()"): Int {
    var enabled = true
    var i = 0
    var totalSum = 0
    var textAccumulator = ""
    while (i < input.length) {
        textAccumulator += input[i]
        when (textAccumulator) {
            enabler -> {
                enabled = true
                textAccumulator = ""
            }
            disabler -> {
                enabled = false
                textAccumulator = ""
            }
            prefix -> {
                var j = i + 1
                var firstArgAccum = ""
                while (input[j].isDigit()) {
                    firstArgAccum += input.get(j)
                    j++
                }
                if (firstArgAccum.toIntOrNull() == null || input[j] != separator) {
                    textAccumulator = ""
                    continue
                }
                j++

                var secondArgAccum = ""
                while (input[j].isDigit()) {
                    secondArgAccum += input.get(j)
                    j++
                }
                if (secondArgAccum.toIntOrNull() == null || input.slice(j..<j + suffix.length) != suffix) {
                    textAccumulator = ""
                    continue
                }
                i = j + suffix.length - 1

                if (enabled) {
                    totalSum += firstArgAccum.toInt() * secondArgAccum.toInt()
                }
                textAccumulator = ""
            }
            else -> {
                if (!prefix.startsWith(textAccumulator) &&
                    !enabler.startsWith(textAccumulator) &&
                    !disabler.startsWith(textAccumulator)) {
                    textAccumulator = ""
                }
            }
        }
        i++
    }
    return totalSum
}
