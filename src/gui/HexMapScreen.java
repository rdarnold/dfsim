
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

// Gos - Game of Systems
public class HexMapScreen extends DfScreen {
    public static int topAreaHeight = 500;

    public static HexMap hexMap;

    public ArrayList<HexMapEntity> partyEntities;
    public ArrayList<HexMapEntity> monsEntities; 

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

    public HexMapScreen(BorderPane root, int wid, int hgt) {
        super(root, wid, hgt);
        thisScreen = this;
        createBuildingBlocks();
        createMainScene();
    }

    public void createBuildingBlocks() {
        hexMap = new HexMap();

        partyEntities = new ArrayList<HexMapEntity>();
        monsEntities = new ArrayList<HexMapEntity>();

        rightListViewStrings = new ArrayList<>();
        leftListViewStrings = new ArrayList<>();

        rightListProperty = new SimpleListProperty<>();
        leftListProperty = new SimpleListProperty<>();
        
        rightListProperty.set(FXCollections.observableArrayList(rightListViewStrings));
        leftListProperty.set(FXCollections.observableArrayList(leftListViewStrings));

        //InputStream stream = getClass().getClassLoader().getResourceAsStream("css/naru.main.css");
        //scene.getStylesheets().add(getClass().getResource("css/naru.main.css").toExternalForm());
        getStylesheets().add("css/dfsim.main.css");

        setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                processKeyRelease(keyEvent.getCode());
            }
        });
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
        hexMap.addToPane(topArea);
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
                        addMonHexMapEntity(mon);
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
                        addPartyMemberHexMapEntity(pers);
                    }
                }
            }
        });
    }

    public void processKeyRelease(KeyCode key) {
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
            }
        });
        bottomArea.getChildren().add(btn);
        GridPane.setRowIndex(btn, 0);
        GridPane.setColumnIndex(btn, 1);
        GridPane.setHalignment(btn, HPos.CENTER);
    }

    public void load() {
        reset();

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

    public void addParty(ArrayList<Person> party) {
        removeAllPartyMembers();
        for (Person person : party) {
            addPartyMemberHexMapEntity(person);
        }
    }

    public void addMons(ArrayList<DfMon> mons) {
        removeAllMons();
        for (DfMon mon : mons) {
            addMonHexMapEntity(mon);
        }
    }

    public void addPartyMemberHexMapEntity(Person person) {
        HexMapEntity ent = new HexMapEntity(person);

        int x = 10;
        int y = 10;
        
        if (partyEntities.size() > 0) {
            // If we have current party entities, find a random spot
            // to place this person based on where current party is.
            // Pick a random person, then a random direction
            int num = Utils.number(0, partyEntities.size()-1);
            HexMapEntity e = partyEntities.get(num);
            HexMapTile eHex = e.getHex();
            // Should not be null but check just in case...
            if (eHex != null) {
                // False means we do not allow a null tile
                HexMapTile nextHex = eHex.getTileInRandomDir(false);
                int dist = Utils.number(0, 2); // Within 3 spaces
                while (dist > 0) {
                    dist--;
                    nextHex = nextHex.getTileInRandomDir(false);
                    // No going back to our original tile.
                    while (nextHex == eHex) {
                        nextHex = nextHex.getTileInRandomDir(false);
                    }
                }
                // Should never be null...
                if (nextHex != null) {
                    x = nextHex.hexMapX;
                    y = nextHex.hexMapY;
                }
            }
        }
        else {
            // If not, just do it totally at random.
            x = Utils.number(1, hexMap.numX - 1);
            y = Utils.number(1, hexMap.numY - 1);
        }
        HexMapTile hex = hexMap.getAt(x, y);
        hex.attach(ent);

        partyEntities.add(ent);
        // Really we should be inserting these at an appropriate place
        ent.addToPane(topArea);
    }

    public void addMonHexMapEntity(DfMon mon) {
        HexMapEntity ent = new HexMapEntity(mon);
        monsEntities.add(ent);
        int x = Utils.number(1, hexMap.numX - 1);
        int y = Utils.number(1, hexMap.numY - 1);
        HexMapTile hex = hexMap.getAt(x, y);
        hex.attach(ent);
        ent.addToPane(topArea);
    }

    public void removeAllPartyMembers() {
        for (int i = partyEntities.size()-1; i >= 0; i--) {
            HexMapEntity ent = partyEntities.get(i);
            removeHexMapEntity(ent);
        }
        partyEntities.clear();
    }

    public void removeAllMons() {
        for (int i = monsEntities.size()-1; i >= 0; i--) {
            HexMapEntity ent = monsEntities.get(i);
            removeHexMapEntity(ent);
        }
        monsEntities.clear();
    }

    public void removeAllEntities() {
        removeAllPartyMembers();
        removeAllMons();
    }

    public void removeHexMapEntity(HexMapEntity ent) {
        ent.removeFromPane(topArea);
        if (ent.getHex() != null) {
            ent.getHex().detach();
        }
        if (ent == DfSim.sim.selectedHexMapEntity) {
            DfSim.sim.deselect();
        }

        monsEntities.remove(ent);
        partyEntities.remove(ent);
    }

    public void resetTurnMovs() {
        for (HexMapEntity ent : partyEntities) {
            ent.turnMovDone = false;
        }
        for (HexMapEntity ent : monsEntities) {
            ent.turnMovDone = false;
        }
    }

    public void resetTurnAtks() {
        for (HexMapEntity ent : partyEntities) {
            ent.turnAtkDone = false;
        }
        for (HexMapEntity ent : monsEntities) {
            ent.turnAtkDone = false;
        }
    }

    public void reset() {
        // remove the entities
        removeAllEntities();

        // regenerate the map
        hexMap.randomizeTerrain();
    }

    public void onClickHexMapEntity(HexMapEntity ent) {
        // Show the movable tiles
        // So firstly clear all movable tiles
        hexMap.clearCanMoveTo();

        if (ent.turnMovDone == true)
            return;

        // Now set the tiles that can be moved to
        if (ent.mon != null) {
            hexMap.setCanMoveTo(ent.getHex(), ent.mon.getMov());
        }
        else if (ent.partyMember != null) {
            hexMap.setCanMoveTo(ent.getHex(), ent.partyMember.getMov());
        }
    }
}