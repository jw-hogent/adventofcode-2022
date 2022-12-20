import java.io.File
import java.util.*


class Day16PartTwo {

    data class Action(val isMove: Boolean, val position: String, val isHalted: Boolean) {
        override fun toString() = when {
            isHalted -> "[halt]"
            isMove -> "[->$position]"
            else -> "[open $position]"
        }
    }

    // used to be a List<Action>, but that needed much more memory
    class ActionList(val currentAction: Action, val previous: ActionList?) {
        companion object {
            fun toList(list: ActionList?): List<Action> {
                val res = mutableListOf<Action>()
                var pos = list
                while (pos != null) {
                    res.add(pos.currentAction)
                    pos = pos.previous
                }
                return res.reversed()
            }
        }
    }

    // for some reason this extension function doesn't work
//    fun ActionList?.toList() = ActionList.toList(this)

    fun ActionList?.add(newAction: Action) = ActionList(newAction, this)

    data class State(
        val startPosition: String,
        val actions: ActionList?,
        val goal: String
    ) {
        val position = actions?.currentAction?.position ?: startPosition
        fun valvesOpened() = ActionList.toList(actions).filter { !it.isMove }.map { it.position }.toSet()

        val isHalted = actions?.currentAction?.isHalted ?: false
        fun futureOpenedValves() = valvesOpened() + setOf(goal)
        fun size() = ActionList.toList(actions).size

        fun open(nextGoal: String): State {
            check(position !in valvesOpened())
            return State(startPosition, ActionList(Action(false, position, false), actions), nextGoal)
        }

        fun moveTo(nextPosition: String): State {
            return State(startPosition, ActionList(Action(true, nextPosition, false), actions), goal)
        }

        fun halt(): State {
            return State(startPosition, ActionList(Action(true, position, true), actions), goal)
        }
    }

    data class CombinedState(val myState: State, val elephantState: State) {
        init {
            // simplifies other stuff
            check(myState.size() == elephantState.size())
        }
        val valvesOpened = myState.valvesOpened() + elephantState.valvesOpened()
    }

    class Maze(val tunnels: Map<String, List<String>>, val flowRates: Map<String, Int>) {
        private val totalTime = 26
        private val allValvesToOpen = flowRates.filter { it.value > 0 }.keys
//        private val checkState = createCheckState()

//        fun createCheckState(): CombinedState {
//            // for testing
//            val fakeGoal = ""
//            val me = State("AA", null, fakeGoal)
//                .moveTo("II")
//                .moveTo("JJ")
//                .open(fakeGoal)
//                .moveTo("II")
//                .moveTo("AA")
//                .moveTo("BB")
//                .open(fakeGoal)
//                .moveTo("CC")
//                .open(fakeGoal)
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//
//            val elephant = State("AA", null, fakeGoal)
//                .moveTo("DD")
//                .open(fakeGoal)
//                .moveTo("EE")
//                .moveTo("FF")
//                .moveTo("GG")
//                .moveTo("HH")
//                .open(fakeGoal)
//                .moveTo("GG")
//                .moveTo("FF")
//                .moveTo("EE")
//                .open(fakeGoal)
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//                .halt()
//
//            return CombinedState(me, elephant)
//        }

