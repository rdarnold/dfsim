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
import javafx.scene.shape.*;
import javafx.animation.PathTransition;
import javafx.util.Duration;

import dfsim.*;

public class HexMapTile extends MovablePolygon {

    public enum HexTileType {
        Blank,
        Grass,
        Field,
        Sand,
        Dirt,
        Tree,
        Log,
        Stone,
        Wall,
        Street,
        ShallowWater,
        DeepWater
    }

    // Where in the HexMap is this HexMapTile?
    int hexMapX = 0; 
    int hexMapY = 0;
    int tileNum = 0;
    public int getTileNumber() { return tileNum; }

    private HexMap hexMap;

    public HexMapEntity contains; // does this HexMapTile contain someone / another HexMapEntity?
    public boolean containsHexMapEntity(HexMapEntity ent) {
        if (ent == null)
            return false;
        return (contains == ent);
    }

    // Entities bordering this one
    public HexMapTile northwest;
    public HexMapTile north;
    public HexMapTile northeast;
    public HexMapTile southwest;
    public HexMapTile south;
    public HexMapTile southeast;

    public HexTileType type = HexTileType.Blank;
    public boolean canMoveTo = false; // Can currently selected HexMapEntity move to this tile?
    public boolean curMovPath = false; // Part of the current movement path?
    public int curMovStep = 0;

    //public int percent = 0; // transient variable to be used for whatever generation

    public void setCanMoveTo(boolean can) { canMoveTo = can; }
    public void setCurMovPath(boolean can) { curMovPath = can; }
    public void setCurMovStep(int num) { curMovStep = num; }
    public boolean getCanMoveTo() { return canMoveTo; }
    public boolean getCurMovPath() { return curMovPath; }
    public int getCurMovStep() { return curMovStep; }

    public void setType(HexTileType newType) { type = newType; updateColor(); }
    public HexTileType getType() { return type; }


    public HexMapTile(HexMap map) {
        super();
        hexMap = map;
        //setupMouseHandler();
        
        // We do not want this to be "visible" in the traditional sense because
        // we do not draw it with the scene graph, it is drawn through the canvas
        // manually.  We also do not want these to capture mouse clicks because those,
        // too, are handled manually.
        setVisible(false);
        shapeText.setVisible(false);
        overlay.setVisible(false);
        setMouseTransparent(true);
        shapeText.setMouseTransparent(true);
        overlay.setMouseTransparent(true);
        
        shapeText.setUserData(this);
        getSelectedCircle().setUserData(this);
        overlay.setUserData(this);
        getSelectedPolygon().setUserData(this);

        makeShape(6);
    }

    /*private void handleMouseEnter(Object objHex, MouseEvent event) {
        if (objHex == null) 
            return;
        HexMapTile hex = (HexMapTile)objHex;
        DfSim.sim.onMouseEnterHexMapTile(hex);
    }

    // Deprecated... it's not handled here anymore
    private void handleClick(Object objHex, MouseEvent event) {
        if (objHex == null) 
            return;
        HexMapTile hex = (HexMapTile)objHex;
        if (event.getButton() == MouseButton.PRIMARY) {
            DfSim.sim.onLeftClickHexMapTile(hex);
        }
        else {
            DfSim.sim.onRightClickHexMapTile(hex);
        }
    }

    // Deprecated... it's not handled here anymore
    private void setupMouseHandler() {
        
        // Override the HexMapEntity clicks with these ones.
        shapeText.setUserData(this);
        getSelectedCircle().setUserData(this);
        overlay.setUserData(this);
        selectedPolygon.setUserData(this);

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
        
        overlay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MovablePolygon source = (MovablePolygon)event.getSource();
                handleClick(source.getUserData(), event);
            }
        });
        overlay.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MovablePolygon source = (MovablePolygon)event.getSource();
                handleMouseEnter(source.getUserData(), event);
            }
        });
        
        shapeText.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
        });

        getSelectedCircle().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Circle source = (Circle)event.getSource();
                handleClick(source.getUserData(), event);
            }
        });
        getSelectedCircle().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Circle source = (Circle)event.getSource();
                handleMouseEnter(source.getUserData(), event);
            }
        });

        selectedPolygon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MovablePolygon source = (MovablePolygon)event.getSource();
                handleClick(source.getUserData(), event);
            }
        });
        selectedPolygon.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MovablePolygon source = (MovablePolygon)event.getSource();
                handleMouseEnter(source.getUserData(), event);
            }
        });
    }*/

