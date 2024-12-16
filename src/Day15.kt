import java.io.File
import kotlin.math.min

fun main() {
    val (map, commands) = parseLanternFishInput("src/resources/day15/simple_input.txt")
    //println("Sum of all boxes GPS coordinates: ${executeLanternFishRobot(map, commands)}")
    println("Sum of all widened boxes GPS coordinates: ${executeLanternFishRobotWithDoubleBoxes(widenMap(map, 2), commands)}")
}

class LanternFishRobot {
    companion object {
        const val NAVIGATION_SYMBOL = '@'
        const val WALL_SYMBOL = '#'
        const val EMPTY_SYMBOL = '.'
        const val BOX_SYMBOL = 'O'
        const val BOX_OPENER_SYMBOL = '['
        const val BOX_CLOSER_SYMBOL = ']'
        const val GPS_ROW_SCORE = 100L
        const val GPS_COLUMN_SCORE = 1L
    }
}

fun executeLanternFishRobotWithDoubleBoxes(map: Array<CharArray>, commands: String): Long {
    var currentCoordinate = findLanternFishRobot(map)
    currentCoordinate = Coordinate(currentCoordinate.row, currentCoordinate.column)
    printLanternFishRobotMap(map)

    for (commandSymbol in commands) {
        println("Command: $commandSymbol")
        val command = when (commandSymbol) {
            Direction.Right.symbol -> Direction.Right
            Direction.Left.symbol -> Direction.Left
            Direction.Up.symbol -> Direction.Up
            Direction.Down.symbol -> Direction.Down
            else -> Direction.Up
        }
        val potentialNextCoordinate = Coordinate(
            currentCoordinate.row + command.row,
            currentCoordinate.column + command.column
        )

        val potentialNextValue = map[potentialNextCoordinate.row][potentialNextCoordinate.column]
        when (potentialNextValue) {
            LanternFishRobot.EMPTY_SYMBOL -> {
                map[currentCoordinate.row][currentCoordinate.column] = LanternFishRobot.EMPTY_SYMBOL
                map[potentialNextCoordinate.row][potentialNextCoordinate.column] = LanternFishRobot.NAVIGATION_SYMBOL
                currentCoordinate = potentialNextCoordinate
            }
            LanternFishRobot.WALL_SYMBOL -> {}
            LanternFishRobot.BOX_OPENER_SYMBOL, LanternFishRobot.BOX_CLOSER_SYMBOL -> {
                var emptySpace: Coordinate? = null
                when (command) {
                    Direction.Up, Direction.Down -> {
                        val modifier = if (potentialNextValue == LanternFishRobot.BOX_OPENER_SYMBOL) 1 else -1
                        val checkCoordinates = mutableListOf(
                            Coordinate(potentialNextCoordinate.row+command.row, potentialNextCoordinate.column),
                            Coordinate(potentialNextCoordinate.row+command.row, potentialNextCoordinate.column+modifier)
                        )

                        while (checkCoordinates.map { isValid(map, it.row, it.column) }.all { it }) {
                            when {
                                checkCoordinates.all { map[it.row][it.column] == LanternFishRobot.EMPTY_SYMBOL } -> {
                                    emptySpace = checkCoordinates.minByOrNull { it.column }
                                    break
                                }
                                checkCoordinates.any() { map[it.row][it.column] == LanternFishRobot.WALL_SYMBOL } -> break
                                checkCoordinates.any() { map[it.row][it.column] == LanternFishRobot.BOX_CLOSER_SYMBOL
                                        || map[it.row][it.column] == LanternFishRobot.BOX_OPENER_SYMBOL } -> {
                                    val nextRow = checkCoordinates.first().row+command.row
                                    val closerSymbols = checkCoordinates.filter { map[it.row][it.column] == LanternFishRobot.BOX_CLOSER_SYMBOL }
                                    val openerSymbols = checkCoordinates.filter { map[it.row][it.column] == LanternFishRobot.BOX_OPENER_SYMBOL }
                                    /*for (i in checkCoordinates.indices) {
                                         checkCoordinates[i] = Coordinate(nextRow, checkCoordinates[i].column)
                                    }*/
                                    checkCoordinates.removeIf { it.row == nextRow+command.row*-1 }

                                    for (closer in closerSymbols) {
                                        checkCoordinates.add(Coordinate(nextRow, closer.column))
                                        checkCoordinates.add(Coordinate(nextRow, closer.column-1))
                                    }
                                    for (opener in openerSymbols) {
                                        checkCoordinates.add(Coordinate(nextRow, opener.column))
                                        checkCoordinates.add(Coordinate(nextRow, opener.column+1))
                                    }
                                }
                                else -> {}
                            }
                        }
                        if (emptySpace != null) {
                            val leftColumn = checkCoordinates.minOf { it.column }
                            val rightColumn = checkCoordinates.maxOf { it.column }
                            for (row in intRangeExclusive(emptySpace.row, currentCoordinate.row+command.row)) {
                                for (column in leftColumn..rightColumn) {
                                    map[row][column] = map[row - command.row][column - command.column]
                                }
                            }
                            for (column in leftColumn..rightColumn) {
                                map[currentCoordinate.row+command.row][column] = LanternFishRobot.EMPTY_SYMBOL
                            }
                            map[potentialNextCoordinate.row][potentialNextCoordinate.column] = LanternFishRobot.NAVIGATION_SYMBOL
                            map[currentCoordinate.row][currentCoordinate.column] = LanternFishRobot.EMPTY_SYMBOL
                            currentCoordinate = potentialNextCoordinate
                        }
                    }
                    Direction.Left, Direction.Right -> {
                        var checkCoordinate = Coordinate(potentialNextCoordinate.row,
                            potentialNextCoordinate.column+command.column * 2)
                        while (isValid(map, checkCoordinate.row, checkCoordinate.column)) {
                            when (map[checkCoordinate.row][checkCoordinate.column]) {
                                LanternFishRobot.EMPTY_SYMBOL -> {
                                    emptySpace = checkCoordinate
                                    break
                                }
                                LanternFishRobot.WALL_SYMBOL -> break
                                else -> checkCoordinate = Coordinate(checkCoordinate.row,
                                    checkCoordinate.column+command.column)
                            }
                        }
                        if (emptySpace != null) {
                            for (row in intRangeInclusive(emptySpace.row, currentCoordinate.row)) {
                                for (column in intRangeInclusive(emptySpace.column, currentCoordinate.column)) {
                                    map[row][column] = map[row - command.row][column - command.column]
                                }
                            }
                            currentCoordinate = potentialNextCoordinate
                        }
                    }
                }
            }
        }
        printLanternFishRobotMap(map)
    }
    var accumulator = 0L
    for (row in map.indices) {
        var column = 0
        while (column < map[row].size) {
            if (map[row][column] == LanternFishRobot.BOX_OPENER_SYMBOL) {
                accumulator += row * LanternFishRobot.GPS_ROW_SCORE + column * LanternFishRobot.GPS_COLUMN_SCORE
                column++
            }
            column++
        }
    }
    return accumulator
}

