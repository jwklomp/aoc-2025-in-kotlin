import kotlin.math.abs

class Grid2D<T>(private val grid: List<MutableList<T>>) {
    private val rowLength: Int = grid.first().size // corresponds to x
    private val columnLength: Int = grid.size // corresponds to y

    private val surrounding: List<Pair<Int, Int>> =
        listOf(Pair(-1, -1), Pair(-1, 0), Pair(-1, 1), Pair(0, -1), Pair(0, 1), Pair(1, -1), Pair(1, 0), Pair(1, 1))
    private val adjacent: List<Pair<Int, Int>> = listOf(Pair(-1, 0), Pair(0, -1), Pair(0, 1), Pair(1, 0))

    fun getNrOfRows(): Int = rowLength

    fun getNrOfColumns(): Int = columnLength

    fun getCell(x: Int, y: Int): Cell<T> = Cell(value = grid[y][x], x = x, y = y)

    // update the underlying grid value based on x and y coordinates and the new value
    fun setCell(x: Int, y: Int, value: T) {
        grid[y][x] = value
    }

    fun getCellOrNull(x: Int, y: Int): Cell<T>? {
        return if (y in grid.indices && x in grid[y].indices) {
            Cell(value = grid[y][x], x = x, y = y)
        } else {
            null
        }
    }

    fun getAllCells(): List<Cell<T>> =
        grid.flatMapIndexed { y, row -> row.mapIndexed { x, v -> Cell(value = v, x = x, y = y) } }

    fun getCellsFiltered(filterFn: (Cell<T>) -> (Boolean)): List<Cell<T>> = getAllCells().filter { filterFn(it) }

    fun getSurrounding(x: Int, y: Int): List<Cell<T>> = filterPositions(surrounding, x, y)

    fun getAdjacent(x: Int, y: Int): List<Cell<T>> = filterPositions(adjacent, x, y)

    fun getRow(rowNr: Int): List<Cell<T>> =
        getCellsFiltered { it.y == rowNr }.sortedBy { it.x } // row: x variable, y fixed

    fun getCol(colNr: Int): List<Cell<T>> =
        getCellsFiltered { it.x == colNr }.sortedBy { it.y } // row: y variable,x fixed

    // print the contents of the grid to the console separating lines by '\n'
    fun printGrid() {
        println(toString())
    }


    fun isOnEdge(x: Int, y: Int) = x == 0 || y == 0 || x == rowLength - 1 || y == columnLength - 1

    fun <T> cellToId(c: Cell<T>): String = "x${c.x}-y${c.y}"

    fun getXY(input: String): Pair<Int, Int>? {
        val regex = Regex("""x(\d+)-y(\d+)""")
        val matchResult = regex.find(input)

        return matchResult?.let {
            val (x, y) = it.destructured
            Pair(x.toInt(), y.toInt())
        }
    }

    private fun filterPositions(positions: List<Pair<Int, Int>>, x: Int, y: Int): List<Cell<T>> =
        positions
            .map { Pair(it.first + x, it.second + y) }
            .filter { it.first >= 0 && it.second >= 0 }
            .filter { it.first < rowLength && it.second < columnLength }
            .map { getCell(it.first, it.second) }

    override fun toString(): String {
        return grid.joinToString(separator = "\n") { row ->
            row.joinToString(separator = "") { it.toString() }
        }
    }
}

data class Cell<T>(var value: T, val x: Int, val y: Int)

enum class Border{
    T, B, L, R
}

data class Bordered<T>(val id: T, val borders: MutableList<Border> = mutableListOf())

typealias Node<T> = Cell<T>

// used for the area and perimeter calculations
data class AreaData<T>(
    val id: T,
    val area: Int,
    val perimeter: Int,
    val sides: Int
)

// Determine if two cells are direct neighbors
fun <T> Cell<T>.isNeighborOf(u: Cell<T>): Boolean {
    val xDist = abs(this.x - u.x)
    val yDist = abs(this.y - u.y)
    return xDist + yDist == 1
}

/**
 * Use Shoelace formula to calculate the area of a simple polygon whose vertices are described
 * by their Cartesian coordinates in the plane. See https://www.101computing.net/the-shoelace-algorithm/
 */
fun calculateShoelaceArea(cells: List<Cell<*>>): Double {
    var area = 0.0

    cells.windowed(2) { (currentCell, nextCell) ->
        area += currentCell.x * nextCell.y - nextCell.x * currentCell.y
    }

    // Add the last edge
    val lastCell = cells.last()
    val firstCell = cells.first()
    area += lastCell.x * firstCell.y - firstCell.x * lastCell.y

    return (abs(area) / 2.0)
}

/**
 * Calculate the perimeter of a region efficiently.
 * Each cell contributes 4 edges, and shared edges are subtracted.
 */
