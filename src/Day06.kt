fun main() {
    fun parseLine(line: String): List<String> =
        Regex("""\S+""").findAll(line).map { it.value }.toList()

    fun calculateResult(problems: List<List<Long>>, operators: List<String>): Long =
        problems.zip(operators).sumOf { (numbers, operator) ->
            when (operator) {
                "*" -> numbers.reduce(Long::times)
                "+" -> numbers.sum()
                else -> 0L
            }
        }

    fun part1(input: List<String>): Long {
        val parsedLines = input.map(::parseLine)
        val numberLines = transpose(parsedLines.dropLast(1))
            .map { line -> line.map(String::toLong) }
        val operators = parsedLines.last()
        return calculateResult(numberLines, operators)
    }

    fun part2(input: List<String>): Long {
        val operators = parseLine(input.last())
        val dataLines = input.dropLast(1)
        val maxLength = dataLines.maxOf { it.length } // needed because of the padding

        val columns = transpose(dataLines.map { it.padEnd(maxLength).toList() })

        val problems = columns
            .map { col -> col.filterNot { it.isWhitespace() }.joinToString("") }
            .fold(mutableListOf(mutableListOf<String>())) { acc, number ->
                when {
                    number.isEmpty() -> acc.apply { if (last().isNotEmpty()) add(mutableListOf()) }
                    else -> acc.apply { last().add(number) }
                }
            }
            .filter { it.isNotEmpty() }
            .map { problem -> problem.map(String::toLong) }

        return calculateResult(problems, operators)
    }

    readInput("Day06").let { part1(it).println() }

    readInput("Day06").let { part2(it).println() }
}
