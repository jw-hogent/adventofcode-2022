import java.io.File
import java.util.PriorityQueue
import kotlin.math.max
import kotlin.math.min

fun <T> List<T>.permutations(): List<List<T>> =
    if (isEmpty()) listOf(emptyList()) else mutableListOf<List<T>>().also { result ->
        for (i in this.indices) {
            (this - this[i]).permutations().forEach {
                result.add(it + this[i])
            }
        }
    }

class Day16 {

    class State(val location: String)

    class Maze(val tunnels: Map<String, List<String>>, val flowRates: Map<String, Int>) {
        val maxOpenValves = flowRates.count { it.value != 0 }
        fun findBestOrder() {
            val valvesToOpen = flowRates.filter { it.value > 0 }.keys.toList()
            println("num valves to open: ${valvesToOpen.size}")
            println("num permutations: ${valvesToOpen.permutations().size}")
            var bestResult = 0
            var bestPermutation = listOf<String>()
            for (permutation in valvesToOpen.permutations()) {
                val result = calculatePressure(permutation)
                if (result > bestResult) {
                    bestResult = result
                    bestPermutation = permutation
                }
            }
            println("best: $bestResult, $bestPermutation")
        }

        fun findBestOrder2() {
            // keep a list of orders with the most optimal prognosis
            val highestFlowrate = flowRates.maxOf { it.value }
            // heuristic: every two minutes, a valve with highestFlowrate is opened
            // sum(i = remainingTime/2 .. 0)(i * 2 * highestFlowrate) would be more exact, but...
            val maxFlowrateEstimate = { remainingTime: Int -> remainingTime * remainingTime * highestFlowrate }
            val valvesToOpen = flowRates.filter { it.value > 0 }.keys
            val potential = {l: List<String> ->
                when {
                    l.size == valvesToOpen.size -> calculatePressure(l)
                    timeLeftAfter(l) == 0 -> calculatePressure(l)
                    else -> maxFlowrateEstimate(timeLeftAfter(l)) + calculatePressure(l)
                }
            }
            val topOrder = PriorityQueue<List<String>> { lhs: List<String>, rhs: List<String> ->
                potential(rhs) - potential(lhs)
            }
            valvesToOpen.forEach { topOrder.add(listOf(it)) }
            while (topOrder.isNotEmpty()) {
                val top = topOrder.poll()!!
                if (timeLeftAfter(top) == 0) {
                    topOrder.add(top)
                    break
                }
                val destinations = valvesToOpen - top.toSet()
                if (destinations.isEmpty()) {
                    topOrder.add(top)
                    break
                }
                topOrder.addAll(destinations.map{top + listOf(it)})
//                val timeLeft = timeLeftAfter(top)
//                val pressure = calculatePressure(top)
//                val estimate = maxFlowrateEstimate(timeLeft)
//                println("$top, remaining: ${timeLeft}, $pressure, $estimate")
            }
            val top = topOrder.poll()
            println("best: $top, pressure: ${calculatePressure(top)}")
        }

        fun timeLeftAfter(order: List<String>): Int {
            return calculatePressureAndTimeLeft(order).second
        }

        fun calculatePressure(order: List<String>): Int {
            return calculatePressureAndTimeLeft(order).first
        }

        fun calculatePressureAndTimeLeft(order: List<String>): Pair<Int, Int> {
            var position = "AA"
            var timeLeft = 30
            var releasedPressure = 0
            for (goal in order) {
                val steps = shortestPath(position, goal)
                if (steps > timeLeft) {
                    return Pair(releasedPressure, 0)
                }
                timeLeft -= steps.plus(1)
                position = goal
                releasedPressure += timeLeft * flowRates[goal]!!
            }
            return Pair(releasedPressure, timeLeft)
        }

        val shortestPaths = mutableMapOf<String, Int>()
        fun shortestPath(start: String, goal: String): Int {
            if (start == goal) {
                return 0
            }
            val key = "$start$goal"
            val keyReverse = "$goal$start"
            val length = shortestPaths[key] ?: shortestPaths[keyReverse]
            if (length != null) {
                return length
            }
            for (depth in 1..30) {
                val found = calculateShortestPath(start, goal, depth)
                if (found) {
                    shortestPaths[key] = depth
                    shortestPaths[keyReverse] = depth
                    return depth
                }
            }
            throw IllegalStateException("No path between $start and $goal in less than 30 steps")
        }

