fun main() {
    fun parseGraph(input: List<String>): Map<String, List<String>> =
        input
            .filter { it.isNotBlank() }
            .associate { line ->
                val (from, toNodes) = line.split(": ")
                from to toNodes.split(" ")
            }

    fun part1(input: List<String>): Long {
        val graph = parseGraph(input)
        val start = "you"
        val end = "out"
        return countAllPathsInDAG(graph, start, end)
    }

    fun part2(input: List<String>): Long {
        val graph = parseGraph(input)
        val start = "svr"
        val end = "out"
        val requiredNodes = setOf("dac", "fft")
        return countPathsWithRequiredNodes(graph, start, end, requiredNodes)
    }
    readInput("Day11").let { part1(it).println() }
    readInput("Day11").let { part2(it).println() }

}
