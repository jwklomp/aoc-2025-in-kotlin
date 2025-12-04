fun main() {
    val roll = "@"

    fun makeFloorGrid(input: List<String>): Grid2D<String> {
        val chunked = input.filterNot { it.isEmpty() }.map { it.chunked(1).toMutableList() }
        return Grid2D(chunked)
    }

    fun getAccessibleRolls(floorGrid: Grid2D<String>, rolls: List<Cell<String>>): List<Cell<String>> = rolls
        .filter { cell -> floorGrid.getSurrounding(cell.x, cell.y).count { it.value == roll } < 4 }

    fun part1(input: List<String>): Int {
        val floorGrid = makeFloorGrid(input)
        val rolls = floorGrid.getAllCells().filter { it.value == roll }
        return getAccessibleRolls(floorGrid, rolls).size
    }

    fun part2(input: List<String>): Int {
        val floorGrid = makeFloorGrid(input)
        val startNumberOfRolls = floorGrid.getAllCells().count { it.value == roll }

        tailrec fun removeAccessible() {
            val rolls = floorGrid.getAllCells().filter { it.value == roll }
            val accessible = getAccessibleRolls(floorGrid, rolls)
            if (accessible.isEmpty()) return
            accessible.forEach { cell -> floorGrid.setCell(cell.x, cell.y, ".") }
            removeAccessible()
        }
        removeAccessible()
        val endNumberOfRolls = floorGrid.getAllCells().count { it.value == roll }
        return startNumberOfRolls - endNumberOfRolls
    }

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
