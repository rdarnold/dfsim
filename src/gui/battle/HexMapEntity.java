package dfsim.gui;

import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.geometry.*;
import java.util.*;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.beans.value.*;
import javafx.beans.property.*;

// For a smooth movement of the screen
import javafx.animation.*;
import javafx.util.*;
import javafx.event.*;

import dfsim.*;

public class HexMapEntity extends MovablePolygon {

    private HexMap hexMap;

    private HexMapTile m_Hex = null;
    public HexMapTile getHex() { return m_Hex; }
    public void setHex(HexMapTile hex) { m_Hex = hex; }

    private HexMapTile m_MovingToHex = null;
    public HexMapTile getMovingToHex() { return m_MovingToHex; }
    public void setMovingToHex(HexMapTile hex) { m_MovingToHex = hex; }

    private HexMapTile m_prevHex = null;
    public HexMapTile getPrevHex() { return m_prevHex; }
    public void setPrevHex(HexMapTile hex) { m_prevHex = hex; }

    private HexMapTile m_nextHex = null;
    public HexMapTile getNextHex() { return m_nextHex; }
    public void setNextHex(HexMapTile hex) { m_nextHex = hex; }

    private ArrayList<HexMapTile> movePath = new ArrayList<HexMapTile>();

    public Person partyMember = null;
    public DfMon mon = null;
    public boolean isPartyMember() { return partyMember != null; }
    public boolean isMonster() { return mon != null; }

    public int hp = 0;
    public int curMov = 0;

    private boolean turnMovDone = false;
    private boolean turnAtkDone = false;

    public void setTurnMovDone(boolean newVal) { turnMovDone = newVal; }
    public void setTurnAtkDone(boolean newVal) { turnAtkDone = newVal; }
    public boolean getTurnMovDone() { return turnMovDone; }
    public boolean getTurnAtkDone() { return turnAtkDone; }

    ///////////////////////////
    /// For the SPRITE ONLY ///
    ///////////////////////////
    // These are JUST for drawing the sprite, not used as the facing of the tile or anything else
    private int moveState = 0; // which of the movement states
    public int getMoveState() { return moveState; }
    private boolean moveStateReverse = false; // Iterating forward or backward on move states, it rotates
    public boolean getMoveStateReverse() { return moveStateReverse; }
    protected Constants.Dir facing = Constants.Dir.SOUTH;
    public Constants.Dir getFacing() { return facing; }
    public void setFacing(Constants.Dir dir) { facing = dir; }
    public void setRandomFacing() { facing = Constants.Dir.getRandomDir(); }
    //////////////////////////////
    // End for the SPRITE ONLY ///
    //////////////////////////////

    // For movement animation
    private Timeline timeline;
    private boolean canMove = true;

    public boolean getCanMove() { return canMove; }

    public String getName() {
        if (partyMember != null)
            return partyMember.getName();
        if (mon != null)
            return mon.getName();
        return "";
    }
    
    public HexMapEntity(HexMap map) {
        super();
        init(map);
    }

    public HexMapEntity(HexMap map, Person person) {
        super(person.getName().substring(0, 2));
        partyMember = person;
        hp = (int)person.getHp();
        init(map);
    }

    public HexMapEntity(HexMap map, DfMon mons) {
        super(mons.name.substring(0, 2));
        mon = mons;
        hp = (int)mons.getHp();
        init(map);
    }

    /*private void handleMouseEnter(Object objEnt, MouseEvent event) {
        if (objEnt == null) 
            return;
        HexMapEntity ent = (HexMapEntity)objEnt;
        DfSim.sim.onMouseEnterHexMapEntity(ent);
    }

    private void handleClick(Object objEnt, MouseEvent event) {
        if (objEnt == null) 
            return;
        HexMapEntity ent = (HexMapEntity)objEnt;
        if (event.getButton() == MouseButton.PRIMARY) {
            DfSim.sim.onLeftClickHexMapEntity(ent);
        }
        else {
            DfSim.sim.onRightClickHexMapEntity(ent);
        }
    }*/

