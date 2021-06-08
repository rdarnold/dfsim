
package dfsim.gui;

import java.io.*;
import java.util.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.value.*;
import javafx.scene.input.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.shape.*;
import javafx.scene.Node;
import javafx.geometry.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.canvas.*;

import dfsim.*;

// The gui element displaying the dialogue window and associated
// processing.
public class DialogueWindow extends Rectangle {

    // Who's talking and what is being said
    private String text;
    private Person person;
    private Font dialogueFont;

    public String getText() { return text; }
    public Person getPerson() { return person; }
    public Font getFont() { return dialogueFont; }

    // References to where the dialogue text is drawing so we can use it
    // in the associated text calss
    private double textDrawX = 0;
    private double textDrawY = 0;
    private double textMaxWidth = 0;
    private Color textColor = Color.WHITE;
    public double getTextDrawX() { return textDrawX; }
    public double getTextDrawY() { return textDrawY; }
    public double getTextMaxWidth() { return textMaxWidth; }
    public Color getTextColor() { return textColor; }

    // This is used to measure the widths and heights of the text
    // for layout purposes
    private Text nameText = new Text();

    // The object that handles text processing and displaying correctly
    private DialogueWindowText dialogueText;

    // Basically the state of the flashing button arrow to move to next / end dialogue
    private MovablePolygon nextButton;

    // Should this be an attribute on the portraits themselves?
    double portraitWidth = Constants.BUTTON_WIDTH * 1.5;

    // Width of the border lines of the rectangles
    int borderWidth = 4;

    public DialogueWindow() {
        super();
        setVisible(false);
        setWidth(DfSim.width - 4);
        setHeight(200);
        setX(2);
        setY(DfSim.height - getHeight() - 2);

        dialogueFont = new Font("Constantia", 24);
        //dialogueFont = new Font("Palatino Linotype", 24);
        
        nameText.setFont(dialogueFont);
        
        // Set where the actual dialogue starts drawing and how wide it is allowed to go 
        // before wrapping
        textDrawX = getX() + portraitWidth + 40;
        textDrawY = getY() + 40;
        textMaxWidth = (getWidth() - textDrawX - 20);

        // Build the 'next' button, the little triangle below the dialogue
        createNextButton();
    }

    // May want to just replace this with an image in the future.
    private void createNextButton() {
        nextButton = new MovablePolygon();
        nextButton.makeShape(3);
        nextButton.setSize(20);
        nextButton.rotateLeft(30);
        nextButton.moveTo(getWidth() - (getTextMaxWidth() / 2) - nextButton.getRadius(), getY() + getHeight() - 10 - nextButton.getRadius());
    }

    // Proceed to next section on a multi-section dialogue
    public boolean next() {
        if (dialogueText != null) {
            if (dialogueText.nextPage() == false) {
                // Nothing left in this dialogue.
                setVisible(false);
                return false;
            }
            return true;
        }
        return false;
    }

    // Could do a back and forth thing where I draw people on opposite
    // sides when they talk to each other.
    public void displayDialogue(Person p, String talk) {
        text = talk;
        person = p;
        nameText.setText(person.getName());
        dialogueText = new DialogueWindowText(this);
        setVisible(true);
    }

    public void drawNameRect(GraphicsContext gc) {
        double height = 50;
        double width = portraitWidth + 20;
        double x = getX();
        double y = getY() - height - borderWidth;

        // Draw the name rectangle with transparency
        gc.setGlobalAlpha(0.7);
        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, width, height);

        // Draw some border around it
        gc.setStroke(Color.BLACK);
        gc.setGlobalAlpha(0.9);
        gc.setLineWidth(borderWidth);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.strokeRect(x, y, width, height);

        gc.setGlobalAlpha(1.0);

        gc.setLineWidth(1);

        // And now the text
        double textWidth = nameText.getLayoutBounds().getWidth();
        double textHeight = nameText.getLayoutBounds().getHeight();

