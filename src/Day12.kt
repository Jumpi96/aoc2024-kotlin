import java.io.File


fun main() {
    val matrix = File("src/resources/day12/input.txt")
        .readLines().map { it -> it.toCharArray()}.toTypedArray()

    var startTime = System.currentTimeMillis()
    println("Fence cost: ${calculateFenceCost(matrix)}")
    var executionTime = System.currentTimeMillis() - startTime
    println("\nExecution time: ${executionTime}ms (${executionTime/1000.0} seconds)")
}

data class FenceSide(val fenceSideDirection: FenceSideDirection, val coordinates: Set<Coordinate>)
data class FenceSideDirection(val region: Char, val direction: Coordinate)

fun calculateFenceCost(matrix: Array<CharArray>): Int {
    val graphSystem = matrixToGraph(matrix)
    var accumulator = 0
    val visited = mutableSetOf<Coordinate>()

    for (startingCoordinate in graphSystem.graph.keys) {
        if (!visited.contains(startingCoordinate)) {
            val visitedBeforeRegion = visited.size
            val fenceSides = calculateFenceCostDfs(matrix, graphSystem.graph, startingCoordinate, visited)
            accumulator += fenceSides.values.sumOf { it.size } * (visited.size-visitedBeforeRegion)
        }
    }
    return accumulator
}

fun calculateFenceCostDfs(matrix: Array<CharArray>, graph: Map<Coordinate, List<Coordinate>>, coordinate: Coordinate,
                      visited: MutableSet<Coordinate>): MutableMap<FenceSideDirection, MutableSet<FenceSide>> {
    val fenceSidesMap = mutableMapOf<FenceSideDirection, MutableSet<FenceSide>>()
    visited.add(coordinate)

    val row = coordinate.row
    val column = coordinate.column
    for (direction in GraphSystem.availableDirections()) {
        if (!isValid(matrix, row+direction.row, column+direction.column)
                || matrix[row+direction.row][column+direction.column] != matrix[row][column]) {
            val fenceSideDirection = if (direction.row == 0) FenceSideDirection(matrix[row][column], direction)
                else FenceSideDirection(matrix[row][column], direction)

            var findFence = true
            fenceSidesMap.putIfAbsent(fenceSideDirection, mutableSetOf())
            for (fenceSide in fenceSidesMap.getOrDefault(fenceSideDirection, listOf())) {
                if (fenceSide.coordinates.contains(Coordinate(row, column))) {
                    findFence = false
                    break
                }
            }
            if (findFence) {
                fenceSidesMap[fenceSideDirection]?.add(findFenceSide(matrix, row, column, fenceSideDirection))
            }
        }
    }

    for (adjacent in graph.getOrDefault(coordinate, listOf())) {
        if (!visited.contains(adjacent)) {
            val responseMap = calculateFenceCostDfs(matrix, graph, adjacent, visited)
            for (key in responseMap.keys) {
                if (fenceSidesMap.containsKey(key)) {
                    fenceSidesMap[key] = ((fenceSidesMap[key]?.toMutableSet() ?: mutableSetOf())
                            + (responseMap[key]?.toMutableSet() ?: mutableSetOf())).toMutableSet()
                } else {
                    fenceSidesMap[key] = responseMap[key]?.toMutableSet() ?: mutableSetOf()
                }
            }
        }
    }
    return fenceSidesMap
}

fun findFenceSide(matrix: Array<CharArray>, startingRow: Int, startingColumn: Int, fenceSide: FenceSideDirection): FenceSide {
    val coordinates = mutableSetOf(Coordinate(startingRow, startingColumn))
    val direction = fenceSide.direction
    if (direction.column == 0) {
        for (modifier in -1..1 step 2) {
            var nextColumn = startingColumn + modifier
            while (isValid(matrix, startingRow, nextColumn) && matrix[startingRow][nextColumn] == fenceSide.region
                && (!isValid(matrix, startingRow+direction.row, nextColumn) || matrix[startingRow+direction.row][nextColumn] != fenceSide.region)) {
                coordinates.add(Coordinate(startingRow, nextColumn))
                nextColumn += modifier
            }
        }
    } else {
        for (modifier in -1..1 step 2) {
            var nextRow = startingRow + modifier
            while (isValid(matrix, nextRow, startingColumn) && matrix[nextRow][startingColumn] == fenceSide.region
                && (!isValid(matrix, nextRow, startingColumn+direction.column) || matrix[nextRow][startingColumn+direction.column] != fenceSide.region)) {
                coordinates.add(Coordinate(nextRow, startingColumn))
                nextRow += modifier
            }
        }
    }

    return FenceSide(fenceSide, coordinates)
}

fun isValid(matrix: Array<CharArray>, row: Int, column: Int): Boolean {
    return row >= 0 && row < matrix.size && column >= 0 && column < matrix.size
}

fun matrixToGraph(matrix: Array<CharArray>): GraphSystem {
    val graph = mutableMapOf<Coordinate, MutableList<Coordinate>>()

    for (row in matrix.indices) {
        for (column in matrix[row].indices) {
            val coordinate = Coordinate(row, column)
            graph.putIfAbsent(coordinate, mutableListOf())

            for (direction in GraphSystem.availableDirections()) {
                if (row+direction.row >= 0 && row+direction.row < matrix.size
                    && column+direction.column >= 0 && column+direction.column < matrix[row].size
                    && matrix[row+direction.row][column+direction.column] == matrix[row][column]) {
                    graph[coordinate]?.add(Coordinate(row+direction.row, column+direction.column))
                }
            }
        }
    }
    return GraphSystem(graph, listOf())
}