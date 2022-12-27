import java.io.File

class Day19 {

    data class Resources(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0) {
        fun isLessThanOrEqualTo(other: Resources) =
            ore <= other.ore && clay <= other.clay && obsidian <= other.obsidian && geode <= other.geode

        fun minus(other: Resources) =
            Resources(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)
    }

    data class Robots(
        val ore: Int = 0,
        val clay: Int = 0,
        val obsidian: Int = 0,
        val geode: Int = 0,
    ) {
        fun addOre() = Robots(ore.plus(1), clay, obsidian, geode)
        fun addClay() = Robots(ore, clay.plus(1), obsidian, geode)
        fun addObsidian() = Robots(ore, clay, obsidian.plus(1), geode)
        fun addGeode() = Robots(ore, clay, obsidian, geode.plus(1))
    }

    data class State(val resources: Resources, val robots: Robots, val timeLeft: Int, val nextRobot: String)

    class Blueprint(input: String) {
        val id = input.split(':')[0].split(' ')[1].toInt()
        val oreRobotCost: Resources
        val clayCost: Resources
        val geodeCost: Resources
        val obsidianCost: Resources

        init {
            val lines = input.split('\n').joinToString("").split(':')[1].split('.')
            check("Each ore robot costs" in lines[0])
            check("Each clay robot costs" in lines[1])
            check("Each obsidian robot costs" in lines[2])
            check("Each geode robot costs" in lines[3])
            val oreRobotCost = lines[0].trim().split(' ')[4].toInt()
            this.oreRobotCost = Resources(ore = oreRobotCost)
            val clayRobotCost = lines[1].trim().split(' ')[4].toInt()
            clayCost = Resources(ore = clayRobotCost)
            val obsidianRobotOreCost = lines[2].trim().split(' ')[4].toInt()
            val obsidianRobotClayCost = lines[2].trim().split(' ')[7].toInt()
            this.obsidianCost = Resources(ore = obsidianRobotOreCost, clay = obsidianRobotClayCost)
            val geodeRobotOreCost = lines[3].trim().split(' ')[4].toInt()
            val geodeRobotObsidianCost = lines[3].trim().split(' ')[7].toInt()
            this.geodeCost = Resources(ore = geodeRobotOreCost, obsidian = geodeRobotObsidianCost)
        }

        fun calculateQuality(timeLeft: Int): Int {
            // first attempt, runs out of memory: potentialGeodes heuristic isn't good enough
//            val states = PriorityQueue<State> { lhs, rhs -> potentialGeodes(rhs) - potentialGeodes(lhs) }
            val states = mutableListOf<State>()
            // we can only build ore and clay robots in the start state
            states.add(State(Resources(), Robots(ore = 1), timeLeft, ORE_ROBOT))
            states.add(State(Resources(), Robots(ore = 1), timeLeft, CLAY_ROBOT))
            var bestGeodes = 0

            while (states.isNotEmpty()) {
                val state = states.removeLast()
                if (state.timeLeft == 0) {
                    if (bestGeodes < state.resources.geode) {
                        bestGeodes = maxOf(state.resources.geode, bestGeodes)
                    }
                    // no need to explore further
                    continue
                }
                val nextRobots = mutableListOf(ORE_ROBOT, CLAY_ROBOT)
                if (state.robots.clay != 0) {
                    nextRobots.add(OBSIDIAN_ROBOT)
                    if (state.robots.obsidian != 0) {
                        nextRobots.add(GEODE_ROBOT)
                    }
                }
                val collectedResources = Resources(
                    ore = state.resources.ore + state.robots.ore,
                    clay = state.resources.clay + state.robots.clay,
                    obsidian = state.resources.obsidian + state.robots.obsidian,
                    geode = state.resources.geode + state.robots.geode,
                )
                when {
                    state.nextRobot == ORE_ROBOT && oreRobotCost.isLessThanOrEqualTo(state.resources) -> {
                        val newResources = collectedResources.minus(oreRobotCost)
                        val newRobots = state.robots.addOre()
                        states.addAll(nextRobots.map{State(newResources, newRobots, state.timeLeft.minus(1), it)})
                    }
                    state.nextRobot == CLAY_ROBOT && clayCost.isLessThanOrEqualTo(state.resources) -> {
                        val newResources = collectedResources.minus(clayCost)
                        val newRobots = state.robots.addClay()
                        states.addAll(nextRobots.map{State(newResources, newRobots, state.timeLeft.minus(1), it)})
                    }
                    state.nextRobot == OBSIDIAN_ROBOT && obsidianCost.isLessThanOrEqualTo(state.resources) -> {
                        val newResources = collectedResources.minus(obsidianCost)
                        val newRobots = state.robots.addObsidian()
                        states.addAll(nextRobots.map{State(newResources, newRobots, state.timeLeft.minus(1), it)})
                    }
                    state.nextRobot == GEODE_ROBOT && geodeCost.isLessThanOrEqualTo(state.resources) -> {
                        val newResources = collectedResources.minus(geodeCost)
                        val newRobots = state.robots.addGeode()
                        states.addAll(nextRobots.map{State(newResources, newRobots, state.timeLeft.minus(1), it)})
                    }
                    else -> {
                        states.add(State(collectedResources, state.robots, state.timeLeft.minus(1), state.nextRobot))
                    }
                }
            }
            return bestGeodes
        }

//        private fun potentialGeodes(state: State): Int {
//            return potentialGeodes(state.resources.geode, state.robots.geode, state.timeLeft)
//        }
//
//        private fun potentialGeodes(currentGeodes: Int, currentGeodeBots: Int, timeLeft: Int): Int {
//            if (timeLeft == 0) {
//                return currentGeodes
//            }
//            if (timeLeft == 1) {
//                return currentGeodes + currentGeodeBots
//            }
//            return potentialGeodes(currentGeodes + currentGeodeBots, currentGeodeBots + 1, timeLeft - 1)
//        }
    }

    companion object {
        val ORE_ROBOT = "ore robot"
        val CLAY_ROBOT = "clay robot"
        val OBSIDIAN_ROBOT = "obsidian robot"
        val GEODE_ROBOT = "geode robot"
        fun run(input: List<String>) {
            val blueprints = parseBlueprints(input)

            println("${blueprints.size} blueprints parsed")

            runPart1(blueprints)
            runPart2(blueprints)
        }

        private fun runPart1(blueprints: List<Blueprint>) {
            var part1 = 0
            blueprints.forEach {
                val quality = it.calculateQuality(24)
                println("Blueprint ${it.id} creates ${quality} geodes")
                part1 += it.id * quality
            }
            println("sum of quality levels: $part1")
        }

        private fun runPart2(blueprints: List<Blueprint>) {
            val firstBlueprints = blueprints.take(3)
            firstBlueprints.map{
                val quality = it.calculateQuality(32)
                println("Blueprint ${it.id} creates ${quality} geodes")
                quality
            }.reduce{acc, q -> acc * q}.let{
                println("Product of qualities: $it")
            }
        }

        fun parseBlueprints(input: List<String>): List<Blueprint> {
            return input.map { Blueprint(it) }
        }


    }
}

fun main() {
    val testInput =
        """Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.""".split(
            '\n'
        )

    println("Test input:")
    Day19.run(testInput)

    // observation: instead of trying all possibilities, we
    // just need an order of robots created, and try those out.
    // Every time we can build a certain type of robot, we do it.
    // We always need one robot of every type, the remaining
    // robots can vary by type and count.

    println("Input:")
    val input = File("src/main/kotlin/day19input.txt").readLines()
    Day19.run(input)
}