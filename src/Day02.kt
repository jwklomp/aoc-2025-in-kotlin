fun main() {
    fun makeRangesOfAllNumbersAsString(input: List<String>): List<String> =
        input.first()
            .split(',')
            .map { s -> s.split('-').map { it.toLong() } }
            .map { range -> range.first()..range.last() }
            .flatMap { intRange -> intRange.map { num -> num.toString() } }

    fun isEntirelyRepeatingSubstring(s: String): Boolean =
        (1..(s.length / 2))
            .any { size -> s.length % size == 0 && s.chunked(size).all { it == s.take(size) } }

    fun part1(input: List<String>): Long {
        val invalids = makeRangesOfAllNumbersAsString(input)
            .filter { it.length % 2 == 0 }
            .filter { it.substring(0, it.length / 2) == it.substring(it.length / 2) }
        return invalids.sumOf { it.toLong() }
    }

    fun part2(input: List<String>): Long {
        val invalids = makeRangesOfAllNumbersAsString(input)
            .filter { isEntirelyRepeatingSubstring(it) }
        return invalids.sumOf { it.toLong() }
    }

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
