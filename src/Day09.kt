import java.io.File


fun main() {
    val input = File("src/resources/day09/simple_input.txt")
        .readLines().first().toList().map {x -> x.toString().toInt() }.toMutableList()

    var startTime = System.currentTimeMillis()
    println("Checksum: ${defrag(input)}")
    var executionTime = System.currentTimeMillis() - startTime
    println("\nExecution time: ${executionTime}ms (${executionTime/1000.0} seconds)")

    startTime = System.currentTimeMillis()
    println("Checksum whole files: ${defragWholeFiles(input)}")
    executionTime = System.currentTimeMillis() - startTime
    println("\nExecution time: ${executionTime}ms (${executionTime/1000.0} seconds)")
}

class StoredFile(val id: Int, var size: Int, var freeSpaceAfter: Int)

fun defragWholeFiles(diskMap: MutableList<Int>): Long {
    val files = processDiskMap(diskMap)
    // 6376613827167 too low
    // 9724294138806 too high
    // this doesn't work fully: 14113

    var fileIndex = files.size-1
    while (fileIndex >= 0) {
        val file = files[fileIndex]
        var posteriorFileIndex = 0
        while (posteriorFileIndex < fileIndex) {
            val posteriorFile = files[posteriorFileIndex]

            if (file.size <= posteriorFile.freeSpaceAfter) {
                val sizeDiff = posteriorFile.freeSpaceAfter - file.size
                files[fileIndex - 1].freeSpaceAfter += file.size + file.freeSpaceAfter
                files.add(posteriorFileIndex + 1, StoredFile(file.id, file.size, sizeDiff))
                posteriorFile.freeSpaceAfter = 0
                files.removeAt(fileIndex + 1)
                fileIndex++
                break
            }
            posteriorFileIndex++
        }
        fileIndex--
    }
    return calculateChecksum(files, true)
}

fun defrag(diskMap: MutableList<Int>): Long {
    val files = processDiskMap(diskMap)

    var minimumNonFilledIndex = 0

    var fileIndex = files.size-1
    while (fileIndex >= 0) {
        val file = files[fileIndex]
        var posteriorFileIndex = minimumNonFilledIndex
        while (posteriorFileIndex < fileIndex) {
            val posteriorFile = files[posteriorFileIndex]
            if (file.size > 0) {
                var count = 0
                while (file.size > 0 && posteriorFile.freeSpaceAfter > 0) {
                    posteriorFile.freeSpaceAfter--
                    file.size--
                    count++
                }
                if (count > 0) {
                    if (posteriorFile.freeSpaceAfter == 0) {
                        files.add(posteriorFileIndex+1, StoredFile(file.id, count, 0))
                        minimumNonFilledIndex = posteriorFileIndex+2
                    } else {
                        files.add(posteriorFileIndex+1, StoredFile(file.id, count, posteriorFile.freeSpaceAfter))
                        posteriorFile.freeSpaceAfter = 0
                        minimumNonFilledIndex = posteriorFileIndex+1
                    }
                    fileIndex++
                }
            } else {
                files.removeAt(fileIndex)
                break
            }
            posteriorFileIndex++
        }
        fileIndex--
    }
    return calculateChecksum(files, false)
}

fun calculateChecksum(files: List<StoredFile>, printFiles: Boolean): Long {
    var accumulator = 0L
    var index = 0
    for (file in files) {
        for (count in 0..< file.size) {
            accumulator += index * file.id
            if (printFiles) {
                print("[${file.id}]")
            }
            index++
        }
        index += file.freeSpaceAfter
        if (printFiles) {
            for (i in 1..file.freeSpaceAfter) {
                print('.')
            }
        }

    }
    println()
    return accumulator
}

fun processDiskMap(diskMap: List<Int>): MutableList<StoredFile> {
    val files = mutableListOf<StoredFile>()
    for (id in diskMap.indices step 2) {
        files.add(StoredFile(id/2, diskMap[id], if (id+1 < diskMap.size) diskMap[id+1] else 0))
    }
    return files
}