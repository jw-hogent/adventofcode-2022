import java.io.File

class Day18 {

    data class Position(val x: Int, val y: Int, val z: Int)

    data class BoundingBox(val min: Position, val max: Position) {

        fun grow() = BoundingBox(
            Position(min.x - 1, min.y - 1, min.z - 1),
            Position(max.x + 1, max.y + 1, max.z + 1),
        )

        fun isInside(p: Position) =
            p.x in min.x..max.x &&
                    p.y in min.y..max.y &&
                    p.z in min.z..max.z

        companion object {
            fun create(input: Iterable<Position>): BoundingBox {
                val min = Position(
                    input.minOf { it.x },
                    input.minOf { it.y },
                    input.minOf { it.z },
                )
                val max = Position(
                    input.maxOf { it.x },
                    input.maxOf { it.y },
                    input.maxOf { it.z },
                )
                return BoundingBox(min, max)
            }
        }
    }

    companion object {
        fun run(input: List<String>) {
            val cubes = parse(input).toSet()

            val sides = cubes.sumOf { generateSides(it).count { i -> i !in cubes } }
            println("Num sides: $sides")

            val outsideCubes = findExternalPointsWithinBBox(cubes, BoundingBox.create(cubes).grow())
            println("cubes: ${cubes.size}, outsideCubes: ${outsideCubes.size}")
            val surface = cubes.sumOf { generateSides(it).count { i -> i !in cubes && i in outsideCubes } }
            println("Exterior surface area: $surface")
        }

        fun generateSides(it: Position) = listOf(
            Position(it.x - 1, it.y, it.z),
            Position(it.x + 1, it.y, it.z),
            Position(it.x, it.y - 1, it.z),
            Position(it.x, it.y + 1, it.z),
            Position(it.x, it.y, it.z - 1),
            Position(it.x, it.y, it.z + 1),
        )

        fun findExternalPointsWithinBBox(cubes: Set<Position>, bbox: BoundingBox): MutableSet<Position> {
            val outside = findExteriorPoint(cubes)
            check(bbox.isInside(outside))
            check(outside !in cubes)
            val externalPoints = mutableSetOf(outside)
            while (true) {
                val newPoints = externalPoints.flatMap {
                    generateSides(it)
                        .filter { p -> p !in cubes }
                        .filter { p -> p !in externalPoints }
                        .filter { p -> bbox.isInside(p) }
                }
                if (newPoints.isEmpty()) {
                    break
                }
                externalPoints.addAll(newPoints)
            }
            return externalPoints
        }

        // just find the most extreme cube on one axis, any will do
        fun findExteriorPoint(cubes: Set<Position>) =
            cubes.minByOrNull { it.x }!!.let { Position(it.x - 1, it.y, it.z) }

        fun parse(input: List<String>) = input.map {
            val coordinates = it.split(',').map { it.toInt() }
            Position(coordinates[0], coordinates[1], coordinates[2])
        }
    }
}

fun main() {
    val testInput1 = """
        1,1,1
        2,1,1
    """.trimIndent().split('\n')
    val testInput2 = """
        2,2,2
        1,2,2
        3,2,2
        2,1,2
        2,3,2
        2,2,1
        2,2,3
        2,2,4
        2,2,6
        1,2,5
        3,2,5
        2,1,5
        2,3,5
    """.trimIndent().split('\n')
    println("Test input:")
    Day18.run(testInput1)
    Day18.run(testInput2)

    println("Input:")
    val input = File("src/main/kotlin/day18input.txt").readLines()
    Day18.run(input)
}