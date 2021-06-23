package com.me.tmw.nodes.control;

import com.me.tmw.nodes.util.DragData;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.properties.NodeProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Point extends Region {

    private final BooleanProperty proportional = new SimpleBooleanProperty(this, "proportional");
    private final BooleanProperty xLocked = new SimpleBooleanProperty(this, "xLocked");
    private final BooleanProperty yLocked = new SimpleBooleanProperty(this, "yLocked");
    private final BooleanProperty movable = new SimpleBooleanProperty(this, "movable", true);
    private final BooleanProperty clamped = new SimpleBooleanProperty(this, "clamped", true);
    private final BooleanProperty centered = new SimpleBooleanProperty(this, "centered", true);
    private final DoubleProperty x = new SimpleDoubleProperty(this, "x");
    private final DoubleProperty y = new SimpleDoubleProperty(this, "y");
    private final BooleanProperty relativeToX = new SimpleBooleanProperty(this, "relativeToX", true);
    private final BooleanProperty relativeToY = new SimpleBooleanProperty(this, "relativeToX", true);
    private final ObjectProperty<Point> relativeTo = new SimpleObjectProperty<>(this, "relativeTo", null);

    private final Stack<PointConnector> connectors = new Stack<>();
    private final Set<Point> relativeToThis = new HashSet<>();

    private final NodeProperty display = new NodeProperty(this, "display");
    private final ReadOnlyObjectWrapper<PointsEditor> editor = new ReadOnlyObjectWrapper<>(this, "editor");

    private final DragData drag = new DragData();

    private final ReadOnlyDoubleWrapper contentWidth = new ReadOnlyDoubleWrapper(this, "contentWidth");
    private final ReadOnlyDoubleWrapper contentHeight = new ReadOnlyDoubleWrapper(this, "contentHeight");

    public Point() {
        this(0, 0, true, false, false, defaultDisplay());
    }
    public Point(Node node) {
        this(0, 0, true, false, false, node);
    }
    public Point(boolean proportional) {
        this (0, 0, proportional, false, false, defaultDisplay());
    }
    public Point(boolean proportional, Node node) {
        this (0, 0, proportional, false, false, node);
    }
    public Point(double x, double y) {
        this(x, y, true, false, false, defaultDisplay());
    }
    public Point(double x, double y, Node node) {
        this(x, y, true, false, false, node);
    }
    public Point(double x, double y, boolean proportional) {
        this(x, y, proportional, false, false, defaultDisplay());
    }
    public Point(double x, double y, boolean proportional, Node node) {
        this(x, y, proportional, false, false, node);
    }
    public Point(double x, double y, boolean proportional, boolean xLocked, boolean yLocked) {
        this(x, y, proportional, xLocked, yLocked, defaultDisplay());
    }
    public Point(double x, double y, boolean proportional, boolean xLocked, boolean yLocked, Node node) {
        setX(x);
        setY(y);
        setXLocked(xLocked);
        setYLocked(yLocked);
        setProportional(proportional);
        setDisplay(node);
        InvalidationListener reloadLayout = observable -> {
            if (getEditor() != null) {
                layout(getEditor().getWidth(), getEditor().getHeight());
            }
        };
        this.proportional.addListener(reloadLayout);
        this.x.addListener(reloadLayout);
        this.y.addListener(reloadLayout);
        this.clamped.addListener(reloadLayout);
        this.centered.addListener(reloadLayout);
        this.relativeTo.addListener(reloadLayout);

        this.relativeTo.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.relativeToThis.remove(this);
            }
            if (newValue != null) {
                newValue.relativeToThis.add(this);
            }
        });

        NodeMisc.runAndAddListener(boundsInParentProperty(), observable -> {
            Bounds boundsInParent = getBoundsInParent();
            contentWidth.set(boundsInParent.getWidth());
            contentHeight.set(boundsInParent.getHeight());
        });
        getStyleClass().add("point-content");

        NodeMisc.runAndAddListener(display, observable -> {
            if (display.get() == null) getChildren().clear();
            else if (!getChildren().contains(display.get())) getChildren().setAll(display.get());
        });

        setOnMousePressed(event -> {
            drag.setStartXPos(getLayoutX());
            drag.setStartYPos(getLayoutY());
            drag.setStartScreenX(event.getScreenX());
            drag.setStartScreenY(event.getScreenY());
        });

        setOnMouseDragged(event -> {
            if (isMovable()) {
                double newX = drag.calculateX(event.getScreenX());
                double newY = drag.calculateY(event.getScreenY());
                if (getRelativeTo() != null) {
                    if (isRelativeToX()) newX -= getRelativeTo().getLayoutX();
                    if (isRelativeToY()) newY -= getRelativeTo().getLayoutY();
                }
                if (isProportional()) {
                    if (!isXLocked() && !xProperty().isBound()) setX(newX / getEditor().getWidth());
                    if (!isYLocked() && !yProperty().isBound()) setY(newY / getEditor().getHeight());
                } else {
                    if (!isXLocked() && !xProperty().isBound()) setX(newX);
                    if (!isYLocked() && !yProperty().isBound()) setY(newY);
                }
            }
        });
    }

    private static Node defaultDisplay() {
        return new BorderPane(new Circle(5, Color.DARKGRAY));
    }

    private double[] clampInEditor(double calculateX, double calculateY) {
        if (!isClamped()) return new double[]{ calculateX, calculateY };
        if (getEditor() == null) return null;
        calculateX = Math.min(Math.max(calculateX, 0), getEditor().getWidth() - getContentWidth());
        calculateY = Math.min(Math.max(calculateY, 0), getEditor().getHeight() - getContentHeight());
        return new double[]{ calculateX, calculateY };
    }

    /* **********************************************
     *
     *  Package-private methods
     *
     * **********************************************/

    void layout(double width, double height) {
        double x;
        double y;
        if (isProportional()) {
            x = getX() * width;
            y = getY() * height;
        } else {
            x = getX();
            y = getY();
        }
        if (isCentered()) {
            Bounds contentSize = getBoundsInParent();
            if (getRelativeTo() == null || !isRelativeToX()) x -= contentSize.getWidth()  / 2;
            if (getRelativeTo() == null || !isRelativeToY()) y -= contentSize.getHeight() / 2;
        }
        if (getRelativeTo() != null) {
            if (isRelativeToX()) x += getRelativeTo().getLayoutX();
            if (isRelativeToY()) y += getRelativeTo().getLayoutY();
        }
        if (isClamped() && getEditor() != null) {
            double[] clamped = clampInEditor(x, y);
            assert clamped != null; // Clamped will only be null if getEditor() == null.
            x = clamped[0];
            y = clamped[1];
        }
        relativeToThis.forEach(point -> point.layout(width, height));
        relocate(x, y);
    }

    Stack<PointConnector> getConnectors() {
        return connectors;
    }

    void setEditor(PointsEditor editor) {
        this.editor.set(editor);
    }

    /* **********************************************
     *
     *  Public methods
     *
     * **********************************************/

    public double getContentWidth() {
        return contentWidth.get();
    }
    public ReadOnlyDoubleProperty contentWidthProperty() {
        return contentWidth.getReadOnlyProperty();
    }

    public double getContentHeight() {
        return contentHeight.get();
    }
    public ReadOnlyDoubleProperty contentHeightProperty() {
        return contentHeight.getReadOnlyProperty();
    }

    public Point getRelativeTo() {
        return relativeTo.get();
    }
    /**
     * Sets a point's location to be relative to another point. So if a point has an x value of 200 and isn't proportional it will stay 200px away from its relative node. This functionality can be disabled by setting
     * relativeToProperty to null.
     * Be careful when using this property since there is no check for a cyclic relationship between relative points. Should such a relationship exist a StackOverflowException would be produced.
     * @return the relativeTo property
     */
    public ObjectProperty<Point> relativeToProperty() {
        return relativeTo;
    }
    public void setRelativeTo(Point relativeTo) {
        this.relativeTo.set(relativeTo);
    }

    /** Controls whether or not to display this Point relative to this node's {@link #relativeToProperty()}'s x location.
     * <p> If {@link #relativeToProperty()} is null this property is ignored </p>*/
    public BooleanProperty relativeToXProperty() {
        return relativeToX;
    }
    public boolean isRelativeToX() {
        return relativeToX.get();
    }
    public void setRelativeToX(boolean relativeToX) {
        this.relativeToX.set(relativeToX);
    }

    /** Controls whether or not to display this Point relative to this node's {@link #relativeToProperty()}'s y location.
     * <p> If {@link #relativeToProperty()} is null this property is ignored </p>*/
    public BooleanProperty relativeToYProperty() {
        return relativeToY;
    }
    public boolean isRelativeToY() {
        return relativeToY.get();
    }
    public void setRelativeToY(boolean relativeToY) {
        this.relativeToY.set(relativeToY);
    }

    public PointsEditor getEditor() {
        return editor.get();
    }
    public ReadOnlyObjectProperty<PointsEditor> editorProperty() {
        return editor.getReadOnlyProperty();
    }

    public boolean isClamped() {
        return clamped.get();
    }
    public BooleanProperty clampedProperty() {
        return clamped;
    }
    public void setClamped(boolean clamped) {
        this.clamped.set(clamped);
    }

    public boolean isCentered() {
        return centered.get();
    }
    public BooleanProperty centeredProperty() {
        return centered;
    }
    public void setCentered(boolean centered) {
        this.centered.set(centered);
    }

    public boolean isMovable() {
        return movable.get();
    }
    public BooleanProperty movableProperty() {
        return movable;
    }
    public void setMovable(boolean movable) {
        this.movable.set(movable);
    }

    public boolean isProportional() {
        return proportional.get();
    }
    public BooleanProperty proportionalProperty() {
        return proportional;
    }
    public void setProportional(boolean proportional) {
        this.proportional.set(proportional);
    }

    public boolean isXLocked() {
        return xLocked.get();
    }
    public BooleanProperty xLockedProperty() {
        return xLocked;
    }
    public void setXLocked(boolean xLocked) {
        this.xLocked.set(xLocked);
    }

    public boolean isYLocked() {
        return yLocked.get();
    }
    public BooleanProperty yLockedProperty() {
        return yLocked;
    }
    public void setYLocked(boolean yLocked) {
        this.yLocked.set(yLocked);
    }

    public double getX() {
        return x.get();
    }
    public DoubleProperty xProperty() {
        return x;
    }
    public void setX(double x) {
        this.x.set(x);
    }

    public double getY() {
        return y.get();
    }
    public DoubleProperty yProperty() {
        return y;
    }
    public void setY(double y) {
        this.y.set(y);
    }

    public Node getDisplay() {
        return display.get();
    }
    public NodeProperty displayProperty() {
        return display;
    }
    public void setDisplay(Node display) {
        this.display.set(display);
    }

    public void clearConnectors() {
        for (int i = connectors.size() - 1; i >= 0; i--) {
            connectors.get(i).dispose();
        }
    }

    public double getClampedX() {
        if (isProportional()) {
            return Math.min(1, Math.max(0, getX()));
        }
        return getX();
    }
    public double getClampedY() {
        if (isProportional()) {
            return Math.min(1, Math.max(0, getY()));
        }
        return getY();
    }

}
