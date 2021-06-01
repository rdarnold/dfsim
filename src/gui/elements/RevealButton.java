package dfsim.gui;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.control.Tooltip;

import dfsim.*;

public class RevealButton extends VBox {

    boolean revealed = false;

    MovableButton btn;
    Label label;

    String strDisplay = "";

    public MovableButton getButton() { return btn; }
    public String getText() { return label.getText(); }
    public Label getLabel() { return label; }

    public RevealButton(String str) {
        super();

        btn = new MovableButton();
        
        btn.setPrefWidth(Constants.BUTTON_WIDTH * 1.3);
        // Give them the default button style rather than our
        // custom one to distinguish them from the actdual control buttons
        btn.getStyleClass().clear();
        btn.getStyleClass().add("button");
        btn.getStyleClass().add("reveal-button");
        btn.setUserData(this); // Set a ref back to our master
        btn.setText(str);
        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Zoids.
                MovableButton thisBtn = (MovableButton)event.getSource();
                RevealButton master = (RevealButton)thisBtn.getUserData();
                master.setRevealed();
            }
        });
        getChildren().add(btn);

        label = new Label();
        getChildren().add(label);

        reset();
    }

    public void setValue(int val) {
        setText("" + val);
    }

    public void setValue(long val) {
        setText("" + val);
    }

    public void setValue(double val) {
        setText(String.format("%.1f", val));
    }

    public void setValue(float val) {
        setText(String.format("%.1f", val));
    }

    public void setText(String str) {
        strDisplay = str;
        if (revealed == true) {
            label.setText(str);
        }
    }

    public boolean setRevealed() {
        if (revealed == false) {
            revealed = true;
            btn.setDisable(true);
            label.setText(strDisplay);
            label.getStyleClass().remove("unrevealed-text");
        }
        return true;
    }

    public void reset() {
        revealed = false;
        btn.setDisable(false);
        strDisplay = "(Hidden)";
        label.setText(strDisplay);
        label.getStyleClass().add("unrevealed-text");
    }

    public void addToolTip(String str) {
        Utils.addToolTip(btn, str);
        Utils.addToolTip(label, str);
    }
}