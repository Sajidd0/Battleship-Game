package uk.ac.bournemouth.ap.battleshiplib

class Pair(
    var column: Int, var row: Int
) {
    init {
        column = 0;
        row = 0;
    }
    fun getColumn(): Int {
        return column
    }

    fun setColumn(value: Int) {
        column = value
    }

    fun getRow(): Int {
        return row
    }

    fun setRow(value: Int) {
        row = value
    }
}