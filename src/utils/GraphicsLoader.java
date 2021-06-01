
package dfsim;

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

import dfsim.gui.*;

// This is essentially like a static class in C#
public final class GraphicsLoader {

    private GraphicsLoader() { // private constructor
    }

    // Load all images from folder filePath, and put them into imageList
    // Assumes all files in folder ARE loadable images
    public static void loadAllImagesFromFolder(String filePath, ArrayList<Image> imageList) {
        String fileNameAndPath;
        Image image;
        File directoryPath;
        String contents[];
        int i = 0;

        directoryPath = new File(filePath);
        //List of all files and directories
        contents = directoryPath.list();
        if (contents == null) {
            Utils.log("Image folder " + directoryPath.getAbsolutePath() + " not found.");
            return;
        }
        //Utils.log("List of files and directories in " + directoryPath.getAbsolutePath() + ":");
        for (i = 0; i < contents.length; i++) {
            //Utils.log(contents[i]);
            fileNameAndPath = filePath + contents[i];
            image = loadOneImage(fileNameAndPath);
            if (image != null) {
                imageList.add(image);
            }
        }

        Utils.log("Loaded " + i + " images from " + directoryPath.getAbsolutePath());
    }

    public static Image loadOneImage(String fileNameAndPath) {
        try {
            InputStream stream = new FileInputStream(fileNameAndPath);
            Image image = new Image(stream);
            return image;
        }
        catch (FileNotFoundException e) {
           Utils.log("Image file " + fileNameAndPath + " not found.");
        }
        return null;
    }

    public static GameSprite loadOneSprite(String fileNameAndPath) {
        try {
            InputStream stream = new FileInputStream(fileNameAndPath);
            GameSprite sprite = new GameSprite(stream);
            return sprite;
        }
        catch (FileNotFoundException e) {
           Utils.log("GameSprite file " + fileNameAndPath + " not found.");
        }
        return null;
    }

    public static void loadImages() {
        loadPortraits();
        // loadGifs(); // Disabled for now just to save time
        loadCharSprites();
        loadTiles();
    }

    // Load up all the character portraits
    public static void loadPortraits() {
        // The . means use the working directory
        loadAllImagesFromFolder("." + Constants.FEMALE_PORTRAIT_PATH, Data.femalePortraitImages);
        loadAllImagesFromFolder("." + Constants.MALE_PORTRAIT_PATH, Data.malePortraitImages);

        // Set up the actual portrait lists, the portrait objs have some extra data on them
        for (Image img : Data.femalePortraitImages) {
            Data.femalePortraits.add(new Portrait(img));
        }
        for (Image img : Data.malePortraitImages) {
            Data.malePortraits.add(new Portrait(img));
        }
    }
    
    public static void loadCharSprites() {
        Data.spriteHero1 = loadOneSprite("." + Constants.FILENAME_CHAR_SPRITE_HERO1);
    }
    

    public static void loadGifs() {
        loadAllImagesFromFolder("." + Constants.GIF_PATH, Data.gifs);
    }

    public static void loadTiles() {
        // Load whichever tileset we want to use
        if (Constants.USING_PIPOYA == true) {
            loadPipoyaImages();
        }
    }

    public static void loadPipoyaImages() {
        // Load up the map image
        Data.spriteOverlandMap = loadOneSprite("." + Constants.PIPOYA_FILENAME_MAP);
        
        // And all the terrain sprite sheets
        Data.spriteGrass = loadOneSprite("." + Constants.PIPOYA_FILENAME_GRASS);
        Data.spritePath = loadOneSprite("." + Constants.PIPOYA_FILENAME_PATH);
        Data.spriteForest = loadOneSprite("." + Constants.PIPOYA_FILENAME_FOREST);
        Data.spriteDesert = loadOneSprite("." + Constants.PIPOYA_FILENAME_DESERT);
        Data.spriteDirt = loadOneSprite("." + Constants.PIPOYA_FILENAME_DIRT);
        Data.spriteSea = loadOneSprite("." + Constants.PIPOYA_FILENAME_SEA);
        Data.spriteMtn1 = loadOneSprite("." + Constants.PIPOYA_FILENAME_MTN1);
        Data.spriteMtn2 = loadOneSprite("." + Constants.PIPOYA_FILENAME_MTN2);
        Data.spriteMtn3 = loadOneSprite("." + Constants.PIPOYA_FILENAME_MTN3);
        
        // Now call the sprite handler to set up these sprites
        SpriteHandler_Pipoya.setupSprites();
    }
}