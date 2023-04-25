package uk.ac.bournemouth.ap.battleshiplib

interface GuessCell {
    val column: Int
    val row: Int
    val state: GuessState
    val ship: Ship?
    var guess: Guess

    fun shoot():GuessCell
}