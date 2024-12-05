import java.io.File


fun main() {
    val (orderingPairs, printingOrders) = processInput("src/resources/day05/input.txt")
    val (orderingScore, invalidOrders) = getOrderingScoreAndInvalidOrders(orderingPairs, printingOrders)
    println("Total ordering score: $orderingScore")
    println("Total fixed invalid orders: ${getInvalidOrdersScore(orderingPairs, invalidOrders)}")
}

// Both solutions are O(p+n*m^2) where p is the orderingPairs number, n the number of orders and m their average length
// A good alternative would be using a graph
fun getInvalidOrdersScore(orderingPairs: List<Pair<Int, Int>>, printingOrders: List<IntArray>): Int {
    var accumulator = 0
    val beforeMap = mutableMapOf<Int, MutableSet<Int>>()

    for (pair in orderingPairs) {
        beforeMap.putIfAbsent(pair.second, mutableSetOf())
        beforeMap[pair.second]?.add(pair.first)
    }

    for (printingOrder in printingOrders) {
        for (i in printingOrder.indices) {
            for (j in i+1..< printingOrder.size) {
                if (beforeMap[printingOrder[i]]?.contains(printingOrder[j]) == true) {
                    val swap = printingOrder[i]
                    printingOrder[i] = printingOrder[j]
                    printingOrder[j] = swap
                }
            }
        }
        accumulator += printingOrder[(printingOrder.size / 2)]
    }

    return accumulator
}

fun getOrderingScoreAndInvalidOrders(orderingPairs: List<Pair<Int, Int>>, printingOrders: List<IntArray>): Pair<Int, List<IntArray>> {
    val beforeMap = mutableMapOf<Int, MutableSet<Int>>()
    var accumulator = 0
    val invalidOrders = mutableListOf<IntArray>()

    for (pair in orderingPairs) {
        beforeMap.putIfAbsent(pair.second, mutableSetOf())
        beforeMap[pair.second]?.add(pair.first)
    }

    order@for (printingOrder in printingOrders) {
        for (i in printingOrder.indices) {
            for (j in i+1..< printingOrder.size) {
                if (beforeMap[printingOrder[i]]?.contains(printingOrder[j]) == true) {
                    invalidOrders.add(printingOrder)
                    continue@order
                }
            }
        }
        accumulator += printingOrder[(printingOrder.size / 2)]
    }
    return Pair(accumulator, invalidOrders)
}


fun processInput(filePath: String): Pair<List<Pair<Int, Int>>, List<IntArray>> {
    val pairs = mutableListOf<Pair<Int, Int>>()
    val lists = mutableListOf<IntArray>()

    File(filePath).useLines { lines ->
        lines.forEach { line ->
            if (line.contains('|')) {
                val (first, second) = line.split("|").map { it.trim().toInt() }
                pairs.add(Pair(first, second))
            } else if (line.contains(',')) {
                val list = line.split(",").map { it.trim().toInt() }.toIntArray()
                lists.add(list)
            }
        }
    }

    return Pair(pairs, lists)
}