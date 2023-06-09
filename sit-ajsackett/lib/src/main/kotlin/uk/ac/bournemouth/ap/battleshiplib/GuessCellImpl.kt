package uk.ac.bournemouth.ap.battleshiplib

class GuessCellImpl(
    override val column: Int,
    override val row: Int,
    override val ship: Ship?
) : GuessCell {
    public var _guess: Guess = Guess.EMPTY

    override var state: GuessState
        get() = when (_guess) {
            Guess.EMPTY -> GuessState.UNKNOWN
            Guess.MISS -> GuessState.MISS
            Guess.HIT, Guess.SUNK -> GuessState.HIT
        }
        set(value) {
            if (value == GuessState.HIT) {
                _guess = if (ship != null) {
                    Guess.HIT
                } else {
                    Guess.MISS
                }
            } else if (value == GuessState.MISS) {
                _guess = Guess.MISS
            } else {
               _guess=Guess.EMPTY
            }
        }

    override fun shoot(): GuessCellImpl {
        if (_guess != Guess.EMPTY) {
            throw IllegalStateException("Cell has already been guessed")
        }
        _guess = if (ship != null) {
            Guess.HIT
        } else {
            Guess.MISS
        }
        return this
    }

    override var guess: Guess
        get() = _guess
        set(value) {
            if (_guess != Guess.EMPTY && value != Guess.SUNK) {
                throw IllegalStateException("Cell has already been guessed")
            }
            _guess = value
        }
}