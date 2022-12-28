import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day24Test {
    val input = """
            #.#####
            #.....#
            #>....#
            #.....#
            #...v.#
            #.....#
            #####.#
        """.trimIndent()

    val complexInput = """
        #E######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
    """.trimIndent()

    @Test
    fun `can parse input`() {
        assertEquals(input, Day24.Valley.parse(input).toString(0))
    }

    @Test
    fun `after one step`() {
        val expected = """
            #.#####
            #.....#
            #.>...#
            #.....#
            #.....#
            #...v.#
            #####.#
        """.trimIndent()

        assertEquals(expected, Day24.Valley.parse(input).toString(1))
    }

    @Test
    fun `after two steps`() {
        val expected = """
            #.#####
            #...v.#
            #..>..#
            #.....#
            #.....#
            #.....#
            #####.#
        """.trimIndent()

        assertEquals(expected, Day24.Valley.parse(input).toString(2))
    }

    // skipping three steps

    @Test
    fun `after four steps`() {
        val expected = """
            #.#####
            #.....#
            #....>#
            #...v.#
            #.....#
            #.....#
            #####.#
        """.trimIndent()

        assertEquals(expected, Day24.Valley.parse(input).toString(4))
    }

    @Test
    fun `after five steps`() {
        val expected = """
            #.#####
            #.....#
            #>....#
            #.....#
            #...v.#
            #.....#
            #####.#
        """.trimIndent()

        assertEquals(expected, Day24.Valley.parse(input).toString(5))
    }

    @Test
    fun `many blizzards, available points on minute 2`() {
        val testInput = """
                    #.######
                    #>>.<^<#
                    #.<..<<#
                    #>v.><>#
                    #<^v^^>#
                    ######.#
        """.trimIndent()
        val valley = Day24.Valley.parse(testInput.replace('E', '.'))

        val availableMoves = valley.availableMoves(Day24.Point(1, 1), 2)
        val testPoint = Day24.Point(2, 1)
        assert(testPoint !in availableMoves)
    }

    @Test
    fun `many blizzards`() {
        val valley = Day24.Valley.parse(complexInput.replace('E', '.'))

        val expected = """
            #.######
            #E>3.<.#
            #<..<<.#
            #>2.22.#
            #>v..^<#
            ######.#
        """.trimIndent().replace('E', '.')

        assertEquals(expected, valley.toString(1))
    }

    @Test
    fun `many blizzards, minute 2`() {
        val valley = Day24.Valley.parse(complexInput.replace('E', '.'))

        val expected = """
            #.######
            #.2>2..#
            #E^22^<#
            #.>2.^>#
            #.>..<.#
            ######.#
        """.trimIndent().replace('E', '.')

        assertEquals(expected, valley.toString(2))
    }

    @Test
    fun `many blizzards, minute 3`() {
        val valley = Day24.Valley.parse(complexInput.replace('E', '.'))

        val expected = """
            #.######
            #<^<22.#
            #E2<.2.#
            #><2>..#
            #..><..#
            ######.#
        """.trimIndent().replace('E', '.')

        assertEquals(expected, valley.toString(3))
    }

}