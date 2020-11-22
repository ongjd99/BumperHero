package com.johnnyong.android.gamedevbumperhero

import android.graphics.Bitmap
import android.graphics.Canvas

class Background(
    private val gameViewModel: GameViewModel,
    private val background: Bitmap
): Sprite {

    override fun draw(canvas: Canvas){
        canvas.drawBitmap(background, 0f, 0f, null)
    }
}