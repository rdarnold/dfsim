package dfsim.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.List;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;

import dfsim.*;

public class MovablePolygon extends Polygon {
    public boolean drawBounds = false;

    protected double maxSize = 30;
    protected double minSize = 1;
    protected double targetSize = 30;
    protected double growthRate = 0.5;

    // How fat should we be?
    // Make it a property so we can bind to it and do various things with it
    private DoubleProperty size = new SimpleDoubleProperty(maxSize);
    public DoubleProperty sizeProperty() { return size; }
    public double getSize() { return size.get(); }
    
    private double m_fRadius = maxSize / 2; 
    public double getRadius() { return m_fRadius; }

    // Min and max size parameters.
    public double getMaxSize() { return maxSize; }
    public double getMinSize() { return minSize; }
    public void setMaxSize(double newSize) { maxSize = newSize; }
    public void setMinSize(double newSize) { minSize = newSize; }

    public boolean setSize(double newSize) {
        if (newSize < minSize) {
            size.set(minSize);
            updatePoints();
            return false;
        }
        size.set(newSize);
        updatePoints();
        return true;
    }
    public void setPrevSize() { prevSize = getSize(); }
    public void setSizeBack() { setSize(prevSize); }
    public double getPrevSize() { return prevSize; }

    private boolean selected = false;
    public boolean getSelected() { return selected; }
    public void setSelected(boolean sel) { 
        selected = sel; 
        if (selected == true) {
            //selectedCircle.setVisible(true);
            //updateSelectedCircle();
            selectedPolygon.setVisible(true);
            updateSelectedPolygon();
        }
        else {
            //selectedCircle.setVisible(false);
            selectedPolygon.setVisible(false);
        }
    }
    
    //public void setSize(double newSize) { prevSize = size; size = newSize; updatePoints(); }
    //public void setSizeBack() { size = prevSize; updatePoints(); }

    //protected double size = 50;
    protected double prevSize = maxSize;
    protected double m_fAngleInDegrees = 0;

    protected double m_fPrevCenterX = 0;
    protected double m_fPrevCenterY = 0;
    protected double m_fCenterX = 0;
    protected double m_fCenterY = 0;

    protected double xSpeed = 0;
    protected double ySpeed = 0;

    // For more efficient collision checking
    private Circle boundingCircle;
    private Circle selectedCircle;
    public Text shapeText;  // If we want to display any text...
    public MovablePolygon overlay; // To draw transparent graphics over this shape
    public MovablePolygon selectedPolygon; // Like selected circle but a polygon instead.

    private double topX;
    private double topY;

    // The leftmost X and Y coordinates
    private double leftX;
    private double leftY;

    public double getTopX() { return topX; }
    public double getTopY() { return topY; }

    public double getLeftX() { return leftX; }
    public double getLeftY() { return leftY; }

    public double getCenterX() { return m_fCenterX; }
    public double getCenterY() { return m_fCenterY; }

    //private double xPoints[];
    //private double yPoints[];

    //public double[] getXPoints() { return xPoints; }
    //public double[] getYPoints() { return yPoints; }
    
    public double getXPoint(int slot) { return xPoints[slot]; }
    public double getYPoint(int slot) { return yPoints[slot]; }

    public double getXSpeed() { return xSpeed; }
    public double getYSpeed() { return ySpeed; }

    public void setAngleDegrees(double deg) { m_fAngleInDegrees = deg; }
    public double getAngleDegrees() {return m_fAngleInDegrees; }
    
    protected String shapeStr = null;
    //public Text getShapeText() { return shapeText; }
    public String getShapeStr() { return shapeStr; }
    public void setShapeStr(String str) {
        shapeStr = str;
    }
    
    // For the canvas, the graphicscontext requires xpoints and ypoints and doesn't
    // just take an array of points like the actual Polygon class, it's so ridiculous
    // that javafx types don't support each other
    // Only create these if we need them, and only recreate them if the size is not
    // correct
    private double[] xPoints = null;
    private double[] yPoints = null;
    public double[] getXPoints() {
        if (xPoints == null || getNumPoints() != xPoints.length) {
            xPoints = new double[getNumPoints()];
        }
        int num = 0;
        int index = 0;
        while (num < xPoints.length) {
            xPoints[num] = getPoints().get(index);
            num++;
            index += 2;
        }
        return xPoints;
    }
    public double[] getYPoints() {
        if (yPoints == null || getNumPoints() != yPoints.length) {
            yPoints = new double[getNumPoints()];
        }
        int num = 0;
        int index = 1;
        while (num < yPoints.length) {
            yPoints[num] = getPoints().get(index);
            num++;
            index += 2;
        }
        return yPoints;
    }
    public int getNumPoints() {
        if (getPoints() == null) {
            return 0;
        }
        // Because getPoints organizes them like x, y, x1, y1, so 2 numbers is 1 point
        return getPoints().size()/2;
    }

