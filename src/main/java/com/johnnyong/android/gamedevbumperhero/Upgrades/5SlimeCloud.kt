package com.johnnyong.android.gamedevbumperhero.Upgrades

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.johnnyong.android.gamedevbumperhero.ActionItem
import com.johnnyong.android.gamedevbumperhero.GameViewModel
import com.johnnyong.android.gamedevbumperhero.Sprite
import com.johnnyong.android.gamedevbumperhero.Updatable
import java.lang.Math.pow
import kotlin.math.pow

class `5SlimeCloud`(
    private val gameViewModel: GameViewModel,
    private val upgradeImage: Bitmap,
    val x: Int,
    val y: Int
): Sprite, ActionItem {

    private var level = gameViewModel.upgrades[4]

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(upgradeImage, x.toFloat(), y.toFloat(), null)

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 50f
        // Upgrade Level
        canvas.drawText(level.toString(), 1350f, 460f, paint)
        // Gold Cost
        canvas.drawText(formula(5).toString(), 1240f, 535f, paint)
    }

    override fun doClick(px: Double, py: Double): Boolean {
        if (px > x && px < x + upgradeImage.width
            && py < y + upgradeImage.height && py > y)
        {
            val upgradePurchased = goldCheck(5)
            if (upgradePurchased)
            {
                level++
            }
            return true
        }
        return false
    }

    private fun goldCheck(i: Int) : Boolean
    {
        val cost = formula(i)
        return if (gameViewModel.getGold() >= cost) {
            gameViewModel.takeGold(cost, i)
            true
        } else
            false
    }

    private fun formula(i: Int) : Long
    {
        return (100 +  (5 * gameViewModel.upgrades[i].toDouble()).pow(2.0).toLong())
    }
}