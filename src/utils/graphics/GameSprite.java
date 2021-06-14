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

    // We can specify an image to draw natively without scaling or stretching
    protected boolean m_bDoNotScaleOrStretch = false;
    public boolean doNotScaleOrStretch() { return m_bDoNotScaleOrStretch; }

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

    // This is just SUPER SUPER weird so I'm leaving this line in here for future posterity-
    // if I change this next line to "public Rectangle frameBounds;" the game crashes on startup
    // with an assertion error! But if I leave it as private it runs just fine! WTFFFF!
    //Rectangle frameBounds;

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

    public double getFrameWidth(int index) {
        if (frames == null) { return 0; }
        if (index >= frames.size()) { return 0; }
        return frames.get(index).getWidth();
    }
    public double getFrameHeight(int index) {
        if (frames == null) { return 0; }
        if (index >= frames.size()) { return 0; }
        return frames.get(index).getHeight();
    }

    // Add more as needed
    public GameSprite(InputStream stream, String fileName) {
        setImage(new Image(stream));
        m_strFileName = fileName;
    }

    public GameSprite(InputStream stream, String fileName, int sizePixels) {
        // Image(InputStream is, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth)
        setImage(new Image(stream, sizePixels, sizePixels, true, false));
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
            frame.setX(frame.getX() + pixels);
            frame.setY(frame.getY() + pixels);
            frame.setWidth(frame.getWidth() - (pixels*2));
            frame.setHeight(frame.getHeight() - (pixels*2));
        }
    }
    
    // Clip just the top part of the frame
    public void clipTop(int pixels) {
        for (Frame frame : frames) {
            frame.setY(frame.getY() + pixels);
            frame.setHeight(frame.getHeight() - pixels);
        }
    }
    
    // Find the transparency rect that bounds the non-transparent part of this
    // particular frame
    public Rectangle getTransparentBoundsForFrame(Frame frame) {
        return GraphicsUtils.getTransparentBounds(getImage(), frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight());
    }

    public void clipTransparent() {
        clipTransparent(0);
    }

    // Clip any 'blank' transparent space based on the smallest possible
    // rect within each frame that will encompass all pixels of all frames
    // the 'padding' parameter determines how many extra pixels are NOT clipped
    //   I could add a facility to increase the size of the image
    // by adding extra transparent pixels on right and bottom side as necessary.
    // if padding goes over; right now I just stop it there.
    public void clipTransparent(int padding) {
        for (Frame frame : frames) {
            Rectangle bounds = getTransparentBoundsForFrame(frame);
            frame.setX(bounds.getX() - padding);
            frame.setY(bounds.getY() - padding);
            frame.setWidth(bounds.getWidth() + (padding*2));
            frame.setHeight(bounds.getHeight() + (padding*2));

            // Just don't let the frame go past on right or bottom of the image even with
            // padding
            if (frame.getX() + frame.getWidth() > getImage().getWidth()) {
                frame.setWidth(getImage().getWidth() - frame.getX());
            }
            if (frame.getY() + frame.getHeight() >  getImage().getHeight()) {
                frame.setHeight(getImage().getHeight() - frame.getY());
            }
        }
    }

    // We would love it if all images were fit within a 32x32 square or whatever standard.
    // Unfortunately many are not.  However,
    // they need to be square-shaped otherwise when they draw in square spaces,
    // the system will auto-stretch/compress them to fit, which looks wonky.  So we need
    // to make sure we have included enough whitespace in each frame that the
    // frame is an exact square.
    // "dir" is the dir to square "off of" - if Dir.None is passed, it'll square
    // off of the center of the square.  Otherwise whatever dir is passed in
    // is considered unchangeable; for ex., maybe the sprites are all at the bottom
    // of their frames so we won't want to expand past that.
    /*public void makeFramesSquare(Constants.Dir dir) {
        for (Frame frame : frames) {
            double x = frame.getX();
            double y = frame.getY();
            double width = frame.getWidth();
            double height = frame.getHeight();

            // Squared, already done.
            if (width == height) {
                continue;
            }
            if (width < height) {
                // Increase width to match height
                switch (dir) {
                    case WEST:
                        break;
                    case EAST:
                        break;
                    default:
                        break;

                }
            }
            else {
                // Increase height to match width
                switch (dir) {
                    case NORTH:
                        break;
                    case SOUTH:
                        break;
                    default:
                        break;

                }
            }
        }
    }*/

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

    // Permanently rotate our image
    public void rotateImage(int angleDegrees) {
        setImage(GraphicsUtils.rotateImage(getImage(), angleDegrees));
    }

    // Draw the entire image, we don't have frames or don't want to draw them here
    public void drawFullImage(GraphicsContext gc, double x, double y, double wid, double hgt) {
        double drawX = x;
        double drawY = y;
        double drawWid = wid;
        double drawHgt = hgt;

        // Some images we don't compress or stretch, especially if the aspect ratio will be weird.
        // This is true for many character sprites which may be rectangles, etc.
        if (doNotScaleOrStretch() == true) {
            drawWid = getImage().getWidth();
            drawHgt = getImage().getHeight();

            drawX += wid/2 - drawWid/2;
            drawY += hgt/2 - drawHgt/2;
            // Utils.log("wid: " + wid + ", hgt: " + hgt + "drawWid: " + drawWid + ", drawHgt: " + drawHgt);
            // No matter what, we don't want to draw below the bottom because it's "sort of" perspective
            // and we pad it with some pixels
            if (drawY + drawHgt > y + hgt - 5) {
                drawY = (y + hgt) - drawHgt - 5;
            }
        }
            
        // We cast to (int) because we don't want blurring to occur.
        // Blurring will occur when a coordinate is not an int; like it's "0.5" or whatever and is not lined up exactly.
        gc.drawImage(getImage(), 0, 0, (int)getImage().getWidth(), (int)getImage().getHeight(), (int)drawX, (int)drawY, (int)drawWid, (int)drawHgt);
    }

    // Draw based on frame width / height automatically
    public void drawFrameByIndex(GraphicsContext gc, int index, double x, double y) {
        drawFrameByIndex(gc, index, x, y, 0, 0, true);
    }

    // Draw based on specified wid and hgt
    public void drawFrameByIndex(GraphicsContext gc, int index, double x, double y, double wid, double hgt) {
        drawFrameByIndex(gc, index, x, y, wid, hgt, false);
    }

    // Not intended to be called externally, you call one of the above functions instead.
    private void drawFrameByIndex(GraphicsContext gc, int index, double x, double y, double wid, double hgt, boolean useFrameDims) {
        if (frames == null || index >= frames.size()) {
            return;
        }
        Frame frame = frames.get(index);
        if (frame == null) {
            return;
        }

        double drawX = x;
        double drawY = y;
        double drawWid = wid;
        double drawHgt = hgt;
        
        // Based the width and height off of just what the frame is, rather than anything external
        if (useFrameDims == true) {
            wid = frame.getWidth();
            hgt = frame.getHeight();
            drawWid = frame.getWidth();
            drawHgt = frame.getHeight();
        }

        // Some images we don't compress or stretch, especially if the aspect ratio will be weird.
        // This is true for many character sprites which may be rectangles, etc.
        if (doNotScaleOrStretch() == true) {
            drawWid = frame.getWidth();
            drawHgt = frame.getHeight();

            drawX += wid/2 - drawWid/2;
            drawY += hgt/2 - drawHgt/2;
            // Utils.log("wid: " + wid + ", hgt: " + hgt + "drawWid: " + drawWid + ", drawHgt: " + drawHgt);
            // No matter what, we don't want to draw below the bottom because it's "sort of" perspective
            // and we pad it with some pixels
            if (drawY + drawHgt > y + hgt - 5) {
                drawY = (y + hgt) - drawHgt - 5;
            }
        }
            
        // We cast to (int) because we don't want blurring to occur.
        // Blurring will occur when a coordinate is not an int; like it's "0.5" or whatever and is not lined up exactly.
        gc.drawImage(m_Image, (int)frame.getX(), (int)frame.getY(), (int)frame.getWidth(), (int)frame.getHeight(), (int)drawX, (int)drawY, (int)drawWid, (int)drawHgt);
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