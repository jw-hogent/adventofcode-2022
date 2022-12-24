import java.io.File
import kotlin.experimental.or

class Day17 {
    class Block(
        val lines: List<Byte>,
        val height: Int,
        val width: Int,
        val xOffset: Int,
        val yOffset: Int,
    ) {

        fun drop() = Block(lines, height, width, xOffset, yOffset.minus(1))

        fun toLeft() = Block(lines, height, width, xOffset.minus(1), yOffset)

        fun toRight() = Block(lines, height, width, xOffset.plus(1), yOffset)

        companion object {
            fun create(lines: List<String>): Block {
                val width = lines.maxOf { it.lastIndexOf('#') }.plus(1)
                val height = lines.size
                val xOffset = 2 // starts two steps from the left
                val yOffset = 3 // starts 3 levels above top

                // maps '#.##' to b1101
                val chars = lines.map {
                    var c = 0
                    for (i in it.indices) {
                        if (it[i] == '#') {
                            c = c or 1.shl(i)
                        }
                    }
                    c.toByte()
                }

                return Block(chars.reversed(), height, width, xOffset, yOffset)
            }

        }
    }

    class Chamber(lines: List<Byte> = listOf()) {
        private val lines = lines.toMutableList()

        fun copy() = Chamber(lines.toList())

        fun height() = lines.size
        // note: first line is bottom line

        fun fits(b: Block): Boolean {
            if (b.xOffset < 0) {
                return false
            }
            if (b.xOffset + b.width > 7) {
                return false
            }
            if (b.yOffset >= 0) {
                return true
            }
            if (b.yOffset + lines.size < 0) {
                // hit the bottom
                return false
            }

            // check that bits for the block don't overlap (binary and) with existing lines
            return List(b.lines.size) { i ->
                val targetLine = lines.size + b.yOffset + i
                if (targetLine in lines.indices) {
                    (lines[targetLine].toInt() and b.lines[i].toInt().shl(b.xOffset)) == 0
                } else {
                    true
                }
            }.all { it }
        }

        fun add(b: Block) {
            val originalSize = lines.size
            if (b.yOffset + b.height > 0) {
                lines.addAll(List(b.yOffset + b.height) { 0 })
            }
            for (blockIndex in b.lines.indices) {
                val lineIndex = originalSize + blockIndex + b.yOffset
                lines[lineIndex] = lines[lineIndex].or(b.lines[blockIndex].shl(b.xOffset))
            }
        }

        override fun toString(): String {
            val res = StringBuilder()
            // from top to bottom
            for (line in lines.reversed()) {
                res.append('|')
                res.append(".......".mapIndexed { i, c -> if (1.shl(i).and(line.toInt()) != 0) '#' else c }
                    .joinToString(""))
                res.append('|')
                res.append('\n')
            }
            res.append("+-------+")
            return res.toString()
        }

        data class PeriodInfo(
            val period: Int,
            val bitsOn: Int,
        )

        fun findPeriod(): PeriodInfo? {
            // expect to repeat every n items
            // start at 1/4
            val startPos = lines.size / 4
            for (i in 1..startPos) {
                if (lines.subList(startPos, startPos + startPos) == lines.subList(
                        startPos + i, i + startPos + startPos
                    )
                ) {
                    println("repetition found after $i")
                    val bitson = lines.subList(startPos, startPos + i).sumOf { it.countOneBits() }
                    println("number of on bits: $bitson")
                    return PeriodInfo(period = i, bitsOn = bitson)
                }
            }
            return null
        }
    }

    class Runner(val winds: String) {
        var chamber = Chamber()
        var windIdx = 0
        var rockIdx = 0
        var rock = blocks[rockIdx]
        var stateIdx = 0
        private val states = listOf("wind", "fall")
        var numDropped = 0L

        fun run() {
            when (states[stateIdx]) {
                "wind" -> wind()
                "fall" -> fall()
            }
            stateIdx = stateIdx.plus(1).mod(states.size)
        }

        fun wind() {
            when (winds[windIdx]) {
                '>' -> {
                    val newRock = rock.toRight()
                    if (chamber.fits(newRock)) {
                        rock = newRock
                    }
                }
                '<' -> {
                    val newRock = rock.toLeft()
                    if (chamber.fits(newRock)) {
                        rock = newRock
                    }
                }
            }
            windIdx = windIdx.plus(1).mod(winds.length)
        }

        fun fall() {
            if (windIdx == 0 && rockIdx == 0) {
                println("num dropped when wind is 0: $numDropped, height: ${chamber.height()}, blockIdx: $rockIdx")
            }
            val newRock = rock.drop()
            if (chamber.fits(newRock)) {
                rock = newRock
            } else {
                numDropped++
                chamber.add(rock)
                rockIdx = rockIdx.plus(1).mod(blocks.size)
                rock = blocks[rockIdx]
            }
        }
    }

    companion object {
        val blocks = """
            ####

            .#.
            ###
            .#.

            ..#
            ..#
            ###

            #
            #
            #
            #

            ##
            ##
        """.trimIndent().split("\n\n").map { Block.create(it.split('\n')) }

        fun run(winds: String, numBlocks: Long) {
            val runner = Runner(winds)
            var skippedBlocks = 0L
            var skippedHeight = 0L
            while (true) {
                runner.run()

                // check only once every 100 blocks
                if (skippedBlocks == 0L && runner.numDropped.mod(100) == 0) {
                    val period = runner.chamber.findPeriod()
                    if (period != null) {
                        // There are 22 bits on for each set of 5 blocks (3 x 4 + 2 x 5)
                        check(period.bitsOn.mod(22) == 0)
//                        println("period info: $period")
                        val blocksPerPeriod = period.bitsOn.div(22).times(5)
                        skippedBlocks = numBlocks.minus(runner.numDropped).div(blocksPerPeriod).times(blocksPerPeriod)
                        val skippedPeriods = skippedBlocks.div(blocksPerPeriod)
                        skippedHeight = skippedPeriods * period.period
                        println("skipped height: $skippedHeight, skipped blocks: $skippedBlocks, skipped periods: $skippedPeriods")
                    }
                }

                if (runner.numDropped + skippedBlocks == numBlocks) {
                    println("Height after numBlocks: ${runner.chamber.height() + skippedHeight}")
                    break
                }
            }
        }
    }
}

private fun Byte.shl(xOffset: Int) = this.toInt().shl(xOffset).toByte()

fun main() {
    println("Test input:")
    val testInput = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"
    Day17.run(testInput, 2022)
    Day17.run(testInput, 1000000000000)

    println("Input:")
    val input = File("src/main/kotlin/day17input.txt").readLines()[0]
    Day17.run(input, 2022)
    Day17.run(input, 1000000000000)
}