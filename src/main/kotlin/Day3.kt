import java.io.File

class Day3 {
    companion object {

        fun run(input: List<String>) {
            var sum = 0
            val list = mutableListOf<String>()
            var sum2 = 0

            input.forEach {
                sum += score(it)
                list.add(it.toSet().joinToString(""))
                if (list.size == 3) {
                    val s = list.joinToString().findTripleOccurrence()
                    if (s != null) {
                        sum2 += priority(s)
                    }
                    list.clear()
                }
            }
            println("Part one: $sum")
            println("Part two: $sum2")
        }

        fun score(line: String): Int {
            val lhs = line.substring(0, line.length / 2)
            val rhs = line.substring(line.length / 2, line.length)

            val el = lhs.toSet().firstOrNull { rhs.toSet().contains(it) } ?: 'a'
            return priority(el)
        }

        fun priority(el: Char) = if (el in 'a'..'z') el - 'a' + 1
        else el - 'A' + 27
    }
}

fun String.findTripleOccurrence(): Char? {
    if (isEmpty()) return null

    val s = toList().sorted()
    var last = 'a'
    var count = 0
    s.forEach {
        when {
            count == 0 -> {
                last = it
                count = 1
            }
            last == it -> {
                count++
                if (count == 3) {
                    return last
                }
            }
            else -> {
                last = it
                count = 1
            }
        }
    }
    return null
}


fun main() {
    val testInput = """vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw""".split('\n')

    println("Test input:")
    Day3.run(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day3input.txt").readLines()
    Day3.run(input)
}