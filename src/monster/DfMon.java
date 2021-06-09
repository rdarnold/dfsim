package dfsim;

import java.util.List;
import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;

import java.io.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import dfsim.gui.*;

public class DfMon  {

    public static final MonVal[] vals = MonVal.values();

    public enum MonVal {
        Name,
        Series,
        Level,
        Hp,
        Mp,
        Mov,
        Hit,
        Pwr,
        Mpwr,
        Eva,
        Def,
        Mdef,
        Move1,
        Move2,
        Move3,
        Move4,
        StrPerc,
        StrMin,
        StrMax,
        AgiPerc,
        AgiMin,
        AgiMax,
        DexPerc,
        DexMin,
        DexMax,
        StaPerc,
        StaMin,
        StaMax,
        KnoPerc,
        KnoMin,
        KnoMax,
        MagPerc,
        MagMin,
        MagMax,
        LukPerc,
        LukMin,
        LukMax
    }

    public DfMon() { }
    
    private CharSprite sprite = null;
    public CharSprite getSprite() { return sprite; }
    public void setSprite(CharSprite s) { sprite = s; }

    public String name;
    public int series;
    public int level;
    public double hp;
    public double mp;
    public double mov;
    public double hit;
    public double pwr;
    public double mpwr;
    public double eva;
    public double def;
    public double mdef;

    public double strMin;
    public double agiMin;
    public double dexMin;
    public double staMin;
    public double knoMin;
    public double magMin;
    public double lukMin;

    public double strMax;
    public double agiMax;
    public double dexMax;
    public double staMax;
    public double knoMax;
    public double magMax;
    public double lukMax;

    public double strPerc;
    public double agiPerc;
    public double dexPerc;
    public double staPerc;
    public double knoPerc;
    public double magPerc;
    public double lukPerc;
    
    public String getName() { return name; }
    public double getSeries() { return series; }
    public double getLevel() { return level; }
    public double getHp() { return hp; }
    public double getMp() { return mp; }
    public int getMov() { return (int)mov; }
    public double getHit() { return hit; }
    public double getPwr() { return pwr; }
    public double getMpwr() { return mpwr; }
    public double getEva() { return eva; }
    public double getDef() { return def; }
    public double getMdef() { return def; }

    public double getStrPerc() { return strPerc; }
    public double getAgiPerc() { return agiPerc; }
    public double getDexPerc() { return dexPerc; }
    public double getStaPerc() { return staPerc; }
    public double getKnoPerc() { return knoPerc; }
    public double getMagPerc() { return magPerc; }
    public double getLukPerc() { return lukPerc; }

    public double getStrMin() { return strMin; }
    public double getAgiMin() { return agiMin; }
    public double getDexMin() { return dexMin; }
    public double getStaMin() { return staMin; }
    public double getKnoMin() { return knoMin; }
    public double getMagMin() { return magMin; }
    public double getLukMin() { return lukMin; }

    public double getStrMax() { return strMax; }
    public double getAgiMax() { return agiMax; }
    public double getDexMax() { return dexMax; }
    public double getStaMax() { return staMax; }
    public double getKnoMax() { return knoMax; }
    public double getMagMax() { return magMax; }
    public double getLukMax() { return lukMax; }

    public String toString() {
        String strRetv = name + ", ";

        if (hp > 0) strRetv += "hp: " + hp + " ";
        if (hp > 0) strRetv += "mp: " + mp + " ";
        if (mov > 0) strRetv += "mov: " + mov + " ";
        if (hit > 0) strRetv += "hit: " + hit + " ";
        if (pwr > 0) strRetv += "pwr: " + pwr + " ";
        if (mpwr > 0) strRetv += "mpwr: " + mpwr + " ";
        if (eva > 0) strRetv += "eva: " + eva + " ";
        if (def > 0) strRetv += "def: " + def + " ";
        if (mdef > 0) strRetv += "mdef: " + mdef + " ";

        if (strMin > 0) strRetv += "str: " + strMin + " ";
        if (agiMin > 0) strRetv += "agi: " + agiMin + " ";
        if (dexMin > 0) strRetv += "dex: " + dexMin + " ";
        if (staMin > 0) strRetv += "sta: " + staMin + " ";
        if (knoMin > 0) strRetv += "kno: " + knoMin + " ";
        if (magMin > 0) strRetv += "mag: " + magMin + " ";
        if (lukMin > 0) strRetv += "luk: " + lukMin + " ";

        return strRetv;
    }

    // These are the things that modify a mon randomly, so they get pre-pended
    // to a mon's name and add various attributes potentially, mons can also get
    // other adjectives to vary them up a bit but they don't show in the name.
    public enum Modifiers {
        Armored, 
        Dark, White, Black, Red, Green, Blue, Yellow, 
        Fire, Ice, Earth, Wind, Water,
        Ghost, Vampire, Skeletal,
        Poison, 
        Big, Large, Huge, Giant, 
        Metal, Bronze, Iron, Steel, Silver, Gold,
        Tree, Sand, Stone, Granite, Obsidian,
        King, Queen, 
        Mad, Dread, Infernal, Death, Shadow, Grim, Skull,
        None;
    }

    public enum Traits {
        Strong, Weak, Fast, Slow;
    }

    public enum Movement {
        Walk, Fly, Swim;
    }

    public enum Type {
        Living, Undead, Construct;
    }

    // Do any kind of post-load processing we need to do on the mons,
    // like generate stats, levels, or whatever.
    public static void postProcessMons() {
        if (Data.dfMonList == null || Data.dfMonList.size() <= 0)
            return;

        // Each section or area could have all kinds of mons,
        // all kinds of dragons that the player may have never seen
        // before, metallic ones, colored ones with all different properties,
        // etc.  I can put all different combos of animals and beasts in here
        // and let the generator just make all these cool things, like
        // combine "dark" and whatever, like all these various adjectives.
        // So I can just have tons and tons of different types of mons,
        // and the adjectives that are added to it define its stats and powers,
        // not all adjectives are in names but many are.  So you could be
        // Strong, Slow, Fast, Flying, Fire, Ice, Dark, Ghost, etc.
        // Some are predefined, like types - living or undead or construct,
        // etc.  So I start with a bunch of predefined base mons with various
        // properties, then depending on those properties adjectives can be
        // added.  And then tons of variations of those base mons are generated.

        // Basically I should say, what do I know about these mons - and define that -
        // rather than define stats in the spreadsheet.

        // And maybe for each game, it doesnt do all types for all mons, just to keep
        // things a little  more interesting.  You might get a Red Stag Beetle but not
        // a Blue Stag Beetle, and you might get a Fire Ogre or an Armored Ogre but
        // not a Green Ogre.  Basically pick a certain number of modifiers for each
        // base mon type and only apply those.

        // So this is establishing the baseline monsters for the entire sim,
        // not something that should regen each time things are loaded.  It should
        // only generate upon new world creation.
        for (DfMon mon : Data.dfMonList) {
            if (mon.getLevel() == 0) {
                // Give it a random level and stats.
            }
            mon.setRandomMonsterSprite();
        }

        // Now, do some kind of auto-balancing creation for all of our mons for
        // the entire game.  Basically we select each base mon, give it a random
        // level within its assigned range if it has one, from that level, use
        // any constraints it has to generate stats, then pick a random number
        // of variations, and base additional levels on those variations, and the
        // end result is the total spread of mons that can occur throughout the
        // game.
    }

    // Static method to load all the mons
    public static void load(String fileName) {
        try {
            InputStream stream = ExcelUtils.class.getResourceAsStream(fileName);
            Workbook wb = new XSSFWorkbook(stream);
            Sheet sheet = wb.getSheetAt(0);
            Row row;
            Cell cell;

            int numRows = sheet.getPhysicalNumberOfRows();
            int numCols = ExcelUtils.getNumberOfColumns(wb, sheet);

            // So first iterate down to our first actual entry.
            int r = ExcelUtils.getRowForString("Name", wb, sheet);
            r++; // And move down one row so we are at the actual data

            //Utils.log("Found it, it's row " + r);
            // Do not restart r, we are right where we wanna be.
            for (; r <= numRows; r++) {
                row = sheet.getRow(r);
                if (row == null) 
                    continue;
                DfMon mon = new DfMon();
                Data.dfMonList.add(mon);
                for (int c = 1; c < numCols; c++) {
                    cell = row.getCell(c);
                    if (ExcelUtils.isCellEmpty(cell)) {
                        if (c == 1) { 
                            // No name, so no mon, remove it and move on
                            Data.dfMonList.remove(mon);
                            break;
                        }
                        continue;
                    }
                    DfMon.MonVal enumValue = DfMon.vals[c-1];
                    switch (enumValue) { //(c-1) {
                        case Name:
                            mon.name = cell.getStringCellValue();
                            break;
                        case Series:
                            mon.series = (int)ExcelUtils.getNumberForCell(cell);
                            break;
                        case Level:
                            mon.level = (int)ExcelUtils.getNumberForCell(cell);
                            break;
                        case Hp:
                            mon.hp = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Mp:
                            mon.mp = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Mov:
                            mon.mov = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Hit:
                            mon.hit = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Pwr:
                            mon.pwr = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Mpwr:
                            mon.mpwr = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Eva:
                            mon.eva = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Def:
                            mon.def = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Mdef:
                            mon.mdef = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Move1:
                            // Move 1
                            break;
                        case Move2:
                            // Move 2
                            break;
                        case Move3:
                            // Move 3
                            break;
                        case Move4:
                            // Move 4
                            break;
                        case StrPerc:
                            mon.strPerc = ExcelUtils.getNumberForCell(cell);
                            break;
                        case StrMin:
                            mon.strMin = ExcelUtils.getNumberForCell(cell);
                            break;
                        case StrMax:
                            mon.strMax = ExcelUtils.getNumberForCell(cell);
                            break;
                        case AgiPerc:
                            mon.agiPerc = ExcelUtils.getNumberForCell(cell);
                            break;
                        case AgiMin:
                            mon.agiMin = ExcelUtils.getNumberForCell(cell);
                            break;
                        case AgiMax:
                            mon.agiMax = ExcelUtils.getNumberForCell(cell);
                            break;
                        case DexPerc:
                            mon.dexPerc = ExcelUtils.getNumberForCell(cell);
                            break;
                        case DexMin:
                            mon.dexMin = ExcelUtils.getNumberForCell(cell);
                            break;
                        case DexMax:
                            mon.dexMax = ExcelUtils.getNumberForCell(cell);
                            break;
                        case StaPerc:
                            mon.staPerc = ExcelUtils.getNumberForCell(cell);
                            break;
                        case StaMin:
                            mon.staMin = ExcelUtils.getNumberForCell(cell);
                            break;
                        case StaMax:
                            mon.staMax = ExcelUtils.getNumberForCell(cell);
                            break;
                        case KnoPerc:
                            mon.knoPerc = ExcelUtils.getNumberForCell(cell);
                            break;
                        case KnoMin:
                            mon.knoMin = ExcelUtils.getNumberForCell(cell);
                            break;
                        case KnoMax:
                            mon.knoMax = ExcelUtils.getNumberForCell(cell);
                            break;
                        case MagPerc:
                            mon.magPerc = ExcelUtils.getNumberForCell(cell);
                            break;
                        case MagMin:
                            mon.magMin = ExcelUtils.getNumberForCell(cell);
                            break;
                        case MagMax:
                            mon.magMax = ExcelUtils.getNumberForCell(cell);
                            break;
                        case LukPerc:
                            mon.lukPerc = ExcelUtils.getNumberForCell(cell);
                            break;
                        case LukMin:
                            mon.lukMin = ExcelUtils.getNumberForCell(cell);
                            break;
                        case LukMax:
                            mon.lukMax = ExcelUtils.getNumberForCell(cell);
                            break;
                        }
                    /*if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                        Utils.log(cell.getNumericCellValue()));
                    } 
                    else  {
                        Utils.log(cell.getStringCellValue());
                    }*/
                } 
            }

            /*for (DfMon dw : Data.dfMonList) {
                Utils.log(dw.toString());
            }*/
            
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        postProcessMons();
    }
    
    // For now we allow reuse of sprites but it probably isn't necessary
    // since there is so much customization available.
    // These sprites should in the future be assigned more like by class,
    // looks, etc.; I need to categorize them better.
    public void setRandomMonsterSprite() {
        while (sprite == null) {
            int n = Utils.number(0, Data.monsterSprites.size()-1);
            setSprite(Data.monsterSprites.get(n));
        }
    }
}