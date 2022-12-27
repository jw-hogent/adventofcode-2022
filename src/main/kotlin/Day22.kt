import java.io.File
import kotlin.math.abs

class Day22 {
    enum class Direction(val value: Int) {
        RIGHT(0), DOWN(1), LEFT(2), UP(3),
    }

    data class Point(val x: Int, val y: Int) {
        fun between(l: Point, r: Point): Boolean {
            // only works when l and r share exactly one coordinate
            check(l.x == r.x || l.y == r.y)
            when {
                l.x == r.x && x == l.x -> {
                    return y in minOf(l.y, r.y)..maxOf(l.y, r.y)
                }
                l.y == r.y && y == l.y -> {
                    return x in minOf(l.x, r.x)..maxOf(l.x, r.x)
                }
                else -> return false
            }
        }
    }

    /**
     * Defines a side transition. Stepping over the a-b side, ends you up on
     * the c-d part. a-end maps to c, and b-end to d.
     */
    data class SidePortal(
        val a: Point, val b: Point, val c: Point, val d: Point, val srcDirection: Direction, val dstDirection: Direction
    ) {
        init {
            // either x or y must be the same
            check(a.x == b.x || a.y == b.y)
            check(c.x == d.x || c.y == d.y)

            // distance between points must always be 49
            check(abs(a.x - b.x) + abs(a.y - b.y) == 49)
            check(abs(c.x - d.x) + abs(c.y - d.y) == 49)
        }

        fun apply(p: Cursor): Cursor {
            if (p.direction != srcDirection) {
                return p
            }
            if (!Point(p.x, p.y).between(a, b)) {
                return p
            }
            when {
                p.x == a.x && p.x == b.x -> when {
                    c.x == d.x -> return Cursor(c.x, (p.y - a.y) * (d.y - c.y) / (b.y - a.y) + c.y, dstDirection)
                    c.y == d.y -> return Cursor((p.y - a.y) * (d.x - c.x) / (b.y - a.y) + c.x, c.y, dstDirection)
                }
                p.y == a.y && p.y == b.y -> when {
                    c.x == d.x -> return Cursor(c.x, (p.x - a.x) * (d.y - c.y) / (b.x - a.x) + c.y, dstDirection)
                    c.y == d.y -> return Cursor((p.x - a.x) * (d.x - c.x) / (b.x - a.x) + c.x, c.y, dstDirection)
                }
            }
            return p
        }
    }

    data class Board(val tiles: List<String>) {
        var sidePortals: List<SidePortal> = listOf()
        private val path = tiles.map { it.toMutableList() }

        override fun toString() = path.map { line ->
            line.joinToString("")
        }.joinToString("\n")

        fun move(c: Cursor, steps: Int): Cursor {
            var position = c
            var next = nextPosition(c)
            for (i in 1..steps) {
                // don't go forward if we encounter a #
                if (tiles[next.y][next.x] == '#') {
                    break
                }
                when (position.direction) {
                    Direction.RIGHT -> path[position.y][position.x] = '>'
                    Direction.DOWN -> path[position.y][position.x] = 'v'
                    Direction.LEFT -> path[position.y][position.x] = '<'
                    Direction.UP -> path[position.y][position.x] = '^'
                }
                position = next
                next = nextPosition(position)
            }
            return position
        }

        fun nextPosition(c: Cursor): Cursor {
            return if (sidePortals.isEmpty()) {
                nextPosition2d(c)
            } else {
                nextPositionCube(c)
            }
        }

        fun nextPosition2d(c: Cursor): Cursor {
            var x = c.x
            var y = c.y
            when (c.direction) {
                Direction.DOWN -> {
                    do {
                        y = y.plus(1).mod(tiles.size)
                    } while (tiles[y][x] !in "#.")
                }
                Direction.UP -> {
                    do {
                        y = y.minus(1).mod(tiles.size)
                    } while (tiles[y][x] !in "#.")
                }
                Direction.RIGHT -> {
                    do {
                        x = x.plus(1).mod(tiles[y].length)
                    } while (tiles[y][x] !in "#.")
                }
                Direction.LEFT -> {
                    do {
                        x = x.minus(1).mod(tiles[y].length)
                    } while (tiles[y][x] !in "#.")
                }
            }
            return Cursor(x, y, c.direction)
        }

        fun nextPositionCube(c: Cursor): Cursor {
            val next = when (c.direction) {
                Direction.RIGHT -> Cursor(c.x.plus(1), c.y, c.direction)
                Direction.DOWN -> Cursor(c.x, c.y.plus(1), c.direction)
                Direction.LEFT -> Cursor(c.x.minus(1), c.y, c.direction)
                Direction.UP -> Cursor(c.x, c.y.minus(1), c.direction)
            }
            sidePortals.forEach {
                val mapped = it.apply(next)
                if (mapped != next) {
                    check(tiles[mapped.y][mapped.x] in ".#")
                    return mapped
                }
            }
            check(tiles[next.y][next.x] in ".#")
            // did not portal
            return next
        }

        fun startPosition() = Cursor(tiles[0].indexOf('.'), 0, Direction.RIGHT)
    }


    data class Cursor(val x: Int, val y: Int, val direction: Direction) {
        fun turnLeft() = Cursor(
            x, y, when (direction) {
                Direction.UP -> Direction.LEFT
                Direction.RIGHT -> Direction.UP
                Direction.LEFT -> Direction.DOWN
                Direction.DOWN -> Direction.RIGHT
            }
        )

