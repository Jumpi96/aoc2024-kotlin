import java.io.File

fun main() {
    val matrix = File("src/resources/day08/input.txt")
        .readLines().map { it.toCharArray() }.toTypedArray()
    println("Total antinode positions: ${countAntinodes(matrix)}")
}

const val emptySpot = '.'

// O(n^3) solution
fun countAntinodes(matrix: Array<CharArray>): Int {
    val antinodes = mutableSetOf<Pair<Int, Int>>()

    for (row in matrix.indices) {
        for (column in matrix[row].indices) {
            if (matrix[row][column] != emptySpot) {
                val signal = matrix[row][column]
                antinodes.add(Pair(row, column))

                var currentRow = row
                while (currentRow < matrix.size) {
                    var currentColumn = 0
                    while (currentColumn < matrix[row].size) {
                        if (currentRow != row && currentColumn != column && matrix[currentRow][currentColumn] == signal) {
                            var count = 1

                            while (currentRow+(currentRow-row)*count >= 0 && currentRow+(currentRow-row)*count < matrix.size
                                && currentColumn+(currentColumn-column)*count >= 0 && currentColumn+(currentColumn-column)*count < matrix[row].size) {
                                antinodes.add(Pair(currentRow+(currentRow-row)*count, currentColumn+(currentColumn-column)*count))
                                count++
                            }

                            count = 1
                            while (row-(currentRow-row)*count >= 0 && row-(currentRow-row)*count < matrix.size
                                && column-(currentColumn-column)*count >= 0 && column-(currentColumn-column)*count < matrix[row].size) {
                                antinodes.add(Pair(row-(currentRow-row)*count, column-(currentColumn-column)*count))
                                count++
                            }
                        }
                        currentColumn++
                    }
                    currentRow++
                }
            }
        }
    }
    printMatrixWithAntinodes(matrix, antinodes)
    return antinodes.size
}

fun printMatrixWithAntinodes(matrix: Array<CharArray>, antinodes: Set<Pair<Int, Int>>) {
    println()
    for (row in matrix.indices) {
        for (column in matrix[row].indices) {
            if (matrix[row][column] != emptySpot) {
                print(matrix[row][column])
            } else if (antinodes.contains(Pair(row, column))) {
                print("#")
            } else {
                print(emptySpot)
            }
        }
        println("")
    }
}