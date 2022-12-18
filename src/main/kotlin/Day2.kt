import java.io.File

class Day2 {
    companion object {
        fun run(lines: List<String>) {
            var sum = 0
            var sum2 = 0

            lines.forEach {
                sum += score(it)
                sum2 += score2(it)
            }
            println("Part one: $sum")
            println("Part two: $sum2")
        }

        private fun score(line: String): Int {
            val split = line.split(' ')
            val them = split[0]
            val us = split[1]
            return when (them) {
                "A" -> when (us) {
                    "X" -> 1 + 3
                    "Y" -> 2 + 6
                    else -> 3 + 0
                }
                "B" -> when (us) {
                    "X" -> 1 + 0
                    "Y" -> 2 + 3
                    else -> 3 + 6
                }
                else -> when (us) {
                    "X" -> 1 + 6
                    "Y" -> 2 + 0
                    else -> 3 + 3
                }
            }
        }

        private fun score2(line: String): Int {
            val split = line.split(' ')
            val them = split[0]
            val us = split[1]
            return when (them) {
                "A" -> when (us) {
                    "X" -> 3 + 0
                    "Y" -> 1 + 3
                    else -> 2 + 6
                }
                "B" -> when (us) {
                    "X" -> 1 + 0
                    "Y" -> 2 + 3
                    else -> 3 + 6
                }
                else -> when (us) {
                    "X" -> 2 + 0
                    "Y" -> 3 + 3
                    else -> 1 + 6
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val testInput = """A Y
B X
C Z""".split("\n")

    println("Test input:")
    Day2.run(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day2input.txt").readLines()
    Day2.run(input)
}