package uk.ac.bournemouth.ap.battleshiplib

import uk.ac.bournemouth.ap.battleshiplib.*

class BattleshipGridImple(
    override val columns: Int,
    override val rows: Int,
    override val opponent: BattleshipOpponent
) : BattleshipGrid {
    override val shipsSunk = BooleanArray(opponent.ships.size)
    override val isFinished: Boolean
        get() = super.isFinished

    private val grid: Array<Array<GuessCell>> =
        Array(rows) { row ->
            Array(columns) { column ->
                GuessCellImpl(column, row,null)
            }
        }

    private val cells = Array(columns) { column ->
        Array(rows) { row -> grid[row][column] }
    }
    private val listeners: MutableList<BattleshipGrid.BattleshipGridListener> = mutableListOf()
    override fun get(column: Int, row: Int): GuessCell {
        if (column < 0 || column >= columns) throw IllegalArgumentException("Invalid column")
        if (row < 0 || row >= rows) throw IllegalArgumentException("Invalid row")
        return cells[column][row]
    }


    override fun shootAt(column: Int, row: Int): GuessResult {
        val cell = get(column, row) as GuessCellImpl
        val previousState = cell.state
        if (previousState != GuessState.UNKNOWN) {
            return GuessResultImpl(cell, previousState,null,3)
        }
        val result = cell.shoot()
        var shipIndex=0
        if (result.guess == Guess.HIT || result.guess == Guess.MISS) {
            if (result.guess == Guess.HIT && result.ship != null) {
                shipIndex = opponent.ships.indexOf(result.ship)
                if (shipIndex == -1) {
                    throw IllegalStateException("Cell contains ship that is not in opponent's list")
                }
                if (cells.any { it.any { it.ship == result.ship && it.state == GuessState.UNKNOWN } }) {
                    // The ship has been hit but not sunk yet
                } else {
                    shipsSunk[shipIndex] = true
                }
            }
        }
        listeners.forEach { it.onGridChanged(this, column, row) }

        return GuessResultImpl(cell, result.state,result.ship,shipIndex)
    }

    override fun addOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        listeners.add(listener)
    }

    override fun removeOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        listeners.remove(listener)
    }
}