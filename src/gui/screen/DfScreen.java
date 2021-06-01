
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

public class DfScreen extends Scene {

    public int wid;
    public int hgt;

    public BorderPane overallRoot;
    
    private StackPane stackPane;
    private Pane mainPane;
    private Pane uiPane;

    private TextArea infoArea;

    private boolean m_bIsActive = false;
    public boolean isActive() {return m_bIsActive; }
    public void setActive(boolean act) { m_bIsActive = act; }

    public Pane getMainPane() { return mainPane; }
    public Pane getUIPane() { return uiPane; }

    public DfScreen(BorderPane root, int width, int height) {
        super(root, width, height, Color.BLACK);
        wid = width;
        hgt = height;
        overallRoot = root;
        init();
    }

    private void init() {
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

        // Need to use event filters because setOnKeyPressed apparently
        // does not pick up space bar presses ????
        addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            processKeyRelease(keyEvent.getCode());
            keyEvent.consume();
        });
        addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            processKeyPress(keyEvent.getCode());
            keyEvent.consume();
        });
        
        stackPane = new StackPane();      
        mainPane = new Pane();
        uiPane = new Pane();
        infoArea = new TextArea();

        // This makes the transparent parts of the UI pane transparent to mouse clicks.
        uiPane.setPickOnBounds(false);

        infoArea.setWrapText(true);
        infoArea.setEditable(false);
        infoArea.setPickOnBounds(false);

        // This is cool, it's like a traditional console look
        infoArea.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Consolas;" +
            " -fx-highlight-fill: #00ff00;" + 
            " -fx-highlight-text-fill: #000000; " +
            " -fx-text-fill: #00ff00;");

        // But the green doesn't work well with a black background.
        /*infoArea.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Consolas;" +
            " -fx-highlight-fill: #00ff00;" + 
            " -fx-highlight-text-fill: #000000; " +
            " -fx-text-fill: #000000;");*/

        overallRoot.setCenter(stackPane);

        mainPane.setPrefWidth(wid);
        mainPane.setPrefHeight(hgt);

        stackPane.getChildren().add(mainPane);
        stackPane.getChildren().add(uiPane);

        uiPane.getChildren().add(infoArea);

        int infoAreaWid = (int)((double)Constants.BUTTON_WIDTH * 2);
        infoArea.setLayoutX(wid - infoAreaWid - 10);
        infoArea.setLayoutY(10);
        infoArea.setPrefWidth(infoAreaWid);
        infoArea.setPrefHeight(300);
        infoArea.setVisible(false);
        //infoArea.setFill(Color.rgb(200, 200, 200, 0.3));

        // Clip it so that it doesnt try to draw tens of thousands of nodes
        mainPane.setClip(new Rectangle(wid, hgt));
    }

    public void updateInfoText(String newInfo) {
        if (newInfo == null || newInfo.equals("")) {
            infoArea.setText("");
            infoArea.setVisible(false);
        }
        else {
            infoArea.setText(newInfo);
            infoArea.setVisible(true);
        }
    }

    protected void addToUIPane(Node node) {
        uiPane.getChildren().add(node);
    }

    // Meant to be overridden
    public void processKeyPress(KeyCode key) { }

    // Meant to be overridden
    public void processKeyRelease(KeyCode key) { }
}