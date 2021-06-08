
package dfsim.gui;

import java.io.*;
import java.util.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.ListProperty;

import dfsim.*;
import dfsim.gui.*;

public class ShopScreen extends DfScreen {
    public static int wid;
    public static int hgt;

    //public static Town town;

    private ShopScreen thisScreen;

    private StackPane stackPane;
    private Pane mainArea;

    public TextArea mainTextArea;

    public ShopScreen(BorderPane root, int width, int height) {
        super(root, width, height);
        wid = width;
        hgt = height;
        thisScreen = this;
        createBuildingBlocks();
        createScene();
    }

    public void createBuildingBlocks() {

        //InputStream stream = getClass().getClassLoader().getResourceAsStream("css/naru.main.css");
        //scene.getStylesheets().add(getClass().getResource("css/naru.main.css").toExternalForm());
        getStylesheets().add("css/dfsim.main.css");

        /*setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                processKeyRelease(keyEvent.getCode());
            }
        });
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                processKeyPress(keyEvent.getCode());
            }
        });*/
    }

    public void createScene() {

        //stackPane = new StackPane();      
        mainArea = new Pane();
        overallRoot.setCenter(mainArea);
        mainArea.setPrefWidth(wid);
        mainArea.setPrefHeight(hgt);

        //town.addToPane(mainArea);

        // Clip it so that it doesnt try to draw tens of thousands of nodes
        mainArea.setClip(new Rectangle(wid, hgt));

        // Kinda weird to do this here.
        //DfSim.sim.avatar.addToPane(mainArea);
    }

    public void processKeyPress(KeyCode key) {
        if (DfSim.noInput == true) {
            return;
        }
        switch (key) {
            case RIGHT:
                //town.onRightArrow();
                break;
            case LEFT:
                //town.onLeftArrow();
                break;
            case UP:
                //town.onUpArrow();
                break;
            case DOWN:
                //town.onDownArrow();
                break;
        }
    }

    public void processKeyRelease(KeyCode key) {
        super.processKeyRelease(key);
        if (DfSim.noInput == true) {
            return;
        }
        switch (key) {
            case ENTER:
                //DfSim.showLandMapScreen();
                break;
            /*case RIGHT:
                DfSim.sim.onLandMapRightArrow();
                break;
            case LEFT:
                DfSim.sim.onLandMapLeftArrow();
                break;
            case UP:
                DfSim.sim.onLandMapUpArrow();
                break;
            case DOWN:
                DfSim.sim.onLandMapDownArrow();
                break;*/
        }
    }
}