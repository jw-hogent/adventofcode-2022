import Day25.Companion.fromSNAFU
import Day25.Companion.toSNAFU
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Day25Test {
    @Test
    fun `read some SNAFU numbers`() {
        assertEquals(1747, fromSNAFU("1=-0-2"))
        assertEquals(906, fromSNAFU("12111"))
    }

    @Test
    fun `create some SNAFU numbers`() {
        assertEquals("1=-0-2", toSNAFU("1747"))
        assertEquals("12111", toSNAFU("906"))
    }
}