
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
public final class SpriteHandler_VNHex {

    // Various Natural Hex Tile Set
    private SpriteHandler_VNHex() { } // private constructor

    // Set up the time fantasy monster sprites, which have already been loaded
    public static void setupHexTileSprites() {
        if (Constants.USING_VNHEX == false) {
            return;
        }

        for (GameSprite sprite : Data.hexTileSprites) {
            // We need to rotate each one since they are the wrong orientation.
            sprite.rotateImage(90);

            // And assign the keys based on filename.
            if (sprite.getFileName().contains("0027")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.Dirt.name());
            }
            else if (sprite.getFileName().contains("0049")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.Wall.name());
            }
            else if (sprite.getFileName().contains("0041")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.Sand.name());
            }
            else if (sprite.getFileName().contains("0050")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.Stone.name());
            }
            else if (sprite.getFileName().contains("0026")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.Grass.name());
            }
            else if (sprite.getFileName().contains("0024")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.Field.name());
            }
            else if (sprite.getFileName().contains("0005")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.Tree.name());
            }
            else if (sprite.getFileName().contains("0014")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.Log.name());
            }
            else if (sprite.getFileName().contains("0061")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.Street.name());
            }
            else if (sprite.getFileName().contains("0012")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.ShallowWater.name());
            }
            else if (sprite.getFileName().contains("0040")) {
                sprite.setSpriteKey(HexMapTile.HexTileType.DeepWater.name());
            }
        }

    }
}