package com.example.boxlinedroprightview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Path
import android.app.Activity
import android.content.Context

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 5
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawOpenSquare(size : Float, paint : Paint) {
    val path : Path = Path()
    path.moveTo(-size / 2, 0f)
    path.lineTo(-size / 2, size)
    path.lineTo(size / 2, size)
    path.lineTo(size / 2, 0f)
    drawPath(path, paint)
}

fun Canvas.drawBoxLineDropRight(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h / 2 + size) * dsc(4)) {
        rotate(rot * dsc(3))
        drawXY(-size * 0.5f * dsc(2), -h * 0.5f * (1 - dsc(1))) {
            drawLine(-size / 2, 0f, size / 2, 0f, paint)
        }
        drawXY(0f, h * 0.5f * (1 - dsc(0))) {
            drawOpenSquare(size, paint)
        }
    }
}

fun Canvas.drawBLDRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawBoxLineDropRight(scale, w, h, paint)
}

class BoxLineDropRightView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}