        fun calculateShortestPath(start: String, goal: String, depth: Int): Boolean {
            if (depth <= 0) {
                return false
            }
            for (out in tunnels[start]!!) {
                if (out == goal) {
                    return true
                }
                val deeper = calculateShortestPath(out, goal, depth.minus(1))
                if (deeper) {
                    return true
                }
            }
            return false
        }

        fun findBestPath() {
            var position = "AA"
//            val timeRemaining = 20
//            var bestPath = listOf<String>()
//            var pressureReleased = 0
            val openValves = mutableSetOf<String>()
            val bestPath = mutableListOf<String>()
            var released = 0
            var minute = 0
            for (timeRemaining in 30 downTo 1) {
                minute++
                println("== Minute $minute ==")
                val releasing = openValves.sumOf { flowRates[it]!! }
                println(if (releasing != 0) "Open valves: $openValves, releasing $releasing pressure" else "No valves are open.")
                released += releasing
                val (pos, _) = estimateBestPath(position, openValves, timeRemaining, min(timeRemaining, 20))
                if (pos == position) {
                    println("opening valve $pos")
                    openValves += position
                } else {
                    println("moving to $pos")
                }
                position = pos
                bestPath.add(pos)
                println()
            }
            println("Released $released, path: $bestPath")
        }

        private fun estimateBestPath(
            position: String,
            openValves: Set<String>,
            timeRemaining: Int,
            lookAhead: Int
        ): Pair<String, Int> {
            var bestReleased = 0
            var bestPath = position
            if (openValves.size == maxOpenValves || lookAhead == 0) {
                return Pair(bestPath, bestReleased)
            }
            val options =
                if (openValves.contains(position)) tunnels[position]!! else listOf(position) + tunnels[position]!!

            for (valve in options) {
                val openedValve = valve == position
                val newValves = if (openedValve) openValves + position else openValves
                val (step, released) = estimateBestPath(valve, newValves, timeRemaining.minus(1), lookAhead.minus(1))
                val actualReleased =
                    released + if (valve == position) max(0, timeRemaining.minus(1)) * flowRates[position]!! else 0
                if (actualReleased > bestReleased) {
                    bestReleased = actualReleased
                    bestPath = valve
                }
            }

            return Pair(bestPath, bestReleased)
        }
    }

    companion object {
        fun run(input: List<String>) {
            parseMaze(input).findBestOrder2()
        }

        fun parseMaze(input: List<String>): Maze {
            val allTunnels = mutableMapOf<String, List<String>>()
            val flowRates = mutableMapOf<String, Int>()
            input.forEach {
                val parts = it.split(' ')
                val valve = parts[1]
                val flowRate = parts[4].split('=')[1].split(';')[0].toInt()
                val tunnels = parts.subList(9, parts.size).joinToString("").split(",")
                val parsed =
                    "Valve $valve has flow rate=$flowRate; ${if (tunnels.size == 1) "tunnel leads" else "tunnels lead"} to ${if (tunnels.size == 1) "valve" else "valves"} ${
                        tunnels.joinToString(", ")
                    }"
                check(parsed == it) { "Expected:\n  $it, got:\n  $parsed" }
                allTunnels[valve] = tunnels
                flowRates[valve] = flowRate
            }
            var maze = Maze(allTunnels, flowRates)
            return maze
        }

        fun doCheck(expected: Int, actual: Int) {
            check(expected == actual) { "Expected $expected, got $actual" }
        }
    }
}

fun main(args: Array<String>) {
    val testInput = """Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
Valve BB has flow rate=13; tunnels lead to valves CC, AA
Valve CC has flow rate=2; tunnels lead to valves DD, BB
Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
Valve EE has flow rate=3; tunnels lead to valves FF, DD
Valve FF has flow rate=0; tunnels lead to valves EE, GG
Valve GG has flow rate=0; tunnels lead to valves FF, HH
Valve HH has flow rate=22; tunnel leads to valve GG
Valve II has flow rate=0; tunnels lead to valves AA, JJ
Valve JJ has flow rate=21; tunnel leads to valve II""".split("\n")

    Day16.doCheck(0, Day16.parseMaze(testInput).calculatePressure(listOf()))
    Day16.doCheck(20 * 28, Day16.parseMaze(testInput).calculatePressure(listOf("DD")))
    Day16.doCheck(20 * 28 + 13 * 25, Day16.parseMaze(testInput).calculatePressure(listOf("DD", "BB")))
    Day16.doCheck(20 * 28 + 13 * 25 + 21 * 21, Day16.parseMaze(testInput).calculatePressure(listOf("DD", "BB", "JJ")))
    println("Test input:")
    Day16.run(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day16input.txt").readLines()
    Day16.run(input)

}