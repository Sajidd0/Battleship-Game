package uk.ac.bournemouth.ap.battleshiplib

import uk.ac.bournemouth.ap.battleshiplib.*
import kotlin.random.Random

class BattleshipGridImple(
    override val columns: Int,
    override val rows: Int,
    override val opponent: BattleshipOpponent
) : BattleshipGrid {
    override val shipsSunk = BooleanArray(opponent.ships.size)
    var lastColumn: Int = -1;
    var lastRow: Int = -1;
    override val isFinished: Boolean
        get() = super.isFinished

    private val grid: Array<Array<GuessCell>> =
        Array(rows) { row ->
            Array(columns) { column ->

                GuessCellImpl(column, row, findShip(column, row))
            }
        }

    private fun findShip(column: Int, row: Int): Ship? {
        for (ship in opponent.ships) {
            if (ship.matchShip(column, row)) {
                return ship;
            }
        }
        return null;
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
            return GuessResultImpl(cell, previousState, null, 3)
        }
        val result = cell.shoot()
        var shipIndex = 0
        if (result.guess == Guess.HIT || result.guess == Guess.MISS) {
            if (result.guess == Guess.HIT && result.ship != null) {
                shipIndex = opponent.ships.indexOf(result.ship)
                lastColumn = column;
                lastRow = row;
                if (shipIndex == -1) {
                    throw IllegalStateException("Cell contains ship that is not in opponent's list")
                }
                if (cells.any { it.any { it.ship == result.ship && it.state == GuessState.UNKNOWN } }) {
                    cell.state=GuessState.HIT;
                } else {
                    shipsSunk[shipIndex] = true
                }
            }else{
                cell.state=GuessState.MISS;
            }
        }
        listeners.forEach { it.onGridChanged(this, column, row) }

        return GuessResultImpl(cell, result.state, result.ship, shipIndex)
    }

    override fun addOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        listeners.add(listener)
    }

    override fun removeOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        listeners.remove(listener)
    }

    override fun computerMove() :GuessResult{
        var cell:GuessResult;
        if (lastColumn > -1 && lastRow > -1) {
            val myList = mutableListOf<Pair>()
            var move1C = lastColumn - 1;
            var move1R = lastRow;

            var move2C = lastColumn + 1;
            var move2R = lastRow;

            var move3C = lastColumn;
            var move3R = lastRow - 1;

            var move4C = lastColumn;
            var move4R = lastRow + 1;

            if (move1C >= 0 && move1R >= 0) {
                var pair = Pair(move1C, move1R);
                myList.add(pair);
            }
            if (move2C >= 0 && move2R >= 0) {
                var pair1 = Pair(move2C, move2R);
                myList.add(pair1);
            }
            if (move3C >= 0 && move3R >= 0) {
                var pair2 = Pair(move3C, move3R);
                myList.add(pair2);
            }
            if (move4C >= 0 && move4R >= 0) {
                var pair3 = Pair(move4C, move4R);
                myList.add(pair3);
            }
            val randomIndex = Random.nextInt(myList.size)
            cell=shootAt(myList[randomIndex].getColumn(), myList[randomIndex].getRow())
        }else{
            val emptyCells = grid.flatMap { it.asList() }
                .filter { it.guess == Guess.EMPTY }
            val randomIndex = Random.nextInt(emptyCells.size)
            cell=shootAt(emptyCells[randomIndex].column, emptyCells[randomIndex].row)
        }
        return cell;
    }
}