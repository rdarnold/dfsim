
package dfsim.gui;

import java.io.*;
import java.util.*;
import java.util.ArrayList;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
/*import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;*/
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
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

public class LandMapScreen extends DfScreen {
    //public static int wid;
    //public static int hgt;

    public static LandMap landMap;

    private LandMapScreen thisScreen;

    //private StackPane stackPane;
    //private Pane mainArea;
    //private Pane uiPane;

    private MovableButton actionBtn;

    private DfCanvas canvas;

    //public TextArea mainTextArea;

    public LandMapScreen(BorderPane root, int width, int height) {
        super(root, width, height);
        //wid = width;
        //hgt = height;
        thisScreen = this;
        landMap = new LandMap();
        canvas = new DfCanvas(width, height);
        getMainPane().getChildren().add(canvas);
        landMap.addToCanvas(canvas);
        //landMap.addToPane(getMainPane());
        createButtons();
    }

    public void createButtons() {
        // Throw on some buttons:
        // Party
        // Spell
        // Item
        // Talk (you can talk to different party members with your party leader)
        // Status (where are all your parties, list all your characters, etc.)
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

        btn = new MovableButton("Status");
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
            case RIGHT:
                landMap.onRightArrow();
                break;
            case LEFT:
                landMap.onLeftArrow();
                break;
            case UP:
                landMap.onUpArrow();
                break;
            case DOWN:
                landMap.onDownArrow();
                break;
        }
    }

    @Override
    public void processKeyRelease(KeyCode key) {
        if (DfSim.noInput == true) {
            return;
        }
        switch (key) {
            case ENTER:
                DfSim.showTownMapScreen();
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

    public void updateOneFrame() {
        if (canvas != null) {
            canvas.updateOneFrame();
        }
    }
}