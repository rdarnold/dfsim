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

// This is a very lightweight 'data holder' for each animation that is actually taking place
// on screen.  We create one of these, stick it on the queue on a canvas, it keeps track
// of state for that particular nimation, draws by calling into the actual SpriteAnimation
// obj it references, and then is deleted when it finishes.
public class SpriteAnimationInstance {
    private int numStates = 0;
    private int state = 0;
    private double durationSeconds = 0.45; // What is the intended duration in seconds?
    public int getNumStates() { return numStates; }
    public int getState() { return state; }
    public boolean isFinished() { return (state == numStates); }

    private long m_nStartTimeMS = 0;

    double x = 0;
    double y = 0;

    private SpriteAnimation refAnim;

    public SpriteAnimationInstance(SpriteAnimation ref) {
        refAnim = ref;
        numStates = ref.getNumStates();
        durationSeconds = ref.getDurationSeconds();
    }

    public void setPosition(double newX, double newY) {
        x = newX;
        y = newY;
    }

    public double getSecondsPerState() {
        return durationSeconds / (double)numStates;
    }

    public double getMillisecondsPerState() {
        return (getSecondsPerState() * 1000);
    }

    public void setStateForTimePassed(double millisPassed) {
        // So we say, if we have let's say 20 frames, and we want
        // to display them evenly across 1 second, that means we 
        // disply a new frame every 0.05 seconds.
        state = (int)(millisPassed / getMillisecondsPerState());
        if (state > numStates) {
            state = numStates;
        }
    }

    public void update() {
        if (m_nStartTimeMS == 0) {
            state = 0;
            m_nStartTimeMS = System.currentTimeMillis();
            return;
        }

        // Set state we are in based on milliseconds and duration.
        double millisPassed = (double)(System.currentTimeMillis() - m_nStartTimeMS);
        setStateForTimePassed(millisPassed);
    }

    // Draw based on current state.
    public void draw(GraphicsContext gc) {
        // Draw the attached refAnim
        refAnim.drawFrameByIndex(gc, state, x, y);
    }
    
}