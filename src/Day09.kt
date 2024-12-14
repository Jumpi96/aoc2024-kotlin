import java.io.File
import java.util.PriorityQueue


fun main() {
    val input = File("src/resources/day09/input.txt")
        .readLines().first()
    val diskMap = processDiskMap(input)

    var startTime = System.currentTimeMillis()
    println("Checksum: ${defrag(diskMap)}")
    var executionTime = System.currentTimeMillis() - startTime
    println("\nExecution time: ${executionTime}ms (${executionTime/1000.0} seconds)")

    startTime = System.currentTimeMillis()
    println("Checksum whole files: ${defragWholeFiles(processDiskMapToStoredFiles(diskMap))}")
    executionTime = System.currentTimeMillis() - startTime
    println("\nExecution time: ${executionTime}ms (${executionTime/1000.0} seconds)")
}

class StoredFile(val start: Int, var size: Int, var fileId: Long?) {
    fun checksum(index: Int = start) =
        (0 ..< size).sumOf {
            (index + it) * (fileId ?: 0L)
        }
}

// A bit of help from: https://todd.ginsberg.com/post/advent-of-code/2024/day9/
fun defragWholeFiles(files: List<StoredFile>): Long {
    val freeSpace = files
        .filter { it.fileId == null }
        .groupBy({ it.size }, {it.start})
        .mapValues({ (_, v) -> PriorityQueue(v)})
        .toMutableMap()

    return files.filterNot { it.fileId == null }.reversed().sumOf { storedFile ->
        storedFile.checksum(freeSpace.findSpace(storedFile))
    }
}

private fun MutableMap<Int, PriorityQueue<Int>>.findSpace(storedFile: StoredFile): Int =
    (storedFile.size .. 9).mapNotNull { trySize ->
        if (this[trySize]?.isNotEmpty() == true) trySize to this.getValue(trySize).first()
        else null
    }.sortedBy { it.second }.filter { it.second < storedFile.start }.firstNotNullOfOrNull { (size, startAt) ->
        this[size]?.poll()
        if (size != storedFile.size) {
            val diff = size - storedFile.size
            computeIfAbsent(diff) { _ -> PriorityQueue() }.add(startAt + storedFile.size)
        }
        startAt
    } ?: storedFile.start

fun defrag(diskMap: List<Long?>): Long {
    val emptyBlocks = diskMap.indices.filter { diskMap[it] == null }.toMutableList()
    return diskMap.withIndex().reversed().sumOf { (index, value) ->
        if (value != null) {
            value * (emptyBlocks.removeFirstOrNull() ?: index)
        } else {
            emptyBlocks.removeLastOrNull()
            0
        }
    }
}

fun processDiskMap(input: String): List<Long?> =
    input
        .trim()
        .windowed(2, 2, true)
        .withIndex()
        .flatMap { (index, value) ->
            List(value.first().digitToInt()) { _ -> index.toLong() } +
                    List(value.getOrElse(1){ _ -> '0' }.digitToInt()) { null }
        }

fun processDiskMapToStoredFiles(diskMap: List<Long?>): List<StoredFile> = buildList {
    var blockStart = -1
    var previousValue: Long? = -1L
    diskMap.withIndex().forEach { (index, value) ->
        if (previousValue == -1L) {
            blockStart = index
            previousValue = value
        } else if (previousValue != value) {
            add(StoredFile(blockStart, index - blockStart, previousValue))
            blockStart = index
            previousValue = value
        }
    }
    if (blockStart != -1) {
        add(StoredFile(blockStart, diskMap.size - blockStart, previousValue))
    }
}
