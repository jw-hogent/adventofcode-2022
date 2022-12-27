import java.io.File

class Day20 {

    // for some reason the numbers can occur multiple times
    data class Number(val value: Long, val originalIndex: Int)

    companion object {
        fun runPart1(input: List<String>) {
            val parsed = input.map{it.toLong()}.mapIndexed{i, v -> Number(v, i)}
            val asList = parsed.toMutableList()
            for (i in parsed) {
                // find i
                val pos = asList.indexOf(i)
                check(pos != -1)
                // move it
                asList.removeAt(pos)
                val newPos = pos.plus(i.value).mod(parsed.size - 1)
                if (newPos == 0) {
                    asList.add(i)
                } else {
                    asList.add(newPos, i)
                }
            }
            check(parsed.count{it.value == 0L} == 1)
            val zeroPos = asList.indexOfFirst { it.value == 0L }
            val groveCoordinates = listOf(1000, 2000, 3000).map{asList[zeroPos.plus(it).mod(parsed.size)]}
            val result = groveCoordinates.sumOf{it.value}
            println("part 1: $result, grove coordinates: $groveCoordinates")
        }
        fun runPart2(input: List<String>) {
            val parsed = input.map{it.toLong().times(811589153)}.mapIndexed{i, v -> Number(v, i)}
            val asList = parsed.toMutableList()

            for (mix in 0..9) {
                for (i in parsed) {
                    // find i
                    val pos = asList.indexOf(i)
                    check(pos != -1)
                    // move it
                    asList.removeAt(pos)
                    val newPos = pos.plus(i.value).mod(parsed.size - 1)
                    if (newPos == 0) {
                        asList.add(i)
                    } else {
                        asList.add(newPos, i)
                    }
                }
            }
            check(parsed.count{it.value == 0L} == 1)
            val zeroPos = asList.indexOfFirst { it.value == 0L }
            val groveCoordinates = listOf(1000, 2000, 3000).map{asList[zeroPos.plus(it).mod(parsed.size)]}
            val result = groveCoordinates.sumOf{it.value}
            println("part 2: $result, grove coordinates: $groveCoordinates")
        }
    }
}

fun main() {
    val testInput =
        """
            1
            2
            -3
            3
            -2
            0
            4
        """.trimIndent().split('\n')

    println("Test input:")
    Day20.runPart1(testInput)
    Day20.runPart2(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day20input.txt").readLines()
    Day20.runPart1(input)
    Day20.runPart2(input)
}