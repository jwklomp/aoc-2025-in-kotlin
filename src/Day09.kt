import kotlin.math.max
import kotlin.math.min

fun main() {

    data class Rectangle(val minX: Long, val maxX: Long, val minY: Long, val maxY: Long) {
        val area: Long get() = (maxX - minX + 1) * (maxY - minY + 1)

        val corners: List<Point>
            get() = listOf(
                Point(minX, minY), Point(maxX, minY),
                Point(minX, maxY), Point(maxX, maxY)
            )
    }

    fun String.toPoint(): Point = split(",").let { (x, y) -> Point(x.toLong(), y.toLong()) }

    fun createRectangle(p1: Point, p2: Point) = Rectangle(
        minX = min(p1.x, p2.x),
        maxX = max(p1.x, p2.x),
        minY = min(p1.y, p2.y),
        maxY = max(p1.y, p2.y)
    )

    fun part1(input: List<String>): Long {
        val points = input.map { it.toPoint() }
        return points.uniquePairs()
            .map { (p1, p2) -> createRectangle(p1, p2) }
            .maxOf { it.area }
    }

    fun part2(input: List<String>): Long {
        val points = input.map { it.toPoint() }

        val polygonEdges = points.indices.map { i -> points[i] to points[(i + 1) % points.size] }

        fun isPointOnEdge(point: Point, edge: Pair<Point, Point>): Boolean {
            val (p1, p2) = edge
            return (p1.x == p2.x && p1.x == point.x && point.y in min(p1.y, p2.y)..max(p1.y, p2.y)) ||
                    (p1.y == p2.y && p1.y == point.y && point.x in min(p1.x, p2.x)..max(p1.x, p2.x))
        }

        fun isPointInsidePolygon(point: Point): Boolean =
            points.indices.count { i ->
                val pi = points[i]
                val pj = points[(i + points.size - 1) % points.size]
                (pi.y > point.y) != (pj.y > point.y) &&
                        point.x < (pj.x - pi.x) * (point.y - pi.y) / (pj.y - pi.y) + pi.x
            } % 2 == 1

        fun isPointInsideOrOnPolygon(point: Point): Boolean =
            point in points ||
                    polygonEdges.any { isPointOnEdge(point, it) } ||
                    isPointInsidePolygon(point)

        fun Rectangle.hasEdgeCrossingInterior(): Boolean =
            polygonEdges.any { (p1, p2) ->
                when {
                    p1.x == p2.x ->
                        p1.x in (minX + 1) until maxX &&
                                max(p1.y, p2.y) > minY && min(p1.y, p2.y) < maxY

                    p1.y == p2.y ->
                        p1.y in (minY + 1) until maxY &&
                                max(p1.x, p2.x) > minX && min(p1.x, p2.x) < maxX

                    else -> false
                }
            }

        fun Rectangle.isValid(): Boolean =
            corners.all { isPointInsideOrOnPolygon(it) } &&
                    !hasEdgeCrossingInterior()

        return points.uniquePairs()
            .map { (p1, p2) -> createRectangle(p1, p2) }
            .filter { it.isValid() }
            .maxOf { it.area }
    }
    readInput("Day09_test").let { part1(it).println() }
    readInput("Day09_test").let { part2(it).println() }

    readInput("Day09").let { part1(it).println() }
    readInput("Day09").let { part2(it).println() }

}
