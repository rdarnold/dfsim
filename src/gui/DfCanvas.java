package dfsim.gui;

import java.io.*;
import java.util.*;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.*;
import javafx.scene.effect.DropShadow;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.Circle;

import dfsim.*;

public class DfCanvas extends Canvas {

    private LandMap landMap = null;
    public void setLandMap(LandMap lm) { landMap = lm; }

    public DfCanvas(double wid, double hgt) {
        super(wid, hgt);
        create();
    }

    public void create() {
        /*VBox.setVgrow(this, Priority.ALWAYS);

        // Add our outline to the playing area
        Line line = new Line(Constants.SIM_WIDTH, 0, Constants.SIM_WIDTH, Constants.SIM_HEIGHT);
        line.setStrokeWidth(4);
        getChildren().add(line);
        
        line = new Line(0, Constants.SIM_HEIGHT, Constants.SIM_WIDTH, Constants.SIM_HEIGHT);
        line.setStrokeWidth(4);
        getChildren().add(line);

        // And beyond the lines I guess I'll just add like a big gray shape or something... kind of
        // weird but should work.
        Rectangle rec = new Rectangle();
        rec.setFill(Color.LIGHTGRAY);
        rec.setStroke(Color.LIGHTGRAY);
        rec.setX(Constants.SIM_WIDTH);
        rec.setY(0);
        rec.setWidth(1200);
        rec.setHeight(1200);
        getChildren().add(rec);

        rec = new Rectangle();
        rec.setFill(Color.LIGHTGRAY);
        rec.setStroke(Color.LIGHTGRAY);
        rec.setX(0);
        rec.setY(Constants.SIM_HEIGHT);
        rec.setWidth(1200);
        rec.setHeight(1200);
        getChildren().add(rec);*/
        
        EventHandler filter2 = 
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        /*sim.updateCanvasMouseCoordinates(event.getX(), event.getY());
                        sim.screenMouseX = event.getScreenX();
                        sim.screenMouseY = event.getScreenY();
                        sim.sceneMouseX = event.getSceneX();
                        sim.sceneMouseY = event.getSceneY();*/
                        //System.out.println("H: " + spCanvasContainer.getHvalue() + " Y:" + spCanvasContainer.getVvalue());
                        //System.out.println("Hm: " + spCanvasContainer.getHmax() + " Ym:" + spCanvasContainer.getVmax());
                        //System.out.println("Move: " + canvasMouseX + " Y:" + canvasMouseY);
                    }
                };
        this.addEventFilter(MouseEvent.ANY, filter2);

        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onMouseClick(event.getX(), event.getY());
                /*if (event.getButton() == MouseButton.PRIMARY) {
                    //scenario.leftClick(event.getX(), event.getY());
                    event.consume();
                }
                else if (event.getButton() == MouseButton.SECONDARY) {
                    //scenario.rightClick(event.getX(), event.getY());
                }*/
            }
        });

        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onMousePressed(event.getX(), event.getY());
                /*if (event.getButton() == MouseButton.PRIMARY) {
                    //scenario.leftClick(event.getX(), event.getY());
                    event.consume();
                }
                else if (event.getButton() == MouseButton.SECONDARY) {
                }*/
            }
        });

        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onMouseDragged(event.getX(), event.getY());
                /*if (event.isPrimaryButtonDown()) {
                   // scenario.onMouseDrag(event.getX(), event.getY());
                    event.consume();
                }
                else if (event.isSecondaryButtonDown()) {
                }*/
            }
        });

        this.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //draggingWell = null;
                //onMouseDragged(event.getX(), event.getY());
                /*if (event.isPrimaryButtonDown()) {
                   // scenario.onMouseDrag(event.getX(), event.getY());
                    event.consume();
                }
                else if (event.isSecondaryButtonDown()) {
                }*/
            }
        });

        /*this.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //scenario.onMouseMove(event.getX(), event.getY());
                event.consume();
            }
        });*/
    }

    private boolean withinClick(double x, double y, MovablePolygon poly) {
        // Was the shape clicked on?  We'll just do a box around
        // each shape to keep it simple
        int leftX = (int)poly.getCenterX() - (int)poly.getRadius();
        int rightX = (int)poly.getCenterX() + (int)poly.getRadius();
        int topY = (int)poly.getCenterY() - (int)poly.getRadius();
        int bottomY = (int)poly.getCenterY() + (int)poly.getRadius();

        if (x >= leftX && x <= rightX && y >= topY && y <= bottomY) {
            return true;
        }
        return false;
    }

    private void onMouseClick(double x, double y) {
        // See if we clicked on a shape
        /*for (SysShape shape : sim.getShapes()) {
            if (withinClick(x, y, shape) == true) {
                Gos.gos.onClickShape(shape);
                return;
            }
        }*/
    }

    // Mouse pressed right now is just for the a gravity well
    private void onMousePressed(double x, double y) {
        // See if we clicked on a gravity well
        /*for (GravityWell well : sim.getGravityWells()) {
            if (withinClick(x, y, well) == true) {
                draggingWell = well;
                well.onMousePressed((int)x, (int)y);
                return;
            }
        }*/
    }
    
    private void onMouseDragged(double x, double y) {
        /*if (draggingWell != null) {
            draggingWell.onMouseDragged(x, y);
        }*/
    }
    
    public void updateOneFrame() {
        draw();
    }

    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        drawLandMap(gc);

        // Draw a border around the canvas
        //drawBorder(gc);

        // And now just draw everything directly from the simulator
        /*for (Raindrop item : sim.getDrops()) {
            drawMovableCircle(gc, item);
        }
        for (Earthpatch item : sim.getPatches()) {
            drawMovableCircle(gc, item);
        }
        for (SysShape item : sim.getShapes()) {
            drawSysShape(gc, item);
        }
        for (Spike item : sim.getSpikes()) {
            drawMovablePolygon(gc, item);
        }
        for (GravityWell item : sim.getGravityWells()) {
            drawGravityWell(gc, item);
        }*/
    }

    // If we have a landmap, draw
    public void drawLandMap(GraphicsContext gc) {
        if (landMap != null) {
            landMap.draw(gc);
        }
    }

    /*private void drawGravityWell(GraphicsContext gc, GravityWell item) {
        // And the dropshadow
        if (item.getDropShadow() != null) {
            gc.setEffect(item.getDropShadow());
        }
        drawMovableCircle(gc, item);
        gc.setEffect(null);
    }

    private void drawMovableCircle(GraphicsContext gc, MovableCircle circle) {
        gc.setFill(circle.getFill());
        //gc.setStroke(circle.getStroke());
        gc.fillOval(circle.getCenterX()-circle.getRadius(), circle.getCenterY()-circle.getRadius(), circle.getRadius()*2, circle.getRadius()*2);
        //gc.strokeOval(circle.getCenterX()-circle.getRadius(), circle.getCenterY()-circle.getRadius(), circle.getRadius()*2, circle.getRadius()*2);
    }*/

    private void drawMovablePolygon(GraphicsContext gc, MovablePolygon poly) {
        //poly.getPoints
        //getPoints().set(0);
        //getPoints().set(1);
        gc.setFill(poly.getFill());
        gc.setStroke(poly.getStroke());
        gc.setLineWidth(poly.getStrokeWidth());
        gc.fillPolygon(poly.getXPoints(), poly.getYPoints(), poly.getNumPoints());
        gc.strokePolygon(poly.getXPoints(), poly.getYPoints(), poly.getNumPoints());
 
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        if (poly.getShapeStr() != null && poly.getShapeStr().equals("") == false) {
            gc.fillText(poly.getShapeStr(), poly.getCenterX() - 3, poly.getCenterY() + 5);
        }

        /*if (poly.getSelected() == true && sim.isUsingSuccess() == true) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(1);
            int var = 4;
            gc.strokeOval(poly.getCenterX()-poly.getRadius() - var, poly.getCenterY()-poly.getRadius() - var, (poly.getRadius()*2) + (var*2), (poly.getRadius()*2) + (var*2));
        }*/
    }

    //private DropShadow lineGlow = Utils.createLineGlow(Color.BLACK);
    /*private void drawSysShape(GraphicsContext gc, SysShape shape) {
        // Using this effect takes a lot of CPU power, especially when we draw a lot of them at once
        //gc.setEffect(lineGlow);

        // Now we're going to try to draw some indication that the shape is
        // "pulling" size from other shapes
        gc.setLineWidth(2);
        gc.setLineDashes(10);
        gc.setLineDashOffset(m_fDashOffset);
        gc.setLineCap(StrokeLineCap.ROUND);
        for (SysShape otherShape : shape.getStoleFrom()) {
            if (otherShape.isDead() == false) {
                // Draw some line from them to us based on some timer variable.
                gc.setStroke(shape.getFill());
                gc.strokeLine(otherShape.getCenterX(), otherShape.getCenterY(), shape.getCenterX(), shape.getCenterY());
            }
        }
        gc.setLineDashes(0);
        //gc.setEffect(null);

        // Actually draw this later so that we can see the shapes over their own lines
        drawMovablePolygon(gc, shape);
    }*/
    
    /*private void drawBorder(GraphicsContext gc) {
        final double canvasWidth = getWidth();
        final double canvasHeight = getHeight();
    
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeRect(0, 0, canvasWidth, canvasHeight);
        gc.setLineWidth(1);
    }*/
}