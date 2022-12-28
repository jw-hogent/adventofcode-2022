import java.io.File

class Day24 {

    data class Point(val x: Int, val y: Int)
    enum class Direction(val marker: Char) {
        UP('^'), DOWN('v'), LEFT('<'), RIGHT('>')
    }

    data class Blizzard(val startLocation: Point, val d: Direction)

    class Valley(
        val topLeft: Point,
        val bottomRight: Point,
        val entrance: Point,
        val exit: Point,
        val blizzards: List<Blizzard>
    ) {
        fun toString(time: Int): String {
            // Valley
            val result = List(bottomRight.y + 2) { List(bottomRight.x + 2) { '.' } }
                // Borders
                .mapIndexed { y, l ->
                    l.mapIndexed { x, c -> if (x == 0 || y == 0 || x == bottomRight.x + 1 || y == bottomRight.y + 1) '#' else c }
                        .toMutableList()
                }

            // add entry and exit
            result[entrance.y][entrance.x] = '.'
            result[exit.y][exit.x] = '.'

            // add blizzards
            blizzards.forEach { b ->
                val pos = blizzardPositionAtTime(b, time)
                // add multiple blizzards
                val count = blizzards.count { blizzardPositionAtTime(it, time) == pos }
                if (count > 1) {
                    result[pos.y][pos.x] = '0' + count
                } else {
                    result[pos.y][pos.x] = b.d.marker
                }
            }

            return result.joinToString("\n") { it.joinToString("") }
        }

        fun availableMoves(p: Point, t: Int): List<Point> {
            return listOf(p, Point(p.x - 1, p.y), Point(p.x + 1, p.y), Point(p.x, p.y + 1), Point(p.x, p.y - 1))
                .filter { (it.x in topLeft.x..bottomRight.x && it.y in topLeft.y..bottomRight.y) || it == exit || it == entrance }
                .filter { isFree(it, t) }
        }

        fun blizzardPositionAtTime(it: Blizzard, time: Int): Point {
            var x = it.startLocation.x
            var y = it.startLocation.y
            when (it.d) {
                Direction.UP -> {
                    y = it.startLocation.y.minus(1).minus(time).mod(bottomRight.y).plus(1)
                }
                Direction.DOWN -> {
                    y = it.startLocation.y.minus(1).plus(time).mod(bottomRight.y).plus(1)
                }
                Direction.LEFT -> {
                    x = it.startLocation.x.minus(1).minus(time).mod(bottomRight.x).plus(1)
                }
                Direction.RIGHT -> {
                    x = it.startLocation.x.minus(1).plus(time).mod(bottomRight.x).plus(1)
                }
            }
            return Point(x, y)
        }

        fun isFree(p: Point, t: Int) = blizzards.filter {
            (it.d in listOf(Direction.LEFT, Direction.RIGHT) && it.startLocation.y == p.y) ||
                    (it.d in listOf(Direction.DOWN, Direction.UP) && it.startLocation.x == p.x)
        }.none { blizzardPositionAtTime(it, t) == p }

        companion object {

            fun parse(input: String): Valley {
                val asLines = input.split('\n')
                val entryX = asLines[0].indexOf('.')
                val topLeft = Point(1, 1)
                val bottomRight = Point(asLines.last().length - 2, asLines.size - 2)
                val exitX = asLines.last().indexOf('.')
                val blizzards = mutableListOf<Blizzard>()
                asLines.forEachIndexed { y, l ->
                    l.forEachIndexed { x, c ->
                        when (c) {
                            '>' -> blizzards.add(Blizzard(Point(x, y), Direction.RIGHT))
                            '<' -> blizzards.add(Blizzard(Point(x, y), Direction.LEFT))
                            '^' -> blizzards.add(Blizzard(Point(x, y), Direction.UP))
                            'v' -> blizzards.add(Blizzard(Point(x, y), Direction.DOWN))
                        }
                    }
                }
                return Valley(topLeft, bottomRight, Point(entryX, 0), Point(exitX, asLines.size - 1), blizzards)
            }
        }
    }

    companion object {
        fun run(input: String) {
            val valley = Valley.parse(input)
            var startPositions = mutableSetOf(valley.entrance)
            var t = 1
            var reachableAtT = valley.availableMoves(startPositions.first(), t).toMutableSet()
            while (valley.exit !in reachableAtT) {
                t++
//                println("time is $t")
                startPositions = reachableAtT
                reachableAtT = mutableSetOf()
                startPositions.forEach { reachableAtT.addAll(valley.availableMoves(it, t)) }
            }
            println("Part 1: ${t}")
        }

        fun run2(input: String) {
            val valley = Valley.parse(input)
            // smaller value is better
            var startPositions = mutableSetOf(valley.entrance)
            var t = 1
            var reachableAtT = valley.availableMoves(startPositions.first(), t).toMutableSet()
            while (valley.exit !in reachableAtT) {
                t++
                startPositions = reachableAtT
                reachableAtT = mutableSetOf()
                startPositions.forEach { reachableAtT.addAll(valley.availableMoves(it, t)) }
            }
            // we're now at exit, return, and to goal again
            startPositions = mutableSetOf(valley.exit)
            reachableAtT = valley.availableMoves(startPositions.first(), t).toMutableSet()
            while (valley.entrance !in reachableAtT) {
                t++
                startPositions = reachableAtT
                reachableAtT = mutableSetOf()
                startPositions.forEach { reachableAtT.addAll(valley.availableMoves(it, t)) }
            }
            startPositions = mutableSetOf(valley.entrance)
            reachableAtT = valley.availableMoves(startPositions.first(), t).toMutableSet()
            while (valley.exit !in reachableAtT) {
                t++
                startPositions = reachableAtT
                reachableAtT = mutableSetOf()
                startPositions.forEach { reachableAtT.addAll(valley.availableMoves(it, t)) }
            }
            println("Part 2: ${t}")
        }
    }
}

fun main() {
    val testInput = """
        #.######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
    """.trimIndent()

    println("Test input:")
    Day24.run(testInput)
    Day24.run2(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day24input.txt").readLines().joinToString("\n")
    Day24.run(input)
    Day24.run2(input)
}