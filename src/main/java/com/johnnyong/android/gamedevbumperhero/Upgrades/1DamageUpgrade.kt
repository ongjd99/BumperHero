package com.johnnyong.android.gamedevbumperhero.Upgrades

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.johnnyong.android.gamedevbumperhero.ActionItem
import com.johnnyong.android.gamedevbumperhero.GameViewModel
import com.johnnyong.android.gamedevbumperhero.Sprite

class `1DamageUpgrade`(
    private val gameViewModel: GameViewModel,
    private val upgradeImage: Bitmap,
    val x: Int,
    val y: Int
): Sprite, ActionItem {

    private var level = gameViewModel.upgrades[1]

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(upgradeImage, x.toFloat(), y.toFloat(), null)

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 50f

        // Upgrade Level
        canvas.drawText(level.toString(), 210f, 440f, paint)
        // Gold Cost
        canvas.drawText(level.toString(), 350f, 440f, paint)
    }

    override fun doClick(px: Double, py: Double): Boolean {
        if (px > x && px < x + upgradeImage.width
            && py < y + upgradeImage.height && py > y)
        {
            val upgradePurchased = gameViewModel.goldCheck(1)
            if (upgradePurchased)
            {
                level++
            }
            return true
        }
        return false
    }
}