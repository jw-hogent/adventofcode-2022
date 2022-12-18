import java.io.File
import kotlin.math.abs

class Day9 {
    class Point(var x: Int, var y: Int)

    class Snake(numPieces: Int) {
        private val knots = mutableListOf<Point>()

        init {
            for (i in 1..numPieces) {
                knots.add(Point(0, 0))
            }
        }

        private fun head() = knots.first()
        fun tail() = knots.last()
        fun move(direction: Char) {
            when (direction) {
                'R' -> head().x++
                'U' -> head().y--
                'L' -> head().x--
                'D' -> head().y++
                else -> throw java.lang.IllegalStateException("$direction is not a direction")
            }
            for (knot in 1..knots.size.minus(1)) {
                val prev = knots[knot.minus(1)]
                val hx = prev.x
                val hy = prev.y
                var tx = knots[knot].x
                var ty = knots[knot].y

                when {
                    hx == tx -> {
                        if (abs(hy - ty) > 1) {
                            ty += norm(hy - ty)
                        }
                    }
                    hy == ty -> {
                        if (abs(hx - tx) > 1) {
                            tx += norm(hx - tx)
                        }
                    }
                    else -> {
                        if (abs(hx - tx) > 1 || abs(hy - ty) > 1) {
                            tx += norm(hx - tx)
                            ty += norm(hy - ty)
                        }
                    }
                }
                knots[knot].x = tx
                knots[knot].y = ty
            }
        }
    }

    companion object {
        fun run(input: List<String>) {
            println("Part one: ${positionsVisited(input, 2)}")
            println("Part two: ${positionsVisited(input, 10)}")
        }

        private fun positionsVisited(input: List<String>, maxDistance: Int): Int {
            val visited = mutableSetOf<String>()

            val s = Snake(maxDistance)
            for (line in input) {
                val direction = line[0]
                val steps = line.substring(2).toInt()
                for (i in 1..steps) {
                    s.move(direction)
                    visited.add("${s.tail().x}|${s.tail().y}")
                }
            }
            return visited.size
        }

        fun norm(x: Int): Int {
            if (x == 0) {
                throw java.lang.IllegalArgumentException("should not be 0 here")
            }
            return x / abs(x)
        }
    }
}

fun main() {
    val testInput = """R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2""".split('\n')

    println("Test input:")
    Day9.run(testInput)

    val testInput2 = """R 5
U 8
L 8
D 3
R 17
D 10
L 25
U 20""".split('\n')

    println("Larger test input:")
    Day9.run(testInput2)

    println("Input:")
    val realInput = File("src/main/kotlin/day9input.txt").readLines()
    Day9.run(realInput)
}
