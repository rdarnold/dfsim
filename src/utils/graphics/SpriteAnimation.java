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

// SpriteAnimation is for an animated sprite based on a png of images rather
// than a gif.  When we run the animation on the sprite, we do so at a certain
// location.  It's all self-contained in here so we can literally just run
// the animation at a location and it just goes.
public class SpriteAnimation extends GameSprite {
    private int numStates = 0;
    public int getNumStates() { return numStates; }
    private double durationSeconds = 0.45; // What is the intended duration in seconds?
    public double getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(double sec) { durationSeconds = sec; }

    public SpriteAnimation(InputStream stream, String fileName) {
        super(stream, fileName);
        m_bDoNotScaleOrStretch = true;
    }

    public SpriteAnimation(InputStream stream, String fileName, int sizePixels) {
        super(stream, fileName, sizePixels);
        m_bDoNotScaleOrStretch = true;
    }

    public SpriteAnimation(Image img, String fileName) {
        super(img, fileName);
        m_bDoNotScaleOrStretch = true;
    }

    public SpriteAnimation(SpriteAnimation anim) {
        super(anim);
        deepCopy(this, anim);
        m_bDoNotScaleOrStretch = true;
    }

    public void deepCopy(SpriteAnimation from, SpriteAnimation to) {
        // Really this should deepcopy from base classes too but I haven't had any 
        // use cases for that yet
        from.numStates = to.numStates;
        from.durationSeconds = to.durationSeconds;
    }

    public int getFrameIndexForState(int state) {
        // May want special processing here?
        return state;
    }

    // Animate on canvas at location x and y
    public void animate(DfCanvas canvas, double centerX, double centerY) {
        numStates = getNumFrames();

        // What we do is, actually create a new instance object based on this one which is the "template"
        // The instance keeps track of state but calls into the master obj to draw.  That way
        // we don't have to keep creating all new frames, etc., we can just use the ones we already
        // created and draw from that.  Saves RAM / CPU.
        SpriteAnimationInstance inst = new SpriteAnimationInstance(this);
        inst.setPosition(centerX - getFrameWidth(0)/2, centerY - getFrameHeight(0)/2);
        canvas.addAnimation(inst);
    }
    
}