import java.io.File
import kotlin.math.abs

fun main() {
    val (firstList, secondList) = loadInputLists("src/input.txt")
    println("Total distance: ${calculateTotalSortedDistance(firstList, secondList)}")
}

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

fun parseListAsMap(firstList: List<Int>): MutableMap<Int, Int> {
    val asMap = mutableMapOf<Int, Int>();
    for (i in 0..< firstList.count()) {
        asMap[firstList[i]] = asMap.getOrPut(firstList[i]) { 0 } + 1
    }
    return asMap
}

fun loadInputLists(filePath: String): Pair<List<Int>, List<Int>> {
    val listA: MutableList<Int> = mutableListOf()
    val listB: MutableList<Int> = mutableListOf()
    File(filePath).useLines { lines ->
        lines.map { line ->
            val parts = line.trim().split("\\s+".toRegex())
            listA.add(parts[0].toInt())
            listB.add(parts[1].toInt())
        }.toList()
    }
    return Pair(listA, listB)
}