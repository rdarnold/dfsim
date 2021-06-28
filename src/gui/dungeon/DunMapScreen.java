
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

import javafx.scene.canvas.*;

import dfsim.*;

public class DunMapScreen extends DfScreen {
    //public static int wid;
    //public static int hgt;

    public static Dun dun;
    public static Dun getDun() { return dun; }

    private DunMapScreen thisScreen;

    //private StackPane stackPane;
    //private Pane mainArea;

    //public TextArea mainTextArea;

    public DunMapScreen(BorderPane root, int width, int height) {
        super(root, width, height);
        thisScreen = this;
        createButtons();
        getMainPane().getChildren().add(canvas);
    }

    public void createButtons() {
        // Throw on some buttons:
        // Party
        // Spell
        // Item
        // Talk (you can talk to different party members with your party leader)

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
    }

    @Override
    public void processKeyPress(KeyCode key) {
        if (DfSim.noInput == true) {
            return;
        }
        /*switch (key) {
            case RIGHT:
                dun.onRightArrow();
                break;
            case LEFT:
                dun.onLeftArrow();
                break;
            case UP:
                dun.onUpArrow();
                break;
            case DOWN:
                dun.onDownArrow();
                break;
        }*/
    }

    @Override
    public void processKeyRelease(KeyCode key) {
        super.processKeyRelease(key);
        if (DfSim.noInput == true) {
            return;
        }
        switch (key) {
            case ENTER: DfSim.showLandMapScreen();  break;
            case SPACE: dun.onSpace();              break;
            case F:     dun.onF();                  break;
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

    public void setupDun(LandMapTile tile, Constants.Dir dir) {
        Dun newDun;
        if (tile == null) {
            newDun = new Dun();
            newDun.start(dir);
        }
        else if (tile.getDun() == null) {
            // This should't happen, if we are trying to enter a town
            // that means the tile should already at least have the data
            // structure created.
            newDun = new Dun();
            tile.setDun(newDun);
            newDun = tile.getDun();
            newDun.start(dir);
        }
        else {
            // This is the typical case / all cases unless we use hotkeys
            // to get to a random town
            newDun = tile.getDun();
            if (newDun.isInitialized() == false) {
                newDun.regen();
            }
            newDun.start(dir);
        }
        
        /*if (dun != null) {
            dun.removeFromPane();
        }*/
        dun = newDun;
        canvas.setSquareMap(dun);
        //dun.addToPane(getMainPane());
    }
    
    public void updateInput() {
        if (DfSim.noInput == true) {
            return;
        }
        if (isArrowRightPressed() == true || isDPressed() == true) {
            dun.onRightArrow();
        }
        if (isArrowLeftPressed() == true || isAPressed() == true) {
            dun.onLeftArrow();
        }
        if (isArrowUpPressed() == true || isWPressed() == true) {
            dun.onUpArrow();
        }
        if (isArrowDownPressed() == true || isSPressed() == true) {
            dun.onDownArrow();
        }
    }
    
    @Override
    public void updateOneFrame() {
        super.updateOneFrame();
        if (isActive() == false) {
            return;
        }

        /*if (canvas != null) {
            canvas.updateOneFrame();
        }*/

        if (dun != null) {
            dun.updateOneFrame();
        }

        updateInput();
        draw();
    }
    
    @Override
    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (dun != null) {
            dun.draw(gc);
        }

        canvas.draw();
    }
}