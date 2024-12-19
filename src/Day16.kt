

fun main() {

}


fun matrixToReindeerGraph(matrix: Array<CharArray>): GraphSystem {
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