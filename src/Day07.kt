fun main() {
    val startCell = "S"
    val splitter = "^"

    data class Position(val x: Int, val y: Int)

    fun makeManifoldGrid(input: List<String>): Grid2D<String> =
        Grid2D(input.filterNot { it.isEmpty() }.map { it.chunked(1).toMutableList() })

    tailrec fun processRows(
        manifoldGrid: Grid2D<String>,
        currentRowNr: Int,
        beamPositions: Set<Position>,
        splitterCount: Int
    ): Int {
        if (currentRowNr >= manifoldGrid.getNrOfRows() - 1) return splitterCount

        val (newBeamPositions, additionalSplitters) = beamPositions.fold(emptySet<Position>() to 0) { (positions, count), pos ->
            val cellBeneath = manifoldGrid.getCell(pos.x, currentRowNr + 1)
            when (cellBeneath.value) {
                splitter -> positions + listOfNotNull(
                    Position(pos.x - 1, currentRowNr + 1).takeIf { pos.x > 0 },
                    Position(pos.x + 1, currentRowNr + 1).takeIf { pos.x < manifoldGrid.getNrOfColumns() - 1 }
                ) to count + 1

                else -> positions + Position(pos.x, currentRowNr + 1) to count
            }
        }

        return processRows(manifoldGrid, currentRowNr + 1, newBeamPositions, splitterCount + additionalSplitters)
    }

    fun part1(input: List<String>): Int = makeManifoldGrid(input).let { grid ->
        val startPos = grid.getRow(0).first { it.value == startCell }
        processRows(grid, 0, setOf(Position(startPos.x, startPos.y + 1)), 0)
    }

    tailrec fun countPaths(manifoldGrid: Grid2D<String>, currentRowNr: Int, pathCounts: Map<Int, Long>): Long {
        if (currentRowNr >= manifoldGrid.getNrOfRows() - 1) return pathCounts.values.sum()

        val nextPathCounts = pathCounts.flatMap { (x, count) ->
            when (manifoldGrid.getCell(x, currentRowNr + 1).value) {
                splitter -> listOfNotNull(
                    (x - 1).takeIf { it >= 0 }?.let { it to count },
                    (x + 1).takeIf { it < manifoldGrid.getNrOfColumns() - 1 }?.let { it to count }
                )

                else -> listOf(x to count)
            }
        }.groupBy { it.first }
            .mapValues { it.value.sumOf { (_, count) -> count } }

        return countPaths(manifoldGrid, currentRowNr + 1, nextPathCounts)
    }

    fun part2(input: List<String>): Long = makeManifoldGrid(input).let { grid ->
        val startX = grid.getRow(0).first { it.value == startCell }.x
        countPaths(grid, 0, mapOf(startX to 1L))
    }

    readInput("Day07").let { part1(it).println() }
    readInput("Day07").let { part2(it).println() }
}
