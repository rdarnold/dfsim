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

import javafx.scene.image.Image;

// For a smooth movement of the screen
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import dfsim.*;
import dfsim.gui.*;

public abstract class DfSquareMapEntity extends Rectangle {

    public enum EntityType {
        Party,
        Person,
        Mon
    }

    public static int defSize = DfSquareTile.defSize-4;
    public static int posOffset = (DfSquareTile.defSize - DfSquareMapEntity.defSize) / 2;

    public int mapX = 0;
    public int mapY = 0;

    // What tile sprite to draw?  If null just draw color
    protected Image img = null;
    public void setImage(Image i) { img = i; }
    public Image getImage() { return img; }

    // For movement animation
    private Timeline timeline;
    private boolean canMove = true;
    public boolean getCanMove() { return canMove; }
    
    // Dun stuff
    protected DfSquareMap map;
    protected DfSquareTile tile;
    protected DfSquareTile prevTile; // Previous one

    public void setTile(DfSquareTile newTile) { 
        prevTile = tile;
        tile = newTile; 
        if (prevTile == null) {
            prevTile = tile;
        }
    }
    public DfSquareTile getTile() { return tile; }
    public DfSquareTile getPrevTile() { return prevTile; }

    protected Constants.Dir lastMoveDir = Constants.Dir.NORTH;
    public Constants.Dir getLastMoveDir() { return lastMoveDir; }
    public void setLastMoveDir(Constants.Dir dir) { lastMoveDir = dir; }

    protected Constants.Dir facing = Constants.Dir.SOUTH;
    public Constants.Dir getFacing() { return facing; }
    public void setFacing(Constants.Dir dir) { facing = dir; }

    protected EntityType type = EntityType.Party;
    public EntityType getType() { return type; }
    
    // For random movement of NPCs
    private static int m_nMaxWait = 10000;
    private int m_nWaitMS = m_nMaxWait; // How many milliseconds time until they move again.
    public int getWait() { return m_nWaitMS; }
    public boolean decWait(int amt) {
        m_nWaitMS -= amt;
        if (m_nWaitMS <= 0) {
            m_nWaitMS = m_nMaxWait;
            return true;
        }
        return false;
    }

    public DfSquareMapEntity(DfSquareMap theMap) {
        super();
        map = theMap;
        init(EntityType.Party);
    }

    public DfSquareMapEntity(DfSquareMap theMap, EntityType theType) {
        super();
        map = theMap;
        init(theType);
    }

    // Meant to be overridden
    protected void handleMouseEnter(Object objEnt, MouseEvent event) {  }

    // Meant to be overridden
    protected void handleClick(Object objEnt, MouseEvent event) {  }

    // Meant to be overridden if we want any custom logic for the subclasses
    protected void onMoveFinished() { }

    private void timelineFinished() {
        onMoveFinished();
        canMove = true;
    }

    private void init(EntityType theType) {
        m_nWaitMS = Utils.number(0, m_nMaxWait);
        
        this.timeline = new Timeline(60);
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timelineFinished();
            }
        });

        type = theType;

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleClick(event.getSource(), event);
            }
        });
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMouseEnter(event.getSource(), event);
            }
        });

        /*shapeText.setUserData(this);
        getSelectedCircle().setUserData(this);
        overlay.setUserData(this);
        getSelectedPolygon().setUserData(this);*/
        /*setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleClick(event.getSource(), event);
            }
        });
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMouseEnter(event.getSource(), event);
            }
        });*/

        // The text sits on top of the shape and will intercept the mouseclick
        // so we want clicking on the text to work the same way as clicking
        // on the entity
        /*shapeText.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Text source = (Text)event.getSource();
                handleClick(source.getUserData(), event);
            }
        });
        shapeText.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Text source = (Text)event.getSource();
                handleMouseEnter(source.getUserData(), event);
            }
        });*/

        //setStroke(Color.BLACK);
        //setStrokeWidth(1);

        setWidth(defSize);
        setHeight(defSize);
        setArcWidth(defSize / 5);
        setArcHeight(defSize / 5);

        //setWidth(defSize);
        switch (type) {
            case Party: 
                setFill(Color.AQUA);
                break;
            case Person:
                setFill(Color.MINTCREAM);
                break;
            case Mon:
                setFill(Color.MINTCREAM);
                break;
        }

        //setupAnimationListeners();
    }

    public void playMoveAnimation(double x, double y) {
        // Can't do another move until we finish animating
        // this one; the variable is reset on the onFinish of the timeline.
        canMove = false;

        // timeline that moves the screen
        timeline.getKeyFrames().clear();

        //timeline = new Timeline(60);
        timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.millis(50), new KeyValue(xProperty(), x)),
            new KeyFrame(Duration.millis(50), new KeyValue(yProperty(), y))
            //new KeyFrame(Duration.millis(50), new KeyValue(getTranslateXProperty(), offsetX)),
            //new KeyFrame(Duration.millis(50), new KeyValue(getTranslateYProperty(), offsetY))
        );
        timeline.play();
    }

    public void unrestrictedMoveTo(double x, double y) {
        //setTranslateX(x);
        //setTranslateY(y); 
        setX(x);
        setY(y);
    }

    // For a mouse click on an entity without a bounded translate,
    // like the party avatar.
    public void unrestrictedMoveTo(DfSquareTile tile) {
        setX(tile.getX() + tile.getTranslateX() + posOffset);
        setY(tile.getY() + tile.getTranslateY() + posOffset); 
        tile.attach(this);
    }

    public boolean moveTo(DfSquareTile tile) {
        return moveTo(tile, Constants.Dir.SOUTH, false);
    }

    public boolean moveTo(DfSquareTile tile, Constants.Dir dir) {
        return moveTo(tile, dir, false);
    }

    public boolean moveTo(DfSquareTile tile, Constants.Dir dir, boolean animate) {
        if (canMove == false)
            return false;
        
        facing = dir;

        // This won't work if we move into a player tile.
        tile.attach(this);

        //setTranslateX(tile.getTranslateX()+2);
        //setTranslateY(tile.getTranslateY()+2); 
        if (animate == false) {
            // This is actually only for clicking on a square, this needs
            // to be changed to like a "freeMove" or something which doesnt
            // involve being bound to translate x and y
            setX(tile.getX() + posOffset);
            setY(tile.getY() + posOffset); 
        }
        else {
            // We aren't using translate X and Y here because this entity has
            // already been bound to the offsetX and Y for its translate values
            // at this point and this just needs to go straight to the XZ coordinates.
            playMoveAnimation(
                tile.getX() + (double)posOffset,
                tile.getY() + (double)posOffset);
        }
        return true;
    }

    public void addToPane(Pane node) {
        node.getChildren().add(this);
    }

    public void removeFromPane(Pane node) {
        node.getChildren().remove(this);
    }

    // At least one adjacent tile can be moved into.
    public boolean canMove() {   
        DfSquareTile tile = getTile();
        if (tile == null) {
            return false;
        }
        if (tile.north == null && tile.east == null && tile.south == null && tile.west == null) {
            return false;
        }

        if (tile.north != null && tile.north.canBeMovedTo() == true)
            return true;
        if (tile.east != null && tile.east.canBeMovedTo() == true)
            return true;
        if (tile.west != null && tile.west.canBeMovedTo() == true)
            return true;
        if (tile.south != null && tile.south.canBeMovedTo() == true)
            return true;
        
        return false;
    }

    public DfSquareTile getTileFacing() {
        if (getTile() == null)
            return null;
        return getTile().getAdjacentTileInDir(facing);
    }
}