fun widenMap(map: Array<CharArray>, repeat: Int): Array<CharArray> {
    val newMap = Array(map.size) { CharArray(map[0].size * repeat) }

    for (row in map.indices) {
        var newMapColumn = 0
        for (column in map[row].indices) {
            when (map[row][column]) {
                LanternFishRobot.NAVIGATION_SYMBOL -> {
                    newMap[row][newMapColumn] = map[row][column]
                    newMapColumn++
                    for (x in 1..< repeat) {
                        newMap[row][newMapColumn] = LanternFishRobot.EMPTY_SYMBOL
                        newMapColumn++
                    }
                }
                LanternFishRobot.BOX_SYMBOL -> {
                    newMap[row][newMapColumn] = LanternFishRobot.BOX_OPENER_SYMBOL
                    newMapColumn++
                    newMap[row][newMapColumn] = LanternFishRobot.BOX_CLOSER_SYMBOL
                    newMapColumn++
                }
                else -> {
                    for (x in 0..< repeat) {
                        newMap[row][newMapColumn] = map[row][column]
                        newMapColumn++
                    }
                }
            }
        }
    }
    return newMap
}

fun executeLanternFishRobot(map: Array<CharArray>, commands: String): Long {
    var currentCoordinate = findLanternFishRobot(map)

    for (commandSymbol in commands) {
        val command = when (commandSymbol) {
            Direction.Right.symbol -> Direction.Right
            Direction.Left.symbol -> Direction.Left
            Direction.Up.symbol -> Direction.Up
            Direction.Down.symbol -> Direction.Down
            else -> Direction.Up
        }
        val potentialNextCoordinate = Coordinate(currentCoordinate.row+command.row,
            currentCoordinate.column+command.column)

        when (map[potentialNextCoordinate.row][potentialNextCoordinate.column]) {
            LanternFishRobot.EMPTY_SYMBOL -> {
                map[currentCoordinate.row][currentCoordinate.column] = LanternFishRobot.EMPTY_SYMBOL
                map[potentialNextCoordinate.row][potentialNextCoordinate.column] = LanternFishRobot.NAVIGATION_SYMBOL
                currentCoordinate = potentialNextCoordinate
            }
            LanternFishRobot.WALL_SYMBOL -> {}
            LanternFishRobot.BOX_SYMBOL -> {
                var emptySpace: Coordinate? = null
                var checkCoordinate = Coordinate(potentialNextCoordinate.row+command.row,
                    potentialNextCoordinate.column+command.column)

                while (isValid(map, checkCoordinate.row, checkCoordinate.column)) {
                    when (map[checkCoordinate.row][checkCoordinate.column]) {
                        LanternFishRobot.EMPTY_SYMBOL -> {
                            emptySpace = checkCoordinate
                            break
                        }
                        LanternFishRobot.WALL_SYMBOL -> break
                        else -> checkCoordinate = Coordinate(checkCoordinate.row+command.row,
                            checkCoordinate.column+command.column)
                    }
                }
                if (emptySpace != null) {
                    for (row in intRangeInclusive(emptySpace.row, currentCoordinate.row)) {
                        for (column in intRangeInclusive(emptySpace.column, currentCoordinate.column)) {
                            map[row][column] = map[row - command.row][column - command.column]
                        }
                    }
                    map[currentCoordinate.row][currentCoordinate.column] = LanternFishRobot.EMPTY_SYMBOL
                    map[potentialNextCoordinate.row][potentialNextCoordinate.column] = LanternFishRobot.WALL_SYMBOL
                    currentCoordinate = potentialNextCoordinate
                }
            }
        }
    }
    return map.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { columnIndex, cell ->
            if (cell == LanternFishRobot.BOX_SYMBOL)
                rowIndex * LanternFishRobot.GPS_ROW_SCORE + columnIndex * LanternFishRobot.GPS_COLUMN_SCORE else 0L
        }}.sum()
}

fun findLanternFishRobot(map: Array<CharArray>): Coordinate {
    for (row in map.indices) {
        for (column in map[row].indices) {
            if (map[row][column] == LanternFishRobot.NAVIGATION_SYMBOL) {
                return Coordinate(row, column)
            }
        }
    }
    return Coordinate(-1, -1)
}

fun printLanternFishRobotMap(map: Array<CharArray>) {
    for (row in map.indices) {
        for (column in map[row].indices) {
            print(map[row][column])
        }
        println()
    }
}


fun intRangeInclusive(start: Int, end: Int): IntProgression {
    return if (start <= end) {
        start..end
    } else {
        start downTo end
    }
}


fun intRangeExclusive(start: Int, end: Int): IntProgression {
    return if (start < end) {
        start..< end
    } else {
        start downTo end-1
    }
}


fun parseLanternFishInput(filePath: String): Pair<Array<CharArray>, String> {
    val input = File(filePath).readText().trim().split("\n\n")
    val map = input[0].split("\n").map { it.toCharArray() }.toTypedArray()
    val commands = input[1].split("\n").joinToString("")
    return Pair(map, commands)
}