
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
import dfsim.gui.*;

public class HireScreen extends DfScreen {

    //public static Town town;
    private HireScreen thisScreen;

    public HireScreen(BorderPane root, int width, int height) {
        super(root, width, height);
        thisScreen = this;
        getMainPane().getChildren().add(canvas);
        init();
    }

    public void init() {
        // Your current parties in this town should be listed at this
        // place too, it's the inn/tavern basically.

        // Throw on some buttons:
        // Switch Party
        // Manage Parties (split parties, change people in the parties in this town, etc.)
        // Hire (pick 1-4 people depending on size of the town)
        // Retire (You can retire someone and they'll just go to the town)

        // You can only hire certain classes, the special classes have to be
        // recruited from the random NPCs that generate everywhere that you can
        // hire (this includes kings princesses, shopkeepers, etc., everyone)

        // Also you can hire people directly from the town if you like them,
        // the town should generate people with different stats, traits,
        // and personality using some giant table, and if you like someone you
        // can attempt to hire them, if they like you (default roll 1-1000) they
        // may accept your attempt, you can attempt once per day, you can also give
        // them money or items to increase their affection and chance that you can
        // hire them into your party.  They will have various classes that you can
        // view, some of the classes could be really rare, but most of them will
        // probably just be "Townswoman" or "Townsman" or "Villager" which is OK
        // but doesn't get a lot of great moves, but can promote to whatever class
        // you want.  But occasionally you'll get something interesting like
        // Minstrel or Wayfarer or Thief or Mysterious Old Man or Princess
        // or Shopkeeper (yes you can recruit the shopkeepers too), or something
        // like, Mutant, Adventurer, Knight Errant, Gifted, Reaper, Angel (maybe
        // they spawn rarely next to fountains at night and things), Servant,
        // Dreamwalker, Dreamer, Antihero, Hero, Merchant, Ronin, Ghost, and anything else
        // that it seems cool that someone might be.  All kinds of weird, neat
        // fantasy tropes.  Henchman, Mercenary, Mermaid, Half-Dragon, etc.
        
        // When you walk up to the person it shows their stats, level, class,
        // and you have the option to talk, fight, give, hire.  You can't hire
        // until their affection is above 500.  Some respond well to fights, some
        // to certain types of gifts like food, herbs, or weapons.

        // But if you hire people out of town they're permanently gone, until a new
        // person regenerates which can take a while.  If there aren't enough people,
        // stuff happens like the shops won't work anymore and such.  But you can
        // do stuff in the towns to increase population, like build new buildings,
        // roads, fountains, statues, shops, defeat local duns, etc.  Or you can hire
        // villagers, take them around to level them up, then put them back in the town
        // and then its defense value will be higher so more people will want to come.

        // Potentially you can start with a random companion of a various type -
        // childhood friend, sister, princess, mentor, protector, etc., and you
        // are just told you have some kind of relationship and then she
        // is captured and you have to rescue her as a plot point, and various other
        // randomly generated quests occur that are actually somewhat deep and
        // interesting to some degree.
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

    @Override
    public void updateOneFrame() {
        super.updateOneFrame();
        if (isActive() == false) {
            return;
        }

        draw();
    }
    
    @Override
    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Any specific drawing here

        canvas.draw();
    }
}