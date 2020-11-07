package com.johnnyong.android.gamedevbumperhero

import android.graphics.Bitmap
import android.graphics.Canvas

class HeroSprite(
    private val gameViewModel: GameViewModel,
    private val playerImage: Bitmap,
    newHeroVelocity: Int
) : Sprite, Updatable {
    private val screenWidth = gameViewModel.screenWidth
    private val screenHeight = gameViewModel.screenHeight

    private var heroX = screenWidth * 0.5f
    // change to bottom of screen / on top of terrain
    private var heroY = (screenHeight - playerImage.height).toFloat()
    private var xVelocity = newHeroVelocity

    override fun draw(canvas: Canvas){
        canvas.drawBitmap(playerImage, heroX, heroY.toFloat(), null)
    }

    override fun update() {
        var newX = heroX + xVelocity
        // Todo?: Maybe make hero get knocked back when coming into contact with monster
        // We would have to get functions to work or do a workaround
        // Bump off sides of wall
        if (newX > screenWidth - playerImage.width || newX < 0)
        {
            xVelocity = -xVelocity
        }
        heroX += xVelocity
    }

    fun getPos(): Float {
        return heroX
    }
}