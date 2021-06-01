package dfsim;

import java.util.List;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;

import javafx.scene.image.Image;

public class Person  {
    
    public Person() { 
    }

    // Conversely I can name them differently fro the ones in the actual
    // XML using the annotation @XmlElement(name="XYZ") where XYZ is the actual
    // name in the XML file.  If not specified it defaults to the name of the variable.
    private final StringProperty name = new SimpleStringProperty("");   
    private final StringProperty chClassStr = new SimpleStringProperty("");
    private final StringProperty genderStr = new SimpleStringProperty("");
    private final StringProperty weapon1 = new SimpleStringProperty("");
    private final StringProperty weapon2 = new SimpleStringProperty("");
    private final StringProperty shield = new SimpleStringProperty("");
    private final StringProperty armor = new SimpleStringProperty("");
    private final StringProperty head = new SimpleStringProperty("");
    private final StringProperty acc = new SimpleStringProperty("");
    private final StringProperty crystl = new SimpleStringProperty("");

    public String getName() {  return name.get(); }
    public String getChClassStr() {  return chClassStr.get();  }
    public String getGenderStr() {  return genderStr.get();  }

    public void setName(String theName) { name.set(theName); }
    public void setChClassStr(String theText) { 
      chClassStr.set(theText); 
      chClass = Constants.CharClass.fromStr(theText);
    }
    public void setGenderStr(String theText) { 
      genderStr.set(theText); 
      gender = Constants.Gender.fromStr(theText);
    }

    public StringProperty nameProperty() {  return name; }
    public StringProperty chClassStrProperty() { return chClassStr; }
    public StringProperty genderStrProperty() { return genderStr; }

    private final IntegerProperty str = new SimpleIntegerProperty(0);
    private final IntegerProperty agi = new SimpleIntegerProperty(0);
    private final IntegerProperty dex = new SimpleIntegerProperty(0);
    private final IntegerProperty sta = new SimpleIntegerProperty(0);
    private final IntegerProperty kno = new SimpleIntegerProperty(0);
    private final IntegerProperty mag = new SimpleIntegerProperty(0);
    private final IntegerProperty luk = new SimpleIntegerProperty(0);

    public int getStr() { return str.get(); }
    public int getAgi() { return agi.get(); }
    public int getDex() { return dex.get(); }
    public int getSta() { return sta.get(); }
    public int getKno() { return kno.get(); }
    public int getMag() { return mag.get(); }
    public int getLuk() { return luk.get(); }

    public void setStr(int num) { str.set(num); }
    public void setAgi(int num) { agi.set(num); }
    public void setDex(int num) { dex.set(num); }
    public void setSta(int num) { sta.set(num); }
    public void setKno(int num) { kno.set(num); }
    public void setMag(int num) { mag.set(num); }
    public void setLuk(int num) { luk.set(num); }

    public String getWeapon1() { return weapon1.get(); }
    public String getWeapon2() { return weapon2.get(); }
    public String getShield() { return shield.get(); }
    public String getArmor() { return armor.get(); }
    public String getHead() { return head.get(); }
    public String getAcc() { return acc.get(); }
    public String getCrystl() { return crystl.get(); }

    public void setWeapon1(String temp) { weapon1.set(temp); }
    public void setWeapon2(String temp) { weapon2.set(temp); }
    public void setShield(String temp) { shield.set(temp); }
    public void setArmor(String temp) { armor.set(temp); }
    public void setHead(String temp) { head.set(temp); }
    public void setAcc(String temp) { acc.set(temp); }
    public void setCrystl(String temp) { crystl.set(temp); }

    // NOT PART OF XML
    public String toString() {
        String temp = getName() + " (" + getChClassStr() +") ";
        temp += " [" + getStr();
        temp += " " + getAgi();
        temp += " " + getDex();
        temp += " " + getSta();
        temp += " " + getKno();
        temp += " " + getMag();
        temp += " " + getLuk() + "]";
        temp += "/r/n" + getWeapon1();
        temp += "/r/n" + getWeapon2();
        temp += "/r/n" + getShield();
        temp += "/r/n" + getArmor();
        temp += "/r/n" + getHead();
        temp += "/r/n" + getAcc();
        temp += "/r/n" + getCrystl();
        temp += "/r/n";
        return temp;
    }

