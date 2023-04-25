package uk.ac.bournemouth.ap.battleshiplib

import java.io.Serializable

enum class GuessState : Serializable {
    UNKNOWN,
    HIT,
    MISS
}