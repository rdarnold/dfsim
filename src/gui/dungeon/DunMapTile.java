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

public class DunMapTile extends DfSquareTile {

    public DunMap map;

    /*public DunMapTile north;
    public DunMapTile east;
    public DunMapTile west;
    public DunMapTile south;*/
    public DunMapTile getNorth() { return (DunMapTile)north; }
    public DunMapTile getEast()  { return (DunMapTile)east; }
    public DunMapTile getWest()  { return (DunMapTile)west; }
    public DunMapTile getSouth() { return (DunMapTile)south; }
    public DunMapTile n() { return (DunMapTile)north; }
    public DunMapTile e() { return (DunMapTile)east; }
    public DunMapTile w() { return (DunMapTile)west; }
    public DunMapTile s() { return (DunMapTile)south; }

    public DunMapEntity getContains() { return (DunMapEntity)contains; }

    public static int defSize = Constants.BASE_TILE_SIZE;
    
    private DunMap.TileType type = DunMap.TileType.Blank;
    private DunMap.TileType prevType = DunMap.TileType.Blank;
    public void setType(DunMap.TileType t) {
        prevType = type;
        type = t;
    }
    public DunMap.TileType getType() { return type; }
    public void revertType() { type = prevType; }

    public DunMapTile getNortheast() {
        return (DunMapTile)super.getNortheast();
    }

    public DunMapTile getNorthwest() {
        return (DunMapTile)super.getNorthwest();
    }

    public DunMapTile getSoutheast() {
        return (DunMapTile)super.getSoutheast();
    }

    public DunMapTile getSouthwest() {
        return (DunMapTile)super.getSouthwest();
    }

    public DunMapTile(DunMap theMap) {
        super();
        map = theMap;
        init();
    }

    private void init() {
        /*setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleClick(event.getSource(), event);
            }
        });*/

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

    @Override
    protected void handleMouseEnter(Object objTile, MouseEvent event) {
        if (objTile == null) 
            return;
        DunMapTile tile = (DunMapTile)objTile;
        map.onMouseEnterTile(tile);
    }

    @Override
    protected void handleClick(Object objTile, MouseEvent event) {
        if (objTile == null) 
            return;
        DunMapTile tile = (DunMapTile)objTile;
        if (event.getButton() == MouseButton.PRIMARY) {
            map.onLeftClickTile(tile);
        }
        else {
            map.onRightClickTile(tile);
        }
    }

    public DunMapTile getAdjacentTileForNumber(int num) {
        return (DunMapTile)super.getAdjacentTileForNumber(num);
    }

    public DunMapTile getRandomValidAdjacentTile() {
        return (DunMapTile)super.getRandomValidAdjacentTile();
    }

    public boolean surroundedByType(DunMap.TileType tileType) {
        if (getNorth() != null && getNorth().type != tileType)   return false;
        if (getWest() != null && getWest().type != tileType)     return false;
        if (getEast() != null && getEast().type != tileType)     return false;
        if (getSouth() != null && getSouth().type != tileType)   return false;

        return true;
    }

    public int numberAdjacentTilesOfType(DunMap.TileType tileType) {
        int num = 0;
        if (getNorth() != null && getNorth().type == tileType)   num++;
        if (getEast() != null && getEast().type == tileType)     num++;
        if (getWest() != null && getWest().type == tileType)     num++;
        if (getSouth() != null && getSouth().type == tileType)   num++;
        return num;
    }

    @Override
    public boolean canBeMovedTo(DfSquareMapEntity ent) {
        return this.canBeMovedTo((DunMapEntity)ent);
    }

    public boolean canBeMovedTo(DunMapEntity ent) {
        if (super.canBeMovedTo(ent) == false) {
            return false;
        }
        if (type == DunMap.TileType.Blank ||
            type == DunMap.TileType.Water ||
            type == DunMap.TileType.Wall) {
            return false;
        }
        return true;
    }

    public static boolean canMoveTo(DunMapTile tile) {
        if (tile == null || tile.canBeMovedTo() == false)
            return false;
        return true;
    }

    // Is this a single file hallway of just one space wide?
    public boolean isSingleFile() {
        // It cannot be a hall, i.e. cannot be blocked e/w or n/s
        if (canMoveTo(n()) == false && canMoveTo(s()) == false) {
            return true;
        }
        if (canMoveTo(e()) == false && canMoveTo(w()) == false) {
            return true;
        }

        // And can't be in like a corner of a narrow hallway.
        if (canMoveTo(n()) == false && canMoveTo(w()) == false && 
            canMoveTo(getSoutheast()) == false)
            return true;
        if (canMoveTo(n()) == false && canMoveTo(e()) == false && 
            canMoveTo(getSouthwest()) == false)
            return true;
        if (canMoveTo(s()) == false && canMoveTo(w()) == false && 
            canMoveTo(getNortheast()) == false)
            return true;
        if (canMoveTo(s()) == false && canMoveTo(e()) == false && 
            canMoveTo(getNorthwest()) == false)
            return true;
        
        return false;
    }

    public void updateColor() {
        
        //setStroke(Color.GRAY);
        switch (type) {
            case Blank:         setFill(Color.BLACK); break;
            case Water:         setFill(Color.BLUE); break;
            case Floor:         setFill(Color.SIENNA); break;
            case Wall:          setFill(Color.BROWN); break;
            case Stairs:        setFill(Color.PERU); break;
            case Chest:         setFill(Color.RED); break;
            case EmptyChest:    setFill(Color.DARKRED); break;
            case Exit:          setFill(Color.YELLOW); break;
        }
    }
}