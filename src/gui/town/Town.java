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

public class Town extends TownMap {
    
    public LandMapTile landMapTile; // Link to the overland tile that holds this town.
    
    public Town() { 
        super(Utils.number(200, 15000), true);
    }

    public Town(int population) { 
        super(population, true);
    }
    
    public Town(int population, boolean generate) { 
        super(population, generate);
    }
}