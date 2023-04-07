package uk.ac.bournemouth.ap.battleships

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up the main view
        val mainView = MainView(this)
        setContentView(mainView)
    }

    // Custom view class for the main view
    private class MainView(context: Context) : View(context) {

        private val titlePaint = Paint().apply {
            textSize = 48f
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
        }
        private val BattleshipPaint = Paint().apply {
            textSize = 100f
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
        }

        private val playButtonRect = Rect(0, 0, 0, 0)
        private val playButtonPaint = Paint().apply {
            color = Color.GREEN
        }

        private val exitButtonRect = Rect(0, 0, 0, 0)
        private val exitButtonPaint = Paint().apply {
            color = Color.RED
        }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)

            // Calculate the center coordinates of the view
            val centerX = width / 2
            val centerY = height / 2

            // Draw the title at the center of the view
            canvas?.drawText("BattleShip Game", centerX.toFloat(), 300f, BattleshipPaint)

            // Calculate the rect for the play button
            val playButtonWidth = 300
            val playButtonHeight = 100
            val playButtonLeft = centerX - playButtonWidth / 2
            val playButtonTop = centerY - playButtonHeight / 2 - 100
            val playButtonRight = centerX + playButtonWidth / 2
            val playButtonBottom = centerY + playButtonHeight / 2 - 100
            playButtonRect.set(playButtonLeft, playButtonTop, playButtonRight, playButtonBottom)

            // Draw the play button
            canvas?.drawRect(playButtonRect, playButtonPaint)
            canvas?.drawText("Play", centerX.toFloat(),
                ((playButtonTop+playButtonBottom)/2+10).toFloat(), titlePaint)

            // Calculate the rect for the exit button
            val exitButtonWidth = 300
            val exitButtonHeight = 100
            val exitButtonLeft = centerX - exitButtonWidth / 2
            val exitButtonTop = centerY - exitButtonHeight / 2 + 100
            val exitButtonRight = centerX + exitButtonWidth / 2
            val exitButtonBottom = centerY + exitButtonHeight / 2 + 100
            exitButtonRect.set(exitButtonLeft, exitButtonTop, exitButtonRight, exitButtonBottom)

            // Draw the exit button
            canvas?.drawRect(exitButtonRect, exitButtonPaint)
            canvas?.drawText("Exit", centerX.toFloat(), ((exitButtonTop+exitButtonBottom)/2+10).toFloat(), titlePaint)
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    val x = event.x.toInt()
                    val y = event.y.toInt()

                    if (playButtonRect.contains(x, y)) {
                        // Play button clicked, navigate to another activity
                        val intent = Intent(context, BattleshipViewActivity::class.java)
                        context.startActivity(intent)
                    } else if (exitButtonRect.contains(x, y)) {
                        // Exit button clicked, stop the application
                        (context as Activity).finish()
                    }
                }
            }
            return true
        }
    }
}
