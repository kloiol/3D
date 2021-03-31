package com.example.sdf

import android.animation.ArgbEvaluator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder



class MyThread(//нужен, для получения canvas
    private val mSurfaceHolder: SurfaceHolder
) : Thread() {
    private val REDRAW_TIME = 10 //частота обновления экрана - 10 мс
    private val ANIMATION_TIME = 1500 //анимация - 1,5 сек
    private var mRunning //запущен ли процесс
            = false
    private var mStartTime //время начала анимации
            : Long = 0
    private var mPrevRedrawTime //предыдущее время перерисовки
            : Long = 0
    private val mPaint: Paint
    private val mArgbEvaluator: ArgbEvaluator
    fun setRunning(running: Boolean) { //запускает и останавливает процесс
        mRunning = running
        mPrevRedrawTime = time
    }

    val time: Long
        get() = System.nanoTime() / 1000000

    override fun run() {
        var canvas: Canvas?
        mStartTime = time
        while (mRunning) {
            val curTime = time
            val elapsedTime = curTime - mPrevRedrawTime
            if (elapsedTime < REDRAW_TIME) //проверяет, прошло ли 10 мс
                continue
            //если прошло, перерисовываем картинку
            canvas = null
            try {
                canvas = mSurfaceHolder.lockCanvas() //получаем canvas
                synchronized(mSurfaceHolder) {
                    draw(canvas) //функция рисования
                }
            } catch (e: NullPointerException) { /*если canvas не доступен*/
            } finally {
                if (canvas != null) mSurfaceHolder.unlockCanvasAndPost(canvas) //освобождаем canvas
            }
            mPrevRedrawTime = curTime
        }
    }

    private fun draw(canvas: Canvas?) {
        val curTime = time - mStartTime
        val width = canvas!!.width
        val height = canvas.height
        canvas.drawColor(Color.BLACK)
        val centerX = width / 2
        val centerY = height / 2
        val maxSize = Math.min(width, height) / 2.toFloat()
        val fraction = (curTime % ANIMATION_TIME).toFloat() / ANIMATION_TIME
        val color = mArgbEvaluator.evaluate(
            fraction,
            Color.RED,
            Color.BLACK
        ) as Int
        mPaint.color = color
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), maxSize * fraction, mPaint)
    }

    init {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mArgbEvaluator = ArgbEvaluator()
    }
}