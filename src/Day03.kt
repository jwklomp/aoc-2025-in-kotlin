fun main() {
    fun parseBanks(input: List<String>): List<List<Int>> =
        input.map { row -> row.chunked(1).map { it.toInt() } }

    fun digitPositions(nums: List<Int>): Map<Int, List<Int>> =
        (1..9)
            .associateWith { d ->
                nums.withIndex()
                    .filter { it.value == d }
                    .map { it.index }
            }
            .filterValues { it.isNotEmpty() }

    fun largestPairFromMap(pos: Map<Int, List<Int>>): Int =
        (9 downTo 1).firstNotNullOf { a ->
            val aPositions = pos[a].orEmpty()

            // For each occurrence of 'a', find the best possible B to the right
            val bestB =
                (9 downTo 1).firstOrNull { b ->
                    pos[b]?.any { j -> aPositions.any { i -> j > i } } == true
                }

            bestB?.let { b -> a * 10 + b }
        }

    fun largestCombinationFromMap(pos: Map<Int, List<Int>>): Long {
        val targetLength = 12

        val indexedDigits = pos
            .flatMap { (digit, positions) -> positions.map { digit to it } }
            .sortedBy { it.second }
            .withIndex()
            .toList()

        val n = indexedDigits.size

        val selectedDigits = (0 until targetLength).fold(emptyList<Pair<Int, Int>>()) { acc, _ ->
            val start = acc.lastOrNull()?.second ?: 0
            val remaining = targetLength - acc.size - 1
            val end = n - remaining
            val best = indexedDigits.subList(start, end).maxBy { it.value.first }
            acc + (best.value.first to (best.index + 1))
        }

        return selectedDigits.map { it.first }.joinToString("").toLong()
    }

    fun part1(input: List<String>): Int =
        parseBanks(input)
            .sumOf { largestPairFromMap(digitPositions(it)) }

    fun part2(input: List<String>): Long =
        parseBanks(input)
            .sumOf { largestCombinationFromMap(digitPositions(it)) }

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
