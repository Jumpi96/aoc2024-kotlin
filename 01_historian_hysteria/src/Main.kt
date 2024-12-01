import java.io.File
import kotlin.math.abs

fun main() {
    val (firstList, secondList) = loadInputLists("src/input.txt")
    println("Total distance: ${calculateTotalSortedDistance(firstList, secondList)}")

    println("Similarity score: ${calculateSimilarityScore(firstList, secondList)}")
}

// O(n) time complexity solution. Optimisation on solution with sorting.
fun calculateTotalSortedDistance(firstList: List<Int>, secondList: List<Int>): Int {
    val firstCountersMap = parseListAsMap(firstList)
    val secondCountersMap = parseListAsMap(secondList)

    var total = 0
    var i = 0
    var j = 0
    while (firstCountersMap.isNotEmpty()) {
        while (!firstCountersMap.containsKey(i)) {
            i++
        }
        if (firstCountersMap.containsKey(i)) {
            firstCountersMap[i] = firstCountersMap.getOrDefault(i, 0) - 1
            if (firstCountersMap[i] == 0) firstCountersMap.remove(i)

            while (!secondCountersMap.containsKey(j)) {
                j++
            }
            secondCountersMap[j] = secondCountersMap.getOrDefault(j, 0) - 1
            if (secondCountersMap[j] == 0) secondCountersMap.remove(j)

            total += abs(j-i)
        }
    }

    return total
}

fun calculateSimilarityScore(firstList: List<Int>, secondList: List<Int>): Int {
    val secondCountersMap: Map<Int, Int> = parseListAsMap(secondList)
    var similarity = 0

    for (number in firstList) {
        secondCountersMap[number]?.let { count ->
            similarity += number * count
        }
    }
    return similarity
}

fun parseListAsMap(list: List<Int>): MutableMap<Int, Int> {
    return list.groupingBy { it }.eachCount().toMutableMap()
}

fun loadInputLists(filePath: String): Pair<List<Int>, List<Int>> {
    val (listA, listB) = File(filePath).useLines { lines ->
        lines.map { line ->
            val (first, second) = line.trim().split("\\s+".toRegex())
            Pair(first.toInt(), second.toInt())
        }.unzip()
    }
    return Pair(listA, listB)
}