fun main() {

    data class Machine(
        val lightEndState: List<Boolean>,
        val buttonWirings: List<List<Boolean>>,
        val joltageLevel: List<Int>
    )

    val lineRegex =
        Regex("""^\s*\[([^\]]+)]\s+((?:\([^)]+\)\s*)+)\{([^}]+)}""")

    val wiringRegex =
        Regex("""\(([^)]+)\)""")

    fun parseLine(line: String): Machine {
        val match = lineRegex.matchEntire(line)
            ?: error("Invalid line format: $line")

        val (lightDiagram, wiringsBlock, joltageBlock) = match.destructured

        val buttonWirings =
            wiringRegex.findAll(wiringsBlock)
                .map { it.groupValues[1] }
                .map { it.split(",").map(String::toInt) }
                .toList()

        val joltage =
            joltageBlock.split(",").map(String::toInt)

        return Machine(
            lightEndState = lightDiagram.chunked(1).map { it == "#" },
            buttonWirings = buttonWirings.map { button ->
                List(lightDiagram.chunked(1).size) { i -> button.contains(i) }
            },
            joltageLevel = joltage
        )
    }

    tailrec fun calculateLightPresses(machine: Machine, currentStates: List<List<Boolean>>, presses: Int): Int {
        if (currentStates.any { it == machine.lightEndState }) return presses
        val newStates =
            currentStates.flatMap { state ->
                machine.buttonWirings.map { wiring ->
                    state.mapIndexed { index, lightState ->
                        if (wiring[index]) !lightState else lightState
                    }
                }
            }
        return calculateLightPresses(machine, newStates, presses + 1)
    }

    tailrec fun calculateJoltagePresses(machine: Machine, currentJoltages: List<List<Int>>, presses: Int): Int {
        presses.println()
        if (currentJoltages.any { it == machine.joltageLevel }) return presses
        val newStates =
            currentJoltages.flatMap { state ->
                machine.buttonWirings.map { wiring ->
                    state.mapIndexed { index, joltage ->
                        if (wiring[index]) joltage + 1 else joltage
                    }
                }
            }
        val legalStates = newStates.filter { state ->
            state.indices.all { index -> state[index] <= machine.joltageLevel[index] }
        }.distinct()
        return calculateJoltagePresses(machine, legalStates, presses + 1)
    }

    fun part1(input: List<String>): Int {
        val machines = input.map(::parseLine)
        return machines.sumOf { machine ->
            calculateLightPresses(machine, listOf(List(machine.lightEndState.size) { false }), 0)
        }
    }

    fun part2(input: List<String>): Int {
        val machines = input.map(::parseLine)
        return machines.sumOf { machine ->
            calculateJoltagePresses(machine, listOf(List(machine.joltageLevel.size) { 0 }), 0)
        }
    }


    //readInput("Day10_test").let { part1(it).println() }
    //readInput("Day10_test").let { part2(it).println() }

    //readInput("Day10").let { part1(it).println() }
    readInput("Day10").let { part2(it).println() }

}


