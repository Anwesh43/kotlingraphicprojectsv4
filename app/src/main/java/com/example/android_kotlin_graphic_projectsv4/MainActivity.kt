package com.example.android_kotlin_graphic_projectsv4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.linerotgodiagview.LineRotDiagView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //setContentView(R.layout.activity_main)
        LineRotDiagView.create(this)
    }
}