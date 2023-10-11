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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawRightCircleToSemi(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 + (w / 2 + size / 2) * dsc(3), h / 2) {
        rotate(rot * dsc(2))
        drawXY((w * 0.5f + size / 2) * (1 - dsc(0)), 0f) {
            drawArc(
                RectF(-size / 2, -size / 2, size / 2, size / 2),
                0f,
                180f * (2 - dsc(1)),
                true,
                paint
            )

        }
    }
}

fun Canvas.drawRCTSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    drawRightCircleToSemi(scale, w, h, paint)
}

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

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : RightCircleToSemiView {
            val view : RightCircleToSemiView = RightCircleToSemiView(activity)
            activity.setContentView(view)
            return view
        }
    }
}