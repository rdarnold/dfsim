
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
public final class SpriteHandler_TimeFantasy {

    private SpriteHandler_TimeFantasy() { } // private constructor

    // Set up the time fantasy monster sprites, which have already been loaded
    public static void setupMonsterSprites() {
        if (Constants.USING_TIMEFANTASY == false) {
            return;
        }

        for (CharSprite sprite : Data.monsterSprites) {
            // At this point, assuming we loaded the JSON data properly,
            // each sprite is a single entity and can be set up normally.
            setupSingleMonsterSprites(sprite);
        }
    }

    // There really isn't much we have to do with these.  We basically
    // just clip frames a bit and remove unneeded whitespace.
    private static void setupSingleMonsterSprites(CharSprite sprite) {
        sprite.createFrames(3, 4);

        // We know, in TimeFantasy, that the sprites are aligned at the bottom
        // of the frames.  so we can slip a little at the top to make sure
        // we don't accidentally include it.
        sprite.clipTop(2);

        // These sprites are all drawn at the VERY BOTTOM of each frame,
        // which fucks things up sometimes, so we are going to buffer the top
        // and bottom of each frame with just a few pixels to make sure they don't
        // "intersect" with other frames.
        // Add a little padding to our transparent clipping.
        sprite.clipTransparent(2);
    }

}