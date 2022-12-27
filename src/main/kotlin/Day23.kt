import java.io.File

class Day23 {
    data class Point(val x: Int, val y: Int)

    data class BBox(val top: Int, val bottom: Int, val left: Int, val right: Int) {
        operator fun contains(p: Point) = p.x in left .. right && p.y in top .. bottom
        fun add(p: Point) = BBox(minOf(top, p.y), maxOf(bottom, p.y), minOf(p.x, left), maxOf(p.x, right))
        fun area() = (bottom - top + 1) * (right - left + 1)

        companion object{
            fun of(points: Set<Point>): BBox {
                check(points.isNotEmpty())
                return BBox(
                    points.minOf{it.y},
                    points.maxOf{it.y},
                    points.minOf{it.x},
                    points.maxOf{it.x}
                )
            }
        }
    }

    enum class Direction {
        N,
        S,
        W,
        E
    }

    class Elves(val locations: Set<Point>, val directions: List<Direction>) {
        fun run(): Elves {
            // first part
            val proposedLocations = mutableMapOf<Point, Int>()
            locations.forEach {
                val newLocation = proposeLocation(it)
                proposedLocations[newLocation] = (proposedLocations[newLocation] ?: 0) + 1
            }

            // second part
            val newLocations = mutableListOf<Point>()
            locations.forEach {
                val newLocation = proposeLocation(it)
                if ((proposedLocations[newLocation] ?: 0) <= 1) {
                    newLocations.add(newLocation)
                } else {
                    newLocations.add(it)
                }
            }
            val newDirections = directions.subList(1, directions.size) + directions.subList(0, 1)
            return Elves(newLocations.toSet(), newDirections)
        }

        fun proposeLocation(p: Point): Point {
            val adjacentPositions = listOf(-1, 0, 1).flatMap { x ->
                listOf(-1, 0, 1).map { y ->
                    Point(p.x + x, p.y + y)
                }
            }.filterNot { it == p }

            if (adjacentPositions.intersect(locations).isEmpty()) {
                return p
            }

            directions.forEach {
                when (it) {
                    Direction.N -> {
                        if (adjacentPositions.filter { it.y == p.y - 1 }.intersect(locations).isEmpty()) {
                            return Point(p.x, p.y - 1)
                        }
                    }
                    Direction.S -> {
                        if (adjacentPositions.filter { it.y == p.y + 1 }.intersect(locations).isEmpty()) {
                            return Point(p.x, p.y + 1)
                        }
                    }
                    Direction.W -> {
                        if (adjacentPositions.filter { it.x == p.x - 1 }.intersect(locations).isEmpty()) {
                            return Point(p.x - 1, p.y)
                        }
                    }
                    Direction.E -> {
                        if (adjacentPositions.filter { it.x == p.x + 1 }.intersect(locations).isEmpty()) {
                            return Point(p.x + 1, p.y)
                        }
                    }
                }
            }
            // no clear next position found: do nothing
            return p
        }

        fun toString(bbox: BBox): String {
            val result = StringBuilder()
            for (y in bbox.top .. bbox.bottom) {
                for (x in bbox.left..bbox.right) {
                    result.append(if (Point(x, y) in locations) '#' else '.')
                }
                result.append('\n')
            }
            return result.toString().trimEnd()
        }

        fun bbox() = BBox.of(locations)

        fun emptyGroundTilesIn(bbox: BBox) = bbox.area() - locations.filter{it in bbox}.size
    }

    companion object {
        fun createElves(input: String): Elves {
            val locs = mutableListOf<Point>()
            input.split('\n').forEachIndexed { row, s ->
                s.forEachIndexed { col, c -> if (c == '#') locs.add(Point(col, row)) }
            }
            return Elves(locs.toSet(), listOf(Direction.N, Direction.S, Direction.W, Direction.E))
        }

        fun run(input: String, times: Int) {
            val elves = createElves(input)
            var newElves = elves
            for (i in 0 until times) {
                newElves = newElves.run()
            }
            val emptyGroundTiles = newElves.emptyGroundTilesIn(newElves.bbox())
            println("Found $emptyGroundTiles empty ground tiles")
        }

        fun run2(input: String) {
            var elves = createElves(input)
            var newElves = elves.run()
            var numRuns = 1
            while (elves.locations != newElves.locations) {
                numRuns++
                elves = newElves
                newElves = newElves.run()
            }
            println("Needed $numRuns runs until all elves stopped moving")
        }
    }
}

fun main() {
    val testInput = """
        ..............
        ..............
        .......#......
        .....###.#....
        ...#...#.#....
        ....#...##....
        ...#.###......
        ...##.#.##....
        ....#..#......
        ..............
        ..............
        ..............
    """.trimIndent()

    println("Test input:")
    Day23.run(testInput, 10)
    Day23.run2(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day23input.txt").readLines().joinToString("\n")
    Day23.run(input, 10)
    Day23.run2(input)
}