import java.io.File
import java.util.PriorityQueue
import kotlin.math.absoluteValue
import kotlin.math.min

class Day12 {

    data class Point(val x: Int, val y: Int) {
        fun distance(other: Point): Int {
            return (x - other.x).absoluteValue + (y - other.y).absoluteValue
        }
        override fun toString(): String {
            return "($x, $y)"
        }
    }

    class Path(val points: List<Point>) {
        fun head() = points.last()

        fun to(p: Point): Path {
            return Path(points + listOf(p))
        }

        override fun toString() = "[${points.size}]->(${head().x}, ${head().y})"
    }

    class PathFinder(val input: List<String>){
        private val goal = findChar('E')
        private val start = findChar('S')
        private val visited = hashSetOf<Point>()
        private val orderedPaths = PriorityQueue<Path> { o1, o2 ->
            o1.points.size + o1.head().distance(goal) -
                    (o2.points.size + o2.head().distance(goal))
        }

        private fun findChar(c: Char): Point {
            for (y in input.indices) {
                for (x in 0 until input[y].length) {
                    if (input[y][x] == c) {
                        return Point(x, y)
                    }
                }
            }
            throw IllegalStateException("$c not found in map")
        }
        fun run(realStart: Point? = null): Int {
            orderedPaths.add(Path(listOf(realStart ?: start)))
            while (orderedPaths.isNotEmpty() && orderedPaths.peek().head() != goal) {
                val current = orderedPaths.poll()
                visited.add(current.head())
                for (i in possiblePaths(current.head())) {
                    if (i !in visited) {
                        orderedPaths.add(current.to(i))
                    }
                }
            }
            if (orderedPaths.isEmpty()) {
                throw IllegalStateException("No path found")
            }
            return orderedPaths.peek().points.size - 1
        }
        private fun possiblePaths(p: Point): List<Point> {
            val res = mutableListOf<Point>()
            val directions = listOf(Point(-1, 0), Point(1, 0), Point(0, -1), Point(0, 1))
            for (d in directions) {
                if (d.x + p.x !in 0 until input[0].length) {
                    continue
                }
                if (d.y + p.y !in input.indices) {
                    continue
                }
                val curHeight = height(p.x, p.y)
                val dstHeight = height(d.x + p.x, d.y + p.y)
                if (dstHeight - curHeight <= 1) {
                    res.add(Point(d.x + p.x, d.y + p.y))
                }
            }
            return res
        }
        private fun height(x: Int, y: Int): Int {
            return when(val h = input[y][x]) {
                'S' -> 0
                'E' -> 'z' - 'a'
                else -> h - 'a'
            }
        }
    }

    companion object {
        fun run(input: List<String>) {
            println("Part one: ${PathFinder(input).run()}")

            var best = Int.MAX_VALUE
            for (y in input.indices) {
                for (x in 0 until input[y].length) {
                    try {
                        if (input[y][x] == 'a') {
                            best = min(best, PathFinder(input).run(Point(x, y)))
                        }
                    } catch (e: IllegalStateException) {
                        // nvm
                    }
                }
            }

            println("Part two: $best")
        }
    }
}

fun main() {
    println("Test input:")
    val testInput = """Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi""".split('\n')
    Day12.run(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day12input.txt").readLines()

    Day12.run(input)
}
