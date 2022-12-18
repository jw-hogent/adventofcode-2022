import java.io.File
import kotlin.math.abs

class Day15 {
    data class Point(val x: Int, val y: Int) {
        fun distanceTo(p: Point): Int = abs(x - p.x) + abs(y - p.y)
    }
    data class SensorRange(val beacon: Point, val d: Int) {
        fun inRange(p: Point): Boolean = p.distanceTo(beacon) <= d
    }

    companion object {
        fun run(input: List<String>, lineToCheck: Int) {
            val notBeacon = mutableSetOf<Point>()
            val beacons = mutableSetOf<Point>()
            for (line in input) {
                val sensor = parseSensorLocation(line)
                val beacon = parseBeaconLocation(line)
                beacons.add(beacon)
                val d = sensor.distanceTo(beacon)
                Point(sensor.x, lineToCheck)
                for (pos in sensor.x.minus(d) .. sensor.x.plus(d)) {
                    val p = Point(pos, lineToCheck)
                    if (sensor.distanceTo(p) <= d) {
                        notBeacon.add(p)
                    }
                }
            }
            val not = notBeacon.subtract(beacons).size
            println("Not beacon on line $lineToCheck: $not")
        }
        fun run2(input: List<String>, minCoordinate: Int, maxCoordinate: Int) {
            val sensorRanges = mutableListOf<SensorRange>()
            for (line in input) {
                val sensor = parseSensorLocation(line)
                val beacon = parseBeaconLocation(line)
                val d = sensor.distanceTo(beacon)
                sensorRanges.add(SensorRange(sensor, d))
            }
            var x = minCoordinate
            var y = minCoordinate
            while (sensorRanges.any { it.inRange(Point(x, y)) }) {
                val matchingSensor = sensorRanges.first{it.inRange(Point(x, y))}
//                while (matchingSensor != null) {
//                }
                val outOfRangePos = Point(matchingSensor.beacon.x + matchingSensor.d - abs(matchingSensor.beacon.y - y) + 1, y)
                check(!matchingSensor.inRange(outOfRangePos))
                x = outOfRangePos.x
                while (matchingSensor.inRange(Point(x, y)) && x <= maxCoordinate) {
                    x++
                }
                if (x > maxCoordinate) {
                    x = 0
                    y++
                    continue
                }
                if (y > maxCoordinate) {
                    break
                }
            }
            println("beacon is at: $x, $y; freq: ${4000000L * x + y}")
        }
        private fun parseSensorLocation(line: String): Point {
            val lhs = line.split(":")[0].split(" ")
            val x = lhs[2].split(",")[0].split("=")[1].toInt()
            val y = lhs[3].split("=")[1].toInt()
            return Point(x, y)
        }
        private fun parseBeaconLocation(line: String): Point {
            val rhs = line.split(":")[1].trimStart().split(" ")
            val x = rhs[4].split(",")[0].split("=")[1].toInt()
            val y = rhs[5].split("=")[1].toInt()
            return Point(x, y)
        }
    }
}

fun main() {
    val testInput = """Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3""".split("\n")
    val input = File("src/main/kotlin/day15input.txt").readLines()

    println("Test input:")
    Day15.run(testInput, 10)
    Day15.run2(testInput, 0, 20)

    println("Input:")
    Day15.run(input, 2000000)
    Day15.run2(input, 0, 4000000)

}
