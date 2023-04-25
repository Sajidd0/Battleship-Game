package uk.ac.bournemouth.ap.battleshiplib

interface GuessResult {
    val cell: GuessCell
    val state: GuessState
}