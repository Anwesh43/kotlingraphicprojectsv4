package com.example.sqfromleftlineview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
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
val parts : Int = 4
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f

fun Int.inverse() : Float = 1f /this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawTriLine(size : Float, paint : Paint) {
    val path : Path = Path()
    path.moveTo(0f, -size / 2)
    path.lineTo(size / 2, 0f)
    path.lineTo(0f, size / 2)
    drawPath(path, paint)
}

fun Canvas.drawSqFromLeftLine(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h / 2 + size) * dsc(3)) {
        rotate(rot * dsc(2))
        drawXY(-w * 0.5f * (1 - dsc(0)), 0f) {
            drawRect(RectF(-size / 2, -size / 2, 0f, size / 2), paint)
        }
        drawXY(w * 0.5f * (1 - dsc(1)), 0f) {
            drawTriLine(size, paint)
        }
    }
}

fun Canvas.drawSFLLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    drawSqFromLeftLine(scale, w, h, paint)
}