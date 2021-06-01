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

import dfsim.*;
import dfsim.gui.*;

public class TownMapPerson extends TownMapEntity {

    // Has this person been discovered or not?  If so,
    // we can hover over them and see their stats and name and stuff.
    // Then again is this just kind of annoying?  Maybe we don't need to
    // discover them.  Or maybe we don't know all about them until
    // we talk to them, then things like personality traits are revealed.
    public boolean discovered = false;

    public TownMapPerson(TownMap theMap) {
        super(theMap, EntityType.Person);
        init();
    }

    @Override
    protected void handleMouseEnter(Object objEnt, MouseEvent event) {
        if (objEnt == null) 
            return;
        TownMapPerson ent = (TownMapPerson)objEnt;
        getMap().onMouseEnterPerson(ent);
    }

    @Override
    protected void handleClick(Object objEnt, MouseEvent event) {
        if (objEnt == null) 
            return;
        TownMapPerson ent = (TownMapPerson)objEnt;
        if (event.getButton() == MouseButton.PRIMARY) {
            getMap().onLeftClickPerson(ent);
        }
        else {
            getMap().onRightClickPerson(ent);
        }
    }

    private void init() {

    }

    public boolean canMoveToTile(TownMapTile tile) {
        if (tile == null || tile.canBeMovedTo() == false) {
            return false;
        }
        if (tile.contains != null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canMove() {
        if (super.canMove() == false) {
            return false;
        }

        // In addition to base class, we can't move into another mon spot
        TownMapTile tile = getTile();
        if (canMoveToTile(tile.n()) == true)
            return true;
        if (canMoveToTile(tile.e()) == true)
            return true;
        if (canMoveToTile(tile.w()) == true)
            return true;
        if (canMoveToTile(tile.s()) == true)
            return true;
        return false;
    }
}