import java.io.File

class Day21 {

    interface Calculation {
        fun toString(lookup: Map<String, Calculation>): String
        fun invoke(lookup: Map<String, Calculation>): Long
    }
    class Value(val value: Long): Calculation{
        override fun toString(lookup: Map<String, Calculation>): String {
            return value.toString()
        }

        override fun invoke(lookup: Map<String, Calculation>): Long = value
    }
    class StringValue(val value: String): Calculation{
        override fun toString(lookup: Map<String, Calculation>) = "humn"

        override fun invoke(lookup: Map<String, Calculation>): Long {
            throw NotImplementedError()
        }

    }
    class CalculatedValue(val lhs: String, val operation: String, val rhs: String): Calculation {
        override fun toString(lookup: Map<String, Calculation>) = "(${lookup[lhs]!!.toString(lookup)} $operation ${lookup[rhs]!!.toString(lookup)})"

        override fun invoke(lookup: Map<String, Calculation>): Long {
            return when (operation) {
                "+" -> lookup[lhs]!!.invoke(lookup) + lookup[rhs]!!.invoke(lookup)
                "-" -> lookup[lhs]!!.invoke(lookup) - lookup[rhs]!!.invoke(lookup)
                "*" -> lookup[lhs]!!.invoke(lookup) * lookup[rhs]!!.invoke(lookup)
                "/" -> lookup[lhs]!!.invoke(lookup) / lookup[rhs]!!.invoke(lookup)
                else -> throw IllegalStateException("unknown operator ${rhs[1]}")
            }
        }

    }

