package com.example.rightcircletosemiview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 4
val scGap : Float = 0.04f / 4
val rot : Float = -90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")

class RightCircleToSemiView(ctx : Context) : View(ctx) {

    val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {
        val w : Float = width.toFloat()
        val h : Float = height.toFloat()
        canvas.drawColor(Color.CYAN)
        paint.color = Color.WHITE
        val text : String = "hello world"
        paint.textSize = Math.min(w, h) / 6
        canvas.drawText("Hello World", canvas.width.toFloat() / 2 - paint.measureText(text) / 2, canvas.height.toFloat() / 2 - paint.textSize / 2, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    companion object {
        fun create(activity : Activity) : RightCircleToSemiView {
            val view : RightCircleToSemiView = RightCircleToSemiView(activity)
            activity.setContentView(view)
            return view
        }
    }
}