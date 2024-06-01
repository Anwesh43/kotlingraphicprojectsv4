package com.example.lineballholderleftview

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Color

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
val rot : Float = -90f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
