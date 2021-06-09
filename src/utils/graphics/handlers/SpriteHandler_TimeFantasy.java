
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

    private static ArrayList<CharSprite> fixedSprites;

    // Set up the time fantasy monster sprites, which have already been loaded
    public static void setupMonsterSprites() {
        if (Constants.USING_TIMEFANTASY == false) {
            return;
        }

        // This is our list of corrected sprites since we have to split some up
        fixedSprites = new ArrayList<CharSprite>();

        for (CharSprite sprite : Data.monsterSprites) {
            /*if (sprite.getFileName().contains("monster1.png") ||
                sprite.getFileName().contains("monster2.png") ||
                sprite.getFileName().contains("monster3.png") ||
                sprite.getFileName().contains("monster4.png")) {
                    // These four are larger sprites with 4 columns and 2 rows
                    splitSpriteEvenly(sprite, 4, 2);
                }
            else {
                // The rest are 'standard' 12-pose
                setupSingleMonsterSprites(sprite);
            }*/
            
            // At this point, assuming we loaded the JSON data properly,
            // each sprite is a single entity and can be set up normally.
            setupSingleMonsterSprites(sprite);
        }

        Data.monsterSprites.clear();
        Data.monsterSprites.addAll(fixedSprites);
    }

    // For the sheets with a single "standard" monster with 12 poses.
    private static void setupSingleMonsterSprites(CharSprite sprite) {
        fixedSprites.add(sprite);
        sprite.createFrames(3, 4);
        
        if (sprite.getFileName().contains("boar")) {
            // The boar needs to be clipped because it's way too small
            sprite.clip(10);
        }
    }

}