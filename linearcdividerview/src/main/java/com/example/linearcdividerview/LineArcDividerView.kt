package com.example.linearcdividerview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.RectF

val colors : Array<Int> = arrayOf(
    "#F44336",
    "#3F51B5",
    "#4CAF50",
    "#03A9F4",
    "#795548"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 2
val arcs : Int = 4
val scGap : Float = 0.02f / (parts + 1)
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val deg : Float = 60f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawLineArcDivider(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val sc1 : Float = scale.divideScale(0, parts)
    val sc2 : Float = scale.divideScale(1, parts)
    val gap : Float = size / arcs
    save()
    translate(w / 2, h / 2)

    for (j in 0..1) {
        save()
        rotate(deg * (1 - 2 * j))
        drawLine(0f, -size * sc2, 0f, -size * sc1, paint)
        restore()
    }

    for (j in 0..(parts - 1)) {
        val sc1j : Float = sc1.divideScale(j, parts)
        val sc2j : Float = sc2.divideScale(j, parts)
        val r : Float = j * gap
        save()
        drawArc(RectF(-r, -r, r, r), 270f - deg + 2 * deg * sc2j, 2 * deg * (sc1j - sc2j), false, paint)
        restore()
    }
    restore()
}

fun Canvas.drawLADNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawLineArcDivider(scale, w, h, paint)
}

class LineArcDividerView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

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
}