        fun findBestActions() {
            // default: sorted lowest first, so reversed rhs & lhs
            val topOrder = PriorityQueue { lhs: CombinedState, rhs: CombinedState ->
                potentialReleasedPressure(rhs) - potentialReleasedPressure(lhs)
            }
            val allValvesToOpenList = allValvesToOpen.toList()
            for (lhs in allValvesToOpen.indices) {
                for (rhs in lhs.plus(1).rangeTo(allValvesToOpenList.size.minus(1))) {
                    topOrder.add(
                        CombinedState(
                            State("AA", null, allValvesToOpenList[rhs]),
                            State("AA", null, allValvesToOpenList[lhs]),
                        )
                    )
                }
            }

            var bestHalted: CombinedState? = null
            var bestReleasedPressure = 0
            var numIterations = 0
            while (topOrder.isNotEmpty()) {
                val top = topOrder.poll()!!

                bestReleasedPressure = maxOf(bestReleasedPressure, calculateReleasedPressure(top))

                if (++numIterations > 10000) {
                    val oldItems = topOrder.toList()
                    topOrder.clear()
                    var numCleaned = 0
                    for (item in oldItems) {
                        if (potentialReleasedPressure(item) < bestReleasedPressure) {
                            numCleaned++
                        } else {
                            topOrder.add(item)
                        }
                    }
                    println("cleared up $numCleaned items of ${oldItems.size}")
                    numIterations = 0
                }

                if (timeLeft(top.myState) == 0 || top.valvesOpened == allValvesToOpen) {
                    if (bestHalted == null || (calculateReleasedPressure(top) > calculateReleasedPressure(bestHalted))) {
                        bestHalted = top
                    }
                    break
                }
                // just generate all possible combinations, and filter out invalid ones
                // we can both move independently, or open valves
                val myPotentialNextStates = mutableListOf<State>()

                val elephantPotentialNextStates = mutableListOf<State>()

                var stateToExtend = top
                while (true) {
                    // advance state as far as possible
                    if (stateToExtend.myState.goal == "END_ME") {
                        myPotentialNextStates.add(stateToExtend.myState.halt())
                    } else if (stateToExtend.myState.isHalted) {
                        myPotentialNextStates.add(stateToExtend.myState.halt())
                    } else if (stateToExtend.myState.goal == stateToExtend.myState.position) {
                        for (valve in allValvesToOpen - stateToExtend.valvesOpened - setOf(
                            stateToExtend.myState.position,
                            stateToExtend.elephantState.goal
                        )) {
                            myPotentialNextStates.add(stateToExtend.myState.open(valve))
                        }
                        myPotentialNextStates.add(stateToExtend.myState.open("END_ME"))
                    } else {
                        val pos = findBestStep(stateToExtend.myState.position, stateToExtend.myState.goal)
                        myPotentialNextStates.add(stateToExtend.myState.moveTo(pos))
                    }

                    if (stateToExtend.elephantState.goal == "END_ELEPHANT") {
                        elephantPotentialNextStates.add(stateToExtend.elephantState.halt())
                    } else if (stateToExtend.elephantState.isHalted) {
                        elephantPotentialNextStates.add(stateToExtend.elephantState.halt())
                    } else if (stateToExtend.elephantState.goal == stateToExtend.elephantState.position) {
                        for (valve in allValvesToOpen - stateToExtend.valvesOpened - setOf(
                            stateToExtend.myState.goal,
                            stateToExtend.elephantState.position
                        )) {
                            elephantPotentialNextStates.add(stateToExtend.elephantState.open(valve))
                        }
                        elephantPotentialNextStates.add(stateToExtend.elephantState.open("END_ELEPHANT"))
                    } else {
                        val pos = findBestStep(stateToExtend.elephantState.position, stateToExtend.elephantState.goal)
                        elephantPotentialNextStates.add(stateToExtend.elephantState.moveTo(pos))
                    }
                    // timeleft: otherwise we could halt until infinity
                    if (timeLeft(stateToExtend.myState) == 1) {
                        break
                    }
                    if (elephantPotentialNextStates.size == 1 && myPotentialNextStates.size == 1) {
                        stateToExtend = CombinedState(myPotentialNextStates[0], elephantPotentialNextStates[0])
                        myPotentialNextStates.clear()
                        elephantPotentialNextStates.clear()
                        continue
                    }
                    break
                }

                // now mix and add
                for (lhs in myPotentialNextStates) {
                    for (rhs in elephantPotentialNextStates) {
                        if (rhs.goal in lhs.valvesOpened() + rhs.valvesOpened()) {
                            continue
                        }
                        if (lhs.goal in lhs.valvesOpened() + rhs.valvesOpened()) {
                            continue
                        }
                        // can't open the same valve
                        if (lhs.futureOpenedValves().intersect(rhs.futureOpenedValves()).isNotEmpty()) {
                            // one of them has a goal that the other just reached
                            continue
                        }
                        val newState = CombinedState(lhs, rhs)
                        if (lhs.isHalted && rhs.isHalted) {
                            if ((bestHalted == null) || (calculateReleasedPressure(newState) > calculateReleasedPressure(
                                    bestHalted
                                ))
                            ) {
                                bestHalted = newState
                            }
                        } else {
                            if (potentialReleasedPressure(newState) > bestReleasedPressure) {
                                topOrder.add(newState)
                            }
                        }
                    }
                }

                if (topOrder.isEmpty()) {
                    println("no more states to examine, last state: ${calculateReleasedPressure(top)}")
                }
            }
            println("Done, released: ${calculateReleasedPressure(bestHalted!!)}")
            println("$bestHalted")

        }

