import java.io.File
import kotlin.math.min

fun main() {
    val corruptedBytesList = File("src/resources/day18/input.txt").useLines { lines ->
        lines.map { line ->
            val (x, y) = line.trim().split(",").map { it.toInt() }
            Coordinate(y, x)
        }.toList()
    }
    val corruptedGraph = corruptData(71, 71, corruptedBytesList, 1024)
    println("Min route with corrupted data is ${findRouteWithCorruptedData(corruptedGraph, corruptedGraph.startingCoordinates.first())}")
}


fun findRouteWithCorruptedData(graphSystem: GraphSystem, coordinate: Coordinate,
                               visited: MutableList<Coordinate> = mutableListOf()): Int? {
    var minRouteLength: Int? = null

    if (coordinate == graphSystem.endingCoordinates.first()) {
        return visited.size
    }

    println(visited)
    visited.add(coordinate)

    for (adjacent in graphSystem.graph.getOrDefault(coordinate, listOf())) {
        if (!visited.contains(adjacent)) {
            val responseLength = findRouteWithCorruptedData(graphSystem, adjacent, visited.toMutableList())
            if (responseLength != null && (minRouteLength == null || minRouteLength > responseLength)) {
                minRouteLength = responseLength
            }
        }
    }
    return minRouteLength
}


fun corruptData(xSize: Int, ySize: Int, corruptedBytes: List<Coordinate>, numberBytes: Int): GraphSystem {
    val graph = mutableMapOf<Coordinate, MutableList<Coordinate>>()

    for (row in 0..< ySize) {
        for (column in 0..< xSize) {
            val coordinate = Coordinate(row, column)
            if (!corruptedBytes.slice(0..< numberBytes).contains(coordinate)) {
                graph.putIfAbsent(coordinate, mutableListOf())

                for (direction in GraphSystem.availableDirections()) {
                    if (row+direction.row in 0..< ySize
                        && column+direction.column in 0..< xSize
                        && !corruptedBytes.slice(0..< numberBytes).contains(Coordinate(row+direction.row, column+direction.column))) {
                        graph[coordinate]?.add(Coordinate(row+direction.row, column+direction.column))
                    }
                }
            }
        }
    }
    return GraphSystem(graph, listOf(Coordinate(0, 0)), listOf(Coordinate(xSize-1, ySize-1)))
}