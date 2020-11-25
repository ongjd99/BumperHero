package com.johnnyong.android.gamedevbumperhero

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import androidx.lifecycle.ViewModel
import com.johnnyong.android.gamedevbumperhero.Upgrades.*

private const val TAG = "MyActivity"

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
    private lateinit var monsterImage: Bitmap
    private lateinit var upgradeIcon: Bitmap
    private lateinit var bossImage: Bitmap
    private lateinit var background: Bitmap
    lateinit var playerImage: Bitmap
    lateinit var heroSprite: HeroSprite

    // gold; for shopping
    // Todo: change this when done
    private var gold = 100

    /*
        Array of the current upgrades level
        i = 0; monsterLevel
        i = 1; damage
        i = 2; heroVelocity
        i = 3; monsterMinVelocity
        i = 4; maxMonstersUserCanSpawn
     */
    private var upgrades: IntArray = intArrayOf(0, 0, 0, 0, 0)
    // START OF UPGRADES SECTION
    // monsterLevel; determine amount of gold given and hp
    // Todo: Change back to level 1 after testing
    private var monsterLevel = upgrades[0]
    // damage; how much hp the monster loses on collision
    private var damage = upgrades[1] + 1
    // heroVelocity; speed of hero
    private var heroVelocity = upgrades[2] + 5f
    // monsterMinVelocity; speed of monster when not in "panic" state
    private var monsterMinVelocity = upgrades[3] + 3.0
    // monsterMaxVelocity; maximum speed of monster in "panic" state
    private var monsterMaxVelocity = monsterMinVelocity + 5f
    // Todo: Change back to an appropriate number after testing (5)
    private var maxMonstersUserCanSpawn = upgrades[4] + 5
    // END OF UPGRADES SECTION

    private var currentMonsterCount = 0
    // Prevent user from spawning too much and potentially crashing
    // (Not entirely sure if it will crash but I'll just assume so
    private val trueMaxMonstersUserCanSpawn = 100

    fun load(resources: Resources?) {
        if (!loaded)
        {
            // Loads the bitmaps from res file
            playerImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.bumper_knight
            )
            monsterImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.small_ball_monster
            )
            upgradeIcon = BitmapFactory.decodeResource(
                resources,
                R.drawable.upgradeicon
            )
            val shopImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.shopicon
            )
            bossImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.big_ball_monster
            )
            background = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.bumper_hero_bg
                ),
                screenWidth,
                screenHeight,
                false
            )

            heroSprite = HeroSprite(this, playerImage,
                screenWidth * 0.5f, heroVelocity)
            val shopSprite = ShopSprite(this, shopImage)
            sprites.add(shopSprite)
            actionItems.add(shopSprite)
            sprites.add(heroSprite)
            updatables.add(heroSprite)
        }
    }

    fun doClick(x: Double, y: Double): Boolean {
        var any = false
        for (item in actionItems) {
            if (item.doClick(x, y)) {
                any = true
           }
        }

        for (item in shopActionItems) {
            if (item.doClick(x, y)) {
                any = true
            }
        }

        /*
            doClick initially checks if any actionItems occurred (opening shop)
            If none, we can assume the user clicked on an empty space
            Spawn a mob if so
        */
        if (!any && currentMonsterCount < maxMonstersUserCanSpawn &&
            currentMonsterCount < trueMaxMonstersUserCanSpawn &&
            (0..5).random() == 5)
        {
            spawnBossMob(x, y)
            currentMonsterCount++
        }
        else if(!any && currentMonsterCount < maxMonstersUserCanSpawn &&
                currentMonsterCount < trueMaxMonstersUserCanSpawn)
        {
            spawnMob(x, y)
            currentMonsterCount++
        }

        return any
    }

    fun draw(canvas: Canvas) {
        // For each item in the sprite list, draw them
        canvas.drawBitmap(background, 0f, 0f, null)

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 100f

        canvas.drawText("Gold: $gold", 100f, 100f, paint)
        for(sprite in sprites) sprite.draw(canvas)
        for(sprite in shopSprites) sprite.draw(canvas)
    }

    fun update() {
        // For each item in updatable, update them
        for (updatable in updatables) updatable.update()
    }

    private fun spawnMob(x: Double, y: Double) {
        // In case the user tries to spawn the monster under the ground
        if (y > screenHeight - monsterImage.height)
        {
            val monsterSprite = MonsterSprite(
                this, monsterImage,
                monsterLevel, damage, x, screenHeight - monsterImage.height.toDouble(),
                monsterMinVelocity, monsterMaxVelocity
            )
            sprites.add(monsterSprite)
            updatables.add(monsterSprite)
        }
        // Otherwise, spawn in the space tapped
        else
        {
            if ((0..1).random() == 1) {
                val monsterSprite = MonsterSprite(
                    this, monsterImage,
                    monsterLevel, damage, x, y,
                    monsterMinVelocity, monsterMaxVelocity
                )
                sprites.add(monsterSprite)
                updatables.add(monsterSprite)
            }
            else {
                val monsterSprite = MonsterSprite(
                    this, monsterImage,
                    monsterLevel, damage, x, y,
                    -monsterMinVelocity, monsterMaxVelocity
                )
                sprites.add(monsterSprite)
                updatables.add(monsterSprite)
            }

        }
    }

    private fun spawnBossMob(x: Double, y: Double) {
        // In case the user tries to spawn the monster under the ground
        if (y > screenHeight + bossImage.height)
        {
            val monsterSprite = MonsterSprite(
                this, bossImage,
                (monsterLevel * 2) + 5, damage, x, screenHeight - monsterImage.height.toDouble(),
                monsterMinVelocity - 2, monsterMaxVelocity
            )
            sprites.add(monsterSprite)
            updatables.add(monsterSprite)
        }
        // Otherwise, spawn in the space tapped
        else
        {
            if ((0..1).random() == 1) {
                val monsterSprite = MonsterSprite(
                    this, bossImage,
                    (monsterLevel * 2) + 5, damage, x, y,
                    (monsterMinVelocity - 2), monsterMaxVelocity
                )
                sprites.add(monsterSprite)
                updatables.add(monsterSprite)
            }
            else {
                val monsterSprite = MonsterSprite(
                    this, bossImage,
                    (monsterLevel * 2) + 5, damage, x, y,
                    -(monsterMinVelocity - 2), monsterMaxVelocity
                )
                sprites.add(monsterSprite)
                updatables.add(monsterSprite)
            }

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


    /*
    Array of the current upgrades level
    i = 0; monsterLevel
    i = 1; damage
    i = 2; heroVelocity
    i = 3; monsterMinVelocity
    i = 4; monsterMaxVelocity
 */
    fun createShop()
    {
        val monsterLevelUpgrade = `0MonsterLevelUpgrade`(this, upgradeIcon, 100, 100)
        shopSprites.add(monsterLevelUpgrade)
        shopActionItems.add(monsterLevelUpgrade)

        val damageUpgrade = `1DamageUpgrade`(this, upgradeIcon, 300, 100)
        shopSprites.add(damageUpgrade)
        shopActionItems.add(damageUpgrade)

        val heroVelocityUpgrade = `2HeroVelocityUpgrade`(this, upgradeIcon, 500, 100)
        shopSprites.add(heroVelocityUpgrade)
        shopActionItems.add(heroVelocityUpgrade)

        val monsterVelocityUpgrade = `3MonsterVelocityUpgrade`(this, upgradeIcon, 700, 100)
        shopSprites.add(monsterVelocityUpgrade)
        shopActionItems.add(monsterVelocityUpgrade)

        val maxMonstersUserCanSpawn = `4MaxMonstersUserCanSpawn`(this, upgradeIcon, 900, 100)
        shopSprites.add(maxMonstersUserCanSpawn)
        shopActionItems.add(maxMonstersUserCanSpawn)
    }

    fun goldCheck(i: Int)
    {
        // Todo: Make an appropriate formula for upgrade costs
        if (gold >= upgrades[i])
        {
            gold -= upgrades[i]
            upgradeIncrease(i)
        }
    }

    private fun upgradeIncrease(i: Int)
    {
        upgrades[i] = upgrades[i] + 1
        when (i)
        {
            0 -> monsterLevel = upgrades[i] + 1
            1 -> damage = upgrades[i] + 5
            2 -> {
                heroVelocity = upgrades[i] + 5f
                if (heroSprite.getVelocity() < 0)
                {
                    heroVelocity = -heroVelocity
                }
                sprites.removeAt(sprites.indexOf(heroSprite))
                updatables.removeAt(updatables.indexOf(heroSprite))
                val currentX = heroSprite.getXPos()
                heroSprite = HeroSprite(this, playerImage, currentX, heroVelocity)
                sprites.add(heroSprite)
                updatables.add(heroSprite)
            }
            3 -> monsterMinVelocity = upgrades[i] + 3.0
            4 -> maxMonstersUserCanSpawn = upgrades[i] + 5
        }
    }
}