        private val cachedBestStep = mutableMapOf<Pair<String, String>, String>()
        private fun findBestStep(from: String, to: String): String {
            check(from != to)
            return cachedBestStep.getOrPut(Pair(from, to)) {
                var depth = 1
                var res: String? = null
                while (res == null) {
                    res = findBestStep(from, to, depth)
                    depth++
                }
                res
            }
        }

        private fun findBestStep(from: String, to: String, depth: Int): String? {
            if (depth == 1) {
                for (step in tunnels[from]!!) {
                    if (step == to) {
                        return step
                    }
                }
                return null
            }
            for (step in tunnels[from]!!) {
                if (findBestStep(step, to, depth.minus(1)) != null) {
                    return step
                }
            }
            return null
        }

        private fun timeLeft(s: State) = totalTime - s.size()

        fun potentialReleasedPressure(s: CombinedState): Int {
            // within a maze, the potential released pressure for a given state
            // doesn't change, we can cache it
            val valvesLeftToOpen =
                (allValvesToOpen - s.valvesOpened).sortedByDescending { flowRates[it]!! }
            // This is a heuristic. The more accurate it is, the fewer nodes we have to evaluate.
            // To open a valve, we need one time unit, to move to next valve, we need one time unit.
            // So in the best case, we open a valve, move one step, open, move one step, open.
            // Opening the best valves first provides an upper limit.

            val oneIsHalted = s.myState.isHalted || s.elephantState.isHalted

            var maxPossibleRelease = 0
            // path will first continue up to goal, with a known value
            // then it can continue in other directions

            val myShortestDistanceToValve = 0
            var timeLeft = timeLeft(s.myState) - myShortestDistanceToValve
            var i = 0
            while (i in valvesLeftToOpen.indices) {
                if (timeLeft <= 0) {
                    break
                }
                // me
                maxPossibleRelease += timeLeft * flowRates[valvesLeftToOpen[i]]!!
                // elephant
                i++
                if (!oneIsHalted && i in valvesLeftToOpen.indices) {
                    maxPossibleRelease += timeLeft * flowRates[valvesLeftToOpen[i]]!!
                    i++
                }
                timeLeft -= 2
            }
            return maxPossibleRelease + calculateReleasedPressure(s)
        }

        private fun calculateReleasedPressure(s: CombinedState) =
            calculateReleasedPressure(s.myState) + calculateReleasedPressure(s.elephantState)

        private fun calculateReleasedPressure(s: State): Int {
            var timeLeft = totalTime
            var released = 0
            for (action in ActionList.toList(s.actions)) {
                timeLeft--
                if (timeLeft <= 0) {
                    break
                }
                if (action.isHalted) {
                    break
                }
                if (!action.isMove) {
                    released += timeLeft * flowRates[action.position]!!
                }
            }
            return released
        }
    }

    companion object {
        fun run(input: List<String>) {
            parseMaze(input).findBestActions()
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
            return Maze(allTunnels, flowRates)
        }
    }
}

fun main() {
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

    println("Test input:")
    Day16PartTwo.run(testInput)

    println("Input:")
    val input = File("src/main/kotlin/day16input.txt").readLines()

    // NOTE: this takes about 4 hours to run
    Day16PartTwo.run(input)

}