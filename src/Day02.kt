import java.io.File
import kotlin.math.abs

fun main() {
    val reports = loadInput("src/resources/day02/input.txt")
    var count = 0
    for (report in reports) {
        count += (if (isReportSafe(report)) 1 else 0)
    }
    println("Safe reports: $count")
}

// O(m^m) time complexity solution.
// Potential improvements: avoid slicing and memoization/dynamic programming.
fun isReportSafe(report: List<Int>, wildcard: Boolean = true): Boolean {
    return when (report.size) {
        0 -> false
        1 -> true
        else -> {
            if (wildcard && report[1] == report[0]) {
                return isReportSafe(report.subList(1, report.size), false)
            }

            val asc = if (report[1] > report[0]) {
                true
            } else if (report[1] < report[0]) {
                false
            } else {
                return false
            }

            for (i in 1..< report.size) {
                if (!isSafeDifference(report[i], report[i-1])
                    || ((asc && report[i] < report[i - 1]) || (!asc && report[i] > report[i - 1]))
                ) {
                    if (wildcard) {
                        for (j in report.indices) {
                            if (isReportSafe(report.slice(report.indices.filter { it != j }), false)) {
                                return true
                            }
                        }
                    }
                    return false
                }
            }
            return true
        }
    }
}

fun isSafeDifference(x: Int, y: Int): Boolean = abs(x-y) in 1..3


fun loadInput(filePath: String): List<List<Int>> {
    return File(filePath).useLines { lines ->
        lines.map { line ->
            line.trim().split("\\s+".toRegex()).map { it.toInt() }
        }.toList()
    }
}