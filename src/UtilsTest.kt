fun main() {
    fun testTranspose() {
        val inputList = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))
        val expected = listOf(listOf(1, 4, 7), listOf(2, 5, 8), listOf(3, 6, 9))
        val transposed = transpose(inputList)
        check(transposed == expected)
        val doubleTransposed = transpose(transposed)
        check(doubleTransposed == inputList)
    }
    // 1 2 3
    // 4 5 6
    // 7 8 9

    // 1 4 7
    // 2 5 8
    // 3 6 9

    testTranspose()
}
