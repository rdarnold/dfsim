
package dfsim.gui;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.image.Image;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Enumeration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import dfsim.*;

// Sprite handlers for specific artists, sets up the sprites with indeces and parameters
// for each one.  Alternatively I could use some kind of key index text file in the future.
public final class SpriteHandler_Sinestesia {

    // Various Natural Hex Tile Set
    private SpriteHandler_Sinestesia() { } // private constructor

    // Set up the time fantasy monster sprites, which have already been loaded
    public static void setupAttackSprites() {
        if (Constants.USING_SINESTESIA == false) {
            return;
        }

        for (SpriteAnimation sprite : Data.atkAnimSprites) {
            
            // Create the frames for it
            sprite.createFrames(8, 8);
            
            // Clip transparent edges
            // Not good for these sprites because some are entirely transparent,
            // so if I index those to set the width / height it doesn't draw in the
            // right place.
            //sprite.clipTransparent(2);

            // Durations are all 0.45 seconds for these
            sprite.setDurationSeconds(0.45);

            // And assign the keys based on filename, will have to decide
            // which weapon goes with which anim.
            if (sprite.getFileName().contains("17.png")) {
                sprite.setSpriteKey("sword");
            }
            else if (sprite.getFileName().contains("20.png")) {
                sprite.setSpriteKey("bow");
            }
        }

    }
}