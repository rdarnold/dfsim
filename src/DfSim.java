
package dfsim;

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
import javafx.stage.Stage;
import javafx.stage.Modality;
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
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import dfsim.gui.*;

public class DfSim extends Application {
    public static int width = 650;
    public static int height = 750;
    public static Stage stage;

    // Some statics that people can access from wherever so we don't have to
    // pass them around.
    public static DfSim dfsim;
    public static DfSimulator sim;

    public static HexMapScreen hexMapScreen;
    public static LandMapScreen landMapScreen;
    public static DunMapScreen dunMapScreen;
    public static TownMapScreen townMapScreen;
    public static PartyScreen partyScreen;

    public static DfScreen activeScene;
    public static DfScreen priorScene;

    // During screen transitions and other things, we don't allow any operator
    // input.
    public static boolean noInput = false;

    @Override
    public void start(Stage primaryStage) {
        dfsim = this;
        Utils.init();

        // We do this first to load all our XML data and have it ready.
        Data.load();

        sim = new DfSimulator();

        hexMapScreen = new HexMapScreen(new BorderPane(), width, height);
        hexMapScreen.load();
        //activeScene = mainScene;

        landMapScreen = new LandMapScreen(new BorderPane(), width, height);
        dunMapScreen = new DunMapScreen(new BorderPane(), width, height);
        townMapScreen = new TownMapScreen(new BorderPane(), width, height);
        partyScreen = new PartyScreen(new BorderPane(), width, height);

        sim.postInit();

        stage = primaryStage;
        stage.setTitle("DfSim v0.1");
        landMapScreen.landMap.start();
        showLandMapScreen();
        stage.show();

        //sim.setupRandomEncounter();
        sim.start();
    }

    public static void showScreen(DfScreen screen) {
        showScreen(screen, true);
    }

    // All screen showing must pass through this function
    protected static FadeTransition fadeOut;
    protected static FadeTransition fadeIn;
    protected static void createFadeIn(DfScreen screen) {
        fadeIn = new FadeTransition(Duration.millis(1000), screen.getRoot());
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                noInput = false;
            }
        });
    }
    public static void showScreen(DfScreen screen, boolean savePrior) {
        noInput = true;
        if (activeScene != null) {
            if (savePrior) {
                priorScene = activeScene;
            }
            activeScene.setActive(false);
        }
        else {
            activeScene = screen;
            activeScene.setActive(true);
            activeScene.getRoot().setOpacity(0.0);
            createFadeIn(screen);
            stage.setScene(activeScene);
            fadeIn.play();
            return;
        }

        DfScreen prevScreen = activeScene;
        
        // Set up the new active scene
        activeScene = screen;
        activeScene.setActive(true);
        activeScene.getRoot().setOpacity(0.0);

        // Create fade-in for new scene
        createFadeIn(screen);

        // Fade out from current scene
        fadeOut = new FadeTransition(Duration.millis(1000), prevScreen.getRoot());
        //ft.setFromValue(0.0);
        fadeOut.setToValue(0.0);
        fadeOut.play();

        // When fade out is finished, fade in the new screen
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.setScene(activeScene);
                fadeIn.play();
            }
        });
    }

    public static void showLastScreen() {
        if (priorScene == null ||activeScene == priorScene)
            return;
        showScreen(priorScene, false);
    }

    public static void showHexMapScreen() {
        showScreen(hexMapScreen);
    }

    public static void showLandMapScreen() {
        showScreen(landMapScreen);
    }

    public static void showDunMapScreen() {
        showDunMapScreen(null, Constants.Dir.fromInt(Utils.number(0, 3)));
    }

    public static void showDunMapScreen(LandMapTile tile, Constants.Dir dir) {
        dunMapScreen.setupDun(tile, dir);
        //dunMapScreen.dunMap.regen();
        //dunMapScreen.dunMap.start();
        showScreen(dunMapScreen);
    }

    public static void showTownMapScreen() {
        showTownMapScreen(null, Constants.Dir.fromInt(Utils.number(0, 3)));
    }

    // We want to know what dir we entered the town from so we can put
    // our person on the correct side.
    public static void showTownMapScreen(LandMapTile tile, Constants.Dir dir) {
        townMapScreen.setupTown(tile, dir);
        showScreen(townMapScreen);
    }

    public static void showPartyScreen() {
        partyScreen.update();
        showScreen(partyScreen);
    }

    public static void main(String[] args) {
        Constants.init();
        Application.launch(args);
    }

    // This is not being used but I'm leaving it here in case I need it
    public void showModalWindow() {
        Stage dialog = new Stage();
        dialog.initOwner(stage);
        dialog.initModality(Modality.WINDOW_MODAL); 
        dialog.showAndWait();
    }
}