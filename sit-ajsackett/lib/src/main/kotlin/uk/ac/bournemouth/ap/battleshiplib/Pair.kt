package uk.ac.bournemouth.ap.battleshiplib

class Pair(
    var column: Int, var row: Int
) {
    init{}

    @JvmName("getColumn1")
    fun getColumn(): Int {
        return column
    }

    @JvmName("setColumn1")
    fun setColumn(value: Int) {
        column = value
    }

    @JvmName("getRow1")
    fun getRow(): Int {
        return row
    }

    @JvmName("setRow1")
    fun setRow(value: Int) {
        row = value
    }
}