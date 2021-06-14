
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
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import dfsim.*;
import dfsim.gui.*;

public class PartyScreen extends DfScreen {

    private PartyScreen thisScreen;
    private ArrayList<MovableToggleButton> memberList;
    private ArrayList<ImageView> portraitViewList;
    private ImageView gifTest;
    CharCard card;
    MovableButton backButton;
    MovableButton changePartyButton;
    MovableButton mateButton;

    public PartyScreen(BorderPane root, int width, int height) {
        super(root, width, height);
        thisScreen = this;
        init();
    }

    public void init() {
        memberList = new ArrayList<MovableToggleButton>();
        portraitViewList = new ArrayList<ImageView>();
        gifTest = new ImageView();
        
        getRoot().setStyle("-fx-background-colour: #000000;");
        getUIPane().setStyle("-fx-background-colour: #000000;");
        getMainPane().setStyle("-fx-background-colour: #000000;"); 
        //setStyle("-fx-background-colour: #000000;");

        // So maybe on the left is the party list, on the right
        // is the card of the currently selected effer.
        card = new CharCard();
        card.addToPane(getUIPane());
        card.moveTo((Constants.BUTTON_WIDTH * 1.5) + 20, 45);

        backButton = new MovableButton("Exit");
        backButton.setPrefWidth(Constants.BUTTON_WIDTH * 1.5);
        backButton.moveTo(10, 10);
        getUIPane().getChildren().add(backButton);
        backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onBackButtonClicked((MovableButton)event.getSource());
            }
        });

        changePartyButton = new MovableButton("Change Party");
        changePartyButton.setPrefWidth(Constants.BUTTON_WIDTH * 1.5);
        changePartyButton.moveTo(20 + Constants.BUTTON_WIDTH * 1.5, 10);
        getUIPane().getChildren().add(changePartyButton);
        changePartyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onChangePartyButtonClicked((MovableButton)event.getSource());
            }
        });

        
        mateButton = new MovableButton("Have Sex");
        mateButton.setPrefWidth(Constants.BUTTON_WIDTH * 1.5);
        mateButton.moveTo(30 + (2 * Constants.BUTTON_WIDTH * 1.5), 10);
        getUIPane().getChildren().add(mateButton);
        mateButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onMateButtonClicked((MovableButton)event.getSource());
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
        super.processKeyRelease(key);
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

    public void onChangePartyButtonClicked(MovableButton mb) {
        Utils.log("Change Party Not Yet Implemented");
    }
    
    public void onMateButtonClicked(MovableButton mb) {
        Person mate = card.getPerson();
        if (mate == Data.personList.get(0)) {
            return;
        }
        //Utils.log("Mate Button Not Yet Implemented");
        gifTest.setImage(Data.gifs.get(Utils.number(0, Data.gifs.size()-1)));
        
        FadeTransition ft = new FadeTransition(Duration.millis(100), this.getRoot());
        this.setFill(Color.VIOLET);
        ft.setFromValue(1.0);
        ft.setToValue(0.3);
        ft.setCycleCount(14);
        ft.setAutoReverse(true);
        ft.play();

        gifTest.setVisible(true);

        DfSim.sim.showDialogue(mate,  
        /*"We just had sex.. you really are the king, right?  You grunt like a common thug... " +
        "Not that I've had sex with a common thug.  Actually you are the first person I've had " +
        "sex with.  I thought it would be good to give myself to the king, but now... you aren't " +
        "really the king, are you.  I can see by your smirk... So I really did just have sex with " +
        "a dirty mercenary..."); */
        "Are you truly the king?  You do not look the part... more like a mercenary.");
        /*"What!?  I only had sex with you because you said you were the king!  That was a lie!?  Ugh, how filthy... "+
        "Now I just have the load of some... stranger... in my belly.  Gross.");*/
        /*"You want to have sex?  Oh, ugh... Fine, if you insist... " +
        "(" + mate.getName() + " removes " + mate.getArmor() + ".  " +
        "You have sex with " + mate.getName() + ".  " + mate.getName() + " wears " + mate.getArmor() + ").  " +
        "Did you have to dump your load in me? (" + mate.getName() + " rolls her eyes).  In Castle Starlock, we don't " +
        "do things this way."
        ); */
        /*"CANDOUR compels me, BECHER! to commend " +
        "the verse which blends the censor with the friend. " +
        "Your strong yet just reproof extorts applause " +
        "from me, the heedless and imprudent cause. " +
        "For this wild error which pervades my strain, " +
        "I sue for pardon, — must I sue in vain? " +
        "The wise sometimes from Wisdom's ways depart: " +
        "Can youth then hush the dictates of the heart? " +
        "Precepts of prudence curb, but can't control " +
        "the fierce emotions of the flowing soul. " +
        "When Love's delirium haunts the glowing mind " +
        "limping decorum lingers far behind: " +
        "Vainly the dotard mends her prudish pace, " +
        "outstript and vanquish'd in the mental chase. " +
        "The young, the old, have worn the chains of love; " +
        "Let those they ne'er confined my lay reprove.");*/
         
        Utils.log("You mate with " + mate.getName() + ".");
        mate.mated++;
        card.update();
    }

    public void updateCharCard(Person person) {
        gifTest.setVisible(false);
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

    public void setupGifTest() {
        if (Data.gifs == null || Data.gifs.size() <= 0) {
            return;
        }
        gifTest.setFitWidth((Constants.BUTTON_WIDTH * 1.5) * 2.5);
        gifTest.setPreserveRatio(true);
        gifTest.setVisible(false);
        gifTest.setSmooth(true);
        gifTest.setCache(true);
        gifTest.setImage(Data.gifs.get(Utils.number(0, Data.gifs.size()-1)));
        gifTest.setLayoutX(Constants.BUTTON_WIDTH * 1.5 + 20);
        gifTest.setLayoutY(400);
    }

    public void update() {
        getUIPane().getChildren().remove(gifTest);
        getUIPane().getChildren().removeAll(memberList);
        getUIPane().getChildren().removeAll(portraitViewList);
        memberList.clear();
        portraitViewList.clear();
        
        // Update with current party members.  Create a "party tile"
        // for each member I guess.
        MovableToggleButton tb;
        ToggleGroup group = new ToggleGroup();

        double x = 10;
        double y = 45;
        for (Person person : Data.personList) {
            tb = new MovableToggleButton(person.getName());
            tb.setPrefWidth(Constants.BUTTON_WIDTH * 1.5);
            tb.setUserData(person);
            tb.setToggleGroup(group);
            tb.moveTo(x, y);
            y += 35;
            memberList.add(tb);

            ImageView pv = new ImageView();
            pv.setFitWidth(Constants.BUTTON_WIDTH * 1.5);
            pv.setPreserveRatio(true);
            pv.setSmooth(true);
            pv.setCache(true);
            pv.setImage(person.getPortraitImage());
            pv.setLayoutX(x);
            pv.setLayoutY(y);
            portraitViewList.add(pv);

            y += Constants.BUTTON_WIDTH * 1.5 + 35;
            tb.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    onToggleButtonClicked((MovableToggleButton)event.getSource());
                }
            });
        }
        getUIPane().getChildren().addAll(memberList);
        getUIPane().getChildren().addAll(portraitViewList);
        tb = memberList.get(0);
        tb.setSelected(true);
        selectPersonFromToggle(tb);

        setupGifTest();
        getUIPane().getChildren().add(gifTest);
    }
}