    public String toStringNPC() {
        String temp = getName();
        temp += " (";
        if (gender != Constants.Gender.None) {
          temp += gender.toString() + " ";
        }

        temp += chClass.toString() +"), (" + affection + " Aff), is ";
        if (personality != null) {
          temp += personality.toString();
        }
        return temp;
    }

    // Basically just header info
    public String toStringInfo() {
        String temp = "";
        temp += getName();
        
        if (gender != Constants.Gender.None) {
          temp += " (" + gender.toString() + ")";
        }
        temp += "\r\n";
        temp += chClass.toString() + "\r\n";
        temp += "Level " + getLevel() + "\r\n";
        if (mated == 1) {
          temp += "Had sex with Borstrom " + mated + " time\r\n";
        }
        else if (mated > 1) {
          temp += "Had sex with Borstrom " + mated + " times\r\n";
        }
        return temp;
    }

    public String toStringTownInfo() {
        String temp = "";
        if (affection > 0) {
          temp += "Level " + getLevel() + ", \u2665" + affection + "\r\n";
        }
        temp += getName();
        
        if (gender != Constants.Gender.None) {
          temp += " (" + gender.toString() + ")";
        }
        temp += "\r\n";
        //temp += ", \u2665" + affection + ")\r\n";
        temp += chClass.toString() + "\r\n";

        temp += toStringStats() + "\r\n";

        if (personality != null) {
          temp += personality.toString(); //Vertical();
        }
        
        return temp;
    }

    public String toStringStats() {
      String temp = "";
      if (met == false) {
        temp += "Str: ???\r\n";
        temp += "Agi: ???\r\n";
        temp += "Dex: ???\r\n";
        temp += "Sta: ???\r\n";
        temp += "Kno: ???\r\n";
        temp += "Mag: ???\r\n";
        temp += "Luk: ???";
      }
      else {
        temp += "Str: " + getStr() + "\r\n";
        temp += "Agi: " + getAgi() + "\r\n";
        temp += "Dex: " + getDex() + "\r\n";
        temp += "Sta: " + getSta() + "\r\n";
        temp += "Kno: " + getKno() + "\r\n";
        temp += "Mag: " + getMag() + "\r\n";
        temp += "Luk: " + getLuk();
      }
      return temp;
    }

    public String toStringEq() {
      String temp = "";
      temp += "Weapon 1 : " + getWeapon1() + "\r\n";
      temp += "Weapon 2 : " + getWeapon2() + "\r\n";
      temp += "Shield   : " + getShield() + "\r\n";
      temp += "Armor    : " + getArmor() + "\r\n";
      temp += "Head     : " + getHead() + "\r\n";
      temp += "Accessory: " + getAcc() + "\r\n";
      temp += "Crystal  : " + getCrystl();
      return temp;
    }

    public Image getPortraitImage() {
      if (portrait != null) {
        return portrait.getImage();
      }

      // Should we do a generic male/female here?
      return null;
    }

    public void setRandomPortrait() {
      if (isFemale()) {
        if (GraphicsUtils.femalePortraitsAvailable() == false) {
          Utils.log("WARNING: Ran out of female portraits!");
          return;
        }
        while (portrait == null) {
          int n = Utils.number(0, Data.femalePortraits.size()-1);
          Portrait p = Data.femalePortraits.get(n);
          if (p.inUse() == false) {
            p.assignTo(this);
            portrait = p;
            break;
          }
        }
      }
      else {
        if (GraphicsUtils.femalePortraitsAvailable() == false) {
          Utils.log("WARNING: Ran out of male portraits!");
          return;
        }
        while (portrait == null) {
          int n = Utils.number(0, Data.malePortraits.size()-1);
          Portrait p = Data.malePortraits.get(n);
          if (p.inUse() == false) {
            p.assignTo(this);
            portrait = p;
            break;
          }
        }
      }
    }
    
