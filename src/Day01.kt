fun main() {
    fun parseMutations(instructions: List<String>) = instructions.map {
        val steps = it.drop(1).toInt()
        if (it[0] == 'L') -steps else steps
    }

    fun part1(input: List<String>): Int =
        parseMutations(input)
            .scan(50) { position, step ->
                Math.floorMod(position + step, 100)
            }.count { it == 0 }

    fun part2(input: List<String>): Int =
        parseMutations(input).fold(50 to 0) { (position, count), step ->
            val newPosition = Math.floorMod(position + step, 100)
            val crossings = when {
                step > 0 -> Math.floorDiv(position + step, 100) - Math.floorDiv(position, 100)
                step < 0 -> Math.floorDiv(position - 1, 100) - Math.floorDiv(position + step - 1, 100)
                else -> 0
            }
            newPosition to count + crossings
        }.second

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
