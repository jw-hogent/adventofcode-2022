import java.io.File
import java.util.*
import java.util.stream.IntStream.range

class Day5 {
    companion object {
        fun run(input: List<String>) {
            runPartOne(input)
            runPartTwo(input)
        }
        private fun runPartOne(lines: List<String>) {
            val stacks = parseStacks(lines)
            lines.filter{it.contains("move")}.forEach{
                val parts = it.split(' ')
                val count = parts[1].toInt()
                val from = parts[3].toInt()
                val to = parts[5].toInt()
                for (i in 0 until count) {
                    stacks[to.minus(1)].push(stacks[from.minus(1)].pop())
                }
            }
            val res = stacks.map{it.peek()}.joinToString("")
            println("Part one: $res")
        }
        private fun runPartTwo(lines: List<String>) {
            val stacks = parseStacks(lines)
            lines.filter{it.contains("move")}.forEach{
                val parts = it.split(' ')
                val count = parts[1].toInt()
                val from = parts[3].toInt()
                val to = parts[5].toInt()
                val tempStack = Stack<Char>()
                for (i in 0 until count) {
                    tempStack.push(stacks[from.minus(1)].pop())
                }
                for (i in 0 until count) {
                    stacks[to.minus(1)].push(tempStack.pop())
                }
            }
            val res = stacks.map{it.peek()}.joinToString("")
            println("Part two: $res")
        }
        private fun parseStacks(lines: List<String>): List<Stack<Char>> {
            var firstMove = 0
            while (!lines[firstMove].contains("move")) firstMove++
            val bottom = firstMove - 2
            val indexes = lines[bottom].split(' ').map{it.toIntOrNull()}.sortedBy{it}
            val numStacks = indexes[indexes.size - 1]!!
            val stacks = mutableListOf<Stack<Char>>()
            range(0, numStacks).forEach {
                val stack = Stack<Char>()
                var row = bottom - 1
                val elPos = 1 + it * 4
                while (row >= 0 && lines[row].length > elPos && lines[row][elPos] != ' ') {
                    stack.push(lines[row][elPos])
                    row -= 1
                }
                stacks.add(stack)
            }
            return stacks.toList()
        }
    }
}


fun main() {
    val testInput = """    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2""".split('\n')

    println("Test input:")
    Day5.run(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day5input.txt").readLines()
    Day5.run(input)
}