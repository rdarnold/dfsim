package dfsim.gui;
import javafx.scene.control.ToggleButton;

import dfsim.*;

public class MovableToggleButton extends ToggleButton {

    public MovableToggleButton() {
        super();
        getStyleClass().add("gos-toggle-button");
        setPrefWidth(Constants.BUTTON_WIDTH);
    }

    public MovableToggleButton(String text) {
        super(text);
        getStyleClass().add("gos-toggle-button");
        setPrefWidth(Constants.BUTTON_WIDTH);
    }
    
    public void moveTo(double x, double y) {
        this.setLayoutX(x);
        this.setLayoutY(y);
    }   
}