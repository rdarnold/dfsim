package dfsim;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.animation.Animation;

import dfsim.gui.*;

public class DfSimulator {

    //public ArrayList<Person> party;
    //public ArrayList<DfMon> mons;

    public HexMapEntity selectedHexMapEntity = null;

    public HexMapScreen hexMapScreen;
    public LandMapScreen landMapScreen;
    public DunMapScreen dunMapScreen;
    public TownMapScreen townMapScreen;

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
        if (dunMapScreen != null && dunMapScreen.isActive() == true) {
            if (dunMapScreen.getDun() != null) {
                dunMapScreen.getDun().updateOneFrame();
            }
        }
        else if (townMapScreen != null && townMapScreen.isActive() == true) {
            if (townMapScreen.getTown() != null) {
                townMapScreen.getTown().updateOneFrame();
            }
        }
        // The other screens should switch to this paradigm
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

    public void onLeftClickHexMapTile(HexMapTile hent) {
        if (selectedHexMapEntity != null && hent.contains == null) {
            hent.attach(selectedHexMapEntity);
            hexMap.clearCanMoveTo();
        }
    }

    public void onRightClickHexMapTile(HexMapTile hent) {
        // Maybe context menu or something
        hexMap.clearCurMovPath();
        selectedHexMapEntity.resetCurMov();
    }

    public void onMouseEnterHexMapTile(HexMapTile hent) {
        //Utils.log(hent.getTileNumber());
        if (selectedHexMapEntity != null) {
            if (selectedHexMapEntity.turnMovDone == true)
                return;

            if (hent.containsHexMapEntity(selectedHexMapEntity) == true) {
                hexMap.clearCurMovPath();
                selectedHexMapEntity.resetCurMov();
            }
            else if (hent.getCurMovPath() == true) {
                // Clear everything past this one
                // if we re-hover on one we've already used
                hexMap.clearCurMovPath(hent.getCurMovStep());
                selectedHexMapEntity.curMov = (int)selectedHexMapEntity.getMov() - hent.getCurMovStep();
            }
            else if (selectedHexMapEntity.curMov > 0) {
                // Check if we are bordering more than one other mov path, we need to have
                // at least one, but if we have more than one we eliminate that and
                // everything past it.
                int pathLen = hent.numberAdjacentCurMovPathTiles(selectedHexMapEntity);
                if (pathLen <= 0) {
                    return;
                }
                if (pathLen > 1) {
                    // So I need to find the adjacent tile to clear past now...
                    // theoretically it should be the highest one
                    //Utils.log("Arsewobbler " +selectedHexMapEntity.curMov);
                    //selectedHexMapEntity.curMov++;
                    //hexMap.clearCurMovPath(selectedHexMapEntity.curMov);
                }
                selectedHexMapEntity.curMov--;
                hent.setCurMovPath(true);
                hent.setCurMovStep((int)selectedHexMapEntity.getMov() - selectedHexMapEntity.curMov);
                //Utils.log(hent.getCurMovStep());
            }
            hent.updateColor();
        }
    }

    // If we mouse over one of our party or a mon, then it should reset the mov
    // path.
    public void onMouseEnterHexMapEntity(HexMapEntity ent) {
        if (selectedHexMapEntity == ent) {
            if (selectedHexMapEntity.curMov < selectedHexMapEntity.getMov()) {
                hexMap.clearCurMovPath();
                selectedHexMapEntity.resetCurMov();
            }
        }
    }

    public void onLeftClickHexMapEntity(HexMapEntity ent) {
        // For now, every time we select a new HexMapEntity we just reset all
        // the others.  At some point we'll want to keep track of actual
        // turns and use them accordingly.
        if (selectedHexMapEntity != ent) {
            hexMapScreen.resetTurnMovs();
            hexMapScreen.resetTurnAtks();
        }
        selectHexMapEntity(ent);
        ent.resetCurMov();
        hexMapScreen.onClickHexMapEntity(ent);
    }

    public void onRightClickHexMapEntity(HexMapEntity ent) {
        // Attack or something
        if (selectedHexMapEntity == null)
            return;

        attack(selectedHexMapEntity, ent);
    }

    public void selectHexMapEntity(HexMapEntity ent) {
        if (selectedHexMapEntity != null) {
            selectedHexMapEntity.setSelected(false);
        }
        selectedHexMapEntity = ent;
        selectedHexMapEntity.setSelected(true);
    }

    public void deselect() {
        DfSim.hexMapScreen.hexMap.clearCanMoveTo();
        if (selectedHexMapEntity != null)
            selectedHexMapEntity.setSelected(false);
        selectedHexMapEntity = null;
    }

    // Pass a party and a mon group
    public void startEncounter(ArrayList<Person> party, ArrayList<DfMon> mons) {
        hexMapScreen.reset();
        hexMapScreen.addParty(party);
        hexMapScreen.addMons(mons);
        DfSim.showHexMapScreen();
    }

    public void setupRandomEncounter() {
        // Set up a random encounter, put our party members
        // in a certain spot
        hexMapScreen.addParty(Data.personList);

        // And add some random monsters.
    }

