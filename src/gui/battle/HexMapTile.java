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

    // Can use HexTileType.Grass.name() to return "Grass" as the string.
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

    // So we can do BFS for pathfinding
    private int m_nCost = 0;
    public int getCost() { return m_nCost; }
    private boolean bfsMark = false;
    public void mark(int cost) { bfsMark = true; m_nCost = cost;}
    public void unmark() { bfsMark = false; m_nCost = 0; }
    public boolean marked() { return bfsMark; }

    // Where in the HexMap is this HexMapTile?
    int hexMapX = 0; 
    int hexMapY = 0;
    int tileNum = 0;
    public int getTileNumber() { return tileNum; }

    private HexMap hexMap;

    /////////////////////////////////////////////////////
    // Processing for the list of entities on the tile //
    /////////////////////////////////////////////////////
    private ArrayList<HexMapEntity> containsList = new ArrayList<HexMapEntity>(); // does this HexMapTile contain someone / another HexMapEntity?
    public ArrayList<HexMapEntity> getContainsList() { return containsList; }; 
    public int getNumberContains() { return containsList.size(); }; 
    public void clearContains() { containsList.clear(); }; 
    public HexMapEntity getContains() { return containsList.get(0); }; 
    public void removeContains(HexMapEntity ent) { containsList.remove(ent); }; 
    public boolean addContains(HexMapEntity ent) { 
        if (ent != null) {
            containsList.add(ent);
            return true;
        }
        return false;
    }
    //public void setContains(HexMapEntity ent) { contains = ent; }
    public boolean setContainsIfEmpty(HexMapEntity ent) { 
        if (getNumberContains() > 0) {
            return false;
        }
        return addContains(ent); 
    }
    public boolean containsHexMapEntity() { return (containsList.size() > 0); }
    public boolean containsHexMapEntity(HexMapEntity ent) {
        if (ent == null)
            return false;
        return (containsList.contains(ent));
    }
    public boolean containsEnemy(HexMapEntity ent) {
        if (containsList.size() != 0 && ent != null) {
            for (HexMapEntity cont : containsList) {
                if (ent.isPartyMember() && cont.isMonster()) {
                    return true;
                }
                else if (cont.isPartyMember() && ent.isMonster()) {
                    return true;
                }
            }
        }
        return false;
    }
    /////////////////////////////////////////////////////
    // End contains processing //////////////////////////
    /////////////////////////////////////////////////////

    // Entities bordering this one
    public HexMapTile northwest;
    public HexMapTile north;
    public HexMapTile northeast;
    public HexMapTile southwest;
    public HexMapTile south;
    public HexMapTile southeast;

    public HexMapTile getNorthwest()    { return northwest; }
    public HexMapTile getNorth()        { return north; }
    public HexMapTile getNortheast()    { return northeast; }
    public HexMapTile getSouthwest()    { return southwest; }
    public HexMapTile getSouth()        { return south; }
    public HexMapTile getSoutheast()    { return southeast; }

    // Get bordsering tile by ordinal direction
    public HexMapTile getByOrdinal(Constants.Ordinal dir) {
        switch (dir) {
            case NORTHWEST: return northwest;
            case NORTH:     return north;
            case NORTHEAST: return northeast;
            case SOUTHWEST: return southwest;
            case SOUTH:     return south;
            case SOUTHEAST: return southeast;
        }
        return null;
    }

    public HexTileType type = HexTileType.Blank;
    public boolean m_bCanMoveToFlag = false; // Can currently selected HexMapEntity move to this tile?
    public boolean m_bIsOnCurMovPath = false; // Part of the current movement path?
    public int m_nCurMovStep = 0;

    //public int percent = 0; // transient variable to be used for whatever generation

    public void setCanMoveToFlag(boolean can) { m_bCanMoveToFlag = can; updateColor(); }
    public void setOnCurMovPath(boolean can) { m_bIsOnCurMovPath = can; updateColor(); }
    public void setCurMovStep(int num) { m_nCurMovStep = num; }
    public boolean getCanMoveToFlag() { return m_bCanMoveToFlag; }
    public boolean isOnCurMovPath() { return m_bIsOnCurMovPath; }
    public int getCurMovStep() { return m_nCurMovStep; }

    public void setType(HexTileType newType) { type = newType; updateColor(); }
    public HexTileType getType() { return type; }

    // What tile sprite to draw?  If null just draw color
    protected GameSprite m_Sprite = null;
    public void setGameSprite(GameSprite gs) { m_Sprite = gs; }
    public GameSprite getGameSprite() { return m_Sprite; }
    public int spriteFrameIndex = 0; // Index into the sprite sheet in terms of what sprite to actually draw

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
        setSize(Constants.BASE_HEX_TILE_SIZE);
    }

    /*public boolean attach(HexMapEntity ent) {
        if (contains != null) {
            return false;
        }
        contains = null;
        ent.moveToTile(this);
        contains = ent;
        return true;
    }*/

    /*public void detach(HexMapEntity ent) {
        if (contains != null) {
            contains.setHex(null);
            contains.setPrevHex(null);
        }
        contains = null;
    }*/

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
        if (northeast != null && northeast.isOnCurMovPath() == true) num++;
        if (north     != null && north.isOnCurMovPath() == true)     num++;
        if (northwest != null && northwest.isOnCurMovPath() == true) num++;
        if (southwest != null && southwest.isOnCurMovPath() == true) num++;
        if (south     != null && south.isOnCurMovPath() == true)     num++;
        if (southeast != null && southeast.isOnCurMovPath() == true) num++;
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

    public boolean traversable(HexMapEntity ent) {
        // If mover is flying, etc., maybe they can pass.
        /*if (isFlying == true) {
            return true;
        }*/
        if (moveCost(ent) < 0) {
            return false;
        }
        return true;
    }
    
    // Should also be based on the entity moving here
    public int moveCost(HexMapEntity ent) {
        // Add in processing for mover entity
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

    public void updateGraphics() {
        updateColor();
        updateImage();
    }

    private void updateImage() {
        if (Data.hexTileSprites == null || Data.hexTileSprites.size() <= 0) {
            m_Sprite = null;
            return;
        }

        // This is LOADS better, I should use this method for other things as well.
        m_Sprite = GraphicsUtils.getSpriteForKey(Data.hexTileSprites, type.name());

        /*switch (type) {
            case Blank:         m_Sprite = null; break;
            case Grass:         m_Sprite = GraphicsUtils.getSpriteForKey(Data.hexTileSprites, HexTileType.Grass.name()); break;
            case Field:         m_Sprite = GraphicsUtils.getSpriteForKey(Data.hexTileSprites, HexTileType.Grass.name()); break;
            case Sand:          m_Sprite = Data.hexTileSprites.get(); break;
            case Dirt:          m_Sprite = Data.hexTileSprites.get(); break;
            case Tree:          m_Sprite = Data.hexTileSprites.get(); break;
            case Log:           m_Sprite = Data.hexTileSprites.get(); break;
            case Stone:         m_Sprite = Data.hexTileSprites.get(); break;
            case Wall:          m_Sprite = Data.hexTileSprites.get(); break;
            case Street:        m_Sprite = Data.hexTileSprites.get(); break;
            case ShallowWater:  m_Sprite = Data.hexTileSprites.get(); break;
            case DeepWater:     m_Sprite = Data.hexTileSprites.get(); break;
        }*/
    }

    private void updateColor() {
        
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
        if (getCanMoveToFlag() == false && isOnCurMovPath() == false) {
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
            if (isOnCurMovPath() == true) {
                overlay.setStroke(Color.BLACK);
                overlay.opacityProperty().set(0.6);
            }
            else if (getCanMoveToFlag() == true) {
                overlay.setStroke(Color.GRAY);
                overlay.opacityProperty().set(0.3);
            }
        }
    }

    public boolean isSelected() {
        return (this == hexMap.getSelectedHexMapTile());
    }

    @Override
    public void draw(GraphicsContext gc) {
    
        // If no graphics, call base class
        if (getGameSprite() == null) {
            super.draw(gc);
            return;
        }

        if (getGameSprite() != null) {
            // Are these weird adjustments working for ALL hex tiles - i.e. tey're a quirk of the calculations
            // done for LeftX etc on MovablePolygon - or are they JUST for the VNHex tiles?
            getGameSprite().drawFullImage(gc, getLeftX() - 2, getTopY(), getSize() + 2, getSize() - 4);
        }
        
        // If we are selected, indicate this with some kind of overlay.
        if (isSelected() == true) {
            gc.setGlobalAlpha(0.5);
            gc.setFill(Color.DARKGRAY);
            gc.setLineWidth(5);
            gc.fillPolygon(getXPoints(), getYPoints(), getNumPoints());
            gc.setGlobalAlpha(1.0);
        }

        super.drawOverlay(gc);
        super.drawText(gc);

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