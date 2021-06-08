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

public class LandMapEntity extends DfSquareMapEntity {

    //public static int defSize = LandMapTile.defSize-4;
    //public static int posOffset = (LandMapTile.defSize - LandMapEntity.defSize) / 2;

    public void setTile(LandMapTile newTile) { super.setTile(newTile); }
    public LandMapTile getTile() { return (LandMapTile)tile; }
    public LandMapTile getPrevTile() { return (LandMapTile)prevTile; }
    
    LandMap getMap() { return (LandMap)map; }

    //public int mapX = 0;
    //public int mapY = 0;

    //public Constants.Dir lastMoveDir = Constants.Dir.NORTH;

    public LandMapEntity(LandMap theMap) {
        super(theMap);
        defSize = LandMapTile.defSize;
        posOffset = 0;
        init();
    }

    // Deprecated...
    /*@Override
    protected void handleMouseEnter(Object objTile, MouseEvent event) {
        if (objTile == null) 
            return;
        LandMapEntity tile = (LandMapEntity)objTile;
        getMap().onMouseEnterAvatar(tile);
    }

    @Override
    protected void handleClick(Object objTile, MouseEvent event) {
        if (objTile == null) 
            return;
        LandMapEntity tile = (LandMapEntity)objTile;
        if (event.getButton() == MouseButton.PRIMARY) {
            getMap().onLeftClickAvatar(tile);
        }
        else {
            getMap().onRightClickAvatar(tile);
        }
    }*/

    private void init() {
        /*shapeText.setUserData(this);
        getSelectedCircle().setUserData(this);
        overlay.setUserData(this);
        getSelectedPolygon().setUserData(this);*/
        /*setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleClick(event.getSource(), event);
            }
        });*/
        /*setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMouseEnter(event.getSource(), event);
            }
        });

        // The text sits on top of the shape and will intercept the mouseclick
        // so we want clicking on the text to work the same way as clicking
        // on the entity
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

       //makeShape(4);
        setWidth(defSize);
        setStroke(Color.BLACK);
        setStrokeWidth(1);

        setWidth(defSize);
        setHeight(defSize);
        //setArcWidth(20);
        //setArcHeight(20);

        setFill(Color.AQUA);
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

    /*public void moveTo(double x, double y) {
        //setTranslateX(x);
        //setTranslateY(y); 
        setX(x);
        setY(y);
    }

    public void moveTo(LandMapTile tile) {
        //setTranslateX(tile.getTranslateX()+2);
        //setTranslateY(tile.getTranslateY()+2); 
        setX(tile.getX() + tile.getTranslateX() + posOffset);
        setY(tile.getY() + tile.getTranslateY() + posOffset); 
    }

    public void addToPane(Pane node) {
        node.getChildren().add(this);
    }

    public void removeFromPane(Pane node) {
        node.getChildren().remove(this);
    }*/

}