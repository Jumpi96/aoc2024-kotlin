import java.io.File


const val NUMBER_OF_BLINKS = 25
const val YEAR_MULTIPLIER = 2024

fun main() {
    val stones = File("src/resources/day11/input.txt").readLines().first()
        .split(" ").map { x -> x.toLong() }.toMutableList()
    println("${blink(stones, NUMBER_OF_BLINKS).size}")
}

fun blink(stones: MutableList<Long>, n: Int): List<Long> {
    for (i in 1.. n) {
        var stoneIndex = 0
        while (stoneIndex < stones.size) {
            val stoneAsString = stones[stoneIndex].toString()
            if (stones[stoneIndex] == 0L) {
                stones[stoneIndex] = 1
            } else if (stoneAsString.length % 2 == 0) {
                stones[stoneIndex] = stoneAsString.slice(0..< stoneAsString.length/2).toLong()
                stones.add(stoneIndex+1, stoneAsString.slice(stoneAsString.length/2..< stoneAsString.length).toLong())
                stoneIndex++
            } else {
                stones[stoneIndex] = stones[stoneIndex] * YEAR_MULTIPLIER
            }
            stoneIndex++
        }
    }
    return stones
}

