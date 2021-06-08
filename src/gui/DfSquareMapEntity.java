package dfsim.gui;

import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import java.util.*;
import javafx.geometry.*;
import javafx.scene.input.*;
import javafx.event.EventHandler;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.beans.value.*;
import javafx.beans.property.*;

import javafx.scene.image.Image;

// For a smooth movement of the screen
import javafx.animation.*;
import javafx.util.*;
import javafx.event.*;

import dfsim.*;
import dfsim.gui.*;

// DfSquareMapEntity is essentially a living, movable thing in the game.
// For objects in the future I might want to make this an intermediate
// class DfLivingEntity and create a new base class that they both inheret
// from.
public abstract class DfSquareMapEntity extends Rectangle {

    // Does this entity map to a person?
    private Person person;
    public void setPerson(Person p) { person = p; }
    public Person getPerson() { return person ; }

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
    private int moveState = 0; // which of the movement states
    public int getMoveState() { return moveState; }
    private boolean moveStateReverse = false; // Iterating forward or backward on move states, it rotates
    public boolean getMoveStateReverse() { return moveStateReverse; }
    
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

    // Meant to be overridden, deprecated
    //protected void handleMouseEnter(Object objEnt, MouseEvent event) {  }

    // Meant to be overridden, deprecated
    //protected void handleClick(Object objEnt, MouseEvent event) {  }

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

        // These are now deprecated when using canvas...
        /*
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
        });*/

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

    // Update the movement state as the sprites walk around.
    public void updateMoveState() {
        if (person == null || person.getSprite() == null) {
            return;
        }
        if (moveStateReverse == true) {
            moveState--;
        }
        else {
            moveState++;
        }

        if (moveState <= 0) {
            moveStateReverse = false;
        }
        else if (moveState >= person.getSprite().getNumMoveStates()-1) {
            moveStateReverse = true;
        }
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

        // Update move state for the walking animation
        updateMoveState();

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
            // at this point and this just needs to go straight to the XY coordinates.
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
    
    public void drawNoGraphics(GraphicsContext gc) {
        double drawX = getX();
        double drawY = getY();

        // Check if we are the main character - if so we are always centered,
        // if not we need to use offsets
        if (getPerson() != Data.personList.get(0)) {
            drawX += map.getXOffset();
            drawY += map.getYOffset();
        }

        // Only draw if we are visible
        gc.setFill(getFill());
        gc.setStroke(getStroke());
        gc.fillRect(drawX, drawY, getWidth(), getHeight());

        gc.setStroke(getStroke());
        gc.setLineWidth(getStrokeWidth());
        gc.strokeRect(drawX, drawY, getWidth(), getHeight());
        gc.setLineWidth(1);
    }

    public void draw(GraphicsContext gc) {
        if (Constants.ENABLE_TILE_GRAPHICS == false || person == null) {
            drawNoGraphics(gc);
            return;
        }
        
        CharSprite sprite = person.getSprite();
        if (sprite == null) {
            drawNoGraphics(gc);
            return;
        }
        /*
        public void drawImage(Image img,
                      double sx,
                      double sy,
                      double sw,
                      double sh,
                      double dx,
                      double dy,
                      double dw,
                      double dh)

        Draws the specified source rectangle of the given image 
        to the given destination rectangle of the Canvas.

        Parameters:
        img - the image to be drawn or null.
        sx - the source rectangle's X coordinate position.
        sy - the source rectangle's Y coordinate position.
        sw - the source rectangle's width.
        sh - the source rectangle's height.
        dx - the destination rectangle's X coordinate position.
        dy - the destination rectangle's Y coordinate position.
        dw - the destination rectangle's width.
        dh - the destination rectangle's height.

        drawImage(image, 0, 0, w/2, h/2, w/4, h/4, w/2, h/2
        );
        */

        // For now just hacking the numbers in
        //gc.drawImage(img, 10, 10, 50, 50, getX(), getX(), getWidth(), getHeight());


        double drawX = getX();// + map.getXOffset();
        double drawY = getY();// + map.getYOffset();
        // Check if we are the main character - if so we are always centered,
        // if not we need to use offsets
        if (getPerson() != Data.personList.get(0)) {
            drawX += map.getXOffset();
            drawY += map.getYOffset();
        }
  
        //if (drawX > DfSim.width || drawX < (0 - getWidth()) || drawY > DfSim.height || drawY < (0 - getHeight())) {
        //    return;
       // }

        int index = sprite.getFrameIndexForMovementState(getMoveState(), getFacing());
  
        //gc.drawImage(img, 10, 10, 50, 50, getX(), getX(), getWidth(), getHeight());
        sprite.drawFrameByIndex(gc, index, drawX, drawY, getWidth(), getHeight());
    }
}