import java.io.File

fun main() {
    val matrix = File("src/resources/day06/input.txt")
        .readLines().map { it.toCharArray() }.toTypedArray()
    println("Covered ground: ${calculateGuardPositions(matrix)}")
    println("Obstruction chances: ${findObstructionChances(matrix)}")
}

sealed class Direction(val symbol: Char, val row: Int, val column: Int) {
    data object Up : Direction('^', -1, 0)
    data object Down : Direction('v', 1, 0)
    data object Left : Direction('<', 0, -1)
    data object Right : Direction('>',0, 1)

    companion object {
        fun availableDirections(): List<Direction> = listOf(Up, Down, Right, Left)
    }
}

const val obstacle = '#'

// O(n^4)
// Potential optimization is reducing the number of obstructions we check
// (as there is just one new obstruction, if you use the original path you don't need
// to check every potential obstruction. It could go to O(n^2)
fun findObstructionChances(matrix: Array<CharArray>): Int {
    val (initialDirection, initialPosition) = findGuard(matrix)
    var obstructionChances = 0

    for (row in matrix.indices) {
        for (column in matrix[row].indices) {
            var position = Pair(initialPosition.first, initialPosition.second)
            var state = initialDirection
            val clashes = mutableMapOf<Pair<Int, Int>, MutableSet<Direction>>()
            if (!(row == position.first && column == position.second)) {
                while (true) {
                    val nextRow = position.first + state.row
                    val nextColumn = position.second + state.column
                    if (nextRow < 0 || nextRow >= matrix.size
                        || nextColumn < 0 || nextColumn >= matrix[0].size) {
                        break
                    } else if (matrix[nextRow][nextColumn] == obstacle
                        || (nextRow == row && nextColumn == column)) {
                        clashes.putIfAbsent(Pair(nextRow, nextColumn), mutableSetOf())
                        if (clashes[Pair(nextRow, nextColumn)]?.contains(state) == true) {
                            obstructionChances++
                            break
                        }
                        clashes[Pair(nextRow, nextColumn)]?.add(state)
                        state = when (state) {
                            Direction.Up -> Direction.Right
                            Direction.Down -> Direction.Left
                            Direction.Right -> Direction.Down
                            Direction.Left -> Direction.Up
                        }
                        continue
                    }
                    position = Pair(nextRow, nextColumn)
                }
            }
        }
    }
    return obstructionChances
}



fun calculateGuardPositions(matrix: Array<CharArray>): Int {
    var (state, position) = findGuard(matrix)
    val visited = mutableSetOf(position)

    while (true) {
        val nextRow = position.first + state.row
        val nextColumn = position.second + state.column
        if (nextRow < 0 || nextRow >= matrix.size
            || nextColumn < 0 || nextColumn >= matrix[0].size) {
            break
        } else if (matrix[nextRow][nextColumn] == obstacle) {
            state = when (state) {
                Direction.Up -> Direction.Right
                Direction.Down -> Direction.Left
                Direction.Right -> Direction.Down
                Direction.Left -> Direction.Up
            }
            continue
        }
        position = Pair(nextRow, nextColumn)
        visited.add(position)
    }
    return visited.size
}


fun findGuard(matrix: Array<CharArray>): Pair<Direction, Pair<Int,Int>> {
    for (i in matrix.indices) {
        for (j in matrix[i].indices) {
            for (direction in Direction.availableDirections()) {
                if (direction.symbol == matrix[i][j]) {
                    return Pair(direction, Pair(i, j))
                }
            }
        }
    }
    return Pair(Direction.Up, Pair(-1, -1))
}