        fun turnRight() = Cursor(
            x, y, when (direction) {
                Direction.UP -> Direction.RIGHT
                Direction.RIGHT -> Direction.DOWN
                Direction.LEFT -> Direction.UP
                Direction.DOWN -> Direction.LEFT
            }
        )
    }

    companion object {
        fun run(input: List<String>) {
            val boardLines = input.subList(0, input.size - 2)
            val lineLength = boardLines.maxOf { it.length }
            val empty = List(lineLength, { ' ' })
            val board = Board(boardLines.mapIndexed { lineIdx, line ->
                empty.mapIndexed { i, c ->
                    boardLines[lineIdx].getOrElse(i) { c }
                }.joinToString("")
            })
            runPath(input.last(), board, board.startPosition())
        }

        fun runPart2(input: List<String>) {
            val boardLines = input.subList(0, input.size - 2)
            val lineLength = boardLines.maxOf { it.length }
            val empty = List(lineLength, { ' ' })
            val board = Board(boardLines.mapIndexed { lineIdx, line ->
                empty.mapIndexed { i, c ->
                    boardLines[lineIdx].getOrElse(i) { c }
                }.joinToString("")
            })
            // TODO: for my specific cube. I'm sure there is a way to construct this based on the input.
            val portals = listOf(
                SidePortal(
                    Point(100, 50), Point(149, 50), Point(99, 50), Point(99, 99), Direction.DOWN, Direction.LEFT
                ), SidePortal(
                    Point(100, 50), Point(100, 99), Point(100, 49), Point(149, 49), Direction.RIGHT, Direction.UP
                ), SidePortal(
                    Point(50, 150), Point(99, 150), Point(49, 150), Point(49, 199), Direction.DOWN, Direction.LEFT
                ), SidePortal(
                    Point(50, 150), Point(50, 199), Point(50, 149), Point(99, 149), Direction.RIGHT, Direction.UP
                ), SidePortal(
                    Point(49, 50), Point(49, 99), Point(0, 100), Point(49, 100), Direction.LEFT, Direction.DOWN
                ), SidePortal(
                    Point(0, 99), Point(49, 99), Point(50, 50), Point(50, 99), Direction.UP, Direction.RIGHT
                ), SidePortal(
                    Point(49, 0), Point(49, 49), Point(0, 149), Point(0, 100), Direction.LEFT, Direction.RIGHT
                ), SidePortal(
                    Point(-1, 100), Point(-1, 149), Point(50, 49), Point(50, 0), Direction.LEFT, Direction.RIGHT
                ), SidePortal(
                    Point(50, -1), Point(99, -1), Point(0, 150), Point(0, 199), Direction.UP, Direction.RIGHT
                ), SidePortal(
                    Point(-1, 150), Point(-1, 199), Point(50, 0), Point(99, 0), Direction.LEFT, Direction.DOWN
                ), SidePortal(
                    Point(150, 0), Point(150, 49), Point(99, 149), Point(99, 100), Direction.RIGHT, Direction.LEFT
                ), SidePortal(
                    Point(100, 100), Point(100, 149), Point(149, 49), Point(149, 0), Direction.RIGHT, Direction.LEFT
                ), SidePortal(
                    Point(100, -1), Point(149, -1), Point(0, 199), Point(49, 199), Direction.UP, Direction.UP
                ), SidePortal(
                    Point(0, 200), Point(49, 200), Point(100, 0), Point(149, 0), Direction.DOWN, Direction.DOWN
                )
            )
            // useful to debug
//            drawSidePortals(portals)
            board.sidePortals = portals
            runPath(input.last(), board, board.startPosition())
        }

//        private fun drawSidePortals(portals: List<SidePortal>) {
//            val chars = "0123456789ABCDEFG"
//            for (y in -1..200) {
//                val line = MutableList(152) { ' ' }
//                for (x in -1..150) {
//                    portals.forEachIndexed { i, portal ->
//                        if (Point(x, y).between(portal.a, portal.b)) {
//                            line[x.plus(1)] = chars[i]
//                        }
//                        // these overlap
////                        if (Point(x,y).between(portal.c, portal.d)) {
////                            line[x.plus(1)] = chars[i]
////                        }
//                    }
//                }
//                println(line.joinToString(""))
//            }
//        }

        private fun runPath(path: String, board: Board, startPosition: Cursor) {
            var position = startPosition
            var steps = 0
            for (i in path) {
                when (i) {
                    'R' -> {
                        position = board.move(position, steps)
                        print(steps)
                        steps = 0
                        position = position.turnRight()
                        print('R')
                    }
                    'L' -> {
                        position = board.move(position, steps)
                        print(steps)
                        steps = 0
                        position = position.turnLeft()
                        print('L')
                    }
                    else -> {
                        val digit = i - '0'
                        steps = steps * 10 + digit
                    }
                }
            }
            println(steps)
            position = board.move(position, steps)
            val column = position.x.plus(1)
            val row = position.y.plus(1)
            val facing = position.direction.value
            val password = listOf(row.times(1000), column.times(4), facing).sum()
            println(board)
            println("final password is: $password, row: $row, column: $column, facing: $facing")
        }
    }
}

fun main() {
    val testInput = """        ...#
        .#..
        #...
        ....
...#.......#
........#...
..#....#....
..........#.
        ...#....
        .....#..
        .#......
        ......#.

10R5L5R10L4R5L5""".split('\n')

    println("Test input:")
    Day22.run(testInput)
    // don't have the side portals worked out, so doesn't work
//    Day22.runPart2(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day22input.txt").readLines()
    Day22.run(input)
    // note: this only works for my shape
    Day22.runPart2(input)
}