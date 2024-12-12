import java.io.File

fun main() {
    val matrix = File("src/resources/day10/input.txt")
        .readLines().map { it -> it.toCharArray().map { it.toString().toInt() }.toTypedArray() }.toTypedArray()

    var startTime = System.currentTimeMillis()
    println("Trailheads score: ${findTrailheads(matrix, true)}")
    var executionTime = System.currentTimeMillis() - startTime
    println("\nExecution time: ${executionTime}ms (${executionTime/1000.0} seconds)")

    startTime = System.currentTimeMillis()
    println("Trailheads rating score: ${findTrailheads(matrix, false)}")
    executionTime = System.currentTimeMillis() - startTime
    println("\nExecution time: ${executionTime}ms (${executionTime/1000.0} seconds)")
}

open class GraphSystem(val graph: Map<Coordinate, List<Coordinate>>, val startingCoordinates: List<Coordinate>) {
    companion object {
        fun availableDirections(): List<Coordinate> {
            return listOf(Coordinate(0, -1), Coordinate(1, 0), Coordinate(0, 1), Coordinate(-1, 0))
        }
    }
}

data class Coordinate(val row: Int, val column: Int)
class TopographicMap(graph: Map<Coordinate, List<Coordinate>>, coordinates: List<Coordinate>): GraphSystem(graph, coordinates) {
    companion object {
        const val MIN_HEIGHT: Int = 0
        const val MAX_HEIGHT: Int = 9
    }
}

fun findTrailheads(matrix: Array<Array<Int>>, useVisited: Boolean): Int {
    val topographicMap = matrixToTopographicMap(matrix)
    var accumulator = 0

    for (startingCoordinate in topographicMap.startingCoordinates) {
        accumulator += findTrailheadsDfs(matrix, topographicMap.graph, startingCoordinate, useVisited)
    }
    return accumulator
}

fun findTrailheadsDfs(matrix: Array<Array<Int>>, graph: Map<Coordinate, List<Coordinate>>, coordinate: Coordinate,
        useVisited: Boolean, visited: MutableList<Coordinate> = mutableListOf()): Int {
    var counter = 0
    if (useVisited) {
        visited.add(coordinate)
    }

    if (matrix[coordinate.row][coordinate.column] == TopographicMap.MAX_HEIGHT) {
        counter++
    }

    for (adjacent in graph.getOrDefault(coordinate, listOf())) {
        if (!visited.contains(adjacent)) {
            counter += findTrailheadsDfs(matrix, graph, adjacent, useVisited, visited)
        }
    }
    return counter
}

fun matrixToTopographicMap(matrix: Array<Array<Int>>): TopographicMap {
    val graph = mutableMapOf<Coordinate, MutableList<Coordinate>>()
    val startingCoordinates = mutableListOf<Coordinate>()
    for (row in matrix.indices) {
        for (column in matrix[row].indices) {
            val coordinate = Coordinate(row, column)
            graph.putIfAbsent(coordinate, mutableListOf())
            for (direction in GraphSystem.availableDirections()) {
                if (row+direction.row >= 0 && row+direction.row < matrix.size
                    && column+direction.column >= 0 && column+direction.column < matrix[row].size
                    && matrix[row+direction.row][column+direction.column]-1 == matrix[row][column]) {
                    graph[coordinate]?.add(Coordinate(row+direction.row, column+direction.column))
                }
            }
            if (matrix[coordinate.row][coordinate.column] == TopographicMap.MIN_HEIGHT) {
                startingCoordinates.add(coordinate)
            }
        }
    }
    return TopographicMap(graph, startingCoordinates)
}