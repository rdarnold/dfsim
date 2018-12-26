package dfsim;

import java.util.ArrayList;
import java.util.List;
import java.io.*; 
import java.security.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

// Try to keep this decoupled from the UI
public class XmlProcessor {
    public XmlProcessor() {
    }

    public void loadPersonListXML(String fileName, ArrayList<Person> personList) {
        //JAXBContext context = null;
        //AccessController.doPrivileged(new PrivilegedAction() {
            //public Object run() {
                try {
                    JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
                    Unmarshaller um = context.createUnmarshaller();

                    InputStream stream = XmlProcessor.class.getResourceAsStream(fileName);
                    //FileInputStream stream = new FileInputStream(fileName);
                    //Utils.log(stream.available());
                    
                    // Reading XML from the file and unmarshalling.
                    PersonListWrapper wrapper = (PersonListWrapper)um.unmarshal(stream);

                    personList.clear();
                    personList.addAll(wrapper.getPersonList());
                    for (Person person : personList) {
                        person.onLoad();
                    }
                } catch (Exception e) { // catches ANY exception
                    Utils.log("Failed to load people.xml (maybe)");
                    Utils.log(e.toString());
                }
                //return null;
            //}
        //});
    }
    /*public Values loadValues(String fileName) {
        return (Values) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    // Wow, this works?  I dont have to parse the XML or anything?  
                    // Holy crap, that's phenomenal.
                    JAXBContext context = JAXBContext.newInstance(Values.class);
                    Unmarshaller um = context.createUnmarshaller();

                    InputStream stream = XmlProcessor.class.getResourceAsStream(fileName);
                    //FileInputStream stream = new FileInputStream(fileName);
                    
                    // Reading XML from the file and unmarshalling.
                    return (Values)um.unmarshal(stream);
                } catch (Exception e) { // catches ANY exception
                    Utils.log(e.toString());
                } 
                return null;
            }
        });
    }*/

    // This is kind of weird, the thing is we are loading the values from the jar file,
    // so we use InputStream, but when we save, we don't want to save to the jar, we want
    // to save to the local disk.  But we may not have permission to do that.  But it
    // shouldn't really matter anyway because we can just keep using our values internally,
    // since we aren't hooked up to an external program there isn't a need to save and load
    // the values.
    /*public boolean saveValues(String fileName, Values values) {
        return (boolean) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    JAXBContext context = JAXBContext.newInstance(Values.class);
                    Marshaller m = context.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                    // Our filename is actually wrong because it has an extra / at the
                    // beginning, since we are typically loading from a jar.  So we need
                    // to modify it slightly as we are not saving to jar here, we are saving
                    // to local disk.  Just clip off the first /
                    String newFileName = fileName.substring(1);
                    FileOutputStream stream = new FileOutputStream(newFileName);

                    m.marshal(values, stream); 
                    return true;
                } catch (Exception e) { 
                    Utils.log(e.toString());
                } 
                return false;
            }
        });
    }*/
}