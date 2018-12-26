package dfsim;

import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;

// For a smooth movement of the screen
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import dfsim.gui.*;

// So this is meant to be the overland map.
public abstract class DfSquareMap {

    // For movement animation
    private Timeline timeline;
    private boolean canMove = true;

    public boolean getCanMove() { return canMove; }

    // Big map, lots of tiles
    protected int numXTiles = 50;
    protected int numYTiles = 50;

    public int getNumXTiles() { return numXTiles; }
    public int getNumYTiles() { return numYTiles; }

    // As the avatar moves, these things change, essentially the 
    // entire map moves around the avatar.
    // I should just bind all the translate values to these ones somehow.
    // Actually what if I just set all the base values for all the things,
    // then bind their translations to this one.
    protected final IntegerProperty xOffset = new SimpleIntegerProperty(0);
    protected final IntegerProperty yOffset = new SimpleIntegerProperty(0);

    protected Pane pane;

    public DfSquareMap() { 
        this.timeline = new Timeline(60);
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timelineFinished();
            }
        });
    }

    // Meant to be override with subclass logic
    protected void updateVisible() { }

    // Meant to be overridden if we want any custom logic for the subclasses
    protected void onMoveFinished() { }

    private void timelineFinished() {
        onMoveFinished();
        canMove = true;
    }

    public void playMoveAnimation(int x, int y) {
        // Can't do another move until we finish animating
        // this one; the variable is reset on the onFinish of the timeline.
        canMove = false;

        // timeline that moves the screen
        timeline.getKeyFrames().clear();

        //timeline = new Timeline(60);
        timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.millis(50), new KeyValue(xOffset, x)),
            new KeyFrame(Duration.millis(50), new KeyValue(yOffset, y))
        );
        timeline.play();
    }

    public void moveAvatarToTile(DfSquareMapEntity ent, DfSquareTile moveTile, Constants.Dir dir) {
        if (moveTile != null && ent != null &&
            moveTile.canBeMovedTo(ent) == true) {
            moveTile.attach(ent);
        }
        centerOnEntity(ent);
        ent.setLastMoveDir(dir);

        // Now, was it a town or a dun?
        // Wish I could have it update the screen before entering
        // the new screen so you could see the avatar step on top of
        // the tile.
        /*if (moveTile.isPopulated()) {
            DfSim.showTownMapScreen(Constants.Dir.revDir(dir));
        }
        else if (moveTile.isDun()) {
            DfSim.showDunMapScreen();
        }*/
    }

    public void moveAvatarByInput(DfSquareMapEntity ent, Constants.Dir dir) {
        ent.setFacing(dir);
        if (getCanMove() == false)
            return;
        DfSquareTile tile = ent.getTile();
        DfSquareTile to = null;
        switch (dir) {
            case NORTH: to = tile.north; break;
            case EAST:  to = tile.east; break;
            case WEST:  to = tile.west; break;
            case SOUTH: to = tile.south; break;
        }
        moveAvatarToTile(ent, to, dir);
    }

    public void centerOnEntity(DfSquareMapEntity ent) {
        centerOnEntity(ent, true);
    }

    public void centerOnEntity(DfSquareMapEntity ent, boolean playAnimation) {
        updateVisible();
        if (ent == null) {
            return;
        }

        centerOnTile(ent.getTile(), (int)ent.getX(), (int)ent.getY(), 
            ent.posOffset, playAnimation);
    }
    
    public void centerOnTile(DfSquareTile tile, int centerX, int centerY, int posOffset) {
        centerOnTile(tile, centerX, centerY, posOffset, true);
    }

    public void centerOnTile(DfSquareTile tile, int centerX, int centerY, int posOffset, boolean playAnimation) {
        if (tile == null)
            return;
        
        if (playAnimation == true) {
            playMoveAnimation(
                -1 * ((int)tile.getX() - centerX + posOffset),
                -1 * ((int)tile.getY() - centerY + posOffset)
            );
        }
        else {
            xOffset.set(-1 * ((int)tile.getX() - centerX + posOffset)); // - tile.defSize/2));
            yOffset.set(-1 * ((int)tile.getY() - centerY + posOffset)); // - tile.defSize/2));
        }
    }
    
    public int getDistance(DfSquareTile tile, DfSquareTile tile2) {
        // Just a subtraction of x,y
        // How far are these two apart just using their coordinates?
        int dist = 0;

        int xdist = Math.abs(tile.mapX - tile2.mapX);
        int ydist = Math.abs(tile.mapY - tile2.mapY);

        if (xdist > ydist) {
            dist = xdist + ((ydist+1)/2);
        }
        else {
            dist = ydist + ((xdist+1)/2);
        }
       
        return dist;
    }

    public int findNextStepDir(DfSquareTile from, DfSquareTile to) {
        // Based on their coordinates, what's the next step in moving
        // from the "from" tile to the "to" tile?

        boolean s = true;
        boolean e = true;
        if (from.mapX > to.mapX) {
            e = false;
        }
        if (from.mapY > to.mapY) {
            s = false;
        }

        // If they're equal, just go straight in one or the other dir.
        if (from.mapY == to.mapY) {
            if (e == true) {
                return Constants.Dir.EAST.val();
            }
            else {
                return Constants.Dir.WEST.val();
            }
        }
        else if (from.mapX == to.mapX) {
            if (s == true) {
                return Constants.Dir.SOUTH.val();
            }
            else {
                return Constants.Dir.NORTH.val();
            }
        }

        // Otherwise, whichever is longer is the right dir
        /*if (Math.abs(from.mapX - to.mapX) > Math.abs(from.mapY - to.mapY)) {
            // x distance is longer so use it
            if (e == true) {
                return Constants.Dir.EAST.val();
            }
            else {
                return Constants.Dir.WEST.val();
            }
        }
        else {
            if (s == true) {
                return Constants.Dir.SOUTH.val();
            }
            else {
                return Constants.Dir.NORTH.val();
            }
        }*/

        // Otherwise, just choose at random.
        if (Utils.pass(50) == true) {
            if (e == true) {
                return Constants.Dir.EAST.val();
            }
            else {
                return Constants.Dir.WEST.val();
            }
        }
        else {
            if (s == true) {
                return Constants.Dir.SOUTH.val();
            }
            else {
                return Constants.Dir.NORTH.val();
            }
        }

        // Can't actually get here, SOUTH is the default value based on above.
        //return -1;
    }
}