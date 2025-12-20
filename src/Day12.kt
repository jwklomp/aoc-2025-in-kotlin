fun main() {
    //val shapeSizes = listOf(7, 7, 7, 7, 7, 7) // test data
    val shapeSizes = listOf(5, 7, 6, 7, 7, 7)

    data class Dimension(val x: Int, val y: Int)
    data class Situation(val boardDimension: Dimension, val presents: List<Int>)

    fun makeSituations(input: List<String>): List<Situation> = input.map {
        val (dimensions, presents) = it.split(": ")
        val (x, y) = dimensions.split("x").map { it.toInt() }
        Situation(boardDimension = Dimension(x, y), presents = presents.split(" ").map { it.toInt() })
    }

    /**
     * For now assume that the AoC Gods are merciful and presents can be perfectly packed
     * as long as there is enough room in the grid. If not.. we are doomed.
     */
    fun part1(input: List<String>): Int {
        val situations = makeSituations(input)
        return situations.filter { situation ->
            val boardArea = situation.boardDimension.x * situation.boardDimension.y
            val totalAreaOfPresents = situation.presents.mapIndexed { index, i -> i * shapeSizes[index] }.sum()
            boardArea >= totalAreaOfPresents
        }.size
    }
    readInput("Day12").let { part1(it).println() }
}
