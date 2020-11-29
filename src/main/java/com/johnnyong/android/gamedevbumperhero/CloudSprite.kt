package com.johnnyong.android.gamedevbumperhero

import android.graphics.Bitmap
import android.graphics.Canvas

class CloudSprite(
    private val gameViewModel: GameViewModel,
    private val cloudImage: Bitmap,
    private var x: Float,
    private var y: Float
): Sprite, Updatable {
    private val screenWidth = gameViewModel.screenWidth
    private val screenHeight = gameViewModel.screenHeight

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(cloudImage, x, y, null)
    }

    override fun update() {
        val newX = x + 1
        if (newX > screenWidth  || newX < -cloudImage.width.toFloat()) {
            x = -cloudImage.width.toFloat()
        }
        x += 1
    }
}