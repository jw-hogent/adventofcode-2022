import java.io.File

class Day4 {
    companion object {
        fun run(input: List<String>) {
            val count = input.map{it.trim()}.map {
                if (hasOverlap(it)) 1 else 0
            }.sum()

            println("Part one: $count")

            val part2 = input.map{it.trim()}.map {
                if (hasOverlap2(it)) 1 else 0
            }.sum()

            println("Part two: $part2")
        }
        fun parse(line: String) : List<Int> {
            val (lhs, rhs) = line.split(',')
            val (firstStart, firstEnd) = lhs.split('-').map{it.toInt()}
            val (secondStart, secondEnd) = rhs.split('-').map{it.toInt()}
            return listOf(firstStart, firstEnd, secondStart, secondEnd)
        }
        fun hasOverlap(line: String) : Boolean{
            val (firstStart, firstEnd, secondStart, secondEnd) = parse(line)
            return when {
                firstStart in secondStart .. secondEnd &&
                        firstEnd in secondStart .. secondEnd -> true
                secondStart in firstStart .. firstEnd &&
                        secondEnd in firstStart .. firstEnd -> true
                else -> false
            }
        }
        fun hasOverlap2(line: String) : Boolean{
            val (firstStart, firstEnd, secondStart, secondEnd) = parse(line)
            return firstStart in secondStart .. secondEnd ||
                    firstEnd in secondStart .. secondEnd ||
                    secondStart in firstStart .. firstEnd ||
                    secondEnd in firstStart .. firstEnd
        }
    }
}


fun main() {
    val testInput = """2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8""".split('\n')

    println("Test input:")
    Day4.run(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day4input.txt").readLines()
    Day4.run(input)
}