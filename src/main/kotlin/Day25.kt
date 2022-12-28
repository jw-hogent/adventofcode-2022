import java.io.File

class Day25 {

    companion object {
        fun fromSNAFU(s: String): Long {
            var result = 0L
            s.forEach { c ->
                val value = when (c) {
                    '2' -> 2
                    '1' -> 1
                    '0' -> 0
                    '-' -> -1
                    '=' -> -2
                    else -> throw IllegalStateException()
                }
                result = result * 5 + value
            }
            return result
        }
        fun toSNAFU(input: String): String {
            var value = input.toLong()
            val result = mutableListOf<Char>()
            while (value != 0L) {
                val remainder = value.plus(2).mod(5)
                value = value.plus(2).div(5)
                when (remainder.minus(2)) {
                    -2 -> result.add('=')
                    -1 -> result.add('-')
                    0 -> result.add('0')
                    1 -> result.add('1')
                    2 -> result.add('2')
                }
            }
            return result.reversed().joinToString("")
        }
        fun run(input: String) {
            val decimal = input.split('\n').sumOf{ fromSNAFU(it)}
            val snafu = toSNAFU(("$decimal"))
            println("Part 1: decimal: $decimal, SNAFU: $snafu")
        }

//        fun run2(input: String) {
//        }
    }
}

fun main() {
    val testInput = """
        1=-0-2
        12111
        2=0=
        21
        2=01
        111
        20012
        112
        1=-1=
        1-12
        12
        1=
        122
    """.trimIndent()

    println("Test input:")
    Day25.run(testInput)
//    Day25.run2(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day25input.txt").readLines().joinToString("\n")
    Day25.run(input)
//    Day25.run2(input)
}