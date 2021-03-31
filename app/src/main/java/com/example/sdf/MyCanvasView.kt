package com.example.sdf

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView


class MySurfaceView(context: Context?) : SurfaceView(context),
    SurfaceHolder.Callback {
    private var mMyThread //наш поток прорисовки
            : MyThread? = null

    override fun surfaceCreated(holder: SurfaceHolder) { //вызывается, когда surfaceView появляется на экране
        mMyThread = MyThread(getHolder())
        mMyThread!!.setRunning(true)
        mMyThread!!.start() //запускает процесс в отдельном потоке
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
        //когда view меняет свой размер
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) { //когда view исчезает из поля зрения
        var retry = true
        mMyThread!!.setRunning(false) //останавливает процесс
        while (retry) {
            try {
                mMyThread!!.join() //ждет окончательной остановки процесса
                retry = false
            } catch (e: InterruptedException) {
                //не более чем формальность
            }
        }
    }

    init {
        holder.addCallback(this)
    }
}