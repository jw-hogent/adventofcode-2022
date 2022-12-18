import java.io.File
import kotlin.math.abs

class Day10 {

    companion object {
        fun generate(input: List<String>) = sequence {
            var value = 1

            for (i in input) {
                when (i) {
                    "noop" -> yield(value)
                    else -> {
                        val num = i.substring("addx ".length).toInt()
                        yield(value)
                        value += num
                        yield(value)
                    }
                }
            }
        }
        fun run(input: List<String>) {
            val indexes = listOf(20, 60, 100, 140, 180, 220).map{it.minus(2)}.toSet()

            generate(input).mapIndexed { idx, v ->
                if (indexes.contains(idx)) {
                    val myIdx = idx.plus(2)
                    val result = v * myIdx
                    println("v $v @ $myIdx -> $result")
                    result
                } else {
                    0
                }
            }.sum().let{println("sum $it")}
        }
        fun run1(input: List<String>) {
            var idx = 0
            val src = generate(input).iterator()
            var x = 1
            while (src.hasNext()) {
                print(if (abs((x % 40) - (idx % 40)) <= 1) '#' else '.')
                x = src.next()
                idx++
                if (idx % 40 == 0) println()
            }
            println()
        }
    }
}

fun main() {
    val testInput = """noop
addx 3
addx -5""".split('\n')

    Day10.generate(testInput).forEach{println(it)}

    val testInput2 = """addx 15
addx -11
addx 6
addx -3
addx 5
addx -1
addx -8
addx 13
addx 4
noop
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx -35
addx 1
addx 24
addx -19
addx 1
addx 16
addx -11
noop
noop
addx 21
addx -15
noop
noop
addx -3
addx 9
addx 1
addx -3
addx 8
addx 1
addx 5
noop
noop
noop
noop
noop
addx -36
noop
addx 1
addx 7
noop
noop
noop
addx 2
addx 6
noop
noop
noop
noop
noop
addx 1
noop
noop
addx 7
addx 1
noop
addx -13
addx 13
addx 7
noop
addx 1
addx -33
noop
noop
noop
addx 2
noop
noop
noop
addx 8
noop
addx -1
addx 2
addx 1
noop
addx 17
addx -9
addx 1
addx 1
addx -3
addx 11
noop
noop
addx 1
noop
addx 1
noop
noop
addx -13
addx -19
addx 1
addx 3
addx 26
addx -30
addx 12
addx -1
addx 3
addx 1
noop
noop
noop
addx -9
addx 18
addx 1
addx 2
noop
noop
addx 9
noop
noop
noop
addx -1
addx 2
addx -37
addx 1
addx 3
noop
addx 15
addx -21
addx 22
addx -6
addx 1
noop
addx 2
addx 1
noop
addx -10
noop
noop
addx 20
addx 1
addx 2
addx 2
addx -6
addx -11
noop
noop
noop""".split('\n')

    println("Test input 2:")
    Day10.run(testInput2)
    Day10.run1(testInput2)

    println("Input:")
    val realInput = File("src/main/kotlin/day10input.txt").readLines()

    Day10.run(realInput)
    Day10.run1(realInput)
}
