package com.johnnyong.android.gamedevbumperhero

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.lifecycle.ViewModel
import com.johnnyong.android.gamedevbumperhero.Upgrades.*

class GameViewModel : ViewModel() {
    // Grab the size of the screen, used to detect bouncing on sides
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private var loaded = false
    private val shopSprites = mutableListOf<Sprite>()
    private val shopActionItems = mutableListOf<ActionItem>()
    private val sprites = mutableListOf<Sprite>()
    private val updatables = mutableListOf<Updatable>()
    private var actionItems = mutableListOf<ActionItem>()
    // monsterImage, playerImage, heroSprite; not entire sure if they should be here
    lateinit var monsterImage: Bitmap
    lateinit var playerImage: Bitmap
    lateinit var upgradeIcon: Bitmap
    lateinit var heroSprite: HeroSprite

    // gold; for shopping
    private var gold = 0

    // START OF UPGRADES SECTION
    // monsterLevel; determine amount of gold given and hp
    // Todo: Change back to level 1 after testing
    private var monsterLevel = 100
    // damage; how much hp the monster loses on collision
    private var damage = 1
    private var heroVelocity = 5
    private var monsterMinVelocity = 5.0
    private var monsterMaxVelocity = 10.0
    // Todo: Change back to an appropriate number after testing
    private var maxMonstersUserCanSpawn = 100
    // END OF UPGRADES SECTION

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
            upgradeIcon = BitmapFactory.decodeResource(resources,
                R.drawable.upgradeicon)
            var shopImage = BitmapFactory.decodeResource(resources,
                R.drawable.shopicon)
            // Initialize heroSprite
            heroSprite = HeroSprite(this, playerImage, heroVelocity)
            var shopSprite = ShopSprite(this, shopImage)
            sprites.add(shopSprite)
            actionItems.add(shopSprite)
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

        for (item in shopActionItems) {
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
        for(sprite in shopSprites) sprite.draw(canvas)
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

    fun destroyShop()
    {
       shopSprites.clear()
       shopActionItems.clear()
    }

    fun createShop()
    {
        val damageUpgrade = DamageUpgrade(upgradeIcon, 100, 100)
        shopSprites.add(damageUpgrade)
        shopActionItems.add(damageUpgrade)

        val heroVelocityUpgrade = HeroVelocityUpgrade(upgradeIcon, 300, 100)
        shopSprites.add(heroVelocityUpgrade)
        shopActionItems.add(heroVelocityUpgrade)

        val monsterLevelUpgrade = MonsterLevelUpgrade(upgradeIcon, 500, 100)
        shopSprites.add(monsterLevelUpgrade)
        shopActionItems.add(monsterLevelUpgrade)

        val monsterMaxVelocityUpgrade = MonsterMaxVelocityUpgrade(upgradeIcon, 700, 100)
        shopSprites.add(monsterMaxVelocityUpgrade)
        shopActionItems.add(monsterMaxVelocityUpgrade)

        val monsterMinVelocityUpgrade = MonsterMinVelocityUpgrade(upgradeIcon, 900, 100)
        shopSprites.add(monsterMinVelocityUpgrade)
        shopActionItems.add(monsterMinVelocityUpgrade)
    }
}