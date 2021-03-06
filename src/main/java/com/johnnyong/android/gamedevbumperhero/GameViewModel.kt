package com.johnnyong.android.gamedevbumperhero

import android.content.res.Resources
import android.graphics.*
import android.os.Handler
import android.os.Looper
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
    private lateinit var bossImage: Bitmap
    private lateinit var powerupMonsterImage: Bitmap
    private lateinit var background: Bitmap
    private lateinit var heroDamageUpgradeImage: Bitmap
    private lateinit var heroVelocityUpgradeImage: Bitmap
    private lateinit var monsterLevelUpgradeImage: Bitmap
    private lateinit var monsterMaxSpawnUpgradeImage: Bitmap
    private lateinit var monsterMinVelocityUpgradeImage: Bitmap
    lateinit var playerImage: Bitmap
    private lateinit var flippedPlayerImage: Bitmap
    private lateinit var slimeCloudImage: Bitmap
    private lateinit var autoSpawnerUpgradeImage: Bitmap

    lateinit var heroSprite: HeroSprite

    // gold; for shopping
    // Todo: change this when done
    private var gold : Long = 250

    /*
        Array of the current upgrades level
        i = 0; monsterLevel
        i = 1; damage
        i = 2; heroVelocity
        i = 3; monsterMinVelocity
        i = 4; maxMonstersUserCanSpawn
     */

    var upgrades: IntArray = intArrayOf(0, 0, 0, 0, 0, 0)
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
    private var monstersKilled = 0

    // Powerups
    private var doubleGold = false

    fun load(resources: Resources?) {
        if (!loaded)
        {
            // Loads the bitmaps from res file
            playerImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.bumper_knight
            )
            flippedPlayerImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.bumper_knight_flipped
            )
            monsterImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.small_ball_monster
            )
            val shopImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.shop_icon
            )
            powerupMonsterImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.powerup_monster
            )
            slimeCloudImage = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.slime_cloud
                ),
                375,
                150,
                false
            )
            bossImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.big_ball_monster
            )
            background = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.bumper_hero_bg_no_cloud
                ),
                screenWidth,
                screenHeight,
                false
            )
            val cloud = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.cloud
                ),
                375,
                150,
                false
            )
            heroDamageUpgradeImage = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.hero_damage_upgrade
                ),
                475,
                200,
                false
            )
            heroVelocityUpgradeImage = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.hero_velocity_upgrade
                    ),
                475,
                200,
                false
            )
            monsterLevelUpgradeImage = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.monster_level_upgrade
                ),
                475,
                200,
                false
            )
            monsterMaxSpawnUpgradeImage = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.monster_max_spawn_upgrade
                ),
                475,
                200,
                false
            )
            monsterMinVelocityUpgradeImage = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.monster_velocity_upgrade
                    ),
            475,
            200,
            false
            )
            autoSpawnerUpgradeImage = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.auto_spawner_upgrade
                ),
                475,
                200,
                false
            )
            // Adding Hero Sprites
            heroSprite = HeroSprite(this, playerImage,
                flippedPlayerImage,
                screenWidth * 0.5f, heroVelocity, false)
            sprites.add(heroSprite)
            updatables.add(heroSprite)
            // Adding Shop Sprites
            val shopSprite = ShopSprite(this, shopImage)
            sprites.add(shopSprite)
            actionItems.add(shopSprite)
            // Adding CloudSprites
            var cloudSprite = CloudSprite(this, cloud,
                1100f, 200f)
            sprites.add(cloudSprite)
            updatables.add(cloudSprite)

            cloudSprite = CloudSprite(this, cloud,
                0f, 300f)
            sprites.add(cloudSprite)
            updatables.add(cloudSprite)

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
        val rand = (0..10).random()
        if (!any && currentMonsterCount < maxMonstersUserCanSpawn &&
            rand == 10)
        {
            spawnBossMob(x, y)
            currentMonsterCount++
        }
        else if (!any && currentMonsterCount < maxMonstersUserCanSpawn &&
            rand == 9)
        {
            spawnPowerUpMob(x, y)
            currentMonsterCount++
        }
        else if(!any && currentMonsterCount < maxMonstersUserCanSpawn)
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
        paint.textSize = 75f

        canvas.drawText("Gold: $gold", 1000f, 75f, paint)
        canvas.drawText( "Score: $monstersKilled", 400f, 75f, paint)

        if (doubleGold)
        {
            val paint = Paint()
            paint.color = Color.BLACK
            paint.textSize = 100f

            canvas.drawText("DOUBLE GOLD", 700f, 700f, paint)
        }

        for(sprite in sprites) sprite.draw(canvas)
        for(sprite in shopSprites) sprite.draw(canvas)
    }

    fun update() {
        // For each item in updatable, update them
        for (updatable in updatables) updatable.update()
    }

    fun spawnMob(x: Double, y: Double) {
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


    private fun spawnPowerUpMob(x: Double, y: Double) {
        if ((0..1).random() == 1) {
            val powerupMonsterSprite = PowerUpMonsterSprite(
                this, powerupMonsterImage,
                monsterLevel, damage, x, y,
                monsterMinVelocity, monsterMaxVelocity
            )
            sprites.add(powerupMonsterSprite)
            updatables.add(powerupMonsterSprite)
        }
        else {
            val powerupMonsterSprite = PowerUpMonsterSprite(
                this, powerupMonsterImage,
                monsterLevel, damage, x, y,
                -monsterMinVelocity, monsterMaxVelocity
            )
            sprites.add(powerupMonsterSprite)
            updatables.add(powerupMonsterSprite)
        }
    }

    private fun spawnBossMob(x: Double, y: Double) {
        if ((0..1).random() == 1) {
            val bossSprite = BossSprite(
                this, bossImage,
                monsterLevel, damage, x, y,
                (monsterMinVelocity - 2), monsterMaxVelocity
            )
            sprites.add(bossSprite)
            updatables.add(bossSprite)
        }
        else {
            val bossSprite = BossSprite(
                this, bossImage,
                monsterLevel, damage, x, y,
                -(monsterMinVelocity - 2), monsterMaxVelocity
            )
            sprites.add(bossSprite)
            updatables.add(bossSprite)
        }
    }

    fun destroyMonsterSpriteAndGrantGold(monsterSprite: MonsterSprite): Boolean{
        return if (monsterSprite.getHealth() <= 0){
            sprites.removeAt(sprites.indexOf(monsterSprite))
            updatables.removeAt(updatables.indexOf(monsterSprite))

            if (doubleGold)
            {
                gold += monsterLevel + 1
            }
            gold += monsterLevel + 1
            currentMonsterCount--
            monstersKilled++
            true
        } else
            false
    }

    fun destroyBossSpriteAndGrantGold(bossSprite: BossSprite): Boolean{
        return if (bossSprite.getHealth() <= 0){
            sprites.removeAt(sprites.indexOf(bossSprite))
            updatables.removeAt(updatables.indexOf(bossSprite))

            if (doubleGold)
            {
                gold += monsterLevel * 2 + 1
            }
            gold += monsterLevel * 2 + 1
            currentMonsterCount--
            monstersKilled++
            true
        } else
            false
    }

    fun destroyPowerUpMonsterSpriteAndGrantGold(powerUpMonsterSprite: PowerUpMonsterSprite): Boolean{
        return if (powerUpMonsterSprite.getHealth() <= 0){
            sprites.removeAt(sprites.indexOf(powerUpMonsterSprite))
            updatables.removeAt(updatables.indexOf(powerUpMonsterSprite))

            doubleGold = true
            Handler(Looper.getMainLooper()).postDelayed({
                doubleGold = false
            }, 60000)

            currentMonsterCount--
            monstersKilled++
            true
        } else
            false
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
        val monsterLevelUpgrade = `0MonsterLevelUpgrade`(this, monsterLevelUpgradeImage, 100, 100)
        shopSprites.add(monsterLevelUpgrade)
        shopActionItems.add(monsterLevelUpgrade)

        val damageUpgrade = `1DamageUpgrade`(this, heroDamageUpgradeImage, 100, 350)
        shopSprites.add(damageUpgrade)
        shopActionItems.add(damageUpgrade)

        val heroVelocityUpgrade = `2HeroVelocityUpgrade`(this, heroVelocityUpgradeImage, 600, 100)
        shopSprites.add(heroVelocityUpgrade)
        shopActionItems.add(heroVelocityUpgrade)

        val monsterVelocityUpgrade = `3MonsterVelocityUpgrade`(this, monsterMinVelocityUpgradeImage, 600, 350)
        shopSprites.add(monsterVelocityUpgrade)
        shopActionItems.add(monsterVelocityUpgrade)

        val maxMonstersUserCanSpawn = `4MaxMonstersUserCanSpawn`(this, monsterMaxSpawnUpgradeImage, 1100, 100)
        shopSprites.add(maxMonstersUserCanSpawn)
        shopActionItems.add(maxMonstersUserCanSpawn)

        val slimeCloud= `5SlimeCloud`(this, autoSpawnerUpgradeImage, 1100, 350)
        shopSprites.add(slimeCloud)
        shopActionItems.add(slimeCloud)
    }

    fun getGold(): Long
    {
        return gold
    }

    fun takeGold(formula: Long, i: Int)
    {
        gold -= formula
        upgradeIncrease(i)
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
                val isFlipped = heroSprite.flipped
                heroSprite = if (isFlipped) {
                    HeroSprite(
                        this, playerImage,
                        flippedPlayerImage, currentX, heroVelocity, isFlipped
                    )
                } else {
                    HeroSprite(
                        this, playerImage,
                        flippedPlayerImage, currentX, heroVelocity, isFlipped
                    )
                }
                sprites.add(heroSprite)
                updatables.add(heroSprite)
            }
            3 -> monsterMinVelocity = upgrades[i] + 3.0
            4 -> maxMonstersUserCanSpawn = upgrades[i] + 5
            5 -> {
                val slimeCloudSprite = SlimeCloudSprite(this, slimeCloudImage,
                    0.0 - slimeCloudImage.width, (100..350).random().toDouble())
                sprites.add(slimeCloudSprite)
                updatables.add(slimeCloudSprite)
            }
        }
    }


}