import java.io.File

const val NUMBER_OF_BLINKS = 75
const val YEAR_MULTIPLIER = 2024

fun main() {
    val stones = File("src/resources/day11/input.txt").readLines().first()
        .split(" ").map { x -> x.toLong() }.toList()
    val cache = mutableMapOf<Long, MutableMap<Int, Long>>()

    var startTime = System.currentTimeMillis()
    println("${blinkAndCount(stones, NUMBER_OF_BLINKS, cache)}")
    var executionTime = System.currentTimeMillis() - startTime
    println("\nExecution time: ${executionTime}ms (${executionTime/1000.0} seconds)")
}

fun blinkAndCount(stones: List<Long>, n: Int, cache: MutableMap<Long, MutableMap<Int, Long>>): Long {
    if (stones.size == 1) {
        if (cache[stones[0]]?.containsKey(n) == true) {
            return cache[stones[0]]?.get(n) ?: 0
        }
        val stoneAsString = stones[0].toString()
        if (stones[0] == 0L) {
            if (n == 1) {
                return 1
            }
            return saveAndReturn(cache, stones[0], n, blinkAndCount(listOf(1L), n-1, cache))
        } else if (stoneAsString.length % 2 == 0) {
            val firstStone = stoneAsString.slice(0..<stoneAsString.length / 2).toLong()
            val secondStone = stoneAsString.slice(stoneAsString.length / 2..<stoneAsString.length).toLong()

            if (n == 1) {
                return 2
            }
            return saveAndReturn(cache, stones[0], n, blinkAndCount(listOf(firstStone), n-1, cache) +
                    blinkAndCount(listOf(secondStone), n-1, cache))
        } else {
            if (n == 1) {
                 return 1
            }
            return saveAndReturn(cache, stones[0], n, blinkAndCount(listOf(stones[0]*YEAR_MULTIPLIER), n-1, cache))
        }
    }
    return blinkAndCount(listOf(stones.first()), n, cache) +
            blinkAndCount(stones.slice(1..< stones.size), n, cache)
}

fun saveAndReturn(cache: MutableMap<Long, MutableMap<Int, Long>>, value: Long, n: Int, result: Long): Long {
    cache.putIfAbsent(value, mutableMapOf())
    cache[value]?.put(n, result)
    return cache[value]?.get(n) ?: 0
}

