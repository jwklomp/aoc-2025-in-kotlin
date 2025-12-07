fun main() {
    fun part1(input: List<String>): Int {
        input.println()
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    readInput("DayXX_test").let { part1(it).println() }
    //readInput("DayXX_test").let { part2(it).println() }

    //readInput("DayXX").let { part1(it).println() }
    //readInput("DayXX").let { part2(it).println() }

}
