import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    data class Machine(
        val lightEndState: List<Boolean>,
        val buttonWirings: List<List<Boolean>>,
        val joltageTarget: List<Int>
    )

    val lineRegex =
        Regex("""^\s*\[([^\]]+)]\s+((?:\([^)]+\)\s*)+)\{([^}]+)}""")

    val wiringRegex =
        Regex("""\(([^)]+)\)""")

    fun parseLine(line: String): Machine {
        val match = lineRegex.matchEntire(line)
            ?: error("Invalid line format: $line")

        val (lightDiagram, wiringsBlock, joltageBlock) = match.destructured

        val buttonIndices =
            wiringRegex.findAll(wiringsBlock)
                .map { it.groupValues[1] }
                .map { it.split(",").map(String::toInt) }
                .toList()

        val joltage = joltageBlock.split(",").map(String::toInt)
        val lightCount = lightDiagram.length

        return Machine(
            lightEndState = lightDiagram.map { it == '#' },
            buttonWirings = buttonIndices.map { indices ->
                List(lightCount) { i -> indices.contains(i) }
            },
            joltageTarget = joltage
        )
    }

    tailrec fun calculateLightPresses(
        machine: Machine,
        currentStates: List<List<Boolean>>,
        presses: Int
    ): Int {
        if (currentStates.any { it == machine.lightEndState }) return presses

        val nextStates =
            currentStates.flatMap { state ->
                machine.buttonWirings.map { wiring ->
                    state.mapIndexed { index, lightState ->
                        if (wiring[index]) !lightState else lightState
                    }
                }
            }

        return calculateLightPresses(machine, nextStates, presses + 1)
    }

    fun part1(input: List<String>): Int {
        val machines = input.map(::parseLine)
        return machines.sumOf { machine ->
            calculateLightPresses(
                machine,
                listOf(List(machine.lightEndState.size) { false }),
                0
            )
        }
    }

    fun minimumButtonPressesILP(machine: Machine): Int {

        // Convert button wirings to index lists
        val buttons: List<IntArray> =
            machine.buttonWirings.map { wiring ->
                wiring.mapIndexedNotNull { index, affects ->
                    if (affects) index else null
                }.toIntArray()
            }

        val target = machine.joltageTarget
        val dimensionCount = target.size
        if (dimensionCount == 0) return 0

        // Create solver
        val solver = MPSolver.createSolver("CBC")
            ?: error("Could not create CBC solver")

        /* ------------------------------------------------------------
           Variables:
           presses[j] = number of times button j is pressed
           ------------------------------------------------------------ */

        val maxPresses = target.maxOrNull()!!.toDouble()

        val pressVars =
            buttons.mapIndexed { j, _ ->
                solver.makeIntVar(0.0, maxPresses, "press_$j")
            }

        /* ------------------------------------------------------------
           Constraints:
           For each joltage index i:
           sum_j presses[j] * affects(j, i) == target[i]
           ------------------------------------------------------------ */

        for (i in 0 until dimensionCount) {
            val constraint =
                solver.makeConstraint(
                    target[i].toDouble(),
                    target[i].toDouble(),
                    "joltage_$i"
                )

            for (j in buttons.indices) {
                if (i in buttons[j]) {
                    constraint.setCoefficient(pressVars[j], 1.0)
                }
            }
        }

        /* ------------------------------------------------------------
           Objective:
           Minimize total number of presses
           ------------------------------------------------------------ */

        val objective = solver.objective()
        for (v in pressVars) {
            objective.setCoefficient(v, 1.0)
        }
        objective.minimization()

        solver.solve()

        return pressVars.sumOf { it.solutionValue().toInt() }
    }

    /**
     * My own algorithm was correct but even after optimization not fast enough by far.
     * "A hundred years is a mere blink in the life of an elf. I’m patient. I can wait.”
     * The above quote does not hold for me so searched online for faster algorithm.
     * Now using Google OR-Tools
     */
    suspend fun part2(input: List<String>): Int {
        Loader.loadNativeLibraries()

        val machines = input.map(::parseLine)

        return machines
            .mapIndexed { index, machine ->
                async(Dispatchers.Default) {
                    println("Solving machine ${index + 1}/${machines.size}")
                    minimumButtonPressesILP(machine)
                }
            }
            .awaitAll()
            .sum()
    }

    val input = readInput("Day10")

    part1(input).println()
    part2(input).println()
}
