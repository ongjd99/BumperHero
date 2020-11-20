package com.johnnyong.android.gamedevbumperhero

import android.content.res.Resources
import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: GameView
) : Thread() {
    private var running = false
    private var targetFPS = 60L
    private lateinit var gameViewModel: GameViewModel
    fun attachViewModel(model: GameViewModel) {
        gameViewModel = model
    }
    override fun run() {
        var startTime = 0L
        val targetTime = 1000L / targetFPS
        while (running) {
            var canvas: Canvas? = null
            try {
                startTime = System.nanoTime()
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    updateGame()
                    gameView.draw(canvas)
                }
            } catch (e: Exception) {} finally {
                if (canvas != null)
                {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val timeMillis =
                (System.nanoTime() - startTime) / 1000000L
            val waitTime = targetTime - timeMillis
            if (waitTime > 0L) {
                try {
                    sleep(waitTime)
                } catch (e: Exception) {}
            }
        }
    }
    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }
    fun doClick(x: Double, y: Double) : Boolean {
        return gameViewModel.doClick(x,y)
    }
    fun updateGame() {
        gameViewModel.update()
    }
}