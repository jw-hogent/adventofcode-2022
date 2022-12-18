import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day6Test {
    @Test
    fun `test part one`() {
        assertEquals(7, Day6.findMarker("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 4))
        assertEquals(5, Day6.findMarker("bvwbjplbgvbhsrlpgdmjqwftvncz", 4))
        assertEquals(6, Day6.findMarker("nppdvjthqldpwncqszvftbrmjlhg", 4))
        assertEquals(10, Day6.findMarker("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 4))
        assertEquals(11, Day6.findMarker("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 4))
    }

    @Test
    fun `test part two`() {
        assertEquals(19, Day6.findMarker("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 14))
        assertEquals(23, Day6.findMarker("bvwbjplbgvbhsrlpgdmjqwftvncz", 14))
        assertEquals(23, Day6.findMarker("nppdvjthqldpwncqszvftbrmjlhg", 14))
        assertEquals(29, Day6.findMarker("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 14))
        assertEquals(26, Day6.findMarker("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 14))
    }
}