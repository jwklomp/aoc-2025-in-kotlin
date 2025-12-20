import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)


class FixedSizeQueue<T>(private val maxSize: Int) {
    private val queue = ArrayDeque<T>()

    fun enqueue(element: T) {
        queue.addFirst(element)

        if (queue.size > maxSize) {
            queue.removeLast()
        }
    }

    fun toList(): List<T> {
        return queue.toList()
    }
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
}


/**
 * Helper extension function to get the next direction when turning right.
 */
fun Direction.turnRight(): Direction = when (this) {
    Direction.UP -> Direction.RIGHT
    Direction.RIGHT -> Direction.DOWN
    Direction.DOWN -> Direction.LEFT
    Direction.LEFT -> Direction.UP
}

// find the GCD (Greatest Common Divisor)
fun gcd(
    a: Long,
    b: Long,
): Long = if (b == 0L) a else gcd(b, a % b)

// find the LCM (Least Common Multiple)
fun lcm(
    a: Long,
    b: Long,
): Long = abs(a * b) / gcd(a, b)

// Find the LCM of a list of numbers
fun findLCM(numbers: List<Long>): Long = numbers.reduce { acc, num -> lcm(acc, num) }

/**
 * Extension function to get all index positions of a given element in a collection
 */
fun <E> Iterable<E>.indexesOf(e: E) = mapIndexedNotNull { index, elem -> index.takeIf { elem == e } }

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun findIndex(
    haystack2D: List<List<String>>,
    needle: String,
): MutableList<Int> =
    mutableListOf(-1, -1).apply {
        haystack2D.forEachIndexed { i, r ->
            r.forEachIndexed { j, c ->
                if (c == needle) {
                    this[0] = j
                    this[1] = i
                }
            }
        }
    }

/**
 * Extension function that is like takeWhile, yet also takes the first element not making the test.
 */
fun <T> Iterable<T>.takeWhileInclusive(predicate: (T) -> Boolean): List<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

data class Point(val x: Long, val y: Long)

fun manhattanDistance(
    first: Point,
    second: Point,
) = abs(first.x - second.x) + abs(first.y - second.y)

data class Interval(val from: Long, val to: Long)

/**
 * Merge overlapping intervals.
 * Takes a list of intervals and returns a list of non-overlapping intervals.
 */
fun mergeIntervals(intervals: List<Interval>) =
    intervals
        .sortedWith(compareBy { it.from })
        .fold(listOf<Interval>()) { sum, item ->
            val last = sum.lastOrNull()
            if (last != null && last.to >= item.from) {
                val old = sum.dropLast(1)
                old + Interval(from = last.from, to = max(last.to, item.to))
            } else {
                sum + item
            }
        }

inline fun <reified T> transpose(xs: List<List<T>>): List<List<T>> {
    val cols = xs[0].size
    val rows = xs.size
    return List(cols) { j ->
        List(rows) { i ->
            xs[i][j]
        }
    }
}

/**
 * Pick's Theorem: A = I + (B/2) - 1
 * Calculates the number of lattice points strictly inside a lattice polygon
 * See https://en.wikipedia.org/wiki/Pick%27s_theorem
 * @param area The area of the lattice polygon.
 * @param boundaryVertices The number of lattice points on the boundary of the polygon.
 * @return The number of lattice points strictly inside the polygon.
 */
fun calculateInsideVertices(
    area: Double,
    boundaryVertices: Double,
): Double {
    val insidePoints = area - (boundaryVertices / 2) + 1
    return if (insidePoints >= 0) insidePoints else 0.0
}

fun splitOnEmptyLine(input: List<String>): List<List<String>> =
    input.fold(mutableListOf(mutableListOf<String>())) { acc, string ->
        if (string.isBlank()) {
            acc.add(mutableListOf())
        } else {
            acc.last().add(string)
        }
        acc
    }

/**
 * Extension function on list to have 2D windowed lists.
 */
fun <T> List<List<T>>.windowed2D(size: Int): List<List<List<T>>> {
    if (size <= 0 || size > this.size || this.any { it.size < size }) return emptyList()

    return (0..this.size - size).flatMap { rowStart ->
        (0..this[0].size - size).map { colStart ->
            (0 until size).map { rowOffset ->
                this[rowStart + rowOffset].subList(colStart, colStart + size)
            }
        }
    }
}

fun <T> List<T>.uniquePairs(): List<Pair<T, T>> {
    return this.flatMapIndexed { index, first ->
        this.subList(index + 1, this.size).map { second -> first to second }
    }
}

data class Point3D(val x: Long, val y: Long, val z: Long)

fun euclideanDistance(first: Point3D, second: Point3D): Double {
    val dx = (second.x - first.x).toDouble()
    val dy = (second.y - first.y).toDouble()
    val dz = (second.z - first.z).toDouble()
    return sqrt(dx * dx + dy * dy + dz * dz)
}

