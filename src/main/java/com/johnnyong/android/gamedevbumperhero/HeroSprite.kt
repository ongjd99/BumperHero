package com.johnnyong.android.gamedevbumperhero

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log

private const val TAG = "MyActivity"

class HeroSprite(
    private val gameViewModel: GameViewModel,
    private val playerImage: Bitmap,
    private val flippedPlayerImage: Bitmap,
    var x: Float,
    newHeroVelocity: Float,
    private val isFlipped: Boolean
) : Sprite, Updatable {
    private val screenWidth = gameViewModel.screenWidth
    private val screenHeight = gameViewModel.screenHeight
    var flipped = isFlipped
    private var heroX = x
    private var heroY = (screenHeight - playerImage.height).toFloat()
    private var xVelocity = newHeroVelocity

    override fun draw(canvas: Canvas){
        if (flipped)
        {
            canvas.drawBitmap(flippedPlayerImage, heroX, heroY, null)
        }
        else
        {
            canvas.drawBitmap(playerImage, heroX, heroY, null)
        }
    }

    override fun update() {
        val newX = heroX + xVelocity
        // Bump off sides of wall
        if (newX > screenWidth - playerImage.width || newX < 0)
        {
            xVelocity = -xVelocity
            flipped = !flipped
        }
        heroX += xVelocity
    }

    fun getXPos(): Float {
        return heroX
    }

    fun getYPos(): Float {
        return heroY
    }

    fun getVelocity(): Float{
        return xVelocity
    }
}