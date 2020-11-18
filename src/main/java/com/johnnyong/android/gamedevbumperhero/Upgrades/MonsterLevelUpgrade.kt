package com.johnnyong.android.gamedevbumperhero.Upgrades

import android.graphics.Bitmap
import android.graphics.Canvas
import com.johnnyong.android.gamedevbumperhero.ActionItem
import com.johnnyong.android.gamedevbumperhero.Sprite

class MonsterLevelUpgrade(
    private val upgradeImage: Bitmap,
    val x: Int,
    val y: Int
): Sprite, ActionItem {


    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(upgradeImage, x.toFloat(), y.toFloat(), null)
    }

    override fun doClick(px: Double, py: Int): Boolean {
        return false
    }
}