package com.kc_hsu.waveanimation

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val waveAnimator = findViewById<WaveAnimator>(R.id.wave_animator)
        with(waveAnimator) {
            duration = 1_500
            style = Paint.Style.FILL
            color = Color.BLUE
            interpolator = LinearInterpolator()
        }

        var started = false
        with(findViewById<Button>(R.id.btn)) {
            setOnClickListener {
                text = if (!started) {
                    waveAnimator.start()
                    started = true
                    "Stop"
                } else {
                    waveAnimator.stop()
                    started = false
                    "Start"
                }
            }
        }
    }
}