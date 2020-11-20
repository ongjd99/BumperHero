package com.johnnyong.android.gamedevbumperhero

import android.graphics.Bitmap
import android.graphics.Canvas
import kotlin.math.abs
import kotlin.math.pow

class MonsterSprite(
    private val gameViewModel: GameViewModel,
    private val image: Bitmap,
    monsterLevel: Int,
    newDamage: Int,
    x: Double,
    y: Double,
    newMonsterMinVelocity: Double,
    newMonsterMaxVelocity: Double
) : Sprite, Updatable {
    private val screenWidth = gameViewModel.screenWidth
    private val screenHeight = gameViewModel.screenHeight
    private var xVelocity = newMonsterMinVelocity
    private var yVelocity = 0.0
    private var monsterX = x
    private var monsterY = y
    // Todo: Create a formula for health
    private var health = 5 + (monsterLevel * 1)
    private var damage = newDamage
    private val monsterMinVelocity = abs(newMonsterMinVelocity)
    private val monsterMaxVelocity = newMonsterMaxVelocity

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, monsterX.toFloat(), monsterY.toFloat(), null)
    }

    override fun update() {
        /*
            Monster is tapped into the air (typically)
            We need monster to fall to the ground before the hero
            can even interact with the monster
        */
        var newX = monsterX + xVelocity

        // Checks to see if the monster has collided with the player
        /*
            newX is the position that the monster will be in after updated
            getPos() is the current position of the hero (assuming the middle pixel)
            + (playerImage.width / 2) hopefully gets the right side of the hero image
            - (playerImage.width / 2) hopefully gets the left side of the hero image
            Therefore the if statement is an attempt to see if the Hero image
            encapsulates newX
         */
        if (newX < gameViewModel.heroSprite.getXPos() + (gameViewModel.playerImage.width / 2) &&
            newX > gameViewModel.heroSprite.getXPos() - (gameViewModel.playerImage.width / 2) &&
                monsterY > gameViewModel.heroSprite.getYPos() - (gameViewModel.playerImage.height/2))
        {
            // Upon collision, deal damage to monster
            health -= damage
            // Destroy monster
            if (health <= 0) {
                gameViewModel.destroyMonsterSpriteAndGrantGold(this)
                return
            }

            /*
                Instead of knocking back the monster like originally planned,
                The monster instead "panics" and runs faster the opposite way
                of the hero for a short period
                "Panic": if abs(xVelocity) > monsterMinVelocity
            */

            if (abs(xVelocity) < monsterMaxVelocity)
            {
                // Todo: Make an actual formula for panic speed
                xVelocity = -xVelocity * 5
            }
            else
            {
                xVelocity = -xVelocity
            }

            newX = monsterX + xVelocity
            if (newX > screenWidth - image.width || newX < 0) {
                xVelocity = -xVelocity
            }

            /*
                Trying to make monsters get knocked up when they get hit
                Not working at all
            /////////
            yVelocity += 20
            monsterY -= yVelocity
           */

            monsterX += xVelocity
        }
        else
        {
            // Bump of sides of wall
            if (newX > screenWidth - image.width || newX < 0) {
                xVelocity = -xVelocity
            }
            // Move as normal
            if (monsterY < screenHeight - image.height && yVelocity > 0)
            {
                yVelocity -= 0.2
                monsterY += yVelocity
            }
            else if (monsterY < screenHeight - image.height)
            {
                monsterY += 10
            }
            monsterX += xVelocity
        }

        // If monster is in a state of panic, slow down
        if (abs(xVelocity) > monsterMinVelocity)
        {
            // Todo: Formula for velocity decay
            if (xVelocity > 0)
            {
                xVelocity -= 0.1
            }
            else
            {
                xVelocity += 0.1
            }
        }
    }
}