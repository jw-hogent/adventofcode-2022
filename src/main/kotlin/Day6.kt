import java.io.File

class Day6 {
    companion object {
        fun findMarker(input: String, numDistinct: Int): Int {
            for (i in 0 .. input.length.minus(numDistinct).dec()) {
                if (input.subSequence(i, i.plus(numDistinct)).toSet().size == numDistinct) {
                    return i.plus(numDistinct)
                }
            }
            throw IllegalStateException("Could not find marker")
        }
        fun run(input: String) {
            println("Part one: ${findMarker(input, 4)}")
            println("Part two: ${findMarker(input, 14)}")
        }
    }
}
fun main() {
    println("Input:")
    val input = File("src/main/kotlin/day6input.txt").readLines()[0]
    Day6.run(input)
}
