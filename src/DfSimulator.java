package dfsim;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.animation.*;
import javafx.scene.canvas.*;

import dfsim.gui.*;

public class DfSimulator {

    //public ArrayList<Person> party;
    //public ArrayList<DfMon> mons;

    public HexMapScreen hexMapScreen;
    public LandMapScreen landMapScreen;
    public DunMapScreen dunMapScreen;
    public TownMapScreen townMapScreen;

    public DialogueWindow dialogueWindow;

    public HexMap hexMap;
    public LandMap landMap;
    //public Town town;
    //public DunMap dunMap;

    private AnimationTimer mainLoop;
    //used to store the current time to calculate fps
    private long currentTime = 0;
    //used to store the last time to calculate fps
    private long lastTime = 0;
    private long lastUpdate = 0;
    private int fps = 0;//text to display fps
    //acumulated difference between current time and last time
    private double delta = 0;

    public DfSimulator() {
        init();
    }
    
    public void init() {
        //party = new ArrayList<Person>();
        //mons = new ArrayList<DfMon>();
        //reset();
    }

    public void postInit() {
        dialogueWindow = DfSim.dialogueWindow;
        // Set pointers and things
        hexMapScreen = DfSim.hexMapScreen;
        landMapScreen = DfSim.landMapScreen;
        townMapScreen = DfSim.townMapScreen;
        dunMapScreen = DfSim.dunMapScreen;
        hexMap = hexMapScreen.hexMap;
        landMap = landMapScreen.landMap;
        //townMap = townMapScreen.townMap;
        //dunMap = dunMapScreen.dunMap;

        initLoop();
        mainLoop.start();
    }

    public void start() {
        // Start on the land map.
        DfSim.showLandMapScreen();
    }

    // Run an animation timer in the background so we can do real-time
    // things as needed.
    public void updateOneFrame() {
        // Do whatever stuff depending on what screen is up.
        /*if (dunMapScreen != null && dunMapScreen.isActive() == true) {
            if (dunMapScreen.getDun() != null) {
                dunMapScreen.getDun().updateOneFrame();
            }
        }
        else if (townMapScreen != null && townMapScreen.isActive() == true) {
            if (townMapScreen.getTown() != null) {
                townMapScreen.getTown().updateOneFrame();
            }
        }*/
        if (dunMapScreen != null && dunMapScreen.isActive() == true) {
            dunMapScreen.updateOneFrame();
        }
        else if (townMapScreen != null && townMapScreen.isActive() == true) {
            townMapScreen.updateOneFrame();
        }
        else if (hexMapScreen != null && hexMapScreen.isActive() == true) {
            hexMapScreen.updateOneFrame();
        }
        else if (landMapScreen != null) {
            landMapScreen.updateOneFrame();
        }
    }

    public void initLoop() {
        lastTime = System.nanoTime();
        mainLoop = new AnimationTimer() {
            @Override
                public void handle(long now) {

                currentTime = now;
                fps++;
                delta += currentTime-lastTime;

                updateOneFrame();

                if (delta > Constants.ONE_SECOND_IN_NANOSECONDS) {
                    delta -= Constants.ONE_SECOND_IN_NANOSECONDS;
                    fps = 0;
                }
                lastTime = currentTime;
            }
        };
    }

    // Pass a party and a mon group
    public void startEncounter(ArrayList<Person> party, ArrayList<DfMon> mons) {
        hexMapScreen.hexMap.reset();
        hexMapScreen.hexMap.addParty(party);
        hexMapScreen.hexMap.addMons(mons);
        DfSim.showHexMapScreen();
    }

    public void setupRandomEncounter() {
        // Set up a random encounter, put our party members
        // in a certain spot
        hexMapScreen.hexMap.addParty(Data.personList);

        // And add some random monsters.
    }

    public boolean isShowingDialogue() {
        return (dialogueWindow.isVisible() == true);
    }

    public void showDialogue(Person p, String text) {
        dialogueWindow.displayDialogue(p, text);
    }

    public void hideDialogue() {
        dialogueWindow.setVisible(false);
    }

    public void nextDialogue() {
        if (dialogueWindow.next() == false) {
            // Probably send some event back up the chain, because now we might
            // want to trigger an additional dialogue.  Or, we might want to
            // create a dialogue by chaining a shitload of them together.
        }
    }
    
    // Block clicks if we have dialogue up.
    public boolean checkAndAdvanceDialogue() {
        if (isShowingDialogue() == false) {
            return false;
        }
        nextDialogue(); 
        return true;
    }
}