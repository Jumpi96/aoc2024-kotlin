import java.io.File
import kotlin.math.abs
import kotlin.math.round


fun main() {
    var machines = parseInput(0)
    var accumulator = 0L
    for (machine in machines) {
        val solution = machine.solve()
        println("Tokens for ${machine.prize}: $solution")
        accumulator += solution
    }
    println("Total tokens: $accumulator")

    machines = parseInput(10000000000000)
    accumulator = 0L
    for (machine in machines) {
        val solution = machine.solve(Long.MAX_VALUE)
        println("Tokens for broken ${machine.prize}: $solution")
        accumulator += solution
    }
    println("Total tokens: $accumulator")
}


data class MachineCoordinate(val x: Long, val y: Long)
data class Machine(val buttonA: MachineCoordinate, val buttonB: MachineCoordinate, val prize: MachineCoordinate, val machineError: Long) {
    fun solve(maxButtonPresses: Long = 100): Long {

        val actualPrize = MachineCoordinate(prize.x+machineError, prize.y+machineError)

        val solution = LinearEquationsHelper(arrayOf(
            doubleArrayOf(buttonA.x.toDouble(), buttonB.x.toDouble(), actualPrize.x.toDouble()),
            doubleArrayOf(buttonA.y.toDouble(), buttonB.y.toDouble(), actualPrize.y.toDouble())
        )).solve()
        if (solution != null && abs(round(solution[0])-solution[0]) < DOUBLE_DIFF
                && abs(round(solution[1])-solution[1]) < DOUBLE_DIFF) {
            val nButtonA = round(solution[0]).toLong()
            val nButtonB = round(solution[1]).toLong()
            if (nButtonA >= 0 && nButtonB >= 0 && nButtonA < maxButtonPresses && nButtonB < maxButtonPresses) {
                return calculateCost(nButtonA, nButtonB)
            }
        }
        return 0
    }

    private fun calculateCost(nButtonA: Long, nButtonB: Long): Long {
        return nButtonA * 3 + nButtonB
    }

    companion object {
        const val DOUBLE_DIFF = 0.01
    }
}



class LinearEquationsHelper(private val matrix: Array<DoubleArray>) {
    // Using Gaussian Elimination
    fun solve(): DoubleArray? {
        val (reducedMatrix, singularFlag) = forwardElimination(matrix)

        if (singularFlag != -1L) {
            return null
        }
        return backSub(reducedMatrix)
    }

    private fun forwardElimination(matrix: Array<DoubleArray>): Pair<Array<DoubleArray>, Long> {
        for (k in matrix.indices) {
            var iMax = k
            var vMax = matrix[iMax][k]

            for (i in k+1..< matrix.size) {
                if (abs(matrix[i][k]) > vMax) {
                    vMax = matrix[i][k]
                    iMax = i
                }
            }

            if (matrix[k][iMax] == 0.0)
                return Pair(matrix, k.toLong())

            if (iMax != k) {
                for (l in 0..matrix.size) {
                    val temp = matrix[k][l]
                    matrix[k][l] = matrix[iMax][l]
                    matrix[iMax][l] = temp
                }
            }

            for (i in k+1..< matrix.size) {
                val f = matrix[i][k]/matrix[k][k]

                for (j in k+1..matrix.size)
                    matrix[i][j] -= matrix[k][j] * f

                matrix[i][k] = 0.0
            }
        }
        return Pair(matrix, -1)
    }

    private fun backSub(matrix: Array<DoubleArray>): DoubleArray {
        val x = DoubleArray(matrix.size)

        for (i in matrix.size-1 downTo 0 ) {
            x[i] = matrix[i][matrix.size]
            for (j in i+1..< matrix.size) {
                x[i] -= matrix[i][j]*x[j]
            }
            x[i] = (x[i]/matrix[i][i])
        }
        return x
    }

}

fun parseInput(machineError: Long): List<Machine> {
    return File("src/resources/day13/input.txt")
        .readText()
        .trim()
        .split("\n\n")
        .map { machineText ->
            val lines = machineText.lines()
            val buttonA = parseCoordinates(lines[0].substringAfter("Button A: "))
            val buttonB = parseCoordinates(lines[1].substringAfter("Button B: "))
            val prize = parseCoordinates(lines[2].substringAfter("Prize: "))

            Machine(buttonA, buttonB, prize, machineError)
        }
}

private fun parseCoordinates(coord: String): MachineCoordinate {
    val parts = coord.split(", ")
    val x = parts[0].substringAfter("X").replace("=", "").replace("+", "").trim().toLong()
    val y = parts[1].substringAfter("Y").replace("=", "").replace("+", "").trim().toLong()
    return MachineCoordinate(x, y)
}