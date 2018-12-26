package dfsim;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

// This is the base class for all of my XML lists except for Values.
// The xml loader knows to look for the xml element that corresponds
// to the property name, and to call the setters.  That's part of
// what we get with JavaFX.
public class XmlDataItem {

    // Conversely I can name them differently from the ones in the actual
    // XML using the annotation @XmlElement(name="XYZ") where XYZ is the actual
    // name in the XML file.  If not specified it defaults to the name of the variable.
    //private final IntegerProperty id = new SimpleIntegerProperty(0);
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty text = new SimpleStringProperty("");

    public XmlDataItem() { 
    }

    public String getName() {
        return name.get();
    }

    public void setName(String theName) {
        name.set(theName);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getText() {
        return text.get();
    }

    public void setText(String theText) {
        text.set(theText);
    }

    public StringProperty textProperty() {
        return text;
    }

    /*public int getId() {
        return id.get();
    }

    public void setId(int num) {
        id.set(num);
    }

    public IntegerProperty idProperty() {
        return id;
    }*/

    public void stripCarriageReturns() {
        // Remove leading and trailing whitespace.
        text.set(getText().trim());

        // Remove newlines.
        text.set(getText().replaceAll("\\n", ""));
        text.set(getText().replaceAll("\\r", ""));

        // Remove all double whitespaces.
        text.set(getText().replaceAll("\\s{2,}", " "));
        //s = s.replaceAll("\\s{2,}", " ");
       // text = text.replaceAll("\\r", "");
    }

    public void addCarriageReturns() {
        // First do all the cr spaces
        text.set(getText().replaceAll("CR ", "\r\n"));
        // Now just regular CRs
        text.set(getText().replaceAll("CR", "\r\n"));
    }

    // Do whatever we need to dowith the text like strip CRs and whitespace or turn
    // certain characters into CRs
    public void processText() {
        //stripCarriageReturns();
        //addCarriageReturns();
    }
}