    public void onLoad() {
        String temp;

        Utils.log("Loaded " + toString());

        // Try to set up their gear.
        temp = getWeapon1();
        if (temp != null && temp != "") {
          eqWeapon1 = Data.getDfEquipWByName(temp);
        }
        temp = getWeapon2();
        if (temp != null && temp != "") {
          eqWeapon2 = Data.getDfEquipWByName(temp);
        }
        temp = getShield();
        if (temp != null && temp != "") {
          eqShield = Data.getDfEquipSByName(temp);
        }
        temp = getArmor();
        if (temp != null && temp != "") {
          eqArmor = Data.getDfEquipAByName(temp);
        }
        temp = getHead();
        if (temp != null && temp != "") {
          eqHead = Data.getDfEquipHByName(temp);
        }
        temp = getAcc();
        if (temp != null && temp != "") {
          eqAcc = Data.getDfEquipAccByName(temp);
        }
        temp = getCrystl();
        if (temp != null && temp != "") {
          eqCrystl = Data.getDfEquipCByName(temp);
        }

        setRandomPortrait();
    }

    public Constants.CharClass chClass = Constants.CharClass.Villager;
    public Personality personality = null;
    public Constants.Gender gender = Constants.Gender.Male;
    public int affection = 0;
    public Portrait portrait = null;
    public int mated = 0;

    public boolean met = false; // Show stats or no over hover?  Have we "met" this person
    public void setMet(boolean isMet) { met = isMet; }

    public DfEquipW eqWeapon1 = null;
    public DfEquipW eqWeapon2 = null;
    public DfEquipS eqShield = null;
    public DfEquipA eqArmor = null;
    public DfEquipH eqHead = null;
    public DfEquipAcc eqAcc = null;
    public DfEquipC eqCrystl = null;

    public boolean isFemale() { return (gender == Constants.Gender.Female); }
    public boolean isMale()   { return (gender == Constants.Gender.Male); }
    
    public void addStr(int num) { str.set(str.get() + num); }
    public void addAgi(int num) { agi.set(agi.get() + num); }
    public void addDex(int num) { dex.set(dex.get() + num); }
    public void addSta(int num) { sta.set(sta.get() + num); }
    public void addKno(int num) { kno.set(kno.get() + num); }
    public void addMag(int num) { mag.set(mag.get() + num); }
    public void addLuk(int num) { luk.set(luk.get() + num); }

    /*
    Hit is dexterity minus accuracy penalty
    Power is strength plus attack
    Evade is agility minus evade penalty
    Defense is armor.  
    */

    public int getLevel() {
      // Literally just average all your stats.
      int lvl = 0;
      lvl += getStr();
      lvl += getAgi();
      lvl += getDex();
      lvl += getSta();
      lvl += getKno();
      lvl += getMag();
      lvl += getLuk();
      lvl /= 7;
      return lvl;
    }

    public double getHp() {
      double val = getSta();
      if (eqWeapon1 != null) {
        val += eqWeapon1.sta;
      }
      if (eqWeapon2 != null) {
        val += eqWeapon2.sta;
      }
      return val;
    }

    public double getMp() {
      double val = getKno();
      if (eqWeapon1 != null) {
        val += eqWeapon1.kno;
      }
      if (eqWeapon2 != null) {
        val += eqWeapon2.kno;
      }
      return val;
    }

    public double getHit() {
      double val = getDex();
      if (eqWeapon1 != null) {
        val += eqWeapon1.dex;
      }
      if (eqWeapon2 != null) {
        val += eqWeapon2.dex;
      }
      if (eqWeapon1 != null) {
        val *= eqWeapon1.acc;
      }
      return val;
    }

    public double getPwr() {
      double val = getStr();
      if (eqWeapon1 != null) {
        val += eqWeapon1.atk;
        val += eqWeapon1.str;
        val += eqWeapon1.litdmg;
        val += eqWeapon1.firdmg;
        val += eqWeapon1.icedmg;
        val += eqWeapon1.magdmg;
      }
      if (eqWeapon2 != null) {
        val += eqWeapon2.str;
      }
      return val;
    }

