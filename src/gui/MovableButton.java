package dfsim.gui;
import javafx.scene.control.Button;

import dfsim.*;

public class MovableButton extends Button {

    public MovableButton() {
        super();
        getStyleClass().add("gos-button");
        setPrefWidth(Constants.BUTTON_WIDTH);
    }

    public MovableButton(String text) {
        super(text);
        getStyleClass().add("gos-button");
        setPrefWidth(Constants.BUTTON_WIDTH);
    }
    
    public void moveTo(double x, double y) {
        this.setLayoutX(x);
        this.setLayoutY(y);
    }   
}