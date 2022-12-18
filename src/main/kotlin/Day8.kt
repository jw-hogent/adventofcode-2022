import java.io.File
import kotlin.math.max

class Day8 {
    companion object {
        fun run(input: List<String>) {
            val origInput = input.map { it.map { v -> v - '0' }.toMutableList() }
            val res = run1(origInput)
            println("Part one: $res")
            val mostTrees = run2(origInput)
            println("Part two: $mostTrees")
        }

        private fun run2(input: List<MutableList<Int>>): Int {
            var res = 0

            for (y in 1 until input.size.minus(1)) {
                for (x in 1 until input[y].size.minus(1)) {
                    val viz = numTreesVisible(x, y, input)
                    res = max(res, viz)
                }
            }
            return res
        }

        private fun numTreesVisible(x: Int, y: Int, input: List<List<Int>>): Int {
            var res = 1
            var viz = 0
            for (xPos in x.plus(1)..input[y].size.minus(1)) {
                viz++
                if (input[y][xPos] >= input[y][x]) {
                    break
                }
            }
            res *= viz
            viz = 0
            for (xPos in x.minus(1) downTo (0)) {
                viz++
                if (input[y][xPos] >= input[y][x]) {
                    break
                }
            }
            res *= viz
            viz = 0
            for (yPos in y.plus(1)..input.size.minus(1)) {
                viz++
                if (input[yPos][x] >= input[y][x]) {
                    break
                }
            }
            res *= viz
            viz = 0
            for (yPos in y.minus(1) downTo (0)) {
                viz++
                if (input[yPos][x] >= input[y][x]) {
                    break
                }
            }
            res *= viz
            return res
        }

        private fun run1(origInput: List<MutableList<Int>>): Int {
            var input = origInput
            var viz = input.map { it.map { false }.toMutableList() }

            for (r in 0..3) {
                findVisibleTrees(viz, input)
                viz = rotate(viz)
                input = rotate(input)
            }
            return viz.sumOf { it.count { t -> t } }
        }

        private fun <T> rotate(input: List<MutableList<T>>): List<MutableList<T>> {
            val res = input.map { it.toMutableList() }
            for (y in input.indices) {
                for (x in 0 until input[y].size) {
                    res[input.size - x - 1][y] = input[y][x]
                }
            }
            return res
        }

        /**
         * finds them visible trees, but only from the left
         */
        private fun findVisibleTrees(viz: List<MutableList<Boolean>>, input: List<MutableList<Int>>) {
            for (y in input.indices) {
                viz[y][0] = true
                var maxTree = input[y][0]
                for (x in 1 until input[y].size) {
                    if (input[y][x] > maxTree) {
                        viz[y][x] = true
                        maxTree = input[y][x]
                    }
                }
            }
        }
    }
}

fun main() {
    val testInput = """30373
25512
65332
33549
35390""".split('\n')

    println("Test input:")
    Day8.run(testInput)

    println("Input:")
    Day8.run(File("src/main/kotlin/day8input.txt").readLines())
}
