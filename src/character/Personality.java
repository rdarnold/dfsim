package dfsim;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;

import dfsim.*;
import dfsim.gui.*;

public class Personality {

    // Pointer to the person who has this personality if any
    Person person;
    
    // A personality has traits and then other specific stuff depending on
    // other values.
    public ArrayList<Trait> traits; 
    
    public Personality() { 
        init();
    }

    public Personality(Person pers) { 
        person = pers;
        init();
    }

    private void init() {
        traits = new ArrayList<Trait>();
        generateTraits();
    }

    public void generateTraits() {
        // Maybe nobles should get a higher chance at bad traits,
        // and angels should get lower chance of bad, demons should get
        // higher chance at bad, etc.
        
        // Usually we get 2 or 3 traits.
        Trait trait = Trait.getRandomTrait();
        traits.add(trait);
        trait = Trait.getRandomTrait();
        traits.add(trait);
        if (Utils.pass() == true) {
            trait = Trait.getRandomTrait();
            traits.add(trait);
        }
    }

    public String toString() {
        String str = "";
        for (Trait trait : traits) {
            if (str.length() > 0) {
                str += ", ";
            }
            str += trait.text();
        }
        return str;
    }

    public String toStringVertical() {
        String str = "";
        for (int i = 0; i < traits.size(); i++) {
            Trait trait = traits.get(i);
            str += " " + trait.text();
            if (i < traits.size() - 1)
                str += ",";
            str += "\r\n";
        }
        return str;
    }
}