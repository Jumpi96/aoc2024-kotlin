import java.io.File

fun main() {
    val matrix = File("src/resources/day04/input.txt")
        .readLines().map { it.toCharArray() }.toTypedArray()
    println("Total result for XMAS: ${searchWord(matrix, "XMAS")}")
    println("Total result for X-MAS: ${searchCross(matrix, "MAS")}")

}

// O(n^2*k) where k is the cross patterns
fun searchCross(input: Array<CharArray>, word: String): Int {
    if (word.length != 3) {
        return 0
    }

    var count = 0
    var y = 0

    val coordinates = listOf(
        Pair(Pair(-1, -1), Pair(1, -1)),
        Pair(Pair(-1, -1), Pair(-1, 1)),
        Pair(Pair(-1, 1), Pair(1, 1)),
        Pair(Pair(1, -1), Pair(1, 1))
    )

    fun isNotPartOfCross(x: Int, y: Int, ch: Char): Boolean {
        return x >= input[0].size || x < 0
                || y >= input.size || y < 0
                || ch != input[y][x]
    }

    while (y < input.size) {
        var x = 0
        while (x < input[0].size) {
            if (input[y][x] == word[1]) {
                for (coordinate in coordinates) {
                    val firstCharCoordinate = coordinate.first
                    val lastCharCoordinate = coordinate.second

                    if (isNotPartOfCross(
                            x + firstCharCoordinate.second,
                            y + firstCharCoordinate.first,
                            word.first())
                        || isNotPartOfCross(
                            x + firstCharCoordinate.second * -1,
                            y + firstCharCoordinate.first * -1,
                            word.last())
                        || isNotPartOfCross(
                            x + lastCharCoordinate.second,
                            y + lastCharCoordinate.first,
                            word.first())
                        || isNotPartOfCross(
                            x + lastCharCoordinate.second * -1,
                            y + lastCharCoordinate.first * -1,
                            word.last())
                        ) {
                        continue
                    }

                    count += 1
                }
            }
            x++
        }
        y++
    }

    return count
}


fun searchWord(input: Array<CharArray>, word: String): Int {
    var count = 0
    var y = 0

    val directions = (-1..1).flatMap { j ->
        (-1..1).mapNotNull { i ->
            if (i == 0 && j == 0) null
            else Pair(j, i)
        }
    }

    while (y < input.size) {
        var x = 0
        while (x < input[0].size) {
            if (input[y][x] == word[0]) {
                for (direction in directions) {
                    var index = 1
                    while (index < word.length) {
                        val yToCheck = y + direction.first * index
                        val xToCheck = x + direction.second * index

                        if (xToCheck >= input[0].size || xToCheck < 0
                                || yToCheck >= input.size || yToCheck < 0
                                || word[index] != input[yToCheck][xToCheck]) {
                            break
                        }
                        index++
                    }
                    if (index == word.length) {
                        count += 1
                    }
                }
            }
            x++
        }
        y++
    }
    return count
}
