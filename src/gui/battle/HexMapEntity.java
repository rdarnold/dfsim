package dfsim.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import java.util.List;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;

import dfsim.*;

public class HexMapEntity extends MovablePolygon {

    private HexMapTile m_Hex = null;
    public HexMapTile getHex() { return m_Hex; }
    public void setHex(HexMapTile hex) { m_Hex = hex; }

    public Person partyMember = null;
    public DfMon mon = null;

    public static int defSize = 30;

    public int hp = 0;
    public int curMov = 0;

    public boolean turnMovDone = false;
    public boolean turnAtkDone = false;

    public String getName() {
        if (partyMember != null)
            return partyMember.getName();
        if (mon != null)
            return mon.getName();
        return "";
    }
    
    public HexMapEntity() {
        super();
        init();
    }

    public HexMapEntity(Person person) {
        super(person.getName().substring(0, 2));
        partyMember = person;
        hp = (int)person.getHp();
        init();
    }

    public HexMapEntity(DfMon mons) {
        super(mons.name.substring(0, 2));
        mon = mons;
        hp = (int)mons.getHp();
        init();
    }

    private void handleMouseEnter(Object objEnt, MouseEvent event) {
        if (objEnt == null) 
            return;
        HexMapEntity ent = (HexMapEntity)objEnt;
        DfSim.sim.onMouseEnterHexMapEntity(ent);
    }

    private void handleClick(Object objEnt, MouseEvent event) {
        if (objEnt == null) 
            return;
        HexMapEntity ent = (HexMapEntity)objEnt;
        if (event.getButton() == MouseButton.PRIMARY) {
            DfSim.sim.onLeftClickHexMapEntity(ent);
        }
        else {
            DfSim.sim.onRightClickHexMapEntity(ent);
        }
    }

    private void init() {
        shapeText.setUserData(this);
        getSelectedCircle().setUserData(this);
        overlay.setUserData(this);
        getSelectedPolygon().setUserData(this);
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleClick(event.getSource(), event);
            }
        });
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMouseEnter(event.getSource(), event);
            }
        });

        // The text sits on top of the shape and will intercept the mouseclick
        // so we want clicking on the text to work the same way as clicking
        // on the HexMapEntity
        shapeText.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Text source = (Text)event.getSource();
                handleClick(source.getUserData(), event);
            }
        });
        shapeText.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Text source = (Text)event.getSource();
                handleMouseEnter(source.getUserData(), event);
            }
        });

        getSelectedCircle().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Circle source = (Circle)event.getSource();
                handleClick(source.getUserData(), event);
            }
        });
        getSelectedCircle().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Circle source = (Circle)event.getSource();
                handleMouseEnter(source.getUserData(), event);
            }
        });
        overlay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MovablePolygon source = (MovablePolygon)event.getSource();
                handleClick(source.getUserData(), event);
            }
        });
        overlay.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MovablePolygon source = (MovablePolygon)event.getSource();
                handleMouseEnter(source.getUserData(), event);
            }
        });
        getSelectedPolygon().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MovablePolygon source = (MovablePolygon)event.getSource();
                handleClick(source.getUserData(), event);
            }
        });
        getSelectedPolygon().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MovablePolygon source = (MovablePolygon)event.getSource();
                handleMouseEnter(source.getUserData(), event);
            }
        });

        makeShape(6);
        setSize(defSize);
        setStroke(Color.BLACK);
        setStrokeWidth(1);

        if (partyMember != null) {
            setFill(Color.AQUA);
        }
        else if (mon != null) {
            setFill(Color.ORANGE);
        }
        else {
            setFill(Color.WHITE);
        }
        setupAnimationListeners();
    }

    private void setupAnimationListeners() {
        getText().translateYProperty().bind(translateYProperty());
        getText().translateXProperty().bind(translateXProperty());
        getSelectedCircle().translateYProperty().bind(translateYProperty());
        getSelectedCircle().translateXProperty().bind(translateXProperty());
        getSelectedPolygon().translateYProperty().bind(translateYProperty());
        getSelectedPolygon().translateXProperty().bind(translateXProperty());
        overlay.translateYProperty().bind(translateYProperty());
        overlay.translateXProperty().bind(translateXProperty());

        //yValue.bind(iv2.translateYProperty());
        /*translateYProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                System.out.println((double) t1);
            }
        });*/
    }

    public void addToPane(Pane node) {
        node.getChildren().add(this);
        node.getChildren().add(overlay);
        node.getChildren().add(getText());
        node.getChildren().add(getSelectedCircle());
        node.getChildren().add(selectedPolygon);
    }

    public void removeFromPane(Pane node) {
        node.getChildren().remove(this);
        node.getChildren().remove(overlay);
        node.getChildren().remove(getText());
        node.getChildren().remove(getSelectedCircle());
        node.getChildren().remove(selectedPolygon);
    }

    public void resetCurMov() {
       curMov = (int)getMov();
    }

    public double getMov() {
      if (partyMember != null) return partyMember.getMov();
      if (mon != null) return mon.getMov();
      return 0;
    }

    public double getHit() {
      if (partyMember != null) return partyMember.getHit();
      if (mon != null) return mon.getHit();
      return 0;
    }

    public double getPwr() {
      if (partyMember != null) return partyMember.getPwr();
      if (mon != null) return mon.getPwr();
      return 0;
    }

    public double getMpwr() {
      if (partyMember != null) return partyMember.getMpwr();
      if (mon != null) return mon.getMpwr();
      return 0;
    }

    public double getEva() {
      if (partyMember != null) return partyMember.getEva();
      if (mon != null) return mon.getEva();
      return 0;
    }

    public double getDef() {
      if (partyMember != null) return partyMember.getDef();
      if (mon != null) return mon.getDef();
      return 0;
    }

    public double getMdef() {
      if (partyMember != null) return partyMember.getMdef();
      if (mon != null) return mon.getMdef();
      return 0;
    }
}