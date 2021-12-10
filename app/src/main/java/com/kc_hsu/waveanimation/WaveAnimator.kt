package com.kc_hsu.waveanimation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.min
import kotlin.math.roundToInt

class WaveAnimator : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint()
    private val circleList = CopyOnWriteArrayList<Circle>()

    var initialRadius: Float = 0f
        set(value) {
            if (value >= 0f) {
                field = value
            } else {
                throw IllegalArgumentException("Radius should be equal or greater than 0")
            }
        }

    var speed: Long = 500 //ms
        set(value) {
            if (value > 0f) {
                field = value
            } else {
                throw IllegalArgumentException("Speed need to be positive")
            }
        }

    var duration = 2_000
        set(value) {
            if (value > 0) {
                field = value
            } else {
                throw IllegalArgumentException("Duration need to be positive")
            }
        }

    var style: Paint.Style = Paint.Style.FILL
        set(value) {
            field = value
            paint.style = value
        }

    @ColorInt
    var color: Int = Color.RED
        set(value) {
            field = value
            paint.color = value
        }

    var interpolator = LinearInterpolator()

    private var maxRadius = 0f
    private var maxRadiusRate = 0.85f
    private var maxRadiusSet = false
    private var lastCreateTime: Long = 0 // ms
    private var isRunning = false

    private val circleGenerator = object : Runnable {
        override fun run() {
            if (isRunning) {
                newCircle()
                postDelayed(this, speed)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (!maxRadiusSet) {
            maxRadius = min(w, h) * maxRadiusRate / 2.0f
        }
    }

    override fun onDraw(canvas: Canvas?) {
        circleList.forEach { circle ->
            val radius = circle.currentRadius
            if (SystemClock.elapsedRealtime() - circle.creationTime < duration) {
                paint.alpha = circle.alpha
                canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
            } else {
                circleList.remove(circle)
            }
        }
        if (circleList.size > 0) {
            postInvalidateDelayed(10)
        }
    }

    fun start() {
        if (!isRunning) {
            isRunning = true
            circleGenerator.run()
        }
    }

    fun stop() {
        isRunning = false
    }

    private fun newCircle() {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastCreateTime < speed) {
            return
        }
        lastCreateTime = currentTime
        val circle = Circle()
        circleList.add(circle)
        invalidate()
    }

    private inner class Circle {
        var creationTime: Long = 0

        init {
            creationTime = SystemClock.elapsedRealtime()
        }

        val alpha: Int
            get() {
                val percent = (currentRadius - initialRadius) / (maxRadius - initialRadius)
                return (255 - interpolator.getInterpolation(percent) * 255).roundToInt()
            }

        val currentRadius: Float
            get() {
                val percent = (SystemClock.elapsedRealtime() - creationTime) * 1.0f / duration
                return initialRadius + interpolator.getInterpolation(percent) * (maxRadius - initialRadius)
            }
    }
}