package dfsim;

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

import dfsim.*;
import dfsim.gui.*;

public class Dun extends DunMap {
    
    public LandMapTile landMapTile; // Link to the overland tile that holds this town.
    
    public Dun() { 
        super(Utils.number(3, 100), true);
    }

    public Dun(int size) { 
        super(size, true);
    }
    
    public Dun(int size, boolean generate) { 
        super(size, generate);
    }
}