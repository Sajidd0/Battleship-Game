package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import uk.ac.bournemouth.ap.battleshiplib.*
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid.Companion.DEFAULT_COLUMNS
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid.Companion.DEFAULT_ROWS
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid.Companion.DEFAULT_SHIP_SIZES
import java.lang.Integer.min
import kotlin.random.Random


class BattleshipViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val args = intent.getBundleExtra("BUNDLE")
        //val getter:ArrayList<Ship> = args?.getSerializable("ARRAYLIST") as ArrayList<Ship>
        setContentView(BattleshipView(this, ships as ArrayList<Ship>))
    }
}

class BattleshipView(context: Context, ships: ArrayList<Ship>) : View(context) {
    private var cellWidth: Int
    private var cellHeight: Int
    private var tempheight: Int
    private var usercellresult: Int
    private var usercellx: Int
    private var usercelly: Int
    private val board: Array<IntArray>
    private lateinit var ships: ArrayList<Ship>
    private var opponentships: ArrayList<Ship>
    val battleshipUser: BattleshipOpponent;
    val battleshipComputer: BattleshipOpponent;
    val computerGrid: BattleshipGrid;
    val userGrid: BattleshipGrid;
    private var usermisses: ArrayList<uk.ac.bournemouth.ap.battleships.Point>
    private var userhits: ArrayList<uk.ac.bournemouth.ap.battleships.Point>
    private var computerhits: ArrayList<uk.ac.bournemouth.ap.battleships.Point>
    init {
        // Calculate the width and height of each cell based on the size of the view
        val displayMetrics = resources.displayMetrics
        cellWidth = displayMetrics.widthPixels / DEFAULT_COLUMNS
        cellHeight = displayMetrics.heightPixels / DEFAULT_ROWS
        tempheight = 0
        usercellresult = 0;
        usercellx = 0;
        usercelly = 0;
        usermisses = ArrayList()
        userhits = ArrayList()
        computerhits= ArrayList()
        // Initialize the game board with empty cells
        board = Array(DEFAULT_ROWS) { IntArray(DEFAULT_COLUMNS) }

        // Place the ships on the game board
        opponentships =
            placeShips(DEFAULT_SHIP_SIZES, DEFAULT_COLUMNS, DEFAULT_ROWS) as ArrayList<Ship>
        this.ships = ships;
        battleshipUser = MyBattleshipOpponent(10, 10, ships);
        battleshipComputer = MyBattleshipOpponent(10, 10, opponentships);
        userGrid = BattleshipGridImple(DEFAULT_COLUMNS, DEFAULT_ROWS, battleshipComputer);
        computerGrid = BattleshipGridImple(DEFAULT_COLUMNS, DEFAULT_ROWS, battleshipUser);

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
        var cellSize = (min(width, height) / 10f) / 2
        cellWidth = ((width / DEFAULT_COLUMNS.toFloat()) / 2).toInt()
        cellHeight = ((height / DEFAULT_ROWS.toFloat()) / 2).toInt()
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
        shipPaint.style = Paint.Style.STROKE
        shipPaint.color = Color.RED
        shipPaint.strokeWidth = 5f
        val textPaint = Paint()
        textPaint.setColor(Color.RED);
        textPaint.setTextSize((cellHeight / 2).toFloat());
        var count = 0;
        for (ship in battleshipUser.ships) {
            var left = startX + ship.left * cellSize
            var top = startY + ship.top * cellSize
            val right = startX + (ship.right + 1) * cellSize
            val bottom = startY + (ship.bottom + 1) * cellSize
            val rect = RectF(left, top, right, bottom)
            canvas?.drawRect(rect, shipPaint)
            var j = 0
            while (j < DEFAULT_SHIP_SIZES[count]) {

                if (right - left < cellSize) {
                    top += cellSize
                } else {
                    left += cellSize
                }
                canvas?.drawText(
                    DEFAULT_SHIP_SIZES[count].toString(),
                    left - cellSize + 10,
                    top + cellSize - 10,
                    textPaint
                )
                j++
            }

            count++

        }
        for(hit in computerhits)
        {
            val redPaint = Paint().apply {
                color = Color.RED
                strokeWidth = 5f
            }
            val centerX =width - cellSize * 10+ (hit.y*cellSize+(cellSize/2))
            val centerY = (hit.x *cellSize +(cellSize/2) )+ 20f
            canvas?.drawCircle(centerX, centerY, cellSize / 4f, redPaint)
        }
        startX = 0F
        startY = (height - cellSize * 10) / 2
        tempheight = startY.toInt()
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
        for (miss in usermisses) {
            val crossPaint = Paint().apply {
                color = Color.BLACK
                strokeWidth = 50f
            }

            val leftX = miss.x
            val rightX = miss.x + 10
            val topY = miss.y + tempheight
            val bottomY = miss.y + tempheight + 10

            canvas?.drawLine(
                leftX.toFloat(), topY.toFloat(), rightX.toFloat(),
                bottomY.toFloat(), crossPaint
            )
            canvas?.drawLine(
                leftX.toFloat(), bottomY.toFloat(), rightX.toFloat(),
                topY.toFloat(), crossPaint
            )
        }
        for (hit in userhits) {
            val redPaint = Paint().apply {
                color = Color.RED
                strokeWidth = 5f
            }
            val centerX = (hit.x + 0.5f)
            val centerY = (hit.y + tempheight + 0.5f)
            canvas?.drawCircle(centerX, centerY, cellSize / 4f, redPaint)
        }
        // Draw any ships that have been placed on the board
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val cellSize =
                    width / DEFAULT_COLUMNS // assuming NUM_COLUMNS is the number of columns in your grid
                val cellX = event.x.toInt() / cellSize
                val y = event.y.toInt() - tempheight
                val cellY = y / cellSize
                if (cellX > 9 || cellY > 9) {
                    Toast.makeText(context, "Touch on the grid goddamnit!!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    var allShipsSunk = userGrid.shipsSunk.all { it }
                    var allShipsSunk2 = computerGrid.shipsSunk.all { it }
                    if (allShipsSunk) {
                        Toast.makeText(context, "Game Over You win", Toast.LENGTH_SHORT).show()
                        return true
                    } else if (allShipsSunk2) {
                        Toast.makeText(context, "Game Over You lost", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    Log.d("screen", "touched $cellX,$cellY")
                    var cell: GuessResult = userGrid.shootAt(cellX, cellY)
                    if (cell.state.toString() == "MISS") {
                        val point = Point(event.x.toInt(), y);
                        usermisses.add(point)

                    } else if (cell.state.toString() == "HIT") {
                        val point = Point(event.x.toInt(), y);
                        userhits.add(point)
                        allShipsSunk = userGrid.shipsSunk.all { it }
                        if (allShipsSunk) {
                            Toast.makeText(context, "Game Over You win", Toast.LENGTH_SHORT).show()
                            return true
                        }
                    }
                    Log.d("state of user cell", cell.cell.state.toString())
                    cell = computerGrid.computerMove()
                    if(cell.state.toString()=="HIT")
                    {
                        val point=Point(cell.cell.row,cell.cell.column)
                        computerhits.add(point)
                    }
                    Log.d("computer", "touched ${cell.cell.column},${cell.cell.row}")
                    allShipsSunk2 = computerGrid.shipsSunk.all { it }
                    if (allShipsSunk2) {
                        Toast.makeText(context, "Game Over You lost", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    Log.d("state of machine cell", cell.cell.state.toString())
                }
                // val x = event.x.toInt()
                //
                //val cellX = x / cellWidth
                //var cellY = (y*2) / cellHeight


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
                    override var top: Int = startY
                    override var left: Int = startX
                    override var bottom: Int = if (isHorizontal) startY else startY + size - 1
                    override var right: Int = if (isHorizontal) startX + size - 1 else startX
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

class Point(val x: Int, val y: Int)
