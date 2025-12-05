fun main() {
    fun makeLines(input: List<String>) = input.map { line -> line.split('-') }

    fun makeRanges(lines: List<List<String>>): List<LongRange> =
        lines.filter { it.size == 2 }.map { (a, b) -> a.toLong()..b.toLong() }

    fun makeIngredients(lines: List<List<String>>): List<Long> =
        lines.filter { it.size == 1 && it.first().isNotBlank() }.mapNotNull { it.first().toLongOrNull() }

    fun part1(input: List<String>): Int {
        val lines = makeLines(input)
        val ranges = makeRanges(lines)
        val ingredients = makeIngredients(lines)
        return ingredients.count { ingredient -> ranges.any { range -> ingredient in range } }
    }

    fun part2(input: List<String>): Long {
        val lines = makeLines(input)
        val ranges = makeRanges(lines)
        val mergedRanges = mergeIntervals(ranges.map { Interval(it.first, it.last) })
        return mergedRanges.sumOf { it.to - it.from + 1 }
    }

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
