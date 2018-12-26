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


import dfsim.*;
import dfsim.gui.*;

public abstract class DfSquareTile extends Rectangle {

    // What level is this tile, if any?
    private int m_nLevel = 0;
    public void setLevel(int lv) { m_nLevel = lv; }
    public int getLevel() { return m_nLevel; }

    public static int defSize = 20;

    public int mapX = 0;
    public int mapY = 0;

    public DfSquareTile north;
    public DfSquareTile east;
    public DfSquareTile west;
    public DfSquareTile south;

    public DfSquareMapEntity contains;

    private boolean marked = false; // For whatever transient use like searches, generation 
    public void mark() { marked = true; }
    public void clearMark() { marked = false; }
    public boolean isMarked() { return marked; }

    public DfSquareTile() {
        super();
        setWidth(defSize);
        setHeight(defSize);
        
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
    }

    // Meant to be overridden
    protected void handleMouseEnter(Object objTile, MouseEvent event) {  }

    // Meant to be overridden
    protected void handleClick(Object objTile, MouseEvent event) { }

    public DfSquareTile getNortheast() {
        if (north != null) { return north.east; }
        if (east != null) { return east.north; }
        return null;
    }
    public DfSquareTile getNorthwest() {
        if (north != null) { return north.west; }
        if (west != null) { return west.north; }
        return null;
    }
    public DfSquareTile getSoutheast() {
        if (south != null) { return south.east; }
        if (east != null) { return east.south; }
        return null;
    }
    public DfSquareTile getSouthwest() {
        if (south != null) { return south.west; }
        if (west != null) { return west.south; }
        return null;
    }

    public void moveTo(double x, double y) {
        //setTranslateX(x);
        //setTranslateY(y); 
        setX(x);
        setY(y);
    }

    public boolean attach(DfSquareMapEntity ent) {
        //if (contains != null) {
        //    return false;
        //}
        //ent.centerOn(this);
        if (ent.getTile() != null) {
            ent.getTile().detach();
        }
        //pullTo(av);
        ent.setTile(this);
        contains = ent;
        return true;
    }

    public void detach() {
        if (contains != null) {
            contains.setTile(null);
        }
        contains = null;
    }

    public void pullTo(DfSquareMapEntity ent) {
        //int steps = DfSim.mainScene.hexMap.countCurMovPathTiles();
        Path path = new Path();

        // Silly that I have to add the def size / 2 to this, I'm using
        // the same values
        path.getElements().add(new MoveTo(
            ent.getTranslateX() + ent.defSize/2, 
            ent.getTranslateY() + ent.defSize/2)); 
        //addCurMovPathTilesToMovement(path);
        path.getElements().add(new LineTo(
            getX() + defSize/2,
            getY() + defSize/2));

        double dist = 0; // steps * 20;
        if (dist <= 0) {
            dist = Utils.calcDistance(ent.getTranslateX(), ent.getTranslateY(),
                getX(), getY());
        }
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(dist * 5));
        pathTransition.setNode(ent);
        pathTransition.setPath(path);
        pathTransition.setCycleCount(1);

        pathTransition.play();
    }

    public boolean canBeMovedTo() { 
        return canBeMovedTo(null);
    }

    // Should be overridden
    public boolean canBeMovedTo(DfSquareMapEntity ent) {
        if (contains != null) {
            return false;
        }
        return true;
    }

    public void addToPane(Pane node) {
        node.getChildren().add(this);
    }

    public void removeFromPane(Pane node) {
        node.getChildren().remove(this);
    }
    
    public DfSquareTile getAdjacentTileForNumber(int num) {
        return (getAdjacentTileInDir(Constants.Dir.fromInt(num)));
    }

    public DfSquareTile getAdjacentTileInDir(Constants.Dir dir) {
        switch (dir) {
            case NORTH: return north;
            case EAST:  return east;
            case WEST:  return west;
            case SOUTH: return south;
        }
        return null;
    }
    
    public DfSquareTile getRandomValidAdjacentTile() {
        if (north == null && south == null && east == null && west == null)
            return null;
        DfSquareTile tile = null;
        while (tile == null) {
            tile = getAdjacentTileForNumber(Utils.number(0, 3));
        }
        return tile;
    }

    public Constants.Dir getRandomValidDir() {
        if (north == null && south == null && east == null && west == null)
            return Constants.Dir.NONE;
        DfSquareTile tile = null;
        int num = 0;
        while (tile == null) {
            num = Utils.number(0, 3);
            tile = getAdjacentTileForNumber(num);
        }
        return Constants.Dir.fromInt(num);
    }

    public static int revDir(Constants.Dir dir) {
        switch (dir) {
            case NORTH: return Constants.Dir.SOUTH.val();
            case EAST:  return Constants.Dir.WEST.val();
            case WEST:  return Constants.Dir.EAST.val();
            case SOUTH: return Constants.Dir.NORTH.val();
        }
        return -1;
    }

    public static int revDir(int num) {
        return revDir(Constants.Dir.fromInt(num));
    }

    public static boolean isRevDir(int dir1, int dir2) {
        // -1 is an invalid direction which means it's always false
        if (dir1 == -1 || dir2 == -1)
            return false;
        return (revDir(dir1) == dir2);
    }
}