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

public class DunMapMon extends DunMapEntity {

    public DunMapMon(DunMap theMap) {
        super(theMap, EntityType.Mon);
        init();
    }

    private void init() {
    }

    @Override
    protected void handleMouseEnter(Object objEnt, MouseEvent event) {
        if (objEnt == null) 
            return;
        DunMapMon ent = (DunMapMon)objEnt;
        getMap().onMouseEnterMon(ent);
    }

    @Override
    protected void handleClick(Object objEnt, MouseEvent event) {
        if (objEnt == null) 
            return;
        DunMapMon ent = (DunMapMon)objEnt;
        if (event.getButton() == MouseButton.PRIMARY) {
            getMap().onLeftClickMon(ent);
        }
        else {
            getMap().onRightClickMon(ent);
        }
    }

    public boolean canMoveToTile(DunMapTile tile) {
        if (tile == null || tile.canBeMovedTo() == false) {
            return false;
        }
        if (tile.contains != null) {
            // Party is fine since that just makes us strike.
            // Mon is not fine.
            if (tile.contains.getType() == DunMapEntity.EntityType.Mon)
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
        DunMapTile tile = getTile();
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