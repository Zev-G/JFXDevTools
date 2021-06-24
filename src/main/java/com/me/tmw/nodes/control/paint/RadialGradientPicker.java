package com.me.tmw.nodes.control.paint;

import com.me.tmw.nodes.control.LabeledNumberField;
import com.me.tmw.nodes.control.Point;
import com.me.tmw.nodes.control.PointsEditor;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.resource.Resources;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static java.lang.Math.*;

import java.util.Objects;

public class RadialGradientPicker extends VBox {

    private static final String STYLE_SHEET = Resources.NODES.getCss("radial-gradient-picker");

    private final ObjectProperty<RadialGradient> value = new SimpleObjectProperty<>(this, "value");
    private RadialGradient loaded;
    private boolean pause;

    /* ********************************************************
    *          Radial Gradient Components
     ******************************************************** */

    private final CheckBox proportional = new CheckBox("Proportional: ");
    private final ComboBox<CycleMethod> cycleMethod = new ComboBox<>(FXCollections.observableArrayList(CycleMethod.values()));
    private final StopsPicker stops = new StopsPicker();

    private final Point center = new Point(createPointDisplay(Color.DARKBLUE, false, 15));
    private final Point radius = new Point();
    private final Point focusPoint = new Point(createPointDisplay(Color.BLACK, false, 7.5));
    private final PointsEditor pointsEditor = new PointsEditor(center, radius, focusPoint);

    // TODO implement variables controls for the below variables.
    // boolean: proportional

    public RadialGradientPicker() {
        this(new RadialGradient(0, 0, 0.5, 0.5, 0.2, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, Color.BLACK)));
    }
    public RadialGradientPicker(RadialGradient initialValue) {
        proportional.setSelected(true);
        value.addListener(observable -> {
            RadialGradient newValue = getValue();
            if (!Objects.equals(newValue, loaded)) {
                load(newValue);
            }
        });
        value.set(initialValue);
        pointsEditor.backgroundProperty().bind(
                Bindings.createObjectBinding(() -> NodeMisc.simpleBackground(getValue()), value
        ));

        radius.setRelativeTo(center);
        radius.setYLocked(true);
        focusPoint.setRelativeTo(center);
        stops.getFooter().getChildren().add(cycleMethod);

        InvalidationListener update = observable -> updateValue();
        center.xProperty().addListener(update);
        center.yProperty().addListener(update);
        radius.xProperty().addListener(update);
        focusPoint.xProperty().addListener(update);
        focusPoint.yProperty().addListener(update);
        proportional.selectedProperty().addListener(update);
        cycleMethod.valueProperty().addListener(update);
        stops.getStops().addListener(update);

        getStylesheets().addAll(STYLE_SHEET);

        VBox.setVgrow(pointsEditor, Priority.ALWAYS);

        getChildren().addAll(stops, pointsEditor);
    }

    private synchronized void updateValue() {
        if (pause) return;

        double width = 0 - focusPoint.getX();
        double height = 0 - focusPoint.getY();
        double deg = atan2(height, width) * (180 / PI) + 180;
        double len = sqrt(pow(width, 2) + pow(height, 2));
        len /= radius.getX();

        loaded = new RadialGradient(
                deg,
                len,
                min(1, max(center.getX(), 0)),
                min(1, max(center.getY(), 0)),
                min(1, max(radius.getX(), 0)),
                proportional.isSelected(),
                cycleMethod.getValue(),
                stops.getStops()
        );
        setValue(loaded);
    }

    private synchronized void load(RadialGradient value) {
        loaded = value;
        pause = true;
        stops.clear();
        stops.addAll(value.getStops());
        center.setX(value.getCenterX());
        center.setY(value.getCenterY());
        radius.setX(value.getRadius());
        cycleMethod.setValue(value.getCycleMethod());

        double angleRadians = (PI / 180) * value.getFocusAngle();
        double focusX = value.getFocusDistance() * sin(angleRadians);
        double focusY = value.getFocusDistance() * cos(angleRadians);
        focusPoint.setX(focusX);
        focusPoint.setY(focusY);

        pause = false;
    }

    private static Node createPointDisplay(Color bg, boolean rectangle, double size) {
        Shape shape = rectangle ? new Rectangle(size, size) : new Circle(size / 2);
        BorderPane pane = new BorderPane();
        pane.setMaxSize(size, size);
        pane.setMinSize(size, size);
        pane.setBackground(NodeMisc.simpleBackground(bg));
        pane.setShape(shape);
        return pane;
    }

    public RadialGradient getValue() {
        return value.get();
    }
    public ObjectProperty<RadialGradient> valueProperty() {
        return value;
    }
    public void setValue(RadialGradient value) {
        this.value.set(value);
    }

}