fun calculatePerimeter(grid: Grid2D<*>, region: List<Cell<*>>): Int {
    var totalCells = 0
    for (cell in region) {
        // Start with 4 edges per cell
        totalCells += 4

        // Subtract edges shared with adjacent cells in the same region
        val sharedEdges = grid.getAdjacent(cell.x, cell.y)
            .count { it in region } // Count only adjacent cells that are part of the same region
        totalCells -= sharedEdges
    }

    return totalCells
}

/**
 * Function to find all connected areas (regions) with the same letter in the grid.
 */
fun findAreas(grid: Grid2D<Bordered<String>>): Map<String, List<List<Cell<Bordered<String>>>>> {
    val visited = mutableSetOf<Cell<Bordered<String>>>()
    fun dfs(cell: Cell<Bordered<String>>, region: MutableList<Cell<Bordered<String>>>) {
        visited.add(cell)
        region.add(cell)

        for (neighbor in grid.getAdjacent(cell.x, cell.y)) {
            if (neighbor.value.id == cell.value.id && neighbor !in visited) {
                dfs(neighbor, region)
            }
        }
    }
    val regions = mutableMapOf<String, MutableList<List<Cell<Bordered<String>>>>>()
    for (cell in grid.getAllCells()) {
        if (cell !in visited) {
            val region = mutableListOf<Cell<Bordered<String>>>()
            dfs(cell, region)
            regions.computeIfAbsent(cell.value.id) { mutableListOf() }.add(region.sortedBy { it.x * 1000 + it.y })
        }
    }
    return regions
}
/**
 * For an area calculate the number of uninterrupted outer border lines.
 */
fun calculateSides(grid: Grid2D<*>, region: List<Cell<Bordered<String>>>): Int {
    region.forEach { cell ->
        setBoundaryBorders(grid, cell, region)
    }
    val rowsWithTopBorder = region.filter { Border.T in it.value.borders }.groupBy { it.y }
    val rowsWithTopBorderGaps = calculateRowGaps(rowsWithTopBorder)
    val rowsWithBottomBorder = region.filter { Border.B in it.value.borders }.groupBy { it.y }
    val rowsWithBottomBorderGaps = calculateRowGaps(rowsWithBottomBorder)

    val columnsWithLeftBorder = region.filter { Border.L in it.value.borders }.groupBy { it.x }
    val columnsWithLeftBorderGaps = calculateColumnGaps(columnsWithLeftBorder)
    val columnsWithRightBorder = region.filter { Border.R in it.value.borders }.groupBy { it.x }
    val columnsWithRightBorderGaps = calculateColumnGaps(columnsWithRightBorder)

    return rowsWithTopBorderGaps + rowsWithBottomBorderGaps + columnsWithLeftBorderGaps + columnsWithRightBorderGaps
}

private fun calculateColumnGaps(columnsWithLeftBorder: Map<Int, List<Cell<Bordered<String>>>>) =
    columnsWithLeftBorder.map { (_, cells) ->
        val yPositions = cells.map { it.y }.distinct()
        val gaps = yPositions.windowed(2).count { (a, b) -> b - a > 1 }
        gaps + 1
    }.sum()

private fun calculateRowGaps(rowsWithTopBorder: Map<Int, List<Cell<Bordered<String>>>>): Int {
    val rowsWithTopBorderGaps = rowsWithTopBorder.map { (_, cells) ->
        val xPositions = cells.map { it.x }.distinct()
        val gaps = xPositions.windowed(2).count { (a, b) -> b - a > 1 }
        gaps + 1
    }.sum()
    return rowsWithTopBorderGaps
}

fun setBoundaryBorders(grid: Grid2D<*>, cell: Cell<Bordered<String>>, region: List<Cell<Bordered<String>>>) {
    val borders = listOfNotNull(
        Border.T.takeIf { cell.y == 0 || grid.getCell(cell.x, cell.y - 1) !in region },
        Border.B.takeIf { cell.y == grid.getNrOfColumns() - 1 || grid.getCell(cell.x, cell.y + 1) !in region },
        Border.L.takeIf { cell.x == 0 || grid.getCell(cell.x - 1, cell.y) !in region },
        Border.R.takeIf { cell.x == grid.getNrOfRows() - 1 || grid.getCell(cell.x + 1, cell.y) !in region }
    )

    if (borders.isNotEmpty()) {
        cell.value.borders.addAll(borders)
    }
}

/**
 * Process the grid to calculate regions, areas (number of cells), and perimeters.
 */
fun getRegionsWithData(grid: Grid2D<Bordered<String>>): List<AreaData<String>> {
    val areas = findAreas(grid)
    return areas.flatMap { (value, regionGroups) ->
        regionGroups.map { regionCells ->
            val area = regionCells.size
            val perimeter = calculatePerimeter(grid, regionCells)
            val sides = calculateSides(grid, regionCells)
            AreaData(value, area, perimeter, sides)
        }
    }
}