/**
 * Finds all paths from start to end in a directed acyclic graph (DAG).
 *
 * @param graph Map from node to list of adjacent nodes
 * @param start Starting node
 * @param end Ending node
 * @return List of all distinct paths, where each path is a list of nodes
 */
fun <T> findAllPathsInDAG(
    graph: Map<T, List<T>>,
    start: T,
    end: T
): List<List<T>> {
    val allPaths = mutableListOf<List<T>>()

    fun findPaths(current: T, currentPath: List<T>) {
        val newPath = currentPath + current

        // Base case: reached the end
        if (current == end) {
            allPaths.add(newPath)
            return
        }

        // Get neighbors, or empty list if no outgoing edges
        val neighbors = graph[current] ?: emptyList()

        // Recursively explore all neighbors
        neighbors.forEach { neighbor ->
            findPaths(neighbor, newPath)
        }
    }

    findPaths(start, emptyList())
    return allPaths
}

/**
 * Counts all paths from start to end in a directed acyclic graph (DAG).
 * Uses memoization for efficiency - much faster than generating all paths.
 *
 * @param graph Map from node to list of adjacent nodes
 * @param start Starting node
 * @param end Ending node
 * @return Total number of distinct paths from start to end
 */
fun <T> countAllPathsInDAG(
    graph: Map<T, List<T>>,
    start: T,
    end: T
): Long {
    val memo = mutableMapOf<T, Long>()

    fun countPaths(current: T): Long {
        // Base case: reached the end
        if (current == end) return 1L

        // Check memo cache
        memo[current]?.let { return it }

        // Get neighbors, or empty list if no outgoing edges
        val neighbors = graph[current] ?: emptyList()

        // Sum all paths from neighbors to end
        val pathCount = neighbors.sumOf { neighbor -> countPaths(neighbor) }

        // Cache and return
        memo[current] = pathCount
        return pathCount
    }

    return countPaths(start)
}

/**
 * Finds all paths from start to end in a DAG that visit all required nodes.
 *
 * @param graph Map from node to list of adjacent nodes
 * @param start Starting node
 * @param end Ending node
 * @param requiredNodes Set of nodes that must be visited on each path
 * @return List of all distinct paths that visit all required nodes
 */
fun <T> findPathsWithRequiredNodes(
    graph: Map<T, List<T>>,
    start: T,
    end: T,
    requiredNodes: Set<T>
): List<List<T>> {
    val allPaths = mutableListOf<List<T>>()

    fun findPaths(current: T, currentPath: List<T>, visitedRequired: Set<T>) {
        val newPath = currentPath + current
        val newVisited = if (current in requiredNodes) visitedRequired + current else visitedRequired

        // Base case: reached the end
        if (current == end) {
            if (newVisited == requiredNodes) {
                allPaths.add(newPath)
            }
            return
        }

        // Get neighbors, or empty list if no outgoing edges
        val neighbors = graph[current] ?: emptyList()

        // Recursively explore all neighbors
        neighbors.forEach { neighbor ->
            findPaths(neighbor, newPath, newVisited)
        }
    }

    findPaths(start, emptyList(), emptySet())
    return allPaths
}

/**
 * Counts all paths from start to end in a DAG that visit all required nodes.
 * Uses memoization with state tracking - much faster than generating all paths.
 *
 * @param graph Map from node to list of adjacent nodes
 * @param start Starting node
 * @param end Ending node
 * @param requiredNodes Set of nodes that must be visited on each path
 * @return Total number of distinct paths that visit all required nodes
 */
fun <T> countPathsWithRequiredNodes(
    graph: Map<T, List<T>>,
    start: T,
    end: T,
    requiredNodes: Set<T>
): Long {
    // State: (current node, set of required nodes visited so far)
    data class State(val node: T, val visited: Set<T>)

    val memo = mutableMapOf<State, Long>()

    fun countPaths(current: T, visitedRequired: Set<T>): Long {
        // Update visited required nodes if current is required
        val newVisited = if (current in requiredNodes) visitedRequired + current else visitedRequired

        // Base case: reached the end
        if (current == end) {
            return if (newVisited == requiredNodes) 1L else 0L
        }

        // Check memo cache
        val state = State(current, newVisited)
        memo[state]?.let { return it }

        // Get neighbors, or empty list if no outgoing edges
        val neighbors = graph[current] ?: emptyList()

        // Sum all paths from neighbors to end
        val pathCount = neighbors.sumOf { neighbor -> countPaths(neighbor, newVisited) }

        // Cache and return
        memo[state] = pathCount
        return pathCount
    }

    return countPaths(start, emptySet())
}

