package com.johnnyong.android.gamedevbumperhero

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import androidx.lifecycle.ViewModel
import com.johnnyong.android.gamedevbumperhero.Upgrades.*
import java.util.*

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
    lateinit var monsterImage: Bitmap
    lateinit var playerImage: Bitmap
    lateinit var upgradeIcon: Bitmap
    lateinit var bossImage: Bitmap
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
        i = 4; monsterMaxVelocity
     */
    private var upgrades: IntArray = intArrayOf(0, 0, 0, 0, 0)
    // START OF UPGRADES SECTION
    // monsterLevel; determine amount of gold given and hp
    // Todo: Change back to level 1 after testing
    private var monsterLevel = upgrades[0] + 100
    // damage; how much hp the monster loses on collision
    private var damage = upgrades[1] + 5
    // heroVelocity; speed of hero
    private var heroVelocity = upgrades[2] + 5
    // monsterMinVelocity; speed of monster when not in "panic" state
    private var monsterMinVelocity = upgrades[3] + 3.0
    // monsterMaxVelocity; maximum speed of monster in "panic" state
    private var monsterMaxVelocity = upgrades[4] + 10.0
    // Todo: Change back to an appropriate number after testing
    private var maxMonstersUserCanSpawn = 100
    // END OF UPGRADES SECTION

    private var currentMonsterCount = 0
    // Prevent user from spawning too much and potentially crashing
    // (Not entirely sure if it will crash but I'll just assume so
    private val trueMaxMonstersUserCanSpawn = 100
    private val monstersSpawned = 0

    fun load(resources: Resources?) {
        if (!loaded)
        {
            // Loads the bitmaps from res file
            playerImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.playerimage
            )
            monsterImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.monsterimage
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
                R.drawable.bossimage
            )
            // Initialize heroSprite
            heroSprite = HeroSprite(this, playerImage, heroVelocity)
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
        for(sprite in sprites) sprite.draw(canvas)
        for(sprite in shopSprites) sprite.draw(canvas)
    }

    fun update() {
        // For each item in updatable, update them
        for (updatable in updatables) updatable.update()
    }

    private fun spawnMob(x: Double, y: Double) {
        // In case the user tries to spawn the monster under the ground
        Log.i(TAG, "minMonsterVelocity $monsterMinVelocity")
        if (y > screenHeight - monsterImage.height)
        {
            var monsterSprite = MonsterSprite(
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
                var monsterSprite = MonsterSprite(
                    this, monsterImage,
                    monsterLevel, damage, x, y,
                    monsterMinVelocity, monsterMaxVelocity
                )
                sprites.add(monsterSprite)
                updatables.add(monsterSprite)
            }
            else {
                var monsterSprite = MonsterSprite(
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
        Log.i(TAG, "minMonsterVelocity $monsterMinVelocity")
        if (y > screenHeight - bossImage.height)
        {
            var monsterSprite = MonsterSprite(
                this, bossImage,
                monsterLevel, damage + 5, x, screenHeight - monsterImage.height.toDouble(),
                monsterMinVelocity - 2, monsterMaxVelocity
            )
            sprites.add(monsterSprite)
            updatables.add(monsterSprite)
        }
        // Otherwise, spawn in the space tapped
        else
        {
            if ((0..1).random() == 1) {
                var monsterSprite = MonsterSprite(
                    this, bossImage,
                    monsterLevel + 5, damage, x, y,
                    monsterMinVelocity - 2, monsterMaxVelocity
                )
                sprites.add(monsterSprite)
                updatables.add(monsterSprite)
            }
            else {
                var monsterSprite = MonsterSprite(
                    this, bossImage,
                    monsterLevel + 5, damage, x, y,
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
        val monsterLevelUpgrade = MonsterLevelUpgrade(this, upgradeIcon, 100, 100)
        shopSprites.add(monsterLevelUpgrade)
        shopActionItems.add(monsterLevelUpgrade)

        val damageUpgrade = DamageUpgrade(this, upgradeIcon, 300, 100)
        shopSprites.add(damageUpgrade)
        shopActionItems.add(damageUpgrade)

        val heroVelocityUpgrade = HeroVelocityUpgrade(this, upgradeIcon, 500, 100)
        shopSprites.add(heroVelocityUpgrade)
        shopActionItems.add(heroVelocityUpgrade)

        val monsterMinVelocityUpgrade = MonsterMinVelocityUpgrade(this, upgradeIcon, 700, 100)
        shopSprites.add(monsterMinVelocityUpgrade)
        shopActionItems.add(monsterMinVelocityUpgrade)

        val monsterMaxVelocityUpgrade = MonsterMaxVelocityUpgrade(this, upgradeIcon, 900, 100)
        shopSprites.add(monsterMaxVelocityUpgrade)
        shopActionItems.add(monsterMaxVelocityUpgrade)
    }

    fun goldCheck(i: Int)
    {
        Log.i(TAG, "Gold: $gold")
        // Todo: Make an appropriate formula for upgrade costs
        if (gold >= upgrades[i])
        {
            Log.i(TAG, "upgrades[i] is " + upgrades[i])
            gold -= upgrades[i]
            upgradeIncrease(i);
            Log.i(TAG, "Gold after purchase: $gold")
        }
    }

    private fun upgradeIncrease(i: Int)
    {
        upgrades[i] = upgrades[i] + 1
        when (i)
        {
            0 -> monsterLevel = upgrades[i] + 1
            1 -> damage = upgrades[i] + 5
            2 -> heroVelocity = upgrades[i] + 5
            3 -> monsterMinVelocity = upgrades[i] + 3.0
            4 -> monsterMaxVelocity = upgrades[i] + 10.0
        }
        Log.i(TAG, "Upgraded $i")

    }
}