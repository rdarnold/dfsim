
package dfsim;

import javafx.scene.shape.*;
import javafx.scene.image.*;
import javafx.scene.effect.*;
import javafx.scene.paint.*;

import java.util.*;

import java.io.*; 
import java.nio.file.*;

import javax.json.*;
import javax.json.stream.*;

import dfsim.gui.*;

// This is essentially like a static class in C#
public final class GraphicsLoader {

    private GraphicsLoader() { // private constructor
    }

    // Check if the file is valid, right now we just make sure
    // the extension is a png.
    public static boolean fileIsValidImage(String fileName) {
        String ext = Utils.getFileExtensionFromName(fileName);
        if (ext == null || ext.length() <= 0) {
            return false;
        }
        if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("bmp") || ext.equals("gif")) {
            return true;
        }
        return false;
    }

    // Can use "plus" to add 1 to the value of plusVal
    // Can use "same" to use the same value as sameVal
    // Can use "width" to say it's the full width of the sprite
    // Can use "height" to say it's the full height of the sprite
    public static double parseJsonSpriteObj(JsonParser parser, CharSprite sprite, double plusVal, double sameVal) {
        String str = Utils.getNextJsonValueAsString(parser);
        if (str != null && str.length() > 0) {
            if (str.equals("same")) {
                return sameVal;
            }
            else if (str.equals("plus")) {
                return plusVal + 1;
            }
            else if (str.equals("width")) {
                return sprite.getImage().getWidth();
            }
            else if (str.equals("height")) {
                return sprite.getImage().getHeight();
            }
        }
        return (Utils.tryParseDouble(str)); //Utils.getNextJsonValueAsDouble(parser));
    }

    // Check if a sprite has an associated data sheet.  This sheet
    // tells us if we should be sub-dividing into sub-sheets and what
    // the dimensions are of each one.
    public static int loadJsonSpriteData(CharSprite sprite, ArrayList<CharSprite> spriteList) {
        int nLoaded = 0;
        if (sprite == null) {
            return nLoaded;
        } 
        if (sprite.getFileName() == null || sprite.getFileName() == "" || sprite.getFileName().contains(".") == false) {
            spriteList.add(sprite);
            return nLoaded;
        }

        // Just replace the extension of .png with .json
        String fileName = Utils.replaceFileExtensionWith(sprite.getFileName(), "json");

        // Read the raw text into a string.
        String jsonData = Utils.readFile(fileName, false);

        if (jsonData == null || jsonData.length() <= 0) {
            spriteList.add(sprite);
            return nLoaded;
        }

        // Parse the JSON data.
        CharSprite newSprite = null;
        double lastStartX = 0;
        double lastStartY = 0;
        double lastEndX = 0;
        double lastEndY = 0;

        JsonParser parser = Json.createParser(new StringReader(jsonData));
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                case START_ARRAY:
                    break;
                case END_ARRAY:
                    if (newSprite != null) {
                        // Sprite done, add it and move on.
                        newSprite = null;
                        //Utils.log("Done with sprite");
                    } 
                    break;
                case START_OBJECT:
                case END_OBJECT:
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_TRUE:
                    //System.out.println(event.toString());
                    break;
                case KEY_NAME:
                    if (parser.getString().contains("sprite")) {
                        newSprite = new CharSprite(sprite);
                        spriteList.add(newSprite);
                        nLoaded++;
                    }
                    if (newSprite != null) {
                        if (parser.getString().equals("name")) {
                            newSprite.setSpriteKey(Utils.getNextJsonValueAsString(parser));
                            //Utils.log("name: " +  newSprite.getSpriteKey());
                        }

                        // Can say "plus" to add 1 to the value of the last end
                        // Can say "same" to use the same value as last one
                        // Can use "width" to say it's the full width of the sprite
                        // Can use "height" to say it's the full height of the sprite
                        else if (parser.getString().equals("startx")) {
                            newSprite.setWithinImageX(parseJsonSpriteObj(parser, newSprite, lastEndX, lastStartX));
                            lastStartX = newSprite.getWithinImageX();

                            // Automatically size to the edge of the image unless we are told otherwise with endx later
                            newSprite.setWithinImageWidth(newSprite.getWithinImageWidth() - newSprite.getWithinImageX());
                            //Utils.log("startx: " + newSprite.getWithinImageX());
                        }
                        else if (parser.getString().equals("starty")) {
                            newSprite.setWithinImageY(parseJsonSpriteObj(parser, newSprite, lastEndY, lastStartY));
                            //newSprite.setWithinImageY(Utils.getNextJsonValueAsInt(parser));

                            lastStartY = newSprite.getWithinImageY();
                            
                            // Automatically size to the edge of the image unless we are told otherwise with endy later
                            newSprite.setWithinImageHeight(newSprite.getWithinImageHeight() - newSprite.getWithinImageY());
                            //Utils.log("starty: " + newSprite.getWithinImageY());
                        }
                        else if (parser.getString().equals("endx")) {
                            // "plus" doesn't really make sense in this context so just pass it as same value
                            double val = parseJsonSpriteObj(parser, newSprite, lastEndX, lastEndX);

                            lastEndX = val;
                            newSprite.setWithinImageWidth(val - newSprite.getWithinImageX());

                            //Utils.log("endx: " + lastEndX);
                        }
                        else if (parser.getString().equals("endy")) {
                            double val = parseJsonSpriteObj(parser, newSprite, lastEndY, lastEndY);

                            lastEndY = val;
                            
                            newSprite.setWithinImageHeight(val - newSprite.getWithinImageY());
                            //Utils.log("endy: " + lastEndY);
                        }
                    }
                    // Zoids.              
 	                break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                    break;
            }
        }

	    return nLoaded;
    }

    // Split a sprite into sub-sprites of specified cols and rows
    // This only works if the sub-sprites are evently spaced
    // This could be useful for a large sheet of multiple art "concepts" but they'd all have to be
    // spaced exactly the same way throughout.  Then we could skip providing the json file and just
    // use this function to automate it.
    private static void splitSpriteIntoSubsprites(ArrayList<CharSprite> fixedSprites, CharSprite sprite, int cols, int rows) {
        // All we need to do is split it up into X parts then subdivide each
        int spriteWidth = (int)sprite.getImage().getWidth() / cols;
        int spriteHeight = (int)sprite.getImage().getHeight() / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                CharSprite newSprite = new CharSprite(sprite);

                // This is creating 3 by 4 sprites, as in a character or monster sprite, we might not always
                // want to do it this way for other types of sprites.
                newSprite.createFrames(3, 4, col * spriteWidth, row * spriteHeight, spriteWidth, spriteHeight);
                fixedSprites.add(newSprite);

                // Clip these
                newSprite.clip(2);
            }
        }
    }

    // Load all images from folder filePath, and put them into imageList
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

    public static void loadAllGameSpritesFromFolder(String filePath, ArrayList<GameSprite> spriteList) {
        String fileNameAndPath;
        GameSprite sprite;
        File directoryPath;
        String contents[];
        int i = 0;

        directoryPath = new File(filePath);
        //List of all files and directories
        contents = directoryPath.list();
        if (contents == null) {
            Utils.log("GameSprite folder " + directoryPath.getAbsolutePath() + " not found.");
            return;
        }
        //Utils.log("List of files and directories in " + directoryPath.getAbsolutePath() + ":");
        for (i = 0; i < contents.length; i++) {
            fileNameAndPath = filePath + contents[i];
            sprite = loadOneSprite(fileNameAndPath);
            if (sprite != null) {
                spriteList.add(sprite);
            }
        }

        Utils.log("Loaded " + spriteList.size() + " game sprites from " + directoryPath.getAbsolutePath());
    }

    // Kind of wish I could just use the function above and then "upgrade" the objects
    public static void loadAllCharSpritesFromFolder(String filePath, ArrayList<CharSprite> spriteList) {
        String fileNameAndPath;
        CharSprite sprite;
        File directoryPath;
        String contents[];
        int i = 0;

        directoryPath = new File(filePath);
        //List of all files and directories
        contents = directoryPath.list();
        if (contents == null) {
            Utils.log("CharSprite folder " + directoryPath.getAbsolutePath() + " not found.");
            return;
        }
        //Utils.log("List of files and directories in " + directoryPath.getAbsolutePath() + ":");
        for (i = 0; i < contents.length; i++) {
            //Utils.log(contents[i]);
            fileNameAndPath = filePath + contents[i];
            sprite = loadOneCharSprite(fileNameAndPath);
            if (sprite != null) {
                loadJsonSpriteData(sprite, spriteList);
                //spriteList.add(sprite);
            }
        }

        Utils.log("Loaded " + spriteList.size() + " char sprites from " + directoryPath.getAbsolutePath());
    }
    
    // Kind of wish I could just use the function above and then "upgrade" the objects
    public static void loadAllSpriteAnimationsFromFolder(String filePath, ArrayList<SpriteAnimation> spriteList, int scalePercent) {
        String fileNameAndPath;
        SpriteAnimation sprite;
        File directoryPath;
        String contents[];
        int i = 0;

        directoryPath = new File(filePath);
        //List of all files and directories
        contents = directoryPath.list();
        if (contents == null) {
            Utils.log("SpriteAnimation folder " + directoryPath.getAbsolutePath() + " not found.");
            return;
        }
        //Utils.log("List of files and directories in " + directoryPath.getAbsolutePath() + ":");
        for (i = 0; i < contents.length; i++) {
            //Utils.log(contents[i]);
            fileNameAndPath = filePath + contents[i];
            sprite = loadOneSpriteAnimation(fileNameAndPath, scalePercent);
            spriteList.add(sprite);
        }

        Utils.log("Loaded " + spriteList.size() + " animation sprites from " + directoryPath.getAbsolutePath());
    }

    public static Image loadOneImage(String fileNameAndPath) {
        if (fileIsValidImage(fileNameAndPath) == false) {
            return null;
        }
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
        if (fileIsValidImage(fileNameAndPath) == false) {
            return null;
        }
        try {
            InputStream stream = new FileInputStream(fileNameAndPath);
            GameSprite sprite = new GameSprite(stream, fileNameAndPath);
            //Data.sprites.add(sprite); // Automatically add ref to master list
            return sprite;
        }
        catch (FileNotFoundException e) {
           Utils.log("GameSprite file " + fileNameAndPath + " not found.");
        }
        return null;
    }

    public static CharSprite loadOneCharSprite(String fileNameAndPath) {
        if (fileIsValidImage(fileNameAndPath) == false) {
            return null;
        }
        try {
            InputStream stream = new FileInputStream(fileNameAndPath);
            CharSprite sprite = new CharSprite(stream, fileNameAndPath);
            //Data.sprites.add(sprite); // Automatically add ref to master list
            return sprite;
        }
        catch (FileNotFoundException e) {
           Utils.log("CharSprite file " + fileNameAndPath + " not found.");
        }
        return null;
    }

    
    public static SpriteAnimation loadOneSpriteAnimation(String fileNameAndPath) {
        return loadOneSpriteAnimation(fileNameAndPath, 0);
    }

    public static SpriteAnimation loadOneSpriteAnimation(String fileNameAndPath, int sizePixels) {
        if (fileIsValidImage(fileNameAndPath) == false) {
            return null;
        }
        try {
            InputStream stream = new FileInputStream(fileNameAndPath);
            SpriteAnimation sprite = null;
            if (sizePixels > 0) {
                // We can load with a requested "scaling size" in sizePixels for sprite images that are too
                // large, like Hit2.
                sprite = new SpriteAnimation(stream, fileNameAndPath, sizePixels);
            }
            else {
                // Or just load it at its native size if the size is fine.
                sprite = new SpriteAnimation(stream, fileNameAndPath);
            }
            //Data.sprites.add(sprite); // Automatically add ref to master list
            return sprite;
        }
        catch (FileNotFoundException e) {
           Utils.log("SpriteAnimation file " + fileNameAndPath + " not found.");
        }
        return null;
    }


    public static void loadImages() {
        loadPortraits();
        loadGifs(); // Disabled for now just to save time
        loadCharSprites();
        loadTiles();
        loadSpriteAnimations();
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
        //Data.spriteHero1 = loadOneSprite("." + Constants.FILENAME_CHAR_SPRITE_HERO1);
        
        // The . means use the working directory
        loadAllCharSpritesFromFolder("." + Constants.FEMALE_CHARSPRITE_PATH, Data.femaleSprites);
        loadAllCharSpritesFromFolder("." + Constants.MALE_CHARSPRITE_PATH, Data.maleSprites);
        //loadAllCharSpritesFromFolder("." + Constants.MONSTER_CHARSPRITE_PATH, Data.monsterSprites);

        // Make the frames...
        for (CharSprite spr : Data.femaleSprites) {
            spr.createStandardFrames();
        }
        for (CharSprite spr : Data.maleSprites) {
            spr.createStandardFrames();
        }
        /*for (CharSprite spr : Data.monsterSprites) {
            spr.createStandardFrames();
        }*/

        // Load whichever set we want to use
        if (Constants.USING_TIMEFANTASY == true) {
            loadTimeFantasyMonsters();
        }
    }
    
    public static void loadGifs() {
        loadAllImagesFromFolder("." + Constants.GIF_PATH, Data.gifs);
    }
    
    public static void loadTiles() {
        // Load whichever tileset we want to use
        if (Constants.USING_PIPOYA == true) {
            loadPipoyaSprites();
        }

        // Now check all of them for dupes
        for (GameSprite spr : Data.sprites) {
            if (spr != null) {
                spr.hasDuplicateKeys();
            }
        }

        // Load hex tiles if we're using
        if (Constants.USING_VNHEX == true) {
            loadVNHexImages();
        }
    }

    public static void loadSpriteAnimations() {
        if (Constants.USING_HITS2 == true) {
            loadHits2Images();
        }
    }

    public static void loadHits2Images() {
        // Scale the large image on load
        loadAllSpriteAnimationsFromFolder("." + Constants.HITS2_PATH, Data.atkAnimSprites, 1024);

        // Now call the sprite handler to set up these sprites
        SpriteHandler_Hits2.setupAttackSprites();
    }

    public static void loadTimeFantasyMonsters() {
        loadAllCharSpritesFromFolder("." + Constants.TIMEFANTASY_PATH_MONSTER, Data.monsterSprites);

        // Now call the sprite handler to set up these sprites
        SpriteHandler_TimeFantasy.setupMonsterSprites();
    }

    public static void loadVNHexImages() {
        loadAllGameSpritesFromFolder("." + Constants.VNHEX_PATH, Data.hexTileSprites);

        // Now call the sprite handler to set up these sprites
        SpriteHandler_VNHex.setupHexTileSprites();
    }

    public static void loadPipoyaSprites() {
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