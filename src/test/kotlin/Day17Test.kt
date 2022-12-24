import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

internal class Day17Test {

    @Test
    fun `Empty chamber as string`() {
        assertEquals("+-------+", Day17.Chamber().toString())
    }

    @Test
    fun `Chamber with new block`() {
        val expected = """
            |..@@@@.|
            |.......|
            |.......|
            |.......|
            +-------+
        """.trimIndent().map { if (it == '@') '#' else it }.joinToString("")
        val c = Day17.Chamber()
        c.add(Day17.Block.create(listOf("####")))
        assertEquals(expected, c.toString())
    }

//    @Test
//    fun `the blocks are ok`() {
//        val block = """
//            ..#
//            ..#
//            ###
//        """.trimIndent()
//
//        assertEquals(block, Day17.blocks[2].lines.joinToString("\n"))
//    }

    @Test
    fun `chamber with asymmetric block`() {
        val block = """
            ..#
            ..#
            ###
        """.trimIndent()

        val expected = """
            |....@..|
            |....@..|
            |..@@@..|
            |.......|
            |.......|
            |.......|
            +-------+
        """.trimIndent().map { if (it == '@') '#' else it }.joinToString("")
        val c = Day17.Chamber()
        c.add(Day17.Block.create(block.split('\n')))
        assertEquals(expected, c.toString())
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16])
    fun `check with given states`(iterations: Int) {
        val runner = Day17.Runner(">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>")
        var verificationState = iterations
        if (verificationState >= 8) verificationState++
        if (verificationState >= 16) verificationState++
        val givenState = File("src/main/kotlin/day17verificationData.txt")
            .readLines()
            .joinToString("\n").split("\n\n")
            .get(verificationState)
            .map { if (it == '@') '#' else it }.joinToString("")
            .split('\n')
            .let { it.subList(1, it.size) } // strip txt
            .joinToString("\n")
        for (i in 1..iterations) {
            runner.run()
        }
        runner.chamber.add(runner.rock)
        assertEquals(givenState, runner.chamber.toString())
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7, 8])
    fun `check states before after drop`(item: Int) {
        // not really a great test, but let's just go through all iterations, and see if we encounter the
        // states mentioned on the site
        val givenState = File("src/main/kotlin/day17moreVerificationData.txt")
            .readLines()
            .joinToString("\n")
            .map { if (it == '@') '#' else it }.joinToString("")
            .split("\n\n")
            .get(item.minus(1))
        val runner = Day17.Runner(">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>")
        var found: String? = null
        for (i in 1..1000) {
            runner.run()
            val c = runner.chamber.copy()
            c.add(runner.rock)
            val str = c.toString()
            if (givenState == str) {
                found = str
                break
            }
        }
        assertEquals(givenState, found)
    }

    @Test
    fun `height after 1 dropped`() {
        val runner = Day17.Runner(">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>")
        while (true) {
            runner.run()
            if (runner.numDropped == 1L) {
                assertEquals(1, runner.chamber.height())
                break
            }
        }
    }

    @Test
    fun `height after 2 dropped`() {
        val runner = Day17.Runner(">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>")
        while (true) {
            runner.run()
            if (runner.numDropped == 2L) {
                assertEquals(4, runner.chamber.height())
                break
            }
        }
    }
}