    public boolean attach(HexMapEntity ent) {
        if (contains != null) {
            return false;
        }
        /*if (ent.getHex() != null) {
            ent.getHex().detach();
        }*/
        contains = null;
        ent.moveToTile(this);
        //ent.centerOn(this);
        //pullTo(ent);
        //ent.setHex(this);
        contains = ent;
        return true;
    }

    public void detach() {
        if (contains != null) {
            contains.setHex(null);
            contains.setPrevHex(null);
        }
        contains = null;
    }

    /*private void addCurMovPathTilesToMovement(Path path) {
        // And follow a move path if we have one
        HexMapTile hex = hexMap.getNextCurMovPathTile();
        while (hex != null) {
            hex.setCurMovPath(false);
            hex.setCurMovStep(0);
            path.getElements().add(new LineTo(hex.getCenterX(), hex.getCenterY()));
            hex = hexMap.getNextCurMovPathTile();
        }
    }*/

    public void pullTo(HexMapEntity ent) {
        //ent.turnMovDone = true;
        //ent.moveToTile(this);
        /*Path path = new Path();
        path.getElements().add(new MoveTo(ent.getTranslateX(), ent.getTranslateY())); 
        addCurMovPathTilesToMovement(path);
        path.getElements().add(new LineTo(getCenterX(), getCenterY()));

        double dist = steps * 20;
        if (dist <= 0) {
            dist = Utils.calcDistance(ent.getTranslateX(), ent.getTranslateY(),
                getCenterX(), getCenterY());
        }
        //path.getElements().add(new CubicCurveTo(380, 0, 380, 120, 200, 120));
        //path.getElements().add(new CubicCurveTo(0, 120, 0, 240, 380, 240));
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(4000));
        //pathTransition.setPath(path);
        //pathTransition.setNode(rectPath);
        //pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        //pathTransition.setCycleCount(Timeline.INDEFINITE);
        //pathTransition.setAutoReverse(true);
        //pathTransition.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
        //pathTransition.setAutoReverse(true);

        pathTransition.setDuration(Duration.millis(dist * 5));
        pathTransition.setNode(ent);
        pathTransition.setPath(path);
        pathTransition.setCycleCount(1);
        pathTransition.play();*/

        //ent.moveTo(getCenterX(), getCenterY());
        //ent.moveTo(this);
    }

    public HexMapTile getTileInRandomDir() {
        return getTileInRandomDir(true);
    }
    
    public HexMapTile getTileInRandomDir(boolean bAllowNull) {
        HexMapTile hex = getAdjacentTileForNumber(Utils.number(0, 5));
        while (hex == null && bAllowNull == false) {
            hex = getAdjacentTileForNumber(Utils.number(0, 5));
        }
        return hex;
    }

    public int numberAdjacentCurMovPathTiles() {
        int num = 0;
        if (northeast != null && northeast.getCurMovPath() == true) num++;
        if (north     != null && north.getCurMovPath() == true)     num++;
        if (northwest != null && northwest.getCurMovPath() == true) num++;
        if (southwest != null && southwest.getCurMovPath() == true) num++;
        if (south     != null && south.getCurMovPath() == true)     num++;
        if (southeast != null && southeast.getCurMovPath() == true) num++;
        return num;
    }

    public int numberAdjacentCurMovPathTiles(HexMapEntity selected) {
        int num = numberAdjacentCurMovPathTiles();

        // With this function, we passed in a selected HexMapEntity so we can also
        // check if that HexMapEntity is adjacent - if so, that "counts" as a mov
        // path tile for this function since it's the origin.
        if (northeast != null && northeast.containsHexMapEntity(selected) == true) num++;
        if (north     != null && north.containsHexMapEntity(selected) == true)     num++;
        if (northwest != null && northwest.containsHexMapEntity(selected) == true) num++;
        if (southwest != null && southwest.containsHexMapEntity(selected) == true) num++;
        if (south     != null && south.containsHexMapEntity(selected) == true)     num++;
        if (southeast != null && southeast.containsHexMapEntity(selected) == true) num++;
        return num;
    }

    public int numberAdjacentWaterTiles() {
        return (numberAdjacentTilesOfType(HexTileType.ShallowWater) +
                numberAdjacentTilesOfType(HexTileType.DeepWater));
    }

    public boolean touchesType(HexTileType touchingType) {
        if (northwest != null && northwest.type == touchingType)  return true;
        if (north != null && north.type == touchingType)          return true;
        if (northeast != null && northeast.type == touchingType)  return true;
        if (southeast != null && southeast.type == touchingType)  return true;
        if (south != null && south.type == touchingType)          return true;
        if (southwest != null && southwest.type == touchingType)  return true;

        return false;
    }