    public double getMpwr() {
      double val = getMag();
      if (eqWeapon1 != null) {
        val += eqWeapon1.mag;
      }
      if (eqWeapon2 != null) {
        val += eqWeapon2.mag;
      }
      return val;
    }

    public double getEva() {
      // Agility should also affect combat speed, like the combat timer
      // uses agility to determine attack order.
      double val = getAgi();
      if (eqWeapon1 != null) {
        val += eqWeapon1.agi;
      }
      if (eqWeapon2 != null) {
        val += eqWeapon2.agi;
      }
      return val;
    }

    public double getDef() {
      double val = 0;
      if (eqArmor != null) {
        val += eqArmor.def;
      }
      if (eqShield != null) {
        val += eqShield.def;
      }
      if (eqHead != null) {
        val += eqHead.def;
      }
      if (eqAcc != null) {
        val += eqAcc.def;
      }
      return val;
    }

    public double getMdef() {
      // For now, magic defense is just the same
      // as your magic stat.
      return getMpwr();
      //double val = 0;
      //return val;
    }

    public int getMov() {
      // Maybe add agility bonuses here then decrease armor?
      return 6; 
    }

    private int getStatNum(int level, int variation) {
      int min = level - variation;
      int max = level + variation;
      if (min < 1) {
        min = 1;
      }
      return Utils.number(min, max);
    }

    public void rollStats(int level, int variation) {
      setStr(getStatNum(level, variation));
      setAgi(getStatNum(level, variation));
      setDex(getStatNum(level, variation));
      setSta(getStatNum(level, variation));
      setKno(getStatNum(level, variation));
      setMag(getStatNum(level, variation));
      setLuk(getStatNum(level, variation));
    }

    public static Person generateRandomTownPerson() {
        Person person = new Person();

        // Let's give this person a gender.
        if (Utils.pass() == true) {
            person.gender = Constants.Gender.Female;
            person.setName(Data.nameListF.getRandomName());
        }
        else {
            person.setName(Data.nameListM.getRandomName());
        }

        // And pick class
        person.chClass = Constants.CharClass.getRandomTownPersonClass(person.gender);

        // And now a personality / traits
        person.personality = new Personality(person);

        // Randomly set affection
        person.affection = Utils.number(1, 1000);

        // Now generate stats
        // Randomize the variation, randomize the level.  We do want
        // people to be able to have like 50 str and 800 dex, because that
        // can be really interesting.  But that is a little weird so it should
        // kind of be an outlier.  Most people should start with a variation 
        // equal to their level / 10.  Then we can randomize this variation.
        // So what we do is, first roll an average
        // level, which is supposed to be the average of all the stats.
        int maxLev = 1000;

        // Villagers have max lev of 100.
        if (person.chClass == Constants.CharClass.Villager) {
          maxLev = 100;
        }

        int level = Utils.number(1, maxLev);
        int var = level / 4;
        // Now vary up the variation.
        if (Utils.pass() == true) {
          // 50% chance that their variation gets varied even more.
          var = level / 2;
          // And another 50% chance from here that their variation is just wild.
          if (Utils.pass() == true) {
            var = level;
          }
        }
        person.rollStats(level, var);

        return person;
    }
}

/*
<people>
  <person>
    <id>1</id>
    <text>Using the information provided, guess the amount of Matter Node 1 will have after 2 turns.</text>
    <answerType>Input</answerType>
  </person>
  <person>
    <id>2</id>
    <task>What will happen to Node 1's Matter in 2 turns if it is not able to draw any Energy?</task>
    <answerType>Choice</answerType>
    <answerOptions>
        <answerOption>Decrease</answerOption>
        <answerOption>Increase</answerOption>
        <answerOption>Stay the same</answerOption>
        <answerOption>Not enough information</answerOption>
    </answerOptions>
  </person>
  <person>
    <id>3</id>
    <text>Does this system remind you of any real life systems?</text>
    <answerType>Input</answerType>
  </person>
</people>*/