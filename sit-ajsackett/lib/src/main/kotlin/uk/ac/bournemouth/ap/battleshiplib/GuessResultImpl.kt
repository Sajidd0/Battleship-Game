package uk.ac.bournemouth.ap.battleshiplib

import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import uk.ac.bournemouth.ap.battleshiplib.GuessState
import uk.ac.bournemouth.ap.battleshiplib.Ship

data class GuessResultImpl(
    override val cell: GuessCell,
    override val state: GuessState,
    val sunkShip: Ship?,
    val sunkShipIndex: Int?
) : GuessResult