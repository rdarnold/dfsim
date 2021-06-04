
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
        Data.spriteOverlandMap.addFrame("cave1", size * 6, y, size, size);
        Data.spriteOverlandMap.addFrame("cave2", size * 7, y, size, size);

        // Row 4
        y += size;
        Data.spriteOverlandMap.addFrame("mine1", size * 6, y, size, size);
        Data.spriteOverlandMap.addFrame("mine2", size * 7, y, size, size);

        // Skip to row 7
        y = size * 6;
        Data.spriteOverlandMap.addFrame("village", size, y, size * 2, size);
        Data.spriteOverlandMap.addFrame("bigcity", size * 3, y, size * 2, size * 2);
        Data.spriteOverlandMap.addFrame("capital", size * 5, y, size * 2, size * 2);
        
        // Row 8
        y += size;
        Data.spriteOverlandMap.addFrame("city", size, y, size * 2, size);

        // Row 9
        y += size;
        Data.spriteOverlandMap.addFrame("town", size, y, size * 2, size);
        Data.spriteOverlandMap.addFrame("castle1", size * 3, y, size * 2, size * 2);
        Data.spriteOverlandMap.addFrame("tower1", size * 5, y, size, size * 2);
        Data.spriteOverlandMap.addFrame("tower2", size * 6, y, size, size * 2);

        // The rest are fairly straightforward, just sets of evenly tiled terrain
        setupTerrainSprite(Data.spriteGrass);
        setupTerrainSprite(Data.spritePath);
        setupTerrainSprite(Data.spriteForest);
        setupTerrainSprite(Data.spriteDesert);
        setupTerrainSprite(Data.spriteDirt);
        setupTerrainSprite(Data.spriteMtn1);
        setupTerrainSprite(Data.spriteMtn2);
        setupTerrainSprite(Data.spriteMtn3);
        setupTerrainSprite(Data.spriteSea);
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

        // Now set up some keys for it.  We set keys for terrain sprites based on the 8
        // frames surrounding this one.  So each key has 8 parts, starting at the top
        // like a clock.  Each one of those keys is either a 1 or a 0.  1 if there is 
        // different terrain, 0 if same terrain.  Nothing counts as a 0.  So this is
        // basically a bitvector in String form, where each of the 8 directions is a bit.
        // x means "I don't care, could be either"
        // n ne e se s sw w nw
        sheet.setFrameKey(0, "1x1x1x1x");
        sheet.setFrameKey(1, "1x0x1x1x");
        sheet.setFrameKey(2, "1x0x1x0x");
        sheet.setFrameKey(3, "1x1x1x0x");
        sheet.setFrameKey(4, "1x1x0x1x");
        sheet.setFrameKey(5, "1x000x1x");
        sheet.setFrameKey(6, "1x00000x");
        sheet.setFrameKey(7, "1x1x000x");
        sheet.setFrameKey(8, "1x010x1x");
        sheet.setFrameKey(9, "1x1x010x");
        sheet.setFrameKey(10, "01010x1x");
        sheet.setFrameKey(11, "1x01010x");
        sheet.setFrameKey(12, "0x1x0x1x");
        sheet.setFrameKey(13, "00000x1x");
        sheet.setFrameKey(14, "00000000");
        sheet.setFrameKey(15, "0x1x0000");
        sheet.setFrameKey(16, "010x1x1x");
        sheet.setFrameKey(17, "0x1x1x01");
        sheet.setFrameKey(18, "010x1x01");
        sheet.setFrameKey(19, "0x1x0101");
        sheet.setFrameKey(20, "0x1x1x1x");
        sheet.setFrameKey(21, "000x1x1x");
        sheet.setFrameKey(22, "000x1x00");
        sheet.setFrameKey(23, "0x1x1x00");
        sheet.setFrameKey(24, "00010x1x");
        sheet.setFrameKey(25, "0x1x0100");
        sheet.setFrameKey(26, "1x01000x");
        sheet.setFrameKey(27, "1x00010x");
        sheet.setFrameKey(28, "00010000");
        sheet.setFrameKey(29, "00000100");
        sheet.setFrameKey(30, "01010100");
        sheet.setFrameKey(31, "00010101");
        sheet.setFrameKey(32, "01000x1x");
        sheet.setFrameKey(33, "0x1x0001");
        sheet.setFrameKey(34, "010x1x00");
        sheet.setFrameKey(35, "000x1x01");
        sheet.setFrameKey(36, "01000000");
        sheet.setFrameKey(37, "00000001");
        sheet.setFrameKey(38, "01010001");
        sheet.setFrameKey(39, "01000101");
        sheet.setFrameKey(40, "01000001");
        sheet.setFrameKey(41, "00010100");
        sheet.setFrameKey(42, "01010000");
        sheet.setFrameKey(43, "00000101");
        sheet.setFrameKey(44, "01000100");
        sheet.setFrameKey(45, "00010001");
        sheet.setFrameKey(46, "01010101");
        // 48 in total, the first is 0, the last is blank

        /*sheet.setFrameKey(0, "11111111");
        sheet.setFrameKey(1, "11011111");
        sheet.setFrameKey(2, "11011101");
        sheet.setFrameKey(3, "11111101");
        sheet.setFrameKey(4, "11110111");
        sheet.setFrameKey(5, "11000111");
        sheet.setFrameKey(6, "11000001");
        sheet.setFrameKey(7, "111x000x");
        sheet.setFrameKey(8, "11010111");
        sheet.setFrameKey(9, "11110101");
        sheet.setFrameKey(10, "01010111");
        sheet.setFrameKey(11, "11010101");
        sheet.setFrameKey(12, "01110111");
        sheet.setFrameKey(13, "00000111");
        sheet.setFrameKey(14, "00000000");
        sheet.setFrameKey(15, "01110000");
        sheet.setFrameKey(16, "01011111");
        sheet.setFrameKey(17, "01111101");
        sheet.setFrameKey(18, "01011101");
        sheet.setFrameKey(19, "01110101");
        sheet.setFrameKey(20, "01111111");
        sheet.setFrameKey(21, "00011111");
        sheet.setFrameKey(22, "00011100");
        sheet.setFrameKey(23, "00111000");
        sheet.setFrameKey(24, "00010111");
        sheet.setFrameKey(25, "01110100");
        sheet.setFrameKey(26, "11010001");
        sheet.setFrameKey(27, "11000101");
        sheet.setFrameKey(28, "00010000");
        sheet.setFrameKey(29, "00000100");
        sheet.setFrameKey(30, "01010100");
        sheet.setFrameKey(31, "00010101");
        sheet.setFrameKey(32, "01000111");
        sheet.setFrameKey(33, "01110001");
        sheet.setFrameKey(34, "01011100");
        sheet.setFrameKey(35, "00011101");
        sheet.setFrameKey(36, "01000000");
        sheet.setFrameKey(37, "00000001");
        sheet.setFrameKey(38, "01010001");
        sheet.setFrameKey(39, "01000101");
        sheet.setFrameKey(40, "01000001");
        sheet.setFrameKey(41, "00010100");
        sheet.setFrameKey(42, "01010000");
        sheet.setFrameKey(43, "00000101");
        sheet.setFrameKey(44, "01000100");
        sheet.setFrameKey(45, "00010001");
        sheet.setFrameKey(46, "01010101");*/
    }
}