    public MovablePolygon() {    
        super();
        init("");
    }

    public MovablePolygon(String text) {    
        super();
        init(text);
    }
    
    public MovablePolygon(boolean isOverlay) {    
        super();
        if (isOverlay == false) {
            init("");
        }
    }

    public void init(String text) {
        boundingCircle = new Circle(getSize());
        boundingCircle.setFill(Color.rgb(100, 100, 100, 0.5));

        selectedCircle = new Circle(getSize());
        selectedCircle.setFill(Color.rgb(255, 255, 255, 0));
        selectedCircle.setStroke(Color.rgb(255, 0, 0, 0.5));
        selectedCircle.setVisible(false);

        shapeText = new Text();
        shapeText.setText(text);
        shapeStr = text;

        // The overlay doesn't call this init function
        overlay = new MovablePolygon(true);
        overlay.setFill(Color.rgb(255, 255, 255, 0));
        overlay.setStroke(Color.BLACK);
        overlay.setVisible(false);

        selectedPolygon = new MovablePolygon(true);
        selectedPolygon.setFill(Color.rgb(255, 255, 255, 0));
        selectedPolygon.setStroke(Color.rgb(255, 0, 0, 0.5));
        selectedPolygon.setStrokeWidth(3);
        selectedPolygon.setVisible(false);
    }

    public void deepCopy(MovablePolygon from) {
        clearAll();
        getPoints().addAll(from.getPoints());
        setSize(from.getSize());
        //setColor((Color)from.getFill());
        //setFill(from.getFill());
        setAngleDegrees(from.getAngleDegrees());
        prevSize = from.getPrevSize();
        matchSpeed(from);
        maxSize = from.maxSize;
        minSize = from.minSize;
        targetSize = from.targetSize;
        growthRate = from.growthRate;
        centerOn(from);
    }
    
    public void clearAll() {
        getPoints().clear();
        xPoints = null;
        yPoints = null;
    }

    public void centerOn(MovablePolygon from) {
        moveTo(from.getCenterX(), from.getCenterY());
    }

    public Circle getBoundingCircle() { return boundingCircle; }
    public Circle getSelectedCircle() { return selectedCircle; }
    public MovablePolygon getSelectedPolygon() { return selectedPolygon; }
    public Text getText() { return shapeText; }
    public void setText(String text) { shapeText.setText(text); shapeStr = text; }

    public void matchSpeed(MovablePolygon other) {
        setSpeed(other.getXSpeed(), other.getYSpeed());
    }

    public void zeroSpeed() {
        setSpeed(0, 0);
    }
    
    public void setSpeed(double x, double y) {
        xSpeed = x;
        ySpeed = y;
    }
    
    public void makeShape(int numCorners) {
       makeShape(getCenterX(), getCenterY(), numCorners);
    }

    public void makeShape(double centerX, double centerY, int numCorners) {
        // So we need to take the center point and calculate the distances
        // to all the corners from there.
        getPoints().clear();
        for (int i = 0; i < numCorners; i++) {
            // Just plop them all in the center then move them.
            addPoint(centerX, centerY);
        }
        if (overlay != null) {
            overlay.deepCopy(this);
        }
        if (selectedPolygon != null) {
            selectedPolygon.deepCopy(this);
            selectedPolygon.setSize(getSize() + 2);
        }
        moveTo(centerX, centerY);
    }

    protected void addPoint(Point2D point) {
        addPoint(point.getX(), point.getY());
    }

    protected void addPoint(int x, int y) {
        addPoint((double)x, (double)y);
    }

    protected void addPoint(double x, double y) {
        getPoints().addAll(x, y);
    }

    protected void updatePoint(int index, Point2D point) {
        updatePoint(index, point.getX(), point.getY());
    }

    protected void updatePoint(int index, double x, double y) {
        // The polygon stores them all as one huge array 
        int polyIndex = index * 2;
        getPoints().set(polyIndex, x);
        getPoints().set(polyIndex + 1, y);
    }

    protected void updatePoints() {
        updatePointsForShape();
    }

    private void updatePointsForShape() {
        int numCorners = getNumCorners();
        List<Double> points = getPoints();
        double anglePerCorner = 360 / numCorners;
        double cornerAngle = m_fAngleInDegrees;
        double x = 0;
        double y = 0;
        for (int i = 0; i < numCorners; i++) {
            // Calc from straight line going up
            x = m_fCenterX;
            y = m_fCenterY - (getSize()/2);
            Point2D point = Utils.calcRotatedPoint(m_fCenterX, m_fCenterY, x, y, cornerAngle);
            updatePoint(i, point.getX(), point.getY());
            cornerAngle += anglePerCorner;
            cornerAngle = Utils.normalizeAngle(cornerAngle);
        }
    }

    private void updatePointsForDrop() {
        // A drop is a thin 6 point thing sort of like a capsule but
        // angled.  But for now we're just making it a regular shape,
        // albeit a small one.
        updatePointsForShape();
    }

