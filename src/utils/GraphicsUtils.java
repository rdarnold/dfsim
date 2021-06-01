
package dfsim;

import java.util.Random;
import javafx.scene.control.Button;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import java.util.Enumeration;

// GUI stuff
import javafx.scene.control.Tooltip;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

// This is essentially like a static class in C#
public final class GraphicsUtils {

    private GraphicsUtils() { // private constructor
    }

    // You use this and call node.setEffect(GraphicsUtils.createBorderGlow(Color.BLUE));
    public static DropShadow createBorderGlow(Color color) {
        int depth = 70;
        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(color);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);
        return borderGlow;
    }

    // Check if any female or male portraits are available
    public static boolean femalePortraitsAvailable() {
        for (Portrait p : Data.femalePortraits) {
            if (p.inUse() == false) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean malePortraitsAvailable() {
        for (Portrait p : Data.malePortraits) {
            if (p.inUse() == false) {
                return true;
            }
        }
        return false;
    }
}