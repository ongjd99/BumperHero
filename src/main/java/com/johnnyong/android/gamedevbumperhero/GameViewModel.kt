package com.johnnyong.android.gamedevbumperhero

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    // Grab the size of the screen, used to detect bouncing on sides
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private var loaded = false
    private val sprites = mutableListOf<Sprite>()
    private val updatables = mutableListOf<Updatable>()
    private var actionItems = mutableListOf<ActionItem>()
    lateinit var monsterImage: Bitmap

    // Monster hp and how much damage the monster will take
    private var health = 5
    private var damage = 1

    fun load(resources: Resources?) {
        if (!loaded)
        {
            // Loads the bitmaps from res file
            val playerImage = BitmapFactory.decodeResource(resources,
                R.drawable.playerimage)
            monsterImage = BitmapFactory.decodeResource(resources,
                R.drawable.monsterimage)
            // Initialize heroSprite
            // Todo: Make HeroSprite initially spawn in the middle on the ground,
            // Todo: Most likely done in HeroSprite
            var heroSprite = HeroSprite(this, playerImage)
            sprites.add(heroSprite)
            updatables.add(heroSprite)
        }
    }
    fun doClick(x: Int, y: Int): Boolean {
        var any = false
        for (item in actionItems) {
            if (item.doClick(x,y)) {
                any = true
           }
        }

        if(!any)
        {
            spawnMob(x, y)
        }

        return any
    }

    fun draw(canvas: Canvas) {
        // For each item in the sprite list, draw them
        for(sprite in sprites) sprite.draw(canvas)
    }

    fun update() {
        // For each item in updatable, update them
        for (updatable in updatables) updatable.update()
    }

    private fun spawnMob(x: Int, y: Int) {
        if (y > screenHeight - monsterImage.height) {
            val monsterSprite = MonsterSprite(this, monsterImage,
                    health, damage, x, screenHeight - monsterImage.height)
            sprites.add(monsterSprite)
            updatables.add(monsterSprite)
        } else {
            val monsterSprite = MonsterSprite(
                    this, monsterImage,
                    health, damage, x, y)
            sprites.add(monsterSprite)
            updatables.add(monsterSprite)
        }
    }
}