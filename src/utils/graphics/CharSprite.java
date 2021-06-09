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

// CharSprite is for a standard 12-frame RPG character moving / walking
public class CharSprite extends GameSprite {
    // Standard 3 states for movement.
    private int numMoveStates = 3;
    public int getNumMoveStates() { return numMoveStates; }

    public CharSprite(InputStream stream, String fileName) {
        super(stream, fileName);
    }

    public CharSprite(Image img, String fileName) {
        super(img, fileName);
    }

    public CharSprite(CharSprite sprite) {
        super(sprite);
    }

    // Based on the standard 12-frame
    public int getFrameIndexForMovementState(int state, Constants.Dir facing) {
        switch (facing) {
            case SOUTH:
                return state;
            case WEST:
                return state + 3;
            case EAST:
                return state + 6;
            case NORTH:
                return state + 9;
        }
        return 0;
    }

    // Standard 3x4 character sprite matrix
    public void createStandardFrames() {
        numMoveStates = 3;

        createFrames(3, 4);

        // 3 states each of down, left, right, up
        setFrameKey(0, "d1");
        setFrameKey(1, "d2");
        setFrameKey(2, "d3");
        setFrameKey(3, "l1");
        setFrameKey(4, "l2");
        setFrameKey(5, "l3");
        setFrameKey(6, "r1");
        setFrameKey(7, "r2");
        setFrameKey(8, "r3");
        setFrameKey(9, "u1");
        setFrameKey(10, "u2");
        setFrameKey(11, "u3");
    }
}