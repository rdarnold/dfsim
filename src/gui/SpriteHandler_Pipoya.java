
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
public final class SpriteHandler_Pipoya {

    private SpriteHandler_Pipoya() { } // private constructor

    final static int size = 48;

    // This assumes sprites have been loaded already
    public static void setupSprites() {
        int x = 0;
        int y = 0;

        if (Constants.USING_PIPOYA == false) {
            return;
        }

        // Row 1
        Data.spriteOverlandMap.addFrame("grass", x, y, size, size);

        // Row 2
        y += size;
        Data.spriteOverlandMap.addFrame("tree", x += size, y, size, size);
        Data.spriteOverlandMap.addFrame("trees", x += size, y, size, size);
        Data.spriteOverlandMap.addFrame("bigtree", x += size, y, size, size);
        Data.spriteOverlandMap.addFrame("rock", x += size, y, size, size);
        Data.spriteOverlandMap.addFrame("bigrock", x += size, y, size, size);
        Data.spriteOverlandMap.addFrame("hole", x += size, y, size, size);
        Data.spriteOverlandMap.addFrame("pond", x += size, y, size, size);
        Data.spriteOverlandMap.addFrame("path", x += size, y, size, size);

        // Row 3
        y += size;
        x = size * 6;
        Data.spriteOverlandMap.addFrame("cave1", x += size, y, size, size);
        Data.spriteOverlandMap.addFrame("cave2", x += size, y, size, size);

        // Row 4
        y += size;
        x = size * 6;
        Data.spriteOverlandMap.addFrame("mine1", x += size, y, size, size);
        Data.spriteOverlandMap.addFrame("mine2", x += size, y, size, size);

        // Skip to row 7
        y = size * 6;
        x = size;
        Data.spriteOverlandMap.addFrame("village", x += (size*2), y, size * 2, size);
        Data.spriteOverlandMap.addFrame("bigcity", x += (size*2), y, size * 2, size * 2);
        Data.spriteOverlandMap.addFrame("capital", x += (size*2), y, size * 2, size * 2);
        
        // Row 8
        y += size;
        x = size;
        Data.spriteOverlandMap.addFrame("city", x += (size*2), y, size * 2, size);

        // Row 9
        y += size;
        x = size;
        Data.spriteOverlandMap.addFrame("town", x += (size*2), y, size * 2, size);
        Data.spriteOverlandMap.addFrame("castle1", x += (size*2), y, size * 2, size * 2);
        Data.spriteOverlandMap.addFrame("tower1", x += size, y, size, size * 2);
        Data.spriteOverlandMap.addFrame("tower2", x += size, y, size, size * 2);

        // The rest are fairly straightforward, just sets of evenly tiled terrain
        setupTerrainSprite(Data.spriteGrass);
        setupTerrainSprite(Data.spritePath);
        setupTerrainSprite(Data.spriteForest);
        setupTerrainSprite(Data.spriteDesert);
        setupTerrainSprite(Data.spriteDirt);
        setupTerrainSprite(Data.spriteSea);
        setupTerrainSprite(Data.spriteMtn1);
        setupTerrainSprite(Data.spriteMtn2);
        setupTerrainSprite(Data.spriteMtn3);
    }
    
    public static void setupTerrainSprite(GameSprite sheet) {
        // Now the others are relatively easy, since they're just terrain sheets.
        // 8 columns, 7 rows.
        int row = 0;
        int col = 0;
        while (row < 8) {
            while (col < 8) {
                // I could probably key these somehow in the future
                sheet.addFrame("", col * size, row * size, size, size);
                col++;
            }
            col = 0;
            row++;
        }
    }
}