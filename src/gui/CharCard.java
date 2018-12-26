package dfsim;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

import dfsim.*;
import dfsim.gui.*;

public class CharCard {
    
    public Person person;
    private TextArea nameArea;
    private TextArea statArea;
    private TextArea eqArea;
    
    public CharCard() { 
        nameArea = new TextArea();
        statArea = new TextArea();
        eqArea = new TextArea();
        // This is basically a collection of GUI elements to allow
        // decent display of char attributes.
        
        nameArea.setWrapText(true);
        nameArea.setEditable(false);
        nameArea.setPickOnBounds(false);
        statArea.setWrapText(true);
        statArea.setEditable(false);
        statArea.setPickOnBounds(false);
        eqArea.setWrapText(true);
        eqArea.setEditable(false);
        eqArea.setPickOnBounds(false);

        // This is cool, it's like a traditional console look
        nameArea.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Consolas;" +
            " -fx-highlight-fill: #00ff00;" + 
            " -fx-highlight-text-fill: #000000; " +
            " -fx-text-fill: #00ff00;");
        statArea.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Consolas;" +
            " -fx-highlight-fill: #00ff00;" + 
            " -fx-highlight-text-fill: #000000; " +
            " -fx-text-fill: #00ff00;");
        eqArea.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Consolas;" +
            " -fx-highlight-fill: #00ff00;" + 
            " -fx-highlight-text-fill: #000000; " +
            " -fx-text-fill: #00ff00;");

        // But the green doesn't work well with a black background.
        /*infoArea.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Consolas;" +
            " -fx-highlight-fill: #00ff00;" + 
            " -fx-highlight-text-fill: #000000; " +
            " -fx-text-fill: #000000;");*/

        int areaWid = (int)((double)Constants.BUTTON_WIDTH * 3 + 10);
        nameArea.setPrefWidth(areaWid);
        nameArea.setPrefHeight(100);

        areaWid = (int)((double)Constants.BUTTON_WIDTH);
        statArea.setPrefWidth(areaWid);
        statArea.setPrefHeight(200);

        areaWid = (int)((double)Constants.BUTTON_WIDTH * 2);
        eqArea.setPrefWidth(areaWid);
        eqArea.setPrefHeight(200);
    }

    public void setPerson(Person person) {
        nameArea.setText(person.toStringInfo());
        statArea.setText(person.toStringStats());
        eqArea.setText(person.toStringEq());
    }

    public void moveTo(double x, double y) {
        nameArea.setLayoutX(x);
        nameArea.setLayoutY(y);

        statArea.setLayoutX(x);
        statArea.setLayoutY(y + 110);

        eqArea.setLayoutX(x + Constants.BUTTON_WIDTH + 10);
        eqArea.setLayoutY(y + 110);
    }

    public void addToPane(Pane pane) {
        pane.getChildren().add(nameArea);
        pane.getChildren().add(statArea);
        pane.getChildren().add(eqArea);
    }

    public void removeFromPane(Pane pane) {
        pane.getChildren().remove(nameArea);
        pane.getChildren().remove(statArea);
        pane.getChildren().remove(eqArea);
    }
}