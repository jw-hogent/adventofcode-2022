import java.io.File

class Day1 {
    companion object {
        fun run(input: List<String>) {
            var list: MutableList<Int> = mutableListOf(0)

            input.forEach {
                if (it == "") {
                    list.add(0)
                } else {
                    list[list.size - 1] = list.last() + it.toInt()
                }
            }

            println("Part one: ${list.maxOrNull()}")
            val sorted = list.sorted()
            println("Part two: ${sorted.takeLast(3).sum()}")
        }
    }
}

fun main(args: Array<String>) {
    val testInput = """1000
2000
3000

4000

5000
6000

7000
8000
9000

10000""".split("\n")

    println("Test input:")
    Day1.run(testInput)

    val input = File("src/main/kotlin/day1input.txt").readLines()

    println("Input:")
    Day1.run(input)
}