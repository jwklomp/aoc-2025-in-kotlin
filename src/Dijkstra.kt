/**
 * Dijkstra shortest path algorithm adapted for a grid. Used for AOC 2022 day 12
 *
 * @param graph as List of nodes.
 * @param source start node
 * @param target end node
 * @param filterNeighborsFn function to filter neighbor cells
 * @return the shortest path as list of nodes.
 */
fun <T> dijkstraSP(
    graph: List<Node<T>>,
    source: Node<T>,
    target: Node<T>,
    filterNeighborsFn: (it: Node<T>, u: Node<T>) -> Boolean,
): ArrayDeque<Node<T>> {
    val q = mutableSetOf<Node<T>>()
    val dist = mutableMapOf<Node<T>, Int?>()
    val prev = mutableMapOf<Node<T>, Node<T>?>()
    for (vertex in graph) {
        dist[vertex] = Int.MAX_VALUE
        prev[vertex] = null
        q.add(vertex)
    }
    dist[source] = 0
    while (q.isNotEmpty()) {
        val u = q.minByOrNull { dist[it]!! }!!
        q.remove(u)
        if (u == target) break
        for (v in q.filter { filterNeighborsFn(it, u) }) {
            val alt = dist[u]!! + 1
            if (alt < dist[v]!!) {
                dist[v] = alt
                prev[v] = u
            }
        }
    }

    // all found.
    val s = ArrayDeque<Node<T>>()
    var u: Node<T>? = target
    if (prev[u] != null || u == source) {
        while (u != null) {
            s.addFirst(u)
            u = prev[u]
        }
    }

    return s
}