    // Same as number of sides
    public int getNumCorners() {
        return (getPoints().size() / 2);
    }

    public int getNumSides() {
        // The number of sides is equal to the number of coordinate pairs in the polygon
        //   (so half the number of points in the array)
        return (getPoints().size() / 2);
    }

    public void setRandomColor() {
        int r = Utils.number(0, 255);
        int g = Utils.number(0, 255);
        int b = Utils.number(0, 255);
        setColor(Color.rgb(r, g, b));
    }

    public void setColor(Color color) {
        //setStroke(Color.AQUA), 
        setFill(color);
    }

    private void updateBoundingCircle() {
        boundingCircle.setCenterX(m_fCenterX);
        boundingCircle.setCenterY(m_fCenterY);
        boundingCircle.setRadius(getSize()/2);
    }

    private void updateSelectedCircle() {
        // Actually we don't even need to bother unless we are selected.
        if (selected == true) {
            selectedCircle.setCenterX(m_fCenterX);
            selectedCircle.setCenterY(m_fCenterY);
            selectedCircle.setRadius((getSize()/2)+1);
        }
    }

    private void updateSelectedPolygon() {
        // Actually we don't even need to bother unless we are selected.
        if (selected == true) {
            selectedPolygon.centerOn(this);
            selectedPolygon.setSize((getSize())+2);
        }
    }

    // Simply move along velocity path
    public boolean move() {
        if (xSpeed == 0 && ySpeed == 0) {
            return false;
        }
        moveBy(xSpeed, ySpeed);
        return true;
    }

    public void moveTo(double newCenterX, double newCenterY) {
        m_fCenterX = newCenterX;
        m_fCenterY = newCenterY;
        updateMetaData();
        updatePoints();
    }

    public void moveBack() {
        moveTo(m_fPrevCenterX, m_fPrevCenterY);
    }

    public void updateMetaData() {
        updateLeftXY();
        updateTopXY();
        if (overlay != null) {
            updateBoundingCircle();
            updateSelectedCircle();
            shapeText.setX(m_fCenterX - (shapeText.getText().length() * 4));
            shapeText.setY(m_fCenterY + 6);
            overlay.centerOn(this);
            updateSelectedPolygon();
        }
    }
    
    public void moveBy(double mX, double mY) {
        double moveX = mX;
        double moveY = mY;
        m_fPrevCenterX = m_fCenterX;
        m_fPrevCenterY = m_fCenterY;
        m_fCenterX += moveX;
        m_fCenterY += moveY;
        updateMetaData();
        List<Double> points = getPoints();
        for (int i = 0; i < points.size(); i+=2) {
            points.set(i, points.get(i) + moveX);
        }
        for (int i = 1; i < points.size(); i+=2) {
            points.set(i, points.get(i) + moveY);
        }
    }
    
    public double findLeftX() {
        double left = 0;
        List<Double> points = getPoints();
        if (points == null || points.size() < 1) {
            return 0;
        }
        left = points.get(0);
        for (int i = 2; i < points.size(); i+=2) {
            if (points.get(i) < left) {
                left = points.get(i);
            }
        }
        return left;
    }

    public double findLeftY() {
        double left = 0;
        List<Double> points = getPoints();
        if (points == null || points.size() < 1) {
            return 0;
        }
        left = points.get(1);
        for (int i = 3; i < points.size(); i+=2) {
            if (points.get(i) < left) {
                left = points.get(i);
            }
        }
        return left;
    }

    private void updateLeftXY() {
        List<Double> points = getPoints();
        if (points == null || points.size() < 1) {
            leftX = 0;
            leftY = 0;
            return;
        }
        leftX = points.get(0);
        leftY = points.get(1);
        for (int i = 2; i < points.size(); i+=2) {
            if (points.get(i) < leftX) {
                leftX = points.get(i);
                leftY = points.get(i + 1);
            }
        }
    }

    private void updateTopXY() {
        List<Double> points = getPoints();
        if (points == null || points.size() < 1) {
            topX = 0;
            topY = 0;
            return;
        }
        topX = points.get(0);
        topY = points.get(1);
        for (int i = 3; i < points.size(); i+=2) {
            if (points.get(i) < topY) {
                topX = points.get(i-1);
                topY = points.get(i);
            }
        }
    }

    // Right now it's just a bounding circle intersect and that should be sufficient.
    public boolean intersects(MovablePolygon otherShape) {
        Circle otherCircle = otherShape.boundingCircle;
        double distanceX = boundingCircle.getCenterX() - otherCircle.getCenterX();
        double distanceY = boundingCircle.getCenterY() - otherCircle.getCenterY();
        double radiusSum = otherCircle.getRadius() + boundingCircle.getRadius();
        return distanceX * distanceX + distanceY * distanceY <= radiusSum * radiusSum;
    }
}