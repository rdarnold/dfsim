
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

public class TownMapScreen extends DfScreen {
    //public static int wid;
    //public static int hgt;

    public static Town town;
    public static Town getTown() { return town; }

    private TownMapScreen thisScreen;

    //private StackPane stackPane;
    //private Pane mainArea;

    //public TextArea mainTextArea;

    public TownMapScreen(BorderPane root, int width, int height) {
        super(root, width, height);
        //wid = width;
        //hgt = height;
        thisScreen = this;
        createButtons();
    }

    public void createButtons() {
        // Throw on some buttons:
        // Party
        // Spell
        // Item
        // Talk (you can talk to different party members with your party leader)
        // Data - Save/Load/Quit

        // Needs to have a function on base class that does this.
        MovableButton btn = new MovableButton("Party");
        super.addToUIPane(btn);
        int x = 10;
        int y = 10;
        int yAdd = 35;
        btn.moveTo(x, y);
        String str = "Access party information";
        Utils.addToolTip(btn, str);
        //btn.moveTo(0, 200);
        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DfSim.showPartyScreen();
            }
        });

        btn = new MovableButton("Spell");
        super.addToUIPane(btn);
        y+= yAdd;
        btn.moveTo(x, y);

        btn = new MovableButton("Item");
        super.addToUIPane(btn);
        y+= yAdd;
        btn.moveTo(x, y);

        btn = new MovableButton("Talk");
        super.addToUIPane(btn);
        y+= yAdd;
        btn.moveTo(x, y);
        
        btn = new MovableButton("Data");
        super.addToUIPane(btn);
        y+= yAdd;
        btn.moveTo(x, y);
    }

    @Override
    public void processKeyPress(KeyCode key) {
        if (DfSim.noInput == true) {
            return;
        }
        switch (key) {
            case RIGHT: town.onRightArrow(); break;
            case LEFT: town.onLeftArrow(); break;
            case UP: town.onUpArrow(); break;
            case DOWN: town.onDownArrow(); break;
            case D: town.onD(); break;
            case A: town.onA(); break;
            case W: town.onW(); break;
            case S: town.onS(); break;
        }
    }

    @Override
    public void processKeyRelease(KeyCode key) {
        if (DfSim.noInput == true) {
            return;
        }
        switch (key) {
            case ENTER:  DfSim.showLandMapScreen(); break;
            case SPACE: town.onSpace(); break;
            case F: town.onF(); break;
        }
    }

    public void setupTown(LandMapTile tile, Constants.Dir dir) {
        Town newTown;
        if (tile == null) {
            newTown = new Town();
            newTown.start(dir);
        }
        else if (tile.getTown() == null) {
            // This should't happen, if we are trying to enter a town
            // that means the tile should already at least have the data
            // structure created.
            newTown = new Town();
            tile.setTown(newTown);
            newTown = tile.getTown();
            newTown.start(dir);
        }
        else {
            // This is the typical case / all cases unless we use hotkeys
            // to get to a random town
            newTown = tile.getTown();
            if (newTown.isInitialized() == false) {
                newTown.regen();
            }
            newTown.start(dir);
        }
        
        if (town != null) {
            town.removeFromPane();
        }
        town = newTown;
        town.addToPane(getMainPane());
    }
}