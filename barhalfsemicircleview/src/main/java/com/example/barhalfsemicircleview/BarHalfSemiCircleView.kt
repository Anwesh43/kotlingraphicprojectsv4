package com.example.barhalfsemicircleview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color
import android.graphics.Paint

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 4
val scGap : Float = 0.04f / parts
val delay : Long = 20
val sizeFactor : Float = 4.9f
val rot : Float = 180f
val backColor : Int = Color.parseColor("#BDBDBD")
val rFactor : Float = 4f