package com.johnnyong.android.gamedevbumperhero

import android.graphics.Bitmap
import android.graphics.Canvas

class MonsterSprite(
    private val gameViewModel: GameViewModel,
    // Maybe we pass in HeroSprite to use its variables
    // Currently we have to make get functions
    // I dunno how to do that
    private val image: Bitmap,
    newHealth: Int,
    newDamage: Int,
    private var x: Int,
    private var y: Int
) : Sprite, Updatable {
    private val screenWidth = gameViewModel.screenWidth
    private val screenHeight = gameViewModel.screenHeight

    private var monsterX = x
    // change to bottom of screen / on top of terrain
    private var monsterY = y
    /*
        Maybe make this 0 so monsters are static
        Otherwise, change xVelocity?
     */
    private var xVelocity = 5
    private var health = newHealth
    private var damage = newDamage
    private var falling = true

    override fun draw(canvas: Canvas){
        canvas.drawBitmap(image, monsterX.toFloat(), monsterY.toFloat(), null)
    }

    override fun update() {
        /*
            Monster is tapped into the air (typically)
            We need monster to fall to the ground before the hero
            can even interact with the monster
        */
        if (falling)
        {
            // Make the monster fall to the ground (adjust falling speed?)
            monsterY += 10
            if (monsterY > screenHeight - image.height) {
                falling = false
            }
        }
        else
        {
            var newX = monsterX + xVelocity
            // Checks to see if the monster has collided with the player
            /*
                newX is the position that the monster will be in after updated
                getPlayerX is the current position of the hero (assuming the middle pixel)
                + (playerImage.width / 2) hopefully gets the right side of the hero image
                - (playerImage.width / 2) hopefully gets the left side of the hero image
                Therefore the entire if statement is an attempt to see if the Hero image
                encapsulates newX
             */
            // Todo: get these functions to work or do a workaround by introducing HeroSprite into parameter
/*            if(newX < getPlayerX + (playerImage.width / 2) && newX > getPlayerX - (playerImage.width / 2))
            {
                newX *= 2
                health -= damage
            }*/
            // Bump of sides of wall
            if (newX > screenWidth - image.width || newX < 0)
            {
                xVelocity = -xVelocity
            }
            monsterX += xVelocity
        }
    }
}