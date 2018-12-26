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

public class LandMapTile extends DfSquareTile {


    public LandMap.MapTileType type = LandMap.MapTileType.Water;

    /*public LandMapTile north;
    public LandMapTile east;
    public LandMapTile west;
    public LandMapTile south;*/
    public LandMapTile getNorth() { return (LandMapTile)north; }
    public LandMapTile getEast()  { return (LandMapTile)east; }
    public LandMapTile getWest()  { return (LandMapTile)west; }
    public LandMapTile getSouth() { return (LandMapTile)south; }
    public LandMapTile n() { return (LandMapTile)north; }
    public LandMapTile e() { return (LandMapTile)east; }
    public LandMapTile w() { return (LandMapTile)west; }
    public LandMapTile s() { return (LandMapTile)south; }

    public LandMapEntity getContains() { return (LandMapEntity)contains; }

    protected Town town; // is there a town here?
    protected Dun dun; // is there a dun here?
    public LandMap map;

    // What region is this?
    private LandRegion region; // The region this tile is associated with

    public LandMapTile(LandMap theMap) {
        super();
        map = theMap;
        init();
    }

    public Town getTown() { return town; }
    public boolean setTown(Town newTown) {
        if (newTown == null) {
            return false;
        }
        newTown.landMapTile = this;
        town = newTown;
        if (type == LandMap.MapTileType.Capital) {
            town.setIsCapital(true);
        }
        return true;
    }

    public Dun getDun() { return dun; }
    public boolean setDun(Dun newDun) {
        if (newDun == null) {
            return false;
        }
        newDun.landMapTile = this;
        dun = newDun;
        return true;
    }

    public LandRegion getRegion() { return region; }
    public void setRegion(LandRegion reg) { 
        if (region != null) {
            region.remove(this);
        }
        region = reg; 
        setLevel(reg.getLevel()); 
    }
    public int getRegionId() { 
        if (region == null) 
            return -1; 
        return region.getId();
    }

    public LandRegion getRegionNorth() {
        if (n() == null)
            return null;
        return n().getRegion();
    }
    public LandRegion getRegionSouth() {
        if (s() == null)
            return null;
        return s().getRegion();
    }
    public LandRegion getRegionEast() {
        if (e() == null)
            return null;
        return e().getRegion();
    }
    public LandRegion getRegionWest() {
        if (w() == null)
            return null;
        return w().getRegion();
    }
    public LandRegion getRegionInDir(Constants.Dir dir) {
        switch (dir) {
            case NORTH: return getRegionNorth();
            case EAST:  return getRegionEast();
            case WEST:  return getRegionWest();
            case SOUTH: return getRegionSouth();
        }
        return null;
    }

    public boolean hasAdjacentRegion() {
        if (getRegionNorth() == null && getRegionSouth() == null &&
            getRegionEast() == null && getRegionWest() == null) {
            return false;
        }
        return true;
    }

    public boolean assignToAdjacentRegion() {
        if (region != null)
            return false;
        if (hasAdjacentRegion() == false)
            return false;

        LandRegion reg = getRegionInDir(Constants.Dir.getRandomDir());
        int loopCheck = 0;
        while (reg == null) {
            reg = getRegionInDir(Constants.Dir.getRandomDir());

            loopCheck++;
            if (loopCheck > 200) {
                Utils.log("loopCheck passed 200 in assignToAdjacentRegion");
                return false;
            }
        }
        reg.addTile(this);
        return true;
    }

    public LandMapTile getTileInRandomDir() {
        int dir = Utils.number(0, 3);
        return (LandMapTile)super.getAdjacentTileForNumber(dir);
    }

    @Override
    protected void handleMouseEnter(Object objTile, MouseEvent event) {
        if (objTile == null) 
            return;
        LandMapTile tile = (LandMapTile)objTile;
        map.onMouseEnterTile(tile);
    }

    @Override
    protected void handleClick(Object objTile, MouseEvent event) {
        if (objTile == null) 
            return;
        LandMapTile tile = (LandMapTile)objTile;
        if (event.getButton() == MouseButton.PRIMARY) {
            map.onLeftClickTile(tile);
        }
        else {
            map.onRightClickTile(tile);
        }
    }

