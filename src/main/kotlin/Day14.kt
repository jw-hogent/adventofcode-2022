import java.io.File
import java.lang.Integer.max
import kotlin.math.abs

class Day14 {

    data class Point(val x: Int, val y: Int)

    companion object {

        fun run(input: List<String>) {
            var maxY = 0
            val occupied = mutableSetOf<Point>()

            input.forEach { line ->
                val corners = line.split(" -> ").map {
                    val (x: Int, y: Int) = it.split(",").map { coordinate -> coordinate.toInt() }
                    Point(x, y)
                }
                var cursor = corners.first()
                occupied.add(cursor)
                maxY = max(cursor.y, maxY)
                val iterator = corners.iterator()
                // skip first point
                iterator.next()
                while (iterator.hasNext()) {
                    val goal = iterator.next()
                    val direction = getDirection(cursor, goal)
                    while (cursor != goal) {
                        cursor = Point(cursor.x + direction.x, cursor.y + direction.y)
                        occupied.add(cursor)
                        maxY = max(cursor.y, maxY)
                    }
                }
            }

            var numGrains = 0
            val drop = { p: Point ->
                when {
                    !occupied.contains(Point(p.x, p.y.plus(1))) -> Point(p.x, p.y.plus(1))
                    !occupied.contains(Point(p.x.minus(1), p.y.plus(1))) -> Point(p.x.minus(1), p.y.plus(1))
                    !occupied.contains(Point(p.x.plus(1), p.y.plus(1))) -> Point(p.x.plus(1), p.y.plus(1))
                    else -> p
                }
            }
            val overflowing = { p: Point -> p.y >= maxY}
            while (true) {
                var grain = Point(500, 0)
                check(!occupied.contains(grain)) { "Input blocked" }
                var newPos = drop(grain)
                while (newPos != grain && !overflowing(newPos)) {
                    grain = newPos
                    newPos = drop(grain)
                }
                if (overflowing(newPos)) {
                    println("Overflowing at number of grains: $numGrains")
                    break
                }
                occupied.add(newPos)
                numGrains++
            }
            while (true) {
                var grain = Point(500, 0)
                if (occupied.contains(grain)) {
                    println("Input blocked at $numGrains")
                    break
                }
                var newPos = drop(grain)
                while (newPos != grain && newPos.y != maxY.plus(1)) {
                    grain = newPos
                    newPos = drop(grain)
                }
                occupied.add(newPos)
                numGrains++
            }
        }

        private fun getDirection(p1: Point, p2: Point): Point {
            check(p1 != p2) { "Can't determine direction when points are equal" }
            val norm = { x: Int -> x / abs(x) }
            return when {
                p1.x == p2.x -> Point(0, norm(p2.y - p1.y))
                p1.y == p2.y -> Point(norm(p2.x - p1.x), 0)
                else -> throw IllegalArgumentException("both dimensions are different")
            }
        }
    }
}

fun main() {
    println("Test input:")
    val testInput = """498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9""".split("\n")
    Day14.run(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day14input.txt").readLines()
    Day14.run(input)
}
