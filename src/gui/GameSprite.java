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
// A GameSprite is a sprite sheet which we subdivide into smaller areas
public class GameSprite extends Image {

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
    public GameSprite(InputStream stream) {
        super(stream);
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

    public void drawFrameByIndex(GraphicsContext gc, int index, double x, double y, double wid, double hgt) {
        if (frames == null) {
            return;
        }
        Frame frame = frames.get(index);
        if (frame == null) {
            return;
        }
        gc.drawImage(this, frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight(), x, y, wid, hgt);
    }

    public void drawFrameByKey(GraphicsContext gc, String key, double x, double y, double wid, double hgt) {
        int index = getIndexForKey(key);
        if (index >= 0) {
            drawFrameByIndex(gc, index, x, y, wid, hgt);
        }
    }
}