    public void kill(HexMapEntity ch, HexMapEntity ent) {
        if (ent.mon != null) {
            hexMapScreen.appendText("   ... " + ent.getName()+ " has died!");
           
            // Award stats
            awardStats(ch, ent.mon);

            // And remove from game
            hexMapScreen.removeHexMapEntity(ent);
        }
        else {
            ent.hp = 0;
            hexMapScreen.appendText("   ... " + ent.getName()+ " collapses!");
        }
    }

    public int getStatAward(Person ch, DfMon vict, double perc, double min, double max) {
        if (ch == null || vict == null || (max <= min))
            return 0;
        int roll = Utils.number(1, 100);

        if (roll > (int)(perc*100)) {
            return 0;
        }

        int num = Utils.number((int)min, (int)max);
        // But now reduce it through trophies or whatever...
        return num;
    }

    public void awardStats(HexMapEntity ent, DfMon vict) {
        if (ent == null || vict == null)
            return;
        
        Person ch = ent.partyMember;

        if (ch == null) 
            return;
        
        int str = getStatAward(ch, vict, vict.getStrPerc(), vict.getStrMin(), vict.getStrMax());
        int agi = getStatAward(ch, vict, vict.getAgiPerc(), vict.getAgiMin(), vict.getAgiMax());
        int dex = getStatAward(ch, vict, vict.getDexPerc(), vict.getDexMin(), vict.getDexMax());
        int sta = getStatAward(ch, vict, vict.getStaPerc(), vict.getStaMin(), vict.getStaMax());
        int kno = getStatAward(ch, vict, vict.getKnoPerc(), vict.getKnoMin(), vict.getKnoMax());
        int mag = getStatAward(ch, vict, vict.getMagPerc(), vict.getMagMin(), vict.getMagMax());
        int luk = getStatAward(ch, vict, vict.getLukPerc(), vict.getLukMin(), vict.getLukMax());

        ch.addStr(str);
        ch.addAgi(agi);
        ch.addDex(dex);
        ch.addSta(sta);
        ch.addKno(kno);
        ch.addMag(mag);
        ch.addLuk(luk);
        if (str + agi + dex + sta + kno + mag + luk <= 0) {
            return;
        }
        if (str > 0) DfSim.hexMapScreen.appendText("   ... +" + str + " Str");
        if (agi > 0) DfSim.hexMapScreen.appendText("   ... +" + agi + " Agi");
        if (dex > 0) DfSim.hexMapScreen.appendText("   ... +" + dex + " Dex");
        if (sta > 0) DfSim.hexMapScreen.appendText("   ... +" + sta + " Sta");
        if (kno > 0) DfSim.hexMapScreen.appendText("   ... +" + kno + " Kno");
        if (mag > 0) DfSim.hexMapScreen.appendText("   ... +" + mag + " Mag");
        if (luk > 0) DfSim.hexMapScreen.appendText("   ... +" + luk + " Luk");
        DfSim.hexMapScreen.appendText("   ... is now Level " + ch.getLevel());
    }

    public void attack(HexMapEntity ch, HexMapEntity vict) {
        if (ch.turnAtkDone == true) {
            hexMapScreen.appendText(ch.getName() + " ...");
            DfSim.hexMapScreen.appendText("   ... has already attacked!");
            return;
        }
        ch.turnAtkDone = true;

        hexMapScreen.appendText(ch.getName() + " attacks...");
        hexMapScreen.appendText("   " + vict.getName() + "!");

        // Now, calculate the hit, damage, etc.
        boolean success = hit(ch, vict);
        if (success == false) {
            DfSim.hexMapScreen.appendText("   ... Miss!");
            return;
        }
            
        int dmg = damage(ch, vict);
        hexMapScreen.appendText("   ... Hit for " + dmg + " damage!");
        vict.hp -= dmg;
        if (vict.hp <= 0) {
            kill(ch, vict);
        }
    }
    

    /*
    Min: Hit * (2/3)
    Max: Min + Hit
    Rng: Max – Min
    */
    public double getHitMin(HexMapEntity ch) {
        return (ch.getHit() * 0.67);
    }

    public double getHitMax(HexMapEntity ch) {
        return (getHitMin(ch) + ch.getHit());
    }

    /*public double getHitRng(HexMapEntity ch) {
        return (getHitMax(ch) - getHitMin(ch));
    }*/

    public boolean hit(HexMapEntity ch, HexMapEntity vict) {

        //Formula: (Target Eva – Attacker Min) * (100 / Attacker Rng)
        //(Anything below 10% is 10%, anything above 90% is 90%)
        double evadeChance = 
            (vict.getEva() - getHitMin(ch)) *
            (100F / ch.getHit());
        
        if (evadeChance < 10) { evadeChance = 10; }
        if (evadeChance > 90) { evadeChance = 90; }

        int chance = (int)evadeChance;
        int roll = Utils.number(1, 100);
        if (chance > roll) {
            return false; 
        }
        return (true);
    }

    public int damage(HexMapEntity ch, HexMapEntity vict) {
        int dmg = (int)(ch.getPwr() / 10);
        
        // But now check def
        int absorb = Utils.number(0, (int)(vict.getDef()/10));
        dmg -= absorb;
        if (dmg < 0) { dmg = 0; }
        return dmg;
    }
}