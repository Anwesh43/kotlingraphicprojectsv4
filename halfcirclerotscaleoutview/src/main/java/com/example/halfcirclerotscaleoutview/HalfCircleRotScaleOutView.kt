package com.example.halfcirclerotscaleoutview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
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
val scGap : Float = 0.08f / parts
val sizeFactor : Float = 4.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float =  Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawHalfCircleRotScaleOut(scale : Float, w : Float, h : Float, paint : Paint) {
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    val size : Float = Math.min(w, h) / sizeFactor
    drawXY(w / 2, h / 2) {
        scale(1f - dsc(3), 1f - dsc(3))
        drawXY((w / 2) * (1 - dsc(0)), 0f) {
            rotate(-rot * dsc(1))
            drawArc(RectF(-size / 2, -size / 2, size / 2, size / 2), -90f, 180f, true, paint)
        }
        drawXY(0f, h * 0.5f * (1 - dsc(2))) {
            drawArc(RectF(-size / 2, -size / 2, size / 2, size / 2), 0f, 180f, true, paint)
        }
    }
}

fun Canvas.drawHCRSONode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    drawHalfCircleRotScaleOut(scale, w, h, paint)
}

class HalfCircleRotScaleOutView(ctx : Context) : View(ctx) {

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

    data class HCRSONode(var i : Int = 0, val state : State = State()) {

        private var next : HCRSONode? = null
        private var prev : HCRSONode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = HCRSONode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawHCRSONode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : HCRSONode {
            var curr : HCRSONode? = prev
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

    data class HalfCircleRotScaleOut(var i : Int) {

        private var curr : HCRSONode = HCRSONode(0)
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

    data class Renderer(var view : HalfCircleRotScaleOutView) {

        private val animator : Animator = Animator(view)
        private val hcrso : HalfCircleRotScaleOut = HalfCircleRotScaleOut(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            hcrso.draw(canvas, paint)
            animator.animate {
                hcrso.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            hcrso.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity: Activity) : HalfCircleRotScaleOutView {
            val view : HalfCircleRotScaleOutView = HalfCircleRotScaleOutView(activity)
            activity.setContentView(view)
            return view
        }
    }
}