    public int numberAdjacentTilesOfType(HexTileType tileType) {
        int num = 0;
        if (northeast != null && northeast.type == tileType) num++;
        if (north     != null && north.type == tileType)     num++;
        if (northwest != null && northwest.type == tileType) num++;
        if (southwest != null && southwest.type == tileType) num++;
        if (south     != null && south.type == tileType)     num++;
        if (southeast != null && southeast.type == tileType) num++;
        return num;
    }

    public boolean surroundedByType(HexTileType tileType) {
        if (northeast != null && northeast.type != tileType) return false;
        if (north != null && north.type != tileType)         return false;
        if (northwest != null && northwest.type != tileType) return false;
        if (southwest != null && southwest.type != tileType) return false;
        if (south != null && south.type != tileType)         return false;
        if (southeast != null && southeast.type != tileType) return false;

        return true;
    }

    public HexMapTile getAdjacentTileForNumber(int num) {
        switch (num) {
            case 0: return northwest;
            case 1: return north;
            case 2: return northeast;
            case 3: return southeast;
            case 4: return south;
            case 5: return southwest;
        }
        return null;
    }

    public boolean traversable(boolean isFlying) {
        if (isFlying == true) {
            return true;
        }
        if (moveCost() < 0)
            return false;
        return true;
    }
    

    public int moveCost() {
        switch (type) {
            case Blank:         return 1;
            case Grass:         return 1;
            case Field:         return 1;
            case Sand:          return 1;
            case Dirt:          return 1;
            case Tree:          return 2;
            case Log:           return -1;
            case Stone:         return -1;
            case Wall:          return -1;
            case Street:        return 1;
            case ShallowWater:  return 2;
            case DeepWater:     return -1;
        }
        return 1;
    }

    public void updateColor() {
        
        setStroke(Color.GRAY);

        switch (type) {
            case Blank:     setFill(Color.WHITE); break;
            case Grass:     setFill(Color.rgb(102, 251, 102, 0.5)); break;// setFill(Color.LIGHTGREEN); break;
            case Field:     setFill(Color.WHEAT); break;
            case Sand:      setFill(Color.SANDYBROWN); break;
            case Dirt:      setFill(Color.TAN); break;
            case Tree:      setFill(Color.GREEN); break;
            case Log:       setFill(Color.SIENNA); break;
            case Stone:     setFill(Color.GRAY); break;
            case Wall:      setFill(Color.DARKGRAY); break;
            case Street:    setFill(Color.LIGHTGRAY); break;
            case ShallowWater: setFill(Color.LIGHTSKYBLUE); break;
            case DeepWater:    setFill(Color.BLUE); break;
        }

        // If can move to, outline it or something.
        if (canMoveTo == false && curMovPath == false) {
            //setStroke(Color.BLACK);
            //setStrokeWidth(1);
            //overlay.setFill(Color.WHITE);
            //overlay.setStroke(Color.WHITE);
            //overlay.opacityProperty().set(0);
            overlay.setVisible(false);
        }
        else {
            //setStroke(Color.RED);
            //setStrokeWidth(1);
            overlay.setFill(Color.TOMATO);
            overlay.setVisible(true);
            if (canMoveTo == true) {
                overlay.setStroke(Color.GRAY);
                overlay.opacityProperty().set(0.3);
            }
            if (curMovPath == true) {
                overlay.setStroke(Color.BLACK);
                overlay.opacityProperty().set(0.6);
            }
        }
    }
    
    @Override
    public void draw(GraphicsContext gc) {

        // If no graphics, call base class
        super.draw(gc);

        // Only draw if we are visible
        /*double drawX = getX() + map.getXOffset();
        double drawY = getY() + map.getYOffset();

        if (drawX > DfSim.width || drawX < (0 - getWidth()) || drawY > DfSim.height || drawY < (0 - getHeight())) {
            return;
        }

        if (Constants.ENABLE_TILE_GRAPHICS == false || gs == null) {
            drawNoGraphics(gc);
            return;
        }

        // If it has a background, draw that first
        if (bgrdSprite != null) {
            bgrdSprite.drawFrameByIndex(gc, bgrdSpriteFrameIndex, drawX, drawY, getWidth(), getHeight());
        }

        //gc.drawImage(img, 10, 10, 50, 50, getX(), getX(), getWidth(), getHeight());
        gs.drawFrameByIndex(gc, spriteFrameIndex, drawX, drawY, getWidth(), getHeight());*/
    }
}