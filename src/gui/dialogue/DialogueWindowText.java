
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

// The text that a dialogue window displays; the text knows
// how many "iterations" to display and what iteration it's
// "on" in order to faciliate the dialogue window operation
public class DialogueWindowText {

    // A section of "one window" worth of dialogue that contains a list of strings
    // it's really just a holder so we don't have to create an arraylist of arraylists
    // of strings. 
    class DialoguePage {
        private ArrayList<String> stringList;
        public ArrayList<String> getStringList() { return stringList; }

        public DialoguePage(ArrayList<String> list) {
            stringList = list;
        }
    }

    private DialogueWindow window;
    private Text dialogueText = new Text();

    private ArrayList<DialoguePage> pages = new ArrayList<DialoguePage>();
    private int pageIndex = 0;

    public DialogueWindowText(DialogueWindow theWindow) {
        window = theWindow;

        dialogueText.setText(window.getText());
        dialogueText.setFont(window.getFont());
        process();
    }

    public boolean onLastPage() {
        return (pageIndex == (pages.size()-1));
    }

    // Proceed to the next section if someone hits enter, etc.
    public boolean nextPage() {
        pageIndex++;
        if (pageIndex >= pages.size()) {
            return false;
        }
        return true;
    }

    private ArrayList<String> buildMasterList() {
        ArrayList<String> masterList = new ArrayList<String>();

        // Create testing object
        Text widthTester = new Text();
        widthTester.setFont(dialogueText.getFont());

        StringBuilder sb = new StringBuilder();
        StringBuilder lastWord = new StringBuilder();
        String masterString = dialogueText.getText();

        double maxWidth = window.getTextMaxWidth();

        // TODO this does CHARACTERS but not full words, we need to do
        // words.

        // So we will literally just slowly build up the string,
        // measuring it after adding each character, and as soon as it's
        // over the max, we step back by one, and create that string.
        for (int i = 0; i < masterString.length(); i++) {
            sb.append(masterString.charAt(i));
            // If we hit a space, we finished the word so check it
            if (masterString.charAt(i) == ' ') {
                widthTester.setText(sb.toString());
                double textWidth = widthTester.getLayoutBounds().getWidth();
                if (textWidth > maxWidth) {
                    // Too long, remove the last word and print the rest
                    int lastSpaceIndex = sb.lastIndexOf(" ");
                    if (lastSpaceIndex < 0) {
                        // But if we have no spaces at all, i.e. just one huge
                        // string, screw it just add it and move on.
                        masterList.add(sb.toString());
                        sb = new StringBuilder();
                        continue;
                    }
                    // Remove the last word plus the space since we are going down a line.
                    int remLen = sb.length() - (lastSpaceIndex + 1);
                    StringBuilder afterRemoval = sb.delete(lastSpaceIndex + 1, sb.length());

                    // Add string to list and reset
                    masterList.add(afterRemoval.toString());
                    sb = new StringBuilder();

                    // Reset the index
                    i -= remLen;
                }
            }
        }
        
        // And add the final bit that wasn't added yet
        masterList.add(sb.toString());
        return masterList;
    }

    // Create the pages and string lists we need
    private void process() {
        double maxWidth = window.getTextMaxWidth();
        double textWidth = dialogueText.getLayoutBounds().getWidth();
        double textHeight = dialogueText.getLayoutBounds().getHeight();
        int maxLinesPerPage = (int)((window.getHeight() - 80) / (textHeight + 10));

        // One-liner... but we still treat it the same way to keep things standardized
        if (textWidth <= maxWidth) {
            ArrayList<String> stringList = new ArrayList<String>();
            stringList.add(dialogueText.getText());
            DialoguePage page = new DialoguePage(stringList);
            pages.add(page);
            pageIndex = 0;
            return;
        }

        // Multiple lines, so create all our lines 
        //int numLines = (int)(textWidth / maxWidth);

        // Build master list of all strings in the dialogue
        ArrayList<String> masterList = buildMasterList();

        // Create and add a separate list and new page to use
        ArrayList<String> stringList = new ArrayList<String>();
        DialoguePage page = new DialoguePage(stringList);
        pages.add(page);

        // Now go through and add strings from the master list
        // into sub-lists which are then added to each page.
        int masterIndex = 0;
        int line = 0;
        while (masterIndex < masterList.size()) {
            if (line > maxLinesPerPage) {
                stringList = new ArrayList<String>();
                page = new DialoguePage(stringList);
                pages.add(page);
                line = 0;
            }
            stringList.add(masterList.get(masterIndex));
            masterIndex++;
            line++;
        }
    }

    public void draw(GraphicsContext gc) {
        // in fillText, 0, 0 actually corresponds to the BOTTOM LEFT corner of the text
        // NOT the top left as is done with images.  To fix, align like so:
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);

        // Now the texts AUTO centers on the y that is it drawn on - so it does NOT
        // need to be adjusted, just draw at the y that you want center of text to be on.
        double maxWidth = window.getTextMaxWidth();
        double textWidth = dialogueText.getLayoutBounds().getWidth();
        double textHeight = dialogueText.getLayoutBounds().getHeight();

        // Draw the text
        double x = window.getTextDrawX();
        double y = window.getTextDrawY();
        gc.setFont(dialogueText.getFont());
        gc.setFill(window.getTextColor());

        gc.setLineWidth(1);

        // Just write out the current section
        DialoguePage page = pages.get(pageIndex);
        for (String str : page.getStringList()) {
            gc.fillText(str, x, y, maxWidth);
            y += textHeight + 10;
        }

        //gc.setStroke(Color.BLACK);
        //gc.strokeText(text, x, y, (getWidth() - x) - 10);
    }

}