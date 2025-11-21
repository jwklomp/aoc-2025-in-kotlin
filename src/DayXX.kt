fun main() {
    fun part1(input: List<String>): Int {
        input.println()
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readInput("Day01_test")
    part1(testInput).println()
    //part2(testInput).println()

    val input = readInput("Day01")
    //part1(input).println()
    //part2(input).println()
}
