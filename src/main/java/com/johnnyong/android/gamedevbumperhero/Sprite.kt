package com.johnnyong.android.gamedevbumperhero

import android.graphics.Canvas

interface Sprite {
    // Draw the sprite onto the canvas
    fun draw(canvas: Canvas)
}