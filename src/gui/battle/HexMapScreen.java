
package dfsim.gui;

import java.io.*;
import java.util.*;
import javafx.application.Application;
import javafx.event.*;
import javafx.beans.value.*;
import javafx.scene.input.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.collections.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.scene.web.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;

import dfsim.*;

// Gos - Game of Systems
public class HexMapScreen extends DfScreen {
    public static int topAreaHeight = 500;

    public static HexMap hexMap;

    private HexMapScreen thisScreen;

    private StackPane centerArea;
    private Pane topArea;
    private VBox leftArea;
    private VBox rightArea;
    private GridPane bottomArea;

    public ListView<String> rightListView;
    public ListView<String> leftListView;

    protected List<String> rightListViewStrings;
    protected List<String> leftListViewStrings;
    protected ListProperty<String> leftListProperty = new SimpleListProperty<>();
    protected ListProperty<String> rightListProperty = new SimpleListProperty<>();

    private MovableButton actionBtn;

    public TextArea mainTextArea;

    private DfCanvas canvas;
    public DfCanvas getCanvas() { return canvas; };

    public HexMapScreen(BorderPane root, int wid, int hgt) {
        super(root, wid, hgt);
        canvas = new DfCanvas(wid, hgt);
        thisScreen = this;
        createBuildingBlocks();
        createMainScene();
    }

    public void createBuildingBlocks() {
        hexMap = new HexMap(this);

        rightListViewStrings = new ArrayList<>();
        leftListViewStrings = new ArrayList<>();

        rightListProperty = new SimpleListProperty<>();
        leftListProperty = new SimpleListProperty<>();
        
        rightListProperty.set(FXCollections.observableArrayList(rightListViewStrings));
        leftListProperty.set(FXCollections.observableArrayList(leftListViewStrings));

        //InputStream stream = getClass().getClassLoader().getResourceAsStream("css/naru.main.css");
        //scene.getStylesheets().add(getClass().getResource("css/naru.main.css").toExternalForm());
        getStylesheets().add("css/dfsim.main.css");

        /*setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                processKeyRelease(keyEvent.getCode());
            }
        });*/
    }

    public void setBorders() {
        leftArea.setStyle("-fx-border-color: darkgray;");
        bottomArea.setStyle("-fx-border-color: darkgray;");
        topArea.setStyle("-fx-border-color: darkgray;");
    }

    public void createMainScene() {
        /*currentTaskText = new Label();
        currentTaskText.setWrapText(true);
        currentExerciseText = new Label();
        currentExerciseText.setWrapText(true);
        currentExerciseName = new Label();
        //currentExerciseName.setWrapText(true);
        discoveryPointText = new Label();
        //discoveryPointText.textProperty().bind(Player.discoveryPointsProperty().asString(
          //  "Discovery Points: %d"
        //));
        //discoveryPointText.setWrapText(true);*/

        createTopArea();
        createCenterArea();
        createLeftArea();
        createRightArea();
        createBottomArea();

        //bottomArea.setFillWidth(true);
        overallRoot.setLeft(leftArea);
        overallRoot.setRight(rightArea);
        overallRoot.setTop(topArea);
        overallRoot.setBottom(bottomArea);
        overallRoot.setCenter(centerArea);

        setBorders();
    }

    public void createTopArea() {
        topArea = new Pane();
        //topArea.prefWidthProperty().bind(overallRoot.widthProperty());
        topArea.setPrefHeight(topAreaHeight);
        //topArea.setPadding(new Insets(10, 10, 10, 10));
        //topArea.setAlignment(Pos.CENTER);
        //topArea.setSpacing(10);
        topArea.getChildren().add(canvas);
        hexMap.addToCanvas(canvas);
        //hexMap.addToPane(topArea);
    }

    public void appendText(String text) {
        mainTextArea.appendText(text);
        mainTextArea.appendText(System.getProperty("line.separator"));
    }

    private void createCenterArea() {
        centerArea = new StackPane();        

        //taskTextVBox.setMaxWidth(Double.MAX_VALUE);
        //HBox.setHgrow(taskTextVBox, Priority.ALWAYS);

        // Transient because this is just a container for the top
        // and bottom parts of the system area
        VBox centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        //centerBox.setPadding(new Insets(10, 10, 10, 10));
        //centerBox.setSpacing(10);
        //centerBox.getChildren().addAll(taskTextVBox, sysPane);
        //centerBox.getChildren().add(sysPane);
        centerArea.getChildren().addAll(centerBox);
        centerBox.setStyle("-fx-border-color: darkgray;");
        //overallRoot.getChildren().addAll(middleStackPane);
        mainTextArea = new TextArea();
        centerBox.getChildren().add(mainTextArea);

        // Bind so that the text area expands with the box
        mainTextArea.prefWidthProperty().bind(centerBox.prefWidthProperty());
        mainTextArea.prefHeightProperty().bind(centerBox.prefHeightProperty());
        mainTextArea.prefWidthProperty().bind(centerBox.widthProperty());
        mainTextArea.prefHeightProperty().bind(centerBox.heightProperty());
    }

