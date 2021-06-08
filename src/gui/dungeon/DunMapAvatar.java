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

public class DunMapAvatar extends DunMapEntity {
    
    public DunMapAvatar(DunMap theMap) {
        super(theMap, EntityType.Party);
        init();
    }

    /*@Override
    protected void handleMouseEnter(Object objEnt, MouseEvent event) {
        if (objEnt == null) 
            return;
        DunMapAvatar ent = (DunMapAvatar)objEnt;
        getMap().onMouseEnterAvatar(ent);
    }

    @Override
    protected void handleClick(Object objEnt, MouseEvent event) {
        if (objEnt == null) 
            return;
        DunMapAvatar ent = (DunMapAvatar)objEnt;
        if (event.getButton() == MouseButton.PRIMARY) {
            getMap().onLeftClickAvatar(ent);
        }
        else {
            getMap().onRightClickAvatar(ent);
        }
    }*/

    private void init() {
    }
}