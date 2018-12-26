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

public class TownMapTile extends DfSquareTile {

    public TownMap map;

    /*public TownMapTile north;
    public TownMapTile east;
    public TownMapTile west;
    public TownMapTile south;*/
    public TownMapTile getNorth() { return (TownMapTile)north; }
    public TownMapTile getEast()  { return (TownMapTile)east; }
    public TownMapTile getWest()  { return (TownMapTile)west; }
    public TownMapTile getSouth() { return (TownMapTile)south; }
    public TownMapTile n() { return (TownMapTile)north; }
    public TownMapTile e() { return (TownMapTile)east; }
    public TownMapTile w() { return (TownMapTile)west; }
    public TownMapTile s() { return (TownMapTile)south; }

    public TownMapEntity getContains() { return (TownMapEntity)contains; }

    public static int defSize = 20;

    private TownMap.TileType type = TownMap.TileType.Grass;
    private TownMap.TileType prevType = TownMap.TileType.Grass;
    public void setType(TownMap.TileType t) {
        prevType = type;
        type = t;
    }
    public TownMap.TileType getType() { return type; }
    public void revertType() { type = prevType; }

    public TownMapTile(TownMap theMap) {
        super();
        map = theMap;
        init();
    }

    @Override
    protected void handleMouseEnter(Object objTile, MouseEvent event) {
        if (objTile == null) 
            return;
        TownMapTile tile = (TownMapTile)objTile;
        map.onMouseEnterTile(tile);
    }

    @Override
    protected void handleClick(Object objTile, MouseEvent event) {
        if (objTile == null) 
            return;
        TownMapTile tile = (TownMapTile)objTile;
        if (event.getButton() == MouseButton.PRIMARY) {
            map.onLeftClickTile(tile);
        }
        else {
            map.onRightClickTile(tile);
        }
    }

    private void init() {

        //setWidth(defSize);
        //setStroke(Color.TRANSPARENT);
        //setStrokeWidth(0);

        setWidth(defSize);
        setHeight(defSize);
        //setArcWidth(20);
        //setArcHeight(20);

        //setFill(Color.WHITE);
        //setupAnimationListeners();
        updateColor();
    }

    public TownMapTile getAdjacentTileForNumber(int num) {
        return (TownMapTile)super.getAdjacentTileForNumber(num);
    }

    public TownMapTile getRandomValidAdjacentTile() {
        return (TownMapTile)super.getRandomValidAdjacentTile();
    }

    public boolean surroundedByType(TownMap.TileType tileType) {
        if (getNorth() != null && getNorth().type != tileType)   return false;
        if (getWest() != null && getWest().type != tileType)     return false;
        if (getEast() != null && getEast().type != tileType)     return false;
        if (getSouth() != null && getSouth().type != tileType)   return false;

        return true;
    }

    public int numberAdjacentTilesOfType(TownMap.TileType tileType) {
        int num = 0;
        if (getNorth() != null && getNorth().type == tileType)   num++;
        if (getEast() != null && getEast().type == tileType)     num++;
        if (getWest() != null && getWest().type == tileType)     num++;
        if (getSouth() != null && getSouth().type == tileType)   num++;
        return num;
    }
    
    public boolean blockMove() {
        if (type == TownMap.TileType.Water ||
            type == TownMap.TileType.Tree || 
            type == TownMap.TileType.WoodWall || 
            type == TownMap.TileType.StoneWall) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeMovedTo(DfSquareMapEntity ent) {
        return this.canBeMovedTo((TownMapEntity)ent);
    }

    public boolean canBeMovedTo(TownMapEntity ent) {
        if (super.canBeMovedTo(ent) == false) {
            return false;
        }
        if (type == TownMap.TileType.Water ||
            type == TownMap.TileType.Tree || 
            type == TownMap.TileType.WoodWall || 
            type == TownMap.TileType.StoneWall) {
            return false;
        }
        return true;
    }

    public static boolean canMoveTo(TownMapTile tile) {
        if (tile == null || tile.canBeMovedTo() == false)
            return false;
        return true;
    }


    public void updateColor() {
        
        //setStroke(Color.GRAY);

        switch (type) {
            case Blank:     setFill(Color.WHITE); break;
            case Grass:     setFill(Color.rgb(102, 251, 102, 0.5)); break;
            case Field:     setFill(Color.WHEAT); break;
            case Water:     setFill(Color.BLUE); break;
            case Sand:      setFill(Color.SANDYBROWN); break;
            case Tree:      setFill(Color.GREEN); break;
            case Road:      setFill(Color.TAN); break;
            case Swamp:     setFill(Color.MEDIUMSEAGREEN); break;
            case StoneWall: setFill(Color.DARKGRAY); break;
            case WoodWall:  setFill(Color.SIENNA); break;
            case Door:      setFill(Color.BLACK); break;
        }
    }
}