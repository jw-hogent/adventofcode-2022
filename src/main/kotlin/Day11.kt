import java.io.File

class Day11 {

    class Monkey(
        val id: Int,
        val items: MutableList<Long>,
        val operation: (i: Long) -> Long,
        val test: (i: Long) -> Boolean,
        val ifTrue: Int,
        val ifFalse: Int,
        var numInspections: Long = 0,
    )

    companion object {
        fun run(input: List<String>, isTestInput: Boolean) {
            val partOne = runMonkeys(input, 20) { it.div(3) }
            println("Part one: $partOne")
            val partTwo = if (isTestInput)
                    runMonkeys(input, 10_000) { it.mod(96577L) } // magic numbers ftw
                else
                    runMonkeys(input, 10_000) { it.mod(9699690L) }
            println("Part two: $partTwo")

        }
        private fun runMonkeys(input: List<String>, rounds: Int, operation: (Long) -> Long): Long {
            val monkeys = parse(input)
            for (round in 1..rounds) {

                for (m in monkeys) {
                    printVerbose("Monkey ${m.id}:")
                    for (item in m.items) {
                        m.numInspections++
                        printVerbose("  Monkey inspects an item with a worry level of $item.\n")
                        val worryLevel = m.operation(item)
                        val newLevel = operation(worryLevel)
                        printVerbose("    Monkey gets bored with item. Worry level is divided by 3 to $newLevel")
                        if (m.test(newLevel)) {
                            printVerbose("    Item with worry level $newLevel is thrown to monkey ${m.ifTrue}")
                            monkeys[m.ifTrue].items.add(newLevel)
                        } else {
                            printVerbose("    Item with worry level $newLevel is thrown to monkey ${m.ifFalse}")
                            monkeys[m.ifFalse].items.add(newLevel)
                        }
                    }
                    m.items.clear()
                }

            }
            return monkeys.map { it.numInspections }.sortedDescending().take(2).reduce { acc, v -> acc * v }
        }

        private fun printVerbose(input: String) {
//            println(input)
        }

        private fun parse(input: List<String>): List<Monkey> {
            val chunks = splitInput(input)
            val res = mutableListOf<Monkey>()
            for (chunk in chunks) {
                res.add(
                    Monkey(
                        id = parseMonkeyId(chunk[0]),
                        items = parseStartingItems(chunk[1]).toMutableList(),
                        operation = parseOperation(chunk[2]),
                        test = parseTest(chunk[3]),
                        ifTrue = parseIfTrue(chunk[4]),
                        ifFalse = parseIfFalse(chunk[5]),
                    )
                )
            }
            return res.toList()
        }

        private fun parseIfFalse(input: String): Int = parseIf(input, "false")
        private fun parseIfTrue(input: String): Int = parseIf(input, "true")

        private fun parseIf(input: String, s: String): Int {
            val num = input.trimStart().split(' ')[5].toInt()
            verify(input, "    If $s: throw to monkey $num")
            return num
        }

        private fun parseTest(input: String): (Long) -> Boolean {
            val num = input.trimStart().split(' ')[3].toInt()
            verify(input, "  Test: divisible by $num")
            return { i: Long ->
                val res = i.mod(num) == 0
                if (res) {
                    printVerbose("    Current worry level is divisible by $num.")
                } else {
                    printVerbose("    Current worry level is not divisible by $num.")
                }
                res
            }
        }

        private fun parseOperation(input: String): (Long) -> Long {
            val s = input.trimStart().split(' ')
            val op = s[4]
            val what = s[5]
            verify(input, "  Operation: new = old $op $what")
            check(op in "* +".split(' ').toSet())
            val thing = { it: Long -> if (what == "old") it else what.toLong() }
            return when (op) {
                "*" -> { it: Long ->
                    val res = it.times(thing(it))
                    printVerbose("    Worry level is multiplied by ${thing(it)} to $res")
                    res
                }
                "+" -> { it: Long ->
                    val res = it.plus(thing(it))
                    printVerbose("    Worry level increases by ${thing(it)} to $res")
                    res
                }
                // these operations don't occur
//                "/" -> { it: Long -> it.div(thing(it)) }
//                "-" -> { it: Long -> it.minus(thing(it)) }
                else -> throw IllegalStateException("unknown operation")
            }
        }

        private fun parseStartingItems(input: String): List<Long> {
            val res = input.split(':')[1].trimStart().split(", ").map { it.toLong() }
            verify(input, "  Starting items: ${res.joinToString(", ")}")
            return res
        }

        private fun parseMonkeyId(input: String): Int {
            val res = input.split(' ')[1].split(':')[0].toInt()
            verify(input, "Monkey $res:")
            return res
        }

        private fun verify(input: String, parsed: String) {
            check(input == parsed) { "Parse error: expected:\n  $input, got\n  $parsed" }
        }

        private fun splitInput(input: List<String>): List<List<String>> {
            val res = mutableListOf<List<String>>()
            val temp = mutableListOf<String>()
            for (line in input) {
                if (line.isEmpty()) {
                    res.add(temp.toList())
                    temp.clear()
                } else {
                    temp.add(line)
                }
            }
            if (temp.isNotEmpty()) {
                res.add(temp.toList())
                temp.clear()
            }
            return res
        }
    }
}

fun main() {
    println("Test input")
    val testInput = File("src/main/kotlin/day11testinput.txt").readLines()
    Day11.run(testInput, true)

    println("Input:")
    val input = File("src/main/kotlin/day11input.txt").readLines()
    Day11.run(input, false)
}
