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

        public Frame(double x, double y, double wid, double hgt) {
            super(x, y, wid, hgt);
        }
    }

    ArrayList<Frame> frames = new ArrayList<Frame>();

    // Add more as needed
    public GameSprite(InputStream stream) {
        super(stream);
    }

    public void addFrame(String keyStr, int x, int y, int wid, int hgt) {
        Frame frame = new Frame(x, y, wid, hgt);
        frame.setKey(keyStr);
        frames.add(frame);
    }

    public int getIndexForKey(String key) {
        for (int i = 0; i < frames.size(); i++) {
            Frame fr = frames.get(i);
            if (fr.getKey().equals(key))
                return i;
        }
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