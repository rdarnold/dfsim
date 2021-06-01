
package dfsim;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Enumeration;

// Instance of a portrait which is an image with some associated data.
public class Portrait {

    public Portrait(Image img) { image = img; }

    public Image image = null;
    public boolean used = false; // Is someone using this portrait yet or not?
    public Person person = null;

    public boolean inUse() { return (used); }
    public Image getImage() { return image; }

    public void unassign() { used = false; }
    public void assignTo(Person p) {
        // We will check to see if we are already using this portrait and give a warning but
        // we won't stop the assignment
        if (used == true) {
            Utils.log("WARNING: Portrait already in use");
        }
        person = p;
        used = true;
    }
}