    public void createLeftArea() {
        leftArea = new VBox();
        leftArea.setPrefWidth(DfSim.width/3);
        leftArea.setAlignment(Pos.CENTER);
        //leftArea.setPadding(new Insets(10, 10, 10, 10));
        //leftArea.setSpacing(10);
        leftListView = new ListView<String>();
        leftArea.getChildren().add(leftListView);
        leftListView.itemsProperty().bind(leftListProperty);
        
        // Bind so that the list expands with the box
        leftListView.prefWidthProperty().bind(leftArea.prefWidthProperty());
        leftListView.prefHeightProperty().bind(leftArea.prefHeightProperty());
        leftListView.prefWidthProperty().bind(leftArea.widthProperty());
        leftListView.prefHeightProperty().bind(leftArea.heightProperty());

        leftListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    String str = leftListView.getSelectionModel().getSelectedItem();
                    DfMon mon = Data.getDfMonByName(str);
                    if (mon != null) {
                        hexMap.addMonHexMapEntity(mon);
                    }
                }
            }
        });
    }

    public void createRightArea() {
        rightArea = new VBox();
        rightArea.setPrefWidth(DfSim.width/3);
        rightArea.setAlignment(Pos.CENTER);
        //rightArea.setPadding(new Insets(10, 10, 10, 10));
        //rightArea.setSpacing(10);
        rightListView = new ListView<String>();
        rightArea.getChildren().add(rightListView);
        rightListView.itemsProperty().bind(rightListProperty);

        // Bind so that the list expands with the box
        rightListView.prefWidthProperty().bind(rightArea.prefWidthProperty());
        rightListView.prefHeightProperty().bind(rightArea.prefHeightProperty());
        rightListView.prefWidthProperty().bind(rightArea.widthProperty());
        rightListView.prefHeightProperty().bind(rightArea.heightProperty());

        rightListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    String str = rightListView.getSelectionModel().getSelectedItem();
                    Person pers = Data.getPersonByName(str);
                    if (pers != null) {
                        hexMap.addPartyMemberHexMapEntity(pers);
                    }
                }
            }
        });
    }

    public void processKeyRelease(KeyCode key) {
        super.processKeyRelease(key);
        if (DfSim.noInput == true) {
            return;
        }
        switch (key) {
            case ENTER:
                DfSim.showLastScreen();
                break;
        }
    }

    public void showDefaultAreas() {
        overallRoot.setTop(topArea);
        overallRoot.setLeft(leftArea);
        overallRoot.setRight(rightArea);
        overallRoot.setBottom(bottomArea);
    }

    private void moveText(Text text, double x, double y)
    {
        text.setLayoutX(x);
        text.setLayoutY(y);
    }

    private void setupBottomGridPane() {
        bottomArea = new GridPane();
        bottomArea.setPrefHeight(30);
        bottomArea.setPadding(new Insets(10));
        //bottomArea.setSpacing(10);
        bottomArea.setAlignment(Pos.CENTER);

        ColumnConstraints col;
        
        col = new ColumnConstraints();
        col.setPercentWidth(25);
        bottomArea.getColumnConstraints().add(col);

        col = new ColumnConstraints();
        col.setPercentWidth(50);
        bottomArea.getColumnConstraints().add(col);

        col = new ColumnConstraints();
        col.setPercentWidth(25);
        bottomArea.getColumnConstraints().add(col);

        //bottomArea.setPrefSize(WINDOW_WIDTH, 30); // Default width and height
        //bottomArea.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    }

    private void createBottomArea() {
        
        setupBottomGridPane();
        
        /*turnText = new Label();
        turnText.setMinWidth(Constants.BUTTON_WIDTH / 2);
        turnText.setAlignment(Pos.CENTER);
        bottomArea.getChildren().add(turnText);
        GridPane.setRowIndex(turnText, 0);
        GridPane.setColumnIndex(turnText, 2);
        GridPane.setHalignment(turnText, HPos.RIGHT);*/

        MovableButton btn;
        Tooltip toolTip;

        actionBtn = new MovableButton("Action");
        btn = actionBtn;
        String str = "Do it";
        Utils.addToolTip(btn, str);
        //btn.moveTo(0, 200);
        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Must check if input allowed on all input methods on this screen
                if (hexMap.inputAllowed() == false) {
                    return;
                }
            }
        });
        bottomArea.getChildren().add(btn);
        GridPane.setRowIndex(btn, 0);
        GridPane.setColumnIndex(btn, 1);
        GridPane.setHalignment(btn, HPos.CENTER);
    }

    public void load() {
        hexMap.reset();

        // Now populate our lists with the stuff
        for (Person person : Data.personList) {
            rightListViewStrings.add(person.getName());
        }
        
        for (DfMon mon : Data.dfMonList) {
            leftListViewStrings.add(mon.name);
        }

        rightListProperty.set(FXCollections.observableArrayList(rightListViewStrings));
        leftListProperty.set(FXCollections.observableArrayList(leftListViewStrings));
    }
    
    public void updateOneFrame() {
        if (isActive() == false) {
            return;
        }

        if (canvas != null) {
            canvas.updateOneFrame();
        }
    }
}