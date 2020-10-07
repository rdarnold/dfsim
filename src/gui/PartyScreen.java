
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
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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

public class PartyScreen extends DfScreen {

    private PartyScreen thisScreen;
    private ArrayList<MovableToggleButton> memberList;
    CharCard card;
    MovableButton backButton;

    public PartyScreen(BorderPane root, int width, int height) {
        super(root, width, height);
        thisScreen = this;
        init();
    }

    public void init() {
        memberList = new ArrayList<MovableToggleButton>();
        
        getRoot().setStyle("-fx-background-colour: #000000;");
        getUIPane().setStyle("-fx-background-colour: #000000;");
        getMainPane().setStyle("-fx-background-colour: #000000;"); 
        //setStyle("-fx-background-colour: #000000;");

        // So maybe on the left is the party list, on the right
        // is the card of the currently selected effer.
        card = new CharCard();
        card.addToPane(getUIPane());
        card.moveTo((Constants.BUTTON_WIDTH * 1.5) + 20, 10);

        // Add a back button at least - maybe we want to move this later
        backButton = new MovableButton("Back");
        backButton.setPrefWidth(Constants.BUTTON_WIDTH * 0.5);
        backButton.moveTo((Constants.BUTTON_WIDTH * 1.5) + 30 + card.getWidth(), 10);
        getUIPane().getChildren().add(backButton);
        backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onBackButtonClicked((MovableButton)event.getSource());
            }
        });
    }

    @Override
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

    @Override
    public void processKeyRelease(KeyCode key) {
        if (DfSim.noInput == true) {
            return;
        }
        switch (key) {
            case ENTER:
                DfSim.showLastScreen();
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

    public void onBackButtonClicked(MovableButton mb) {
        DfSim.showLastScreen();
    }

    public void updateCharCard(Person person) {
        // So maybe name, class, affection, personality just like
        // in the quick cards for hover-over.
        // Then stats in a text area going down the left,
        // then equipment to the right of that
        card.setPerson(person);
    }

    public void selectPerson(Person person) {
        updateCharCard(person);
    }

    public void selectPersonFromToggle(ToggleButton tb) {
        Person person = (Person)tb.getUserData();
        selectPerson(person);
    }

    public void onToggleButtonClicked(ToggleButton tb) {
        selectPersonFromToggle(tb);
    }

    public void update() {
        getUIPane().getChildren().removeAll(memberList);
        memberList.clear();
        
        // Update with current party members.  Create a "party tile"
        // for each member I guess.
        MovableToggleButton tb;
        ToggleGroup group = new ToggleGroup();

        double x = 10;
        double y = 10;
        for (Person person : Data.personList) {
            tb = new MovableToggleButton(person.getName());
            tb.setPrefWidth(Constants.BUTTON_WIDTH * 1.5);
            tb.setUserData(person);
            tb.setToggleGroup(group);
            tb.moveTo(x, y);
            y += 35;
            memberList.add(tb);
            tb.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    onToggleButtonClicked((MovableToggleButton)event.getSource());
                }
            });
        }
        getUIPane().getChildren().addAll(memberList);
        tb = memberList.get(0);
        tb.setSelected(true);
        selectPersonFromToggle(tb);
    }
}