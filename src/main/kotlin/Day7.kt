import java.io.File
import java.util.*

class Day7 {
    companion object {

        fun run(input: List<String>) {
            val path = Stack<String>()
            val dirSizes = mutableMapOf<String, Int>()
            for (line in input) {
                when {
                    line == "$ cd /" -> path.clear()
                    line == "$ cd .." -> path.pop()
                    line.startsWith("$ cd ") -> path.push(line.substring("$ cd ".length))
                    line == "$ ls" -> continue
                    line.startsWith("dir ") -> continue
                    else -> {
                        val size = line.substring(0, line.indexOf(" ")).toInt()
                        val pathCopy = path.toMutableList()
                        while (pathCopy.isNotEmpty()) {
                            val dir = pathCopy.joinToString("/")
                            dirSizes[dir] = dirSizes.getOrDefault(dir, 0) + size
                            pathCopy.removeAt(pathCopy.size.minus(1))
                        }
                        val dir = pathCopy.joinToString("/")
                        dirSizes[dir] = dirSizes.getOrDefault(dir, 0) + size
                    }
                }
            }
            val q = dirSizes.filterValues { it <= 100000 }.values.sum()
            val remaining = 70_000_000 - dirSizes[""]!!
            val toClear = 30_000_000 - remaining
            val res = dirSizes.filterValues { it >= toClear }.toList().minByOrNull { it.second }!!
            println("Part one: $q")
            println("Part two: ${res.second}")
        }
    }
}

fun main() {
    val testInput = """
        ${'$'} cd /
        ${'$'} ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        ${'$'} cd a
        ${'$'} ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        ${'$'} cd e
        ${'$'} ls
        584 i
        ${'$'} cd ..
        ${'$'} cd ..
        ${'$'} cd d
        ${'$'} ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k
    """.trimIndent().split('\n')

    println("Test input:")
    Day7.run(testInput)

    println("Input:")
    Day7.run(File("src/main/kotlin/day7input.txt").readLines())

}
