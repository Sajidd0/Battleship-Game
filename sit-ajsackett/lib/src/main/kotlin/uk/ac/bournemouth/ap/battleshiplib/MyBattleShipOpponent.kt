package uk.ac.bournemouth.ap.battleshiplib

import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate

class MyBattleshipOpponent(
    override val columns: Int,
    override val rows: Int,
    override val ships: List<Ship>  // Example list of ships)
) : BattleshipOpponent {

    private val shipPositions: MutableMap<Coordinate, Ship> = mutableMapOf()

    override fun shipAt(column: Int, row: Int): BattleshipOpponent.ShipInfo<Ship>? {
        val ship = shipPositions[Coordinate(column, row)]
        return ship?.let { BattleshipOpponent.ShipInfo(ships.indexOf(ship), it) }
    }

    fun placeShip(ship: Ship, start: Coordinate, end: Coordinate) {
        // Place the ship on the board, represented by the shipPositions map
    }
}