    companion object {
        fun runPart1(input: List<String>) {
            val lookup = mutableMapOf<String, () -> Long>()
            for (line in input) {
                val parts = line.split(':')
                val key = parts[0]
                val rhs = parts[1].trimStart().split(' ')
                if (rhs.size == 1) {
                    lookup[key] = { rhs[0].toLong() }
                } else {
                    lookup[key] = when (rhs[1]) {
                        "+" -> {
                            { lookup[rhs[0]]!!.invoke() + lookup[rhs[2]]!!.invoke() }
                        }
                        "-" -> {
                            { lookup[rhs[0]]!!.invoke() - lookup[rhs[2]]!!.invoke() }
                        }
                        "*" -> {
                            { lookup[rhs[0]]!!.invoke() * lookup[rhs[2]]!!.invoke() }
                        }
                        "/" -> {
                            { lookup[rhs[0]]!!.invoke() / lookup[rhs[2]]!!.invoke() }
                        }
                        else -> throw IllegalStateException("unknown operator ${rhs[1]}")
                    }
                }
            }
            val rootValue = lookup["root"]!!.invoke()
            println("Part 1: $rootValue")
        }

        fun runPart2(input: List<String>) {
            val lookup = mutableMapOf<String, Calculation>()
            var rootLhs = ""
            var rootRhs = ""
            for (line in input) {
                val parts = line.split(':')
                val key = parts[0]
                val rhs = parts[1].trimStart().split(' ')
                if (rhs.size == 1) {
                    lookup[key] = Value(rhs[0].toLong())
                } else {
                    if (key == "root") {
                        rootLhs = rhs[0]
                        rootRhs = rhs[2]
                    }
                    lookup[key] = CalculatedValue(rhs[0], rhs[1], rhs[2])
                }
            }
            // try to simplify the map: if humn doesn't exit, lookup will throw
            // try to calculate all keys, if they don't throw -> simplify
            lookup.remove("humn")
            for (key in lookup.keys) {
                try {
                    val calculated = lookup[key]!!.invoke(lookup)
                    lookup[key] = Value(calculated)
                } catch (e: NullPointerException) {
                    // ignore
                }
            }

            lookup["humn"] = StringValue("humn")
            solve(rootLhs, rootRhs, lookup)
        }

        fun solve(rootLhs: String, rootRhs: String, lookup: MutableMap<String, Calculation>) {
            println("${lookup[rootLhs]!!.toString(lookup)} = ${lookup[rootRhs]!!.toString(lookup)}")
            val lhs = lookup[rootLhs]!!
            if (lhs !is Value) {
                // one of both should be just a value
                check(lookup[rootRhs]!! is Value)
                return solve(rootRhs, rootLhs, lookup)
            }
            // lhs is value here, so:
            // [value] = [something]

            // rhs is either calculated or humn
            val rhs = lookup[rootRhs]!!
            if (rhs is StringValue) {
                println("${rhs.value} is ${lhs.value}")
                return
            }
            if (rhs !is CalculatedValue) {
                throw IllegalStateException("Not expecting anything else")
            }
            val rhslhs = lookup[rhs.lhs]!!
            if (rhslhs is Value) {
                // [value] = [value] [op] [something]
                // transform to: [value] [op] [value] = [something]
                // calculate and update lookup, run solve again
                when (rhs.operation) {
                    "+" -> {
                        // [lhs] = [rhs.lhs] + [rhs.rhs]
                        // -> [lhs - rhs.lhs] = [rhs.rhs]
                        lookup[rootLhs] = Value(lhs.value - rhslhs.value)
                        return solve(rootLhs, rhs.rhs, lookup)
                    }
                    "-" -> {
                        // [lhs] = [rhs.lhs] - [rhs.rhs]
                        // -> [rhs.lhs - lhs] = [rhs.rhs]
                        lookup[rootLhs] = Value(rhslhs.value - lhs.value)
                        return solve(rootLhs, rhs.rhs, lookup)
                    }
                    "*" -> {
                        // [lhs] = [rhs.lhs] * [rhs.rhs]
                        // -> [lhs / rhs.lhs] = [rhs.rhs]
                        lookup[rootLhs] = Value(lhs.value / rhslhs.value)
                        return solve(rootLhs, rhs.rhs, lookup)
                    }
                    "/" -> {
                        // [lhs] = [rhs.lhs] / [rhs.rhs]
                        // -> [rhs.lhs / lhs] = [rhs.rhs]
                        check(rhslhs.value.mod(lhs.value) == 0L)
                        lookup[rootLhs] = Value(rhslhs.value / lhs.value)
                        return solve(rootLhs, rhs.rhs, lookup)
                    }
                    else -> {
                        throw IllegalStateException("Unknown op")
                    }
                }
            }
            // needs to be a value now
            val rhsrhs = lookup[rhs.rhs]!! as Value
            when (rhs.operation) {
                "+" -> {
                    // [lhs] = [rhs.lhs] + [rhs.rhs]
                    // -> [lhs - rhs.rhs] = [rhs.lhs]
                    lookup[rootLhs] = Value(lhs.value - rhsrhs.value)
                    return solve(rootLhs, rhs.lhs, lookup)
                }
                "-" -> {
                    // [lhs] = [rhs.lhs] - [rhs.rhs]
                    // -> [rhs.rhs + lhs] = [rhs.lhs]
                    lookup[rootLhs] = Value(rhsrhs.value + lhs.value)
                    return solve(rootLhs, rhs.lhs, lookup)
                }
                "*" -> {
                    // [lhs] = [rhs.lhs] * [rhs.rhs]
                    // -> [lhs / rhs.rhs] = [rhs.lhs]
                    check(lhs.value.mod(rhsrhs.value) == 0L)
                    lookup[rootLhs] = Value(lhs.value / rhsrhs.value)
                    return solve(rootLhs, rhs.lhs, lookup)
                }
                "/" -> {
                    // [lhs] = [rhs.lhs] / [rhs.rhs]
                    // -> [rhs.rhs * lhs] = [rhs.lhs]
                    lookup[rootLhs] = Value(rhsrhs.value * lhs.value)
                    return solve(rootLhs, rhs.lhs, lookup)
                }
                else -> {
                    throw IllegalStateException("Unknown op")
                }
            }
        }

    }
}

fun main() {
    val testInput =
        """
            root: pppw + sjmn
            dbpl: 5
            cczh: sllz + lgvd
            zczc: 2
            ptdq: humn - dvpt
            dvpt: 3
            lfqf: 4
            humn: 5
            ljgn: 2
            sjmn: drzm * dbpl
            sllz: 4
            pppw: cczh / lfqf
            lgvd: ljgn * ptdq
            drzm: hmdt - zczc
            hmdt: 32
        """.trimIndent().split('\n')

    println("Test input:")
    Day21.runPart1(testInput)
    Day21.runPart2(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day21input.txt").readLines()
    Day21.runPart1(input)
    Day21.runPart2(input)
}