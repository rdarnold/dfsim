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

// For a smooth movement of the screen
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import dfsim.*;
import dfsim.gui.*;

public abstract class TownMapEntity extends DfSquareMapEntity {

    public static int defSize = TownMapTile.defSize-4;
    public static int posOffset = (TownMapTile.defSize - TownMapEntity.defSize) / 2;
    
    // Town stuff
    public Person person;
    protected TownMap getMap() { return (TownMap)map; }

    public void setTile(TownMapTile newTile) { super.setTile(newTile); }
    public TownMapTile getTile() { return (TownMapTile)tile; }
    public TownMapTile getPrevTile() { return (TownMapTile)prevTile; }

    public TownMapEntity(TownMap theMap) {
        super(theMap);
        init();
    }

    public TownMapEntity(TownMap theMap, EntityType theType) {
        super(theMap, theType);
        init();
    }

    private void init() {
        setStroke(Color.BLACK);
        setStrokeWidth(1);
    }

    // For hovering over and such
    public String printInfo() {
        if (person == null) {
            return "";
        }
        return person.toStringTownInfo();
    }
}