    private void init(HexMap map) {
        hexMap = map;

        this.timeline = new Timeline(60);
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timelineFinished();
            }
        });
        
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
        });

        // The text sits on top of the shape and will intercept the mouseclick
        // so we want clicking on the text to work the same way as clicking
        // on the HexMapEntity
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
        getSelectedPolygon().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MovablePolygon source = (MovablePolygon)event.getSource();
                handleClick(source.getUserData(), event);
            }
        });
        getSelectedPolygon().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MovablePolygon source = (MovablePolygon)event.getSource();
                handleMouseEnter(source.getUserData(), event);
            }
        });*/

        makeShape(6);
        setSize(Constants.BASE_HEX_TILE_SIZE);
        setStroke(Color.BLACK);
        setStrokeWidth(1);

        if (partyMember != null) {
            setFill(Color.AQUA);
        }
        else if (mon != null) {
            setFill(Color.ORANGE);
        }
        else {
            setFill(Color.WHITE);
        }
        //setupAnimationListeners();
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

    /*public void addToPane(Pane node) {
        node.getChildren().add(this);
        node.getChildren().add(overlay);
        node.getChildren().add(getText());
        node.getChildren().add(getSelectedCircle());
        node.getChildren().add(selectedPolygon);
    }

    public void removeFromPane(Pane node) {
        node.getChildren().remove(this);
        node.getChildren().remove(overlay);
        node.getChildren().remove(getText());
        node.getChildren().remove(getSelectedCircle());
        node.getChildren().remove(selectedPolygon);
    }*/

    public void resetCurMov() {
       curMov = (int)getMov();
    }

    public double getMov() {
      if (partyMember != null) return partyMember.getMov();
      if (mon != null) return mon.getMov();
      return 0;
    }

    public double getHit() {
      if (partyMember != null) return partyMember.getHit();
      if (mon != null) return mon.getHit();
      return 0;
    }

    public double getPwr() {
      if (partyMember != null) return partyMember.getPwr();
      if (mon != null) return mon.getPwr();
      return 0;
    }

    public double getMpwr() {
      if (partyMember != null) return partyMember.getMpwr();
      if (mon != null) return mon.getMpwr();
      return 0;
    }

    public double getEva() {
      if (partyMember != null) return partyMember.getEva();
      if (mon != null) return mon.getEva();
      return 0;
    }

    public double getDef() {
      if (partyMember != null) return partyMember.getDef();
      if (mon != null) return mon.getDef();
      return 0;
    }

    public double getMdef() {
      if (partyMember != null) return partyMember.getMdef();
      if (mon != null) return mon.getMdef();
      return 0;
    }

    public void setAnimating(boolean val) { 
        if (val == true) {
            // Checks dupes
            hexMap.addAnimatingEntity(this);
            // Can't do another move until we finish animating
            // this one; the variable is reset on the onFinish of the timeline.
            canMove = false;
        }
        else {
            // May or may not be able to move upon completion so we don't reset
            // the canMove here, we do it elsewhere
            hexMap.removeAnimatingEntity(this);
            canMove = true;
        }
    }

    // Update the movement state as the sprites walk around.
    public void updateMoveState() {
        CharSprite spr = null;
        if (partyMember != null) {
            spr = partyMember.getSprite();
        }
        if (mon != null) {
            spr = mon.getSprite();
        }
        if (spr == null) {
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
        else if (moveState >= spr.getNumMoveStates()-1) {
            moveStateReverse = true;
        }
    }

    // Can be overridden if we want any custom logic for any subclasses
    protected void onMoveFinished() { }

    // Finished with the movement
    private void timelineFinished() {
        onMoveFinished();
        setHex(getMovingToHex());
        setMovingToHex(null);
        movePath.clear();
        setAnimating(false);
    }

    // Which ordinal is this entity moving towards?
    public Constants.Ordinal getMovingInOrdinal() {
        if (getHex() == null || movePath == null || movePath.size() <= 0) {
            return Constants.Ordinal.NONE;
        }
        // Set our facing dir based on where we are gonna move.
        HexMapTile nextTile = movePath.get(0);
        HexMapTile curTile = getHex();

        if (nextTile == curTile.getAdjacentTileForNumber(Constants.Ordinal.NORTHWEST.val())) {
            return Constants.Ordinal.NORTHWEST;
        }
        else if (nextTile == curTile.getAdjacentTileForNumber(Constants.Ordinal.NORTH.val())) {
            return Constants.Ordinal.NORTH;
        }
        else if (nextTile == curTile.getAdjacentTileForNumber(Constants.Ordinal.NORTHEAST.val())) {
            return Constants.Ordinal.NORTHEAST;
        }
        else if (nextTile == curTile.getAdjacentTileForNumber(Constants.Ordinal.SOUTHEAST.val())) {
            return Constants.Ordinal.SOUTHEAST;
        }
        else if (nextTile == curTile.getAdjacentTileForNumber(Constants.Ordinal.SOUTH.val())) {
            return Constants.Ordinal.SOUTH;
        }
        else if (nextTile == curTile.getAdjacentTileForNumber(Constants.Ordinal.SOUTHWEST.val())) {
            return Constants.Ordinal.SOUTHWEST;
        }
        return Constants.Ordinal.NONE;
    }

    // Before we do the move, we want to turn in the right direction, etc.,
    // maybe check something else, who knows
    protected void onPreMoveOneTile() { 
        switch (getMovingInOrdinal()) {
            case NORTHWEST: setFacing(Constants.Dir.WEST);  break;
            case NORTH:     setFacing(Constants.Dir.NORTH); break;
            case NORTHEAST: setFacing(Constants.Dir.EAST);  break;
            case SOUTHEAST: setFacing(Constants.Dir.EAST);  break;
            case SOUTH:     setFacing(Constants.Dir.SOUTH); break;
            case SOUTHWEST: setFacing(Constants.Dir.WEST);  break;
        }
    }

    // We have now moved into the tile and we are in a new tile
    protected void onPostMoveOneTile() { 
        updateMoveState();

        if (movePath == null || movePath.size() <= 0) {
            return;
        }

        // Transition to next hex tile
        getHex().removeContains(this);
        setPrevHex(getHex());
        setHex(movePath.get(0));
        getHex().addContains(this);
        movePath.remove(0);

        if (movePath.size() > 0) {
            setNextHex(movePath.get(0));
        }
        else {
            setNextHex(null);
        }
        
        // If we are passing through an entity, it "moves aside" slightly
        // depending on what direction we are moving to.  This could also
        // be based on the direction we are coming from to make it look 
        // nicer.
        if (getHex().getNumberContains() > 1) {
            for (HexMapEntity ent : getHex().getContainsList()) {
                if (ent != this) {
                    ent.doAnimatedDodge(getMovingInOrdinal());
                }
            }
        }

        // We might pass by enemies and take AoAs and shit.
        // TODO check enemies etc.
    }

    private int m_nTimeIntervalMS = 150;
    public void doAnimatedMove(double x, double y) {
        setAnimating(true);

        // timeline that moves the screen
        timeline.getKeyFrames().clear();

        // Add frames based on steps of the move.
        int timePointMS = m_nTimeIntervalMS;

        movePath.clear();

        HexMapTile hex = hexMap.getNextCurMovPathTile();
        setNextHex(hex);
        setPrevHex(null);

        while (hex != null) {
            movePath.add(hex);

            hex.setOnCurMovPath(false);
            hex.setCurMovStep(0);
            
            // The "millis" is at what point in time this frame occurs, so we can do "multiple things at once"
            // if we set the same time point but we have to increase this to cause a multiple-frame animation.
            // So first we do onPreMoveOneTile before we do the move, then we move, then we do onPostMoveOneTile
            timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(timePointMS - m_nTimeIntervalMS), e -> onPreMoveOneTile()),
                new KeyFrame(Duration.millis(timePointMS), e -> onPostMoveOneTile()),
                new KeyFrame(Duration.millis(timePointMS), new KeyValue(centerXProperty(), hex.getCenterX())),
                new KeyFrame(Duration.millis(timePointMS), new KeyValue(centerYProperty(), hex.getCenterY()))
            );

            hex = hexMap.getNextCurMovPathTile();
            timePointMS += m_nTimeIntervalMS;
        }

        // If our last one in the path isn't the one we're moving to (like we are basically click-moving where we
        // can't move) then add it.
        if (movePath.size() == 0 || movePath.get(movePath.size()-1) != getMovingToHex()) {
            movePath.add(getMovingToHex());
        }
        setNextHex(movePath.get(0));

        timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.millis(timePointMS - m_nTimeIntervalMS), e -> onPreMoveOneTile()),
            new KeyFrame(Duration.millis(timePointMS), e -> onPostMoveOneTile()),
            new KeyFrame(Duration.millis(timePointMS), new KeyValue(centerXProperty(), x)),
            new KeyFrame(Duration.millis(timePointMS), new KeyValue(centerYProperty(), y))
        );

        timeline.play();
    }
    
    // Finished with the dodge
    private void dodgeFinished() {
        setAnimating(false);
    }

    public void doAnimatedDodge(Constants.Ordinal dir) {
        setAnimating(true);

        Timeline tl = new Timeline(60);
        tl.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dodgeFinished();
            }
        });

        double movedX = getCenterX();
        double movedY = getCenterY();
        double diff = 10;
        // If the person is moving southeast, we want to dodge
        // northeast.  Basically we dodge in the 90 degree of
        // dir so it looks like we are moving away.
        switch (dir) {
            case NORTHWEST: movedX += diff; movedY -= diff; break;
            case NORTH:     movedX += diff; break;
            case NORTHEAST: movedX -= diff; movedY -= diff; break;
            case SOUTHEAST: movedX += diff; movedY -= diff; break;
            case SOUTH:     movedX -= diff; break; // Could randomize for more flavor.
            case SOUTHWEST: movedX -= diff; movedY -= diff; break;
            default:
                movedX += diff; movedY -= diff; break;
        }

        tl.getKeyFrames().addAll(
            new KeyFrame(Duration.millis(m_nTimeIntervalMS), new KeyValue(centerXProperty(), movedX)),
            new KeyFrame(Duration.millis(m_nTimeIntervalMS), new KeyValue(centerYProperty(), movedY)),
            new KeyFrame(Duration.millis(m_nTimeIntervalMS*2), new KeyValue(centerXProperty(), getCenterX())),
            new KeyFrame(Duration.millis(m_nTimeIntervalMS*2), new KeyValue(centerYProperty(), getCenterY()))
        );

        tl.play();
    }

    
    public boolean moveToTile(HexMapTile tile) {
        return moveToTile(tile, true);
    }

    public boolean moveToTile(HexMapTile tile, boolean animate) {
        if (canMove == false)
            return false;

        setMovingToHex(tile);
        
        if (animate == false) {
            // This is actually only for clicking on a square, this needs
            // to be changed to like a "freeMove" or something which doesnt
            // involve being bound to translate x and y
            centerOn(tile);

            // Warp to the end and do post-processing
            timelineFinished();
        }
        else {
            doAnimatedMove(tile.getCenterX(), tile.getCenterY());
        }
        return true;
    }
    
    // Finished with the attack
    // TODO all of this
    /*private void attackFinished() {
        setAnimating(false);
    }

    public void doAnimatedAttack(HexMapEntity target) {
        setAnimating(true);

        Timeline tl = new Timeline(60);
        tl.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                attackFinished();
            }
        });

        double movedX = getCenterX();
        double movedY = getCenterY();
        double diff = 10;
        // If the person is moving southeast, we want to dodge
        // northeast.  Basically we dodge in the 90 degree of
        // dir so it looks like we are moving away.
        switch (dir) {
            case NORTHWEST: movedX += diff; movedY -= diff; break;
            case NORTH:     movedX += diff; break;
            case NORTHEAST: movedX -= diff; movedY -= diff; break;
            case SOUTHEAST: movedX += diff; movedY -= diff; break;
            case SOUTH:     movedX -= diff; break; // Could randomize for more flavor.
            case SOUTHWEST: movedX -= diff; movedY -= diff; break;
            default:
                movedX += diff; movedY -= diff; break;
        }

        tl.getKeyFrames().addAll(
            new KeyFrame(Duration.millis(m_nTimeIntervalMS), new KeyValue(centerXProperty(), movedX)),
            new KeyFrame(Duration.millis(m_nTimeIntervalMS), new KeyValue(centerYProperty(), movedY)),
            new KeyFrame(Duration.millis(m_nTimeIntervalMS*2), new KeyValue(centerXProperty(), getCenterX())),
            new KeyFrame(Duration.millis(m_nTimeIntervalMS*2), new KeyValue(centerYProperty(), getCenterY()))
        );

        tl.play();
    }*/

    @Override
    public void draw(GraphicsContext gc) {
        // Draw the selection box
        if (isSelected() == true) {
            drawSelected(gc);
        }

        // Draw the sprite
        if (partyMember != null) {
            double x = getCenterX() - getSize()/2;
            double y = getCenterY() - getSize()/2;
            //gc.drawImage(partyMember.getPortraitImage(), x, y, getSize(), getSize());
            
            CharSprite sprite = partyMember.getSprite();
            int index = sprite.getFrameIndexForMovementState(getMoveState(), getFacing());
        
            //gc.drawImage(img, 10, 10, 50, 50, getX(), getX(), getWidth(), getHeight());
            sprite.drawFrameByIndex(gc, index, x, y, getSize(), getSize());
        }
        else if (mon != null) {
            double x = getCenterX() - getSize()/2;
            double y = getCenterY() - getSize()/2;
            //gc.drawImage(partyMember.getPortraitImage(), x, y, getSize(), getSize());
            
            CharSprite sprite = mon.getSprite();
            int index = sprite.getFrameIndexForMovementState(getMoveState(), getFacing());
        
            //gc.drawImage(img, 10, 10, 50, 50, getX(), getX(), getWidth(), getHeight());
            sprite.drawFrameByIndex(gc, index, x, y, getSize(), getSize());
        }
        else {
            //If no graphics, call base class
            super.draw(gc);
        }
        
        /*gc.setLineWidth(1);
        gc.setStroke(Color.DARKGRAY);
        //gc.setLineJoin(StrokeLineJoin.MITER);
        //gc.setLineJoin(StrokeLineJoin.BEVEL);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.strokeRect(x, y, getSize(), getSize());*/

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