    private void init() {
        /*shapeText.setUserData(this);
        getSelectedCircle().setUserData(this);
        overlay.setUserData(this);
        getSelectedPolygon().setUserData(this);
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

       //makeShape(4);
        setWidth(defSize);
        setStroke(Color.TRANSPARENT);
        setStrokeWidth(0);

        setWidth(defSize);
        setHeight(defSize);
        //setArcWidth(20);
        //setArcHeight(20);

        setFill(Color.WHITE);
        setupAnimationListeners();
    }

    private void setupAnimationListeners() {
        /*getText().translateYProperty().bind(translateYProperty());
        getText().translateXProperty().bind(translateXProperty());
        getSelectedCircle().translateYProperty().bind(translateYProperty());
        getSelectedCircle().translateXProperty().bind(translateXProperty());
        getSelectedPolygon().translateYProperty().bind(translateYProperty());
        getSelectedPolygon().translateXProperty().bind(translateXProperty());
        overlay.translateYProperty().bind(translateYProperty());
        overlay.translateXProperty().bind(translateXProperty());*/

        //yValue.bind(iv2.translateYProperty());
        /*translateYProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                System.out.println((double) t1);
            }
        });*/
    }

    public void createTown(int population) {
        // Create the town but don't generate it yet until someone
        // actually walks onto it.
        town = new Town(population, false);
    }
    
    public LandMapTile getAdjacentTileForNumber(int num) {
        return (LandMapTile)super.getAdjacentTileForNumber(num);
    }

    // Is a town, city, etc.
    public boolean isPopulated() {
        if (type == LandMap.MapTileType.Village || 
            type == LandMap.MapTileType.Town ||
            type == LandMap.MapTileType.City ||
            type == LandMap.MapTileType.BigCity ||
            type == LandMap.MapTileType.Capital) {
            return true;
        }
        return false;
    }

    public boolean isDun() {
        if (type == LandMap.MapTileType.Cave || 
            type == LandMap.MapTileType.Mine ||
            type == LandMap.MapTileType.Tower ||
            type == LandMap.MapTileType.Ruin) {
            return true;
        }
        return false;
    }

    public boolean surroundedByType(LandMap.MapTileType tileType) {
        if (n() != null && n().type != tileType) return false;
        if (e() != null && e().type != tileType) return false;
        if (w() != null && w().type != tileType) return false;
        if (s() != null && s().type != tileType) return false;

        return true;
    }

    public int numberAdjacentTilesOfType(LandMap.MapTileType tileType) {
        int num = 0;
        if (n() != null && n().type == tileType) num++;
        if (e() != null && e().type == tileType) num++;
        if (w() != null && w().type == tileType) num++;
        if (s() != null && s().type == tileType) num++;
        return num;
    }
    
    @Override
    public boolean canBeMovedTo(DfSquareMapEntity ent) {
        return this.canBeMovedTo((LandMapEntity)ent);
    }

    public boolean canBeMovedTo(LandMapEntity ent) {
        if (super.canBeMovedTo(ent) == false) {
            return false;
        }
        if (type == LandMap.MapTileType.Water ||
            type == LandMap.MapTileType.Mountain) {
            return false;
        }
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
            case Hill:      setFill(Color.TAN); break;
            case Mountain:  setFill(Color.LIGHTSLATEGRAY); break;
            case Forest:    setFill(Color.GREEN); break;
            case Swamp:     setFill(Color.MEDIUMSEAGREEN); break;
            case Deadlands: setFill(Color.DARKSALMON); break;
            case Village:   setFill(Color.YELLOW); setStroke(Color.BLACK); break;
            case Town:      setFill(Color.GOLD); setStroke(Color.BLACK); break;
            case City:      setFill(Color.GOLDENROD ); setStroke(Color.BLACK); break;
            case BigCity:   setFill(Color.DARKGOLDENROD ); setStroke(Color.BLACK); break;
            case Capital:   setFill(Color.CRIMSON); setStroke(Color.BLACK); break;
            case Cave:      setFill(Color.BLACK); setStroke(Color.BLACK); break;
            case Mine:      setFill(Color.DIMGRAY); setStroke(Color.BLACK); break;
            case Tower:     setFill(Color.GAINSBORO); setStroke(Color.BLACK); break;
            case Ruin:      setFill(Color.GRAY); setStroke(Color.BLACK); break;
        }


        // Let's check region difficulty by color - the lighter it is, the harder
        // it is.
        if (getLevel() == 1000) {
            setFill(Color.RED);
        }
        else {
            int diff = getLevel() / 4;
            setFill(Color.rgb(diff, diff, diff, 1.0));
        }
    }
}