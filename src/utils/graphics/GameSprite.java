package dfsim.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import java.util.List;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.animation.PathTransition;
import javafx.util.Duration;
import javafx.scene.image.Image;
import java.io.InputStream;


import dfsim.*;
import dfsim.gui.*;

// Generic sprite processing that covers all areas

// A GameSprite is one art "concept" in the game.  So it is an entire
// set of images for one character sprite, or a single image for a town on the overland map, 
// or a set of images for a building, or a door (that might have multiple states).
// There is not a 1 to 1 ratio between Images and GameSprites.  Multiple GameSprites can
// point into different parts of a single image.
// A GameSprite is a sprite sheet which we subdivide into frames
// It can also be pointing to just a part of a larger image, and that part itself is
// subdivided into frames.  
public class GameSprite {

    // We want the image as a variable on the sprite, that way we can use the same image
    // for multiple different sprites if we want
    protected Image m_Image;
    public Image getImage() { return m_Image; }
    public void setImage(Image img) { 
        m_Image = img;
        if (img == null) {
            m_WithinImageX = 0;
            m_WithinImageY = 0;
            m_WithinImageWidth = 0;
            m_WithinImageHeight = 0;
            return;
        }
        m_WithinImageWidth = img.getWidth();
        m_WithinImageHeight = img.getHeight();
    }

    // The dimensions of this sprite within the Image.  Some are the entire image,
    // others are sprite sheets within a larger sprite sheets like some sprite sheets
    // have multiple "concepts" on them including monsters, etc.  This tells us where,
    // within m_Image, the border of the images we are using for this sprite, are.
    // These are used to create the frames of the sprite, as an "internal bounding box"
    // Assume 0, 0, image width/height unless we are told otherwise
    protected double m_WithinImageX = 0;
    protected double m_WithinImageY = 0;
    protected double m_WithinImageWidth = 0;
    protected double m_WithinImageHeight = 0;
    public double getWithinImageX() { return m_WithinImageX; }  // Remember, the X coordinate WITHIN the image, NOT the game
    public double getWithinImageY() { return m_WithinImageY; }
    public double getWithinImageWidth() { return m_WithinImageWidth; }
    public double getWithinImageHeight() { return m_WithinImageHeight; }
    public void setWithinImageX(double num) { m_WithinImageX = num; }  
    public void setWithinImageY(double num) { m_WithinImageY = num; }
    public void setWithinImageWidth(double num) { m_WithinImageWidth = num; }
    public void setWithinImageHeight(double num) { m_WithinImageHeight = num; }
    
    // Within the bounds above, where does the first frame start?  We can then use
    // that to craft all the other frames.
    // Guess these could be rects too or Rectangles.
    // This is for further defining how the frames should be cut out of the image
    public Rectangle frameBounds = null;

    private String m_strFileName;
    public String getFileName() { return m_strFileName; }

    // Like frames, sprites also can be accessed through key strings
    private String m_strKey = "";
    public String getSpriteKey() { return m_strKey; }
    public void setSpriteKey(String key) { m_strKey = key; }

    class Frame extends Rectangle {
        // Key allows us to search by a string so we can swap out sprite sheets
        private String key = "";
        public void setKey(String newKey) { key = newKey; }
        public String getKey() { return key; }

        int index = 0;
        public int getIndex() { return index; }
        public void setIndex(int i) { index = i; }

        public Frame(double x, double y, double wid, double hgt) {
            super(x, y, wid, hgt);
        }
    }

    ArrayList<Frame> frames = new ArrayList<Frame>();
    public int getNumFrames() { return frames.size(); }

    // Add more as needed
    public GameSprite(InputStream stream, String fileName) {
        setImage(new Image(stream));
        m_strFileName = fileName;
    }

    public GameSprite(Image img, String fileName) {
        setImage(img);
        m_strFileName = fileName;
    }

    public GameSprite(GameSprite sprite) {
        setImage(sprite.getImage());
        m_strFileName = sprite.getFileName();
    }

    public void setFrameKey(int index, String strKey) {
        Frame f = frames.get(index);
        if (f != null) {
            f.setKey(strKey);
        }
    }

    public void addFrame(String keyStr, int x, int y, int wid, int hgt) {
        Frame frame = new Frame(x, y, wid, hgt);
        frame.setKey(keyStr);
        frame.setIndex(frames.size());
        frames.add(frame);
    }
    
