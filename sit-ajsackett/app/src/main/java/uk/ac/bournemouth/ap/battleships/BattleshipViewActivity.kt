package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid.Companion.DEFAULT_COLUMNS
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid.Companion.DEFAULT_ROWS
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid.Companion.DEFAULT_SHIP_SIZES
import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import uk.ac.bournemouth.ap.battleshiplib.Ship
import uk.ac.bournemouth.ap.battleshiplib.forEachIndex
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import java.lang.Integer.min
import kotlin.random.Random

class BattleshipViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(BattleshipView(this));

    }
}
class BattleshipView(context: Context) : View(context) {
    private var cellWidth: Int
    private var cellHeight: Int

    private val board: Array<IntArray>

    private val ships: List<Ship>

    init {
        // Calculate the width and height of each cell based on the size of the view
        val displayMetrics = resources.displayMetrics
        cellWidth = displayMetrics.widthPixels / DEFAULT_COLUMNS
        cellHeight = displayMetrics.heightPixels / DEFAULT_ROWS

        // Initialize the game board with empty cells
        board = Array(DEFAULT_ROWS) { IntArray(DEFAULT_COLUMNS) }

        // Place the ships on the game board
        ships = placeShips(DEFAULT_SHIP_SIZES, DEFAULT_COLUMNS, DEFAULT_ROWS)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw the game board
        val boardPaint = Paint()
        boardPaint.color = Color.WHITE
        boardPaint.strokeWidth = 5f
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), boardPaint)
        //val cellSize = min((width.toFloat()*0.5).toInt(), (height.toFloat()*0.5).toInt()) / 10f

        val gridPaint = Paint()
        gridPaint.color = Color.BLACK
        gridPaint.strokeWidth = 5f
        // Draw the grid lines
        var cellSize = (min(width, height) / 10f)/2
        cellWidth = ((width / DEFAULT_COLUMNS.toFloat())/2).toInt()
        cellHeight = ((height / DEFAULT_ROWS.toFloat())/2).toInt()
        // Calculate the starting position of the grid based on the view's dimensions
        var startX = width - cellSize * 10
        var startY = 20F
        for (i in 0..10) {
            val x = startX + i * cellSize
            canvas?.drawLine(x, startY, x, startY + cellSize * 10, gridPaint)
            canvas?.drawLine(
                startX,
                startY + i * cellSize,
                startX + cellSize * 10,
                startY + i * cellSize,
                gridPaint
            )
        }
        val shipPaint = Paint()
        shipPaint.style=Paint.Style.STROKE
        shipPaint.color = Color.RED
        shipPaint.strokeWidth = 5f
        val textPaint=Paint()
        textPaint.setColor(Color.RED);
        textPaint.setTextSize((cellHeight / 2).toFloat());
        var count=0;
        for (ship in ships) {
            var left = startX + ship.left * cellSize
            var top = startY + ship.top * cellSize
            val right = startX + (ship.right + 1) * cellSize
            val bottom = startY + (ship.bottom + 1) * cellSize
            val rect=RectF(left, top, right, bottom)
            canvas?.drawRect(rect, shipPaint)
            var j=0
            while(j<DEFAULT_SHIP_SIZES[count])
            {

                if(right-left<cellSize)
                {
                    top+=cellSize
                }
                else
                {
                    left+=cellSize
                }
                canvas?.drawText(DEFAULT_SHIP_SIZES[count].toString(), left-cellSize+10, top+cellSize-10, textPaint)
                j++
            }

            count++;

        }
        startX = 0F
        startY = (height - cellSize * 10)/ 2
        cellSize = min(width, height) / 10f
        cellWidth = (width / DEFAULT_COLUMNS.toFloat()).toInt()
        cellHeight = (height / DEFAULT_ROWS.toFloat()).toInt()
        // Draw the grid lines
        for (i in 0..10) {
            val x = startX + i * cellSize
            canvas?.drawLine(x, startY, x, startY + cellSize * 10, gridPaint)
            canvas?.drawLine(
                startX,
                startY + i * cellSize,
                startX + cellSize * 10,
                startY + i * cellSize,
                gridPaint
            )

        }
        // Draw any ships that have been placed on the board
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x.toInt()
                val y = event.y.toInt()
                val cellX = x / cellWidth
                val cellY = y / cellHeight
                Log.d("screen","touched $cellX,$cellY")
                /*if (board[cellY][cellX] == GuessResult.HIT()) {
                    // The cell contains a ship
                    board[cellY][cellX] = HIT
                    // Check if the ship has been sunk
                    val ship = getShipAt(cellX, cellY)
                    if (isShipSunk(ship)) {
                        // The ship has been sunk
                        // Update the UI to show that the ship has been sunk
                    } else {
                        // The ship has been hit but not sunk
                        // Update the UI to show that the ship has been hit
                    }
                } else {
                    // The cell does not contain a ship
                    board[cellY][cellX] = MISS
                    // Update the UI to show that the cell has been missed
                }*/
                invalidate()
            }
        }
        return true
    }

    private fun placeShips(shipSizes: IntArray, columns: Int, rows: Int): List<Ship> {
        // Implement the logic to place the ships on the game board
        val ships = mutableListOf<Ship>()
        for (size in shipSizes) {
            var placed = false
            while (!placed) {
                // Generate a random starting position for the ship
                val startX = Random.nextInt(columns)
                val startY = Random.nextInt(rows)

                // Generate a random orientation for the ship
                val isHorizontal = Random.nextBoolean()

                // Create the ship object
                val ship: Ship = object : Ship {
                    override val top: Int = startY
                    override val left: Int = startX
                    override val bottom: Int = if (isHorizontal) startY else startY + size - 1
                    override val right: Int = if (isHorizontal) startX + size - 1 else startX
                }
                if (ship.left >= 0 && ship.right < DEFAULT_COLUMNS && ship.top >= 0 && ship.bottom < DEFAULT_ROWS) {
                    var overlaps = false
                    for (otherShip in ships) {
                        otherShip.forEachIndex { x, y ->
                            if (ship.columnIndices.contains(x) && ship.rowIndices.contains(y)) {
                                overlaps = true
                                return@forEachIndex
                            }
                        }
                    }
                    if (!overlaps) {
                        // The ship does not overlap with any other ships
                        ships.add(ship)
                        placed = true
                    }
                }

                // Check if the ship overlaps with any other ships

            }
        }
        return ships

    }

/* private fun getShipAt(x: Int, y: Int): Ship? {
     // Implement the logic to get the ship at the given coordinates
    val coordinate = Coordinate(y, x)
      for (ship in ships) {
     if (coordinate in ship.topLeft..ship.bottomRight) {
         return ship
     }
 }
 }

 private fun isShipSunk(s: Ship): Boolean {
     // Implement the logic to check if the given ship has been sunk
 }*/
}
