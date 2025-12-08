fun main() {

    data class EuclideanDistance(val first: Point3D, val second: Point3D, val distance: Double)

    fun toPoint3D(it: String): Point3D {
        val dims = it.split(",").map { it.toLong() }
        return Point3D(x = dims.first(), y = dims[1], z = dims[2])
    }

    fun getPointsAndDistances(input: List<String>): Pair<List<Point3D>, List<EuclideanDistance>> {
        val points = input.map { toPoint3D(it) }
        val distances = points.uniquePairs().map { (first, second) ->
            EuclideanDistance(first, second, euclideanDistance(first, second))
        }.sortedBy { it.distance }
        return points to distances
    }

    fun clusterWithLimitedMerges(
        points: List<Point3D>,
        distances: List<EuclideanDistance>,
        maxMerges: Int
    ): List<List<Point3D>> {
        var clusters = points.map { listOf(it) }
        for (dist in distances.take(maxMerges)) {
            val cluster1Index = clusters.indexOfFirst { it.contains(dist.first) }
            val cluster2Index = clusters.indexOfFirst { it.contains(dist.second) }
            if (cluster1Index != -1 && cluster2Index != -1 && cluster1Index != cluster2Index) {
                val newCluster = clusters[cluster1Index] + clusters[cluster2Index]
                clusters =
                    clusters.filterIndexed { index, _ -> index != cluster1Index && index != cluster2Index } + listOf(
                        newCluster
                    )
            }
        }
        return clusters
    }

    fun findLastMergeForSingleCluster(points: List<Point3D>, distances: List<EuclideanDistance>): EuclideanDistance? {
        var clusters = points.map { listOf(it) }
        var lastMerge: EuclideanDistance? = null
        for (dist in distances) {
            val cluster1Index = clusters.indexOfFirst { it.contains(dist.first) }
            val cluster2Index = clusters.indexOfFirst { it.contains(dist.second) }
            if (cluster1Index != -1 && cluster2Index != -1 && cluster1Index != cluster2Index) {
                val newCluster = clusters[cluster1Index] + clusters[cluster2Index]
                clusters =
                    clusters.filterIndexed { index, _ -> index != cluster1Index && index != cluster2Index } + listOf(
                        newCluster
                    )
                lastMerge = dist
                if (clusters.size == 1) break
            }
        }
        return lastMerge
    }

    fun part1(input: List<String>): Long {
        val (points, distances) = getPointsAndDistances(input)
        val nrOfPairs = 1000
        val clusters = clusterWithLimitedMerges(points, distances, nrOfPairs)
        return clusters.map { it.size }.sortedDescending().take(3).fold(1L) { acc, i -> acc * i }
    }

    fun part2(input: List<String>): Long {
        val (points, distances) = getPointsAndDistances(input)
        val lastMerge = findLastMergeForSingleCluster(points, distances)
        return lastMerge?.let { it.first.x * it.second.x } ?: 0L
    }

    readInput("Day08").let { part1(it).println() }
    readInput("Day08").let { part2(it).println() }
}
