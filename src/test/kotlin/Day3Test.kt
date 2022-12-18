import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day3Test {
    @Test
    fun `test basic inputs`() {
        assertEquals(16, Day3.score("vJrwpWtwJgWrhcsFMMfFFhFp"))
        assertEquals(38, Day3.score("jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL"))
        assertEquals(42, Day3.score("PmmdzqPrVvPwwTWBwg"))
        assertEquals(22, Day3.score("wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn"))
        assertEquals(20, Day3.score("ttgJtRGJQctTZtZT"))
        assertEquals(19, Day3.score("CrZsJsPPZsGzwwsLwLmpwMDw"))
    }
}