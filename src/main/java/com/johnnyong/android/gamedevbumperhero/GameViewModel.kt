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
    // monsterImage, playerImage, heroSprite; not entire sure if they should be here
    lateinit var monsterImage: Bitmap
    lateinit var playerImage: Bitmap
    lateinit var heroSprite: HeroSprite

    // monsterLevel; determine amount of gold given and hp
    // Todo: Change back to level 1 after testing
    private var monsterLevel = 100
    // damage; how much hp the monster loses on collision
    private var damage = 1
    // gold; for shopping
    private var gold = 0

    // Upgrades section variables
    private var heroVelocity = 5
    private var monsterMinVelocity = 5.0
    private var monsterMaxVelocity = 10.0
    private var maxMonstersUserCanSpawn = 10
    private var currentMonsterCount = 0
    // Prevent user from spawning too much and potentially crashing
    // (Not entirely sure if it will crash but I'll just assume so
    private val trueMaxMonstersUserCanSpawn = 100

    fun load(resources: Resources?) {
        if (!loaded)
        {
            // Loads the bitmaps from res file
            playerImage = BitmapFactory.decodeResource(resources,
                R.drawable.playerimage)
            monsterImage = BitmapFactory.decodeResource(resources,
                R.drawable.monsterimage)
            // Initialize heroSprite
            heroSprite = HeroSprite(this, playerImage, heroVelocity)
            sprites.add(heroSprite)
            updatables.add(heroSprite)
        }
    }
    fun doClick(x: Double, y: Int): Boolean {
        var any = false
        for (item in actionItems) {
            if (item.doClick(x,y)) {
                any = true
           }
        }

        /*
            doClick initially checks if any actionItems occurred (opening shop)
            If none, we can assume the user clicked on an empty space
            Spawn a mob if so
        */
        if(!any && currentMonsterCount < maxMonstersUserCanSpawn &&
                currentMonsterCount < trueMaxMonstersUserCanSpawn)
        {
            spawnMob(x, y)
            currentMonsterCount++
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

    private fun spawnMob(x: Double, y: Int) {
        // In case the user tries to spawn the monster under the ground
        if (y > screenHeight - monsterImage.height)
        {
            val monsterSprite = MonsterSprite(this, monsterImage,
                    monsterLevel, damage, x, screenHeight - monsterImage.height,
                    monsterMinVelocity, monsterMaxVelocity)
            sprites.add(monsterSprite)
            updatables.add(monsterSprite)
        }
        // Otherwise, spawn in the space tapped
        else
        {
            val monsterSprite = MonsterSprite(this, monsterImage,
                    monsterLevel, damage, x, y,
                    monsterMinVelocity, monsterMaxVelocity)
            sprites.add(monsterSprite)
            updatables.add(monsterSprite)
        }
    }

    fun destroyMonsterSpriteAndGrantGold(monsterSprite: MonsterSprite){
        sprites.removeAt(sprites.indexOf(monsterSprite))
        updatables.removeAt(updatables.indexOf(monsterSprite))
        // Todo: Maybe make a formula for earning gold cause rn its linear
        gold += monsterLevel
        currentMonsterCount--
    }
}