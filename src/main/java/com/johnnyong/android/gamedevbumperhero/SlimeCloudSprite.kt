package com.johnnyong.android.gamedevbumperhero

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.lang.Thread.sleep

private const val TAG = "MyActivity"

class SlimeCloudSprite(
    private val gameViewModel: GameViewModel,
    private val cloudImage: Bitmap,
    private var x: Double,
    private var y: Double
): Sprite, Updatable {
    private val screenWidth = gameViewModel.screenWidth
    private val screenHeight = gameViewModel.screenHeight
    private val time = System.nanoTime()

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(cloudImage, x.toFloat(), y.toFloat(), null)
    }

    override fun update() {
        val newX = x + 1
        if (newX > screenWidth || newX < -cloudImage.width) {
            x = -cloudImage.width.toDouble()
        }
        x += 1

        Log.i(TAG,"x: $x")
        if (x % 500 == 0.0)
        {
            gameViewModel.spawnMob(x,y)
        }
    }
}