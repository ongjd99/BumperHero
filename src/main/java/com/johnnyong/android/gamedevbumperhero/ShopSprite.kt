package com.johnnyong.android.gamedevbumperhero

import android.graphics.Bitmap
import android.graphics.Canvas

class ShopSprite(
    private val gameViewModel: GameViewModel,
    private val shopImage: Bitmap,
): Sprite, ActionItem {
    private val screenWidth = gameViewModel.screenWidth
    private val screenHeight = gameViewModel.screenHeight
    private var shopDisplayed = false;

    private val xOffset = (screenWidth - shopImage.width).toFloat()
    private val yOffset = 0f

    override fun draw(canvas: Canvas){
        canvas.drawBitmap(shopImage, xOffset, yOffset, null)
    }

    override fun doClick(px: Double, py:Double): Boolean {
        if (px > xOffset && py < yOffset + shopImage.height)
        {
            shopDisplayed = if (shopDisplayed) {
                gameViewModel.destroyShop()
                false
            } else {
                gameViewModel.createShop()
                true
            }
            return true
        }
        return false
    }
}