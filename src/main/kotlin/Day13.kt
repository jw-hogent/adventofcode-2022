import java.io.File

class Day13 {

    interface Item {
        fun getChildren(): List<Item> {
            throw NotImplementedError()
        }
        fun getValue(): Int {
            throw NotImplementedError()
        }
        fun isList(): Boolean
    }
    class IntItem(private val value: Int): Item {
        override fun getValue() = value
        override fun isList(): Boolean = false
        override fun toString() = "$value"
    }
    class ListItem(private val list: List<Item>): Item {
        override fun isList() = true
        override fun getChildren() = list
        override fun toString() = "[${list.joinToString(",")}]"
    }
    class Builder(str: String) {
        private var level = 0
        private var currentNumber: Int? = null
        var items = listOf<Item>()

        init {
            str.forEach{onChar(it)}
        }

        private fun onStartList() {
            if (level != 0) {
                append(ListItem(listOf()))
            }
            level++
        }
        private fun onEndList() {
            onNextNumber()
            level--
        }
        private fun onNextNumber() {
            if (currentNumber != null) {
                append(IntItem(currentNumber!!))
                currentNumber = null
            }
        }
        private fun addDigit(c: Char) {
            val value = c - '0'
            currentNumber = currentNumber?.times(10)?.plus(value) ?: value
        }
        private fun append(value: Item) {
            items = append(items, level, value)
        }
        private fun append(list: List<Item>, lvl: Int, value: Item): List<Item> {
            if (lvl <= 1) {
                return listOf(*list.toTypedArray(), value)
            }
            val updated = ListItem(append(list.last().getChildren(), lvl.minus(1), value))
            if (list.size == 1) {
                return listOf(updated)
            }
            return listOf(*list.subList(0, list.size - 1).toTypedArray(), updated)
        }
        private fun onChar(c: Char) {
            when (c) {
                ' ' -> {}
                ',' -> onNextNumber()
                in '0'..'9' -> addDigit(c)
                '[' -> onStartList()
                ']' -> onEndList()
            }
        }
    }
    companion object {
        fun run(input: List<String>) {
            val pairs = input.joinToString("\n").split("\n\n").map{it.split("\n")}
            val result = pairs.mapIndexed{ idx: Int, str: List<String> ->
                if (isOrdered(str[0], str[1])) {
                    idx.plus(1)
                } else 0
            }.sum()
            println("Part one: $result")
        }
        fun run2(input: List<String>) {
            val withDividers = listOf(*input.filterNot{it.isEmpty()}.toTypedArray(), "[[2]]", "[[6]]")
            val sorted = withDividers.sortedWith { lhs: String, rhs: String -> -compare(parseList(lhs), parseList(rhs)) }
            val pos1 = sorted.indexOf("[[2]]").plus(1)
            val pos2 = sorted.indexOf("[[6]]").plus(1)
            println("Part two: indexes: $pos1, $pos2, decoder key: ${pos1*pos2}")
        }
        private fun isOrdered(lhs: String, rhs: String): Boolean {
            return compare(parseList(lhs), parseList(rhs)) != -1
        }
        private fun parseList(input: String): ListItem {
            val res = ListItem(Builder(input).items)
            check(input == res.toString()) {"input:\n  $input, parsed:\n  $res"}
            return res
        }
        private fun compare(lhs: ListItem, rhs: ListItem): Int {
            for (pos in 0 until lhs.getChildren().size) {
                if (pos >= rhs.getChildren().size) {
                    return -1
                }
                val l = lhs.getChildren()[pos]
                val r = rhs.getChildren()[pos]
                if (!l.isList() && !r.isList()) {
                    if (l.getValue() > r.getValue()) {
                        return -1
                    } else if (l.getValue() < r.getValue()) {
                        return 1
                    }
                    continue
                }
                if (l.isList() && r.isList()) {
                    val subRes = compare(l as ListItem, r as ListItem)
                    if (subRes != 0) {
                        return subRes
                    }
                    continue
                }
                // now either l is a list or r is a list, but not both
                if (l.isList()) {
                    val res = compare(l as ListItem, ListItem(listOf(r)))
                    if (res != 0) {
                        return res
                    }
                    continue
                }
                // only other option
                check(r.isList())
                val res = compare(ListItem(listOf(l)), r as ListItem)
                if (res != 0) {
                    return res
                }
            }
            if (lhs.getChildren().size < rhs.getChildren().size) {
                return 1
            }
            return 0
        }
     }
}

fun main() {
    val testInput = """[1,1,3,1,1]
[1,1,5,1,1]

[[1],[2,3,4]]
[[1],4]

[9]
[[8,7,6]]

[[4,4],4,4]
[[4,4],4,4,4]

[7,7,7,7]
[7,7,7]

[]
[3]

[[[]]]
[[]]

[1,[2,[3,[4,[5,6,7]]]],8,9]
[1,[2,[3,[4,[5,6,0]]]],8,9]""".split("\n")

    println("Test input:")
    Day13.run(testInput)
    Day13.run2(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day13input.txt").readLines()
    Day13.run(input)
    Day13.run2(input)
}