    // Make sure we don't have any duplicate terrain keys
    public boolean hasDuplicateKeys() {
        for (Frame fr : frames) {
            for (Frame fr2 : frames) {
                if (fr == fr2) 
                    continue;
                if (fr.getKey() == null || fr2.getKey() == null || fr.getKey().equals("") || fr2.getKey().equals(""))
                    continue;
                if (fr.getKey().equals(fr2.getKey()) == true) {
                    // Oops, duplicate
                    Utils.log("FRAMES (" + fr.getIndex() + ", " + fr2.getIndex() + ") DUPLICATE KEY: " + fr.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    // True means we have match - this frame holds this key
    public boolean frameHasKey(Frame fr, String key) {
        if (fr == null || key == null || fr.getKey() == null || fr.getKey() == "") {
            return false;
        }
        // We are looking either for equal strings, or for
        // strings that have an 'x' it means any number is fine.
        // So search until we find something non-x that is not equal.
        // convert string to char array
        char[] keyChars = key.toCharArray();
        char[] frameChars = fr.getKey().toCharArray();

        for (int i = 0; i < keyChars.length; i++) {
            // The frame might have an 'x' which means we don't
            // care about this one.
            if (frameChars[i] == 'x' || frameChars[i] == 'X' || frameChars[i] == ' ')
                continue;
            if (keyChars[i] != frameChars[i]) {
                return false;
            }
        }
        return true;
    }

    public int getIndexForKey(String key) {
        for (int i = 0; i < frames.size(); i++) {
            if (frameHasKey(frames.get(i), key) == true) {
                return i;
            }
        }
        Utils.log("HARD STOP: " + key);
        System.exit(0);
        return -1;
    }

    // Clip the "edges" of every frame to make the sprite tighter,
    // like if it has a lot of whitespace we don't want for example.
    public void clip(int pixels) {
        for (Frame frame : frames) {
            frame.setY(frame.getY() - pixels);
            frame.setX(frame.getX() - pixels);
            frame.setWidth(frame.getWidth() - pixels);
            frame.setHeight(frame.getHeight() - pixels);
        }
    }

    // Shift all the frames if the sprites, for example, are not centered
    // in the actual image.
    public void shiftUp(int pixels) {
        for (Frame frame : frames) {
            frame.setY(frame.getY() - pixels);
        }
    }

    public void shiftDown(int pixels) {
        for (Frame frame : frames) {
            frame.setY(frame.getY() + pixels);
        }
    }

    public void shiftRight(int pixels) {
        for (Frame frame : frames) {
            frame.setX(frame.getX() + pixels);
        }
    }

    public void shiftLeft(int pixels) {
        for (Frame frame : frames) {
            frame.setX(frame.getX() - pixels);
        }
    }

    public void drawFrameByIndex(GraphicsContext gc, int index, double x, double y, double wid, double hgt) {
        if (frames == null) {
            return;
        }
        Frame frame = frames.get(index);
        if (frame == null) {
            return;
        }
        gc.drawImage(m_Image, frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight(), x, y, wid, hgt);
    }

    public void drawFrameByKey(GraphicsContext gc, String key, double x, double y, double wid, double hgt) {
        int index = getIndexForKey(key);
        if (index >= 0) {
            drawFrameByIndex(gc, index, x, y, wid, hgt);
        }
    }

    // Used for a sprite sheet of a single "concept" that needs to be divided into
    // frames of the same size.
    // Create frames of same size automatically based on the number of frames,
    // framesWide is how many frames across, framesLong is how many frames
    // down, so that we can automatically load the right number based on size.
    public void createFrames(int framesWide, int framesLong) {
        //createFrames(framesWide, framesLong, 0, 0, (int)getImage().getWidth(), (int)getImage().getHeight());
        createFrames(framesWide, framesLong, 
                    (int)m_WithinImageX, (int)m_WithinImageY, 
                    (int)m_WithinImageWidth, (int)m_WithinImageHeight);
    }

    // We can also create frames at a different "location" in the sheet, not just starting at the top left.  In case
    // we are using one sheet for multiple different sprites.
    public void createFrames(int framesWide, int framesLong, int startX, int startY, int spriteWidth, int spriteHeight) {
        int sizeWid = spriteWidth / framesWide;
        int sizeHgt = spriteHeight / framesLong;

        for (int row = 0; row < framesLong; row++) {
            for (int col = 0; col < framesWide; col++) {
                addFrame("", startX + (col * sizeWid), startY + (row * sizeHgt), sizeWid, sizeHgt);
            }
        }
    }
}