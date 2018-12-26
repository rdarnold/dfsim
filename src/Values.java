package dfsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;

// Apparently this works with the lowercase too...
@XmlRootElement
public class Values {
    public Values () { }
    public Values(Values fromVals) { 
        setTo(fromVals);
    }

    // These all use the wrapper (capitalized) versions so that
    // I can set them to null to record which ones have changed.
    public Double globalMoveRate = 0.0;

    public Integer rainOrigin = 0;
    public Double rainRate = 0.0;
    public Double rainSpeedVar = 0.0;

    public Double movingDotSpeedRate = 0.0;
    public Integer movingDotR = 0;
    public Integer movingDotG = 0;
    public Integer movingDotB = 0;
    public Double movingDotSize = 0.0;

    public Integer turnSeconds = 0;

    public Double gravityRate = 0.0;
    //public int gravityReversed;

    public Boolean noGiveSize = false;
    public Boolean noTakeSize = false;
    public Integer gravityRules = 0;
    public Integer armorRules = 0;
    public Integer paradigm = 0;
    // These are actually all loaded through XML so
    // maybe I shouldn't even default these.  If we don't
    // have the values it should probably just fail.
    /*public int energyGrowthPerTurn;// = 100;
    public double matterGrowthRate;// = 0.2;

    public int overallMatter;// = 500;
    public int overallEnergy;// = 500;
    public int environment;// = 50;

    public int matterBinSize;// = 100;
    public int matterFlowRate;// = 50;
    public int energyBinSize;// = 100;
    public int energyFlowRate;// = 50;
    public int successProcessingRate;// = 50;

    public int success;// = 0;
    public int matter;// = 0;
    public int energy;// = 0;
    public int knowledge;// = 0;
    public int security;// = 0;
    public int entropy;// = 0;

    // Start wrapping stuff
    @XmlElement(name="cultureTraits")
    private CultureTraitsWrapper cultureTraitsWrapper; // Don't need a new since the class is static

    public static class CultureTraitsWrapper {
        // We need the annotations because the variable name is different from the XML element name.
        @XmlElement(name="cultureTrait")
        public ArrayList<Constants.CultureTraits> cultureTraits = new ArrayList<Constants.CultureTraits>();
    }
    public ArrayList<Constants.CultureTraits> getCultureTraits() {  return cultureTraitsWrapper.cultureTraits;  }
    //End wrapping stuff
    */

    /*public ArrayList<Constants.CultureTraits> cultureTraits
         = new ArrayList<>(Arrays.asList(
             Constants.CultureTraits.Industrious, 
             Constants.CultureTraits.Ethnocentric
             ));*/
    
    public void setTo(Values from) {
        // Could just load the current values from the starting values XML
        globalMoveRate = from.globalMoveRate;

        rainOrigin = from.rainOrigin;
    	rainRate = from.rainRate;
    	rainSpeedVar = from.rainSpeedVar;

    	movingDotSpeedRate = from.movingDotSpeedRate;
    	movingDotR = from.movingDotR;
    	movingDotG = from.movingDotG;
    	movingDotB = from.movingDotB;
    	movingDotSize = from.movingDotSize;

    	turnSeconds = from.turnSeconds;

        gravityRate = from.gravityRate;
        //gravityReversed = from.gravityReversed;
        noGiveSize = from.noGiveSize;
        noTakeSize = from.noTakeSize;
        gravityRules = from.gravityRules;
        armorRules = from.armorRules;
        paradigm = from.paradigm;
    }

    // Compare values from this one to from,
    // anything that is SAME, set to null.
    public void setChanged(Values from) {
        // Have to use .equals since we are using the capitalized types...
        // This kind of sucks.
        if (globalMoveRate.equals(from.globalMoveRate))          globalMoveRate = null;
        if (rainOrigin.equals(from.rainOrigin))                  rainOrigin = null;
        if (rainRate.equals(from.rainRate))                      rainRate = null;
        if (rainSpeedVar.equals(from.rainSpeedVar))              rainSpeedVar = null;
        if (movingDotSpeedRate.equals(from.movingDotSpeedRate))  movingDotSpeedRate = null;
        if (movingDotR.equals(from.movingDotR))                  movingDotR = null;
        if (movingDotG.equals(from.movingDotG))                  movingDotG = null;
        if (movingDotB.equals(from.movingDotB))                  movingDotB = null;
        if (movingDotSize.equals(from.movingDotSize))            movingDotSize = null;
        if (turnSeconds.equals(from.turnSeconds))                turnSeconds = null;
        if (gravityRate.equals(from.gravityRate))                gravityRate = null;
        if (noGiveSize.equals(from.noGiveSize))                  noGiveSize = null;
        if (noTakeSize.equals(from.noTakeSize))                  noTakeSize = null;
        if (gravityRules.equals(from.gravityRules))              gravityRules = null;
        if (armorRules.equals(from.armorRules))                  armorRules = null;
        if (paradigm.equals(from.paradigm))                      paradigm = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Print all non-null values to a string.  This is not really human readable
        // as it is meant to be tranferred over a potentially low bandwidth connection.
        // One line for values
        sb.append("V");

        if (globalMoveRate != null)      sb.append(" A" + globalMoveRate);
        if (rainOrigin != null)          sb.append(" B" + rainOrigin);
        if (rainRate != null)            sb.append(" C" + rainRate);
        if (rainSpeedVar != null)        sb.append(" D" + rainSpeedVar);
        if (movingDotSpeedRate != null)  sb.append(" E" + movingDotSpeedRate);
        if (movingDotR != null)          sb.append(" F" + movingDotR);
        if (movingDotG != null)          sb.append(" G" + movingDotG);
        if (movingDotB != null)          sb.append(" H" + movingDotB);
        if (movingDotSize != null)       sb.append(" I" + movingDotSize);
        if (turnSeconds != null)         sb.append(" J" + turnSeconds);
        if (gravityRate != null)         sb.append(" K" + gravityRate);
        if (noGiveSize != null)          sb.append(" L" + noGiveSize);
        if (noTakeSize != null)          sb.append(" M" + noTakeSize);
        if (gravityRules != null)        sb.append(" N" + gravityRules);
        if (armorRules != null)          sb.append(" O" + armorRules);
        if (paradigm != null)            sb.append(" P" + paradigm);

        return sb.toString();
    }

    // Just makes it a little easier to get a color this way.
    public Color getMovingDotColor() {
        return Color.rgb(movingDotR, movingDotG, movingDotB);
    }
}