        // in fillText, 0, 0 actually corresponds to the BOTTOM LEFT corner of the text
        // NOT the top left as is done with images.  To fix, align like so:
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);

        // Now the texts AUTO centers on the y that is it drawn on - so it does NOT
        // need to be adjusted

        // Center the text
        x += ((width / 2) - (textWidth / 2));
        y += (height / 2);// - (textHeight / 2));

        gc.setFont(dialogueFont);
        gc.setFill(Color.WHITE);
        gc.fillText(person.getName(), x, y, (width - x) - 10);
    }

    public void drawBackgroundRect(GraphicsContext gc) {
        // Draw the background rectangle with transparency
        gc.setGlobalAlpha(0.7);
        gc.setFill(Color.BLACK);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());

        // Draw some border around it
        gc.setStroke(Color.BLACK);
        gc.setGlobalAlpha(0.9);
        gc.setLineWidth(borderWidth);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.strokeRect(getX(), getY(), getWidth(), getHeight());
        gc.setGlobalAlpha(1.0);
    }

    public void drawPortrait(GraphicsContext gc) {
        // Draw the portrait
        double x = getX() + 10;
        double y = getY() + 10;
        gc.drawImage(person.getPortraitImage(), x, y, portraitWidth, portraitWidth);
        gc.setLineWidth(3);
        gc.setStroke(Color.DARKGRAY);
        //gc.setLineJoin(StrokeLineJoin.MITER);
        //gc.setLineJoin(StrokeLineJoin.BEVEL);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.strokeRect(x, y, portraitWidth, portraitWidth);
    }

    // TODO - this needs to wrap text correctly and etc.
    public void drawText(GraphicsContext gc) {
        
        dialogueText.draw(gc);
        /*// in fillText, 0, 0 actually corresponds to the BOTTOM LEFT corner of the text
        // NOT the top left as is done with images.  To fix, align like so:
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);

        // Now the texts AUTO centers on the y that is it drawn on - so it does NOT
        // need to be adjusted, just draw at the y that you want center of text to be on.

        // Draw the text
        double x = getX() + portraitWidth + 30;
        double y = getY() + 20;
        gc.setFont(dialogueFont);
        gc.setLineWidth(1);
        gc.setFill(Color.WHITE);

        // Now we need to wrap correctly.
        // First measure it
        double maxWidth = (getWidth() - x - 10);
        double textWidth = nameText.getLayoutBounds().getWidth();
        double textHeight = nameText.getLayoutBounds().getHeight();

        if (textWidth < maxWidth) {
            gc.fillText(text, x, y, maxWidth);
            return;
        }

        //gc.setStroke(Color.BLACK);
        //gc.strokeText(text, x, y, (getWidth() - x) - 10);*/
    }

    // Not really a "button" really just a graphic
    private double flashState = 0;
    private boolean flashDown = false;
    public void drawNextButton(GraphicsContext gc) {
        // Only draw if we have more text
        if (dialogueText.onLastPage() == true) {
            return;
        }

        double inc = 0.01;

        if (flashDown == true) {
            flashState -= inc;
            if (flashState < 0.2) {
                flashDown = false;
            }
        }
        else {
            flashState += inc;
            if (flashState > 1.0) {
                flashDown = true;
            }
        }

        gc.setGlobalAlpha(flashState);

        gc.setLineJoin(StrokeLineJoin.ROUND);
        
        gc.setFill(Color.DARKGRAY);
        gc.fillPolygon(nextButton.getXPoints(), nextButton.getYPoints(), nextButton.getNumPoints());
        gc.setStroke(Color.GRAY);
        gc.strokePolygon(nextButton.getXPoints(), nextButton.getYPoints(), nextButton.getNumPoints());

        gc.setGlobalAlpha(1.0);
    }

    // What does it look like?
    public void draw(GraphicsContext gc) {
        if (isVisible() == false || person == null || text == null) {
            return;
        }

        drawBackgroundRect(gc);
        drawNameRect(gc);
        drawPortrait(gc);
        drawText(gc);
        drawNextButton(gc);
    }
}