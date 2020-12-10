package com.johnnyong.android.gamedevbumperhero.Upgrades

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.johnnyong.android.gamedevbumperhero.ActionItem
import com.johnnyong.android.gamedevbumperhero.GameViewModel
import com.johnnyong.android.gamedevbumperhero.Sprite
import com.johnnyong.android.gamedevbumperhero.Updatable
import kotlin.math.pow

class `4MaxMonstersUserCanSpawn`(
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
        canvas.drawText(level.toString(), 1350f, 210f, paint)
        // Gold Cost
        canvas.drawText(formula(4).toString(), 1240f, 285f, paint)
    }

    override fun doClick(px: Double, py: Double): Boolean {
        if (px > x && px < x + upgradeImage.width
            && py < y + upgradeImage.height && py > y)
        {
            val upgradePurchased = goldCheck(4)
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
        // Todo: Make an appropriate formula for upgrade costs
        val cost = formula(i)
        return if (gameViewModel.getGold() >= cost) {
            gameViewModel.takeGold(cost, i)
            true
        } else
            false
    }

    private fun formula(i: Int) : Long
    {
        return (0.042 * gameViewModel.upgrades[i].toDouble().pow(5.0) +
                0.42 * gameViewModel.upgrades[i].toDouble().pow(4.0) -
                0.64 * gameViewModel.upgrades[i].toDouble().pow(3.0) -
                0.039 * gameViewModel.upgrades[i].toDouble().pow(2.0) +
                10.652 * gameViewModel.upgrades[i] +
                5).toLong()
    }
}