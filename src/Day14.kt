import java.io.File
import kotlin.math.abs


fun main() {
    val robots = parseRobotsInput("src/resources/day14/input.txt")
    println("Safety factor: ${calculateSafetyFactor(robots, 101, 103, 100)}")
    findChristmasTree(robots, 101, 103)
}

data class Robot(val position: Coordinate, val velocity: Coordinate)

fun findChristmasTree(robots: List<Robot>, width: Int, height: Int) {
    var seconds = 1
    while (true) {
        val quadrant = Array(height) { IntArray(width) }

        for (robot in robots) {
            val nextPosition = getNextValue(robot, height, width, seconds)
            quadrant[nextPosition.row][nextPosition.column]++
        }

        for (y in 0..< height) {
            for (x in 0..< width) {
                print(if (quadrant[y][x] > 0)  quadrant[y][x] else ".")
            }
            println()
        }
        println("Seconds: $seconds")
        Thread.sleep(200)

        seconds++
    }
}

fun calculateSafetyFactor(robots: List<Robot>, width: Int, height: Int, seconds: Int): Int {
    val quadrantFactors = arrayOf(intArrayOf(0, 0), intArrayOf(0, 0))
    val middleRows = if (height % 2 == 0) setOf(height/2, height/2+1) else setOf(height/2)
    val middleColumns = if (width % 2 == 0) setOf(width/2, width/2+1) else setOf(width/2)
    for (robot in robots) {
        val nextPosition = getNextValue(robot, height, width, seconds)
        if (!middleRows.contains(nextPosition.row) && !middleColumns.contains(nextPosition.column)) {
            val widthPositiveQuadrant = if (nextPosition.column > width / 2) 1 else 0
            val heightPositiveQuadrant = if (nextPosition.row > height / 2) 1 else 0

            quadrantFactors[widthPositiveQuadrant][heightPositiveQuadrant]++
        }
    }
    return quadrantFactors[0].reduce {acc, value -> acc * value} * quadrantFactors[1].reduce {acc, value -> acc * value}
}

fun getNextValue(robot: Robot, height: Int, width: Int, iterations: Int): Coordinate {
    val newColumnRaw = robot.position.column + robot.velocity.column * iterations
    val newColumn = if (newColumnRaw >= 0) (newColumnRaw % width) % width else (width-abs(newColumnRaw % width)) % width
    val newRowRaw = robot.position.row + robot.velocity.row * iterations
    val newRow = if (newRowRaw >= 0) (newRowRaw % height) % height else (height-abs(newRowRaw % height)) % height

    return Coordinate(newRow, newColumn)
}


fun parseRobotsInput(filePath: String): List<Robot> {
    return File(filePath)
        .readText()
        .trim()
        .split("\n")
        .map { robotLine ->
            val regex = Regex("p=([\\d-]+),([\\d-]+) v=([\\d-]+),([\\d-]+)")
            val matchResult = regex.find(robotLine)

            if (matchResult != null) {
                val (posX, posY, velX, velY) = matchResult.destructured
                val position = Coordinate(posY.toInt(), posX.toInt())
                val velocity = Coordinate(velY.toInt(), velX.toInt())
                
                Robot(position, velocity)
            } else {
                throw IllegalArgumentException("Invalid robot line format: $robotLine")
            }
        }
}