package com.example.openlinebreakdownview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
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
val rot : Float = 90f
val sizeFactor : Float = 4.9f
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawOpenLineBreakDown(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h / 2 + size) * dsc(3)) {
        drawXY(0f, -h * 0.5f * (1 - dsc(1))) {
            drawLine(0f, 0f, 0f, -size, paint)
        }
        for (j in 0..1) {
            drawXY(0f, h * 0.5f * (1 - dsc(0))) {
                rotate(rot * (1f - 2 * j) * dsc(2))
                drawLine(0f, 0f, 0f, size, paint)
            }
        }
    }
}

fun Canvas.drawOLBDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawOpenLineBreakDown(scale, w, h, paint)
}

class OpenLineBreakDownView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class OLBDNode(var i : Int = 0, val state : State = State()) {

        private var next : OLBDNode? = null
        private var prev : OLBDNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = OLBDNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawOLBDNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : OLBDNode {
            var curr : OLBDNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class OpenLineBreakDown(var i : Int) {

        private var curr : OLBDNode = OLBDNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : OpenLineBreakDownView) {

        private val animator : Animator = Animator(view)
        private val olbd : OpenLineBreakDown = OpenLineBreakDown(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            olbd.draw(canvas, paint)
            animator.animate {
                olbd.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            olbd.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity: Activity) : OpenLineBreakDownView {
            val view : OpenLineBreakDownView = OpenLineBreakDownView(activity)
            activity.setContentView(view)
            return view
        }
    }
}