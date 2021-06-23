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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Objects;

public class RadialGradientPicker extends VBox {

    private static final String STYLE_SHEET = Resources.NODES.getCss("radial-gradient-picker");

    private final ObjectProperty<RadialGradient> value = new SimpleObjectProperty<>(this, "value");
    private RadialGradient loaded;

    /* ********************************************************
    *          Radial Gradient Components
     ******************************************************** */

    private final CheckBox proportional = new CheckBox("Proportional: ");
    private final ComboBox<CycleMethod> cycleMethod = new ComboBox<>(FXCollections.observableArrayList(CycleMethod.values()));
    private final StopsPicker stops = new StopsPicker();

    private final Point center = new Point();
    private final Point radius = new Point(0.2, 0);
    private final PointsEditor pointsEditor = new PointsEditor(center, radius);

    // double: focusAngle, focusDistance, centerX, centerY, radius TODO implement as many as possible with PointEditor
    // boolean: proportional
    // CycleMethod: cycleMethod
    // List<Stop>: stops

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

        InvalidationListener update = observable -> updateValue();
        center.xProperty().addListener(update);
        center.yProperty().addListener(update);
        radius.xProperty().addListener(update);
        proportional.selectedProperty().addListener(update);
        stops.getStops().addListener(update);

        VBox.setVgrow(pointsEditor, Priority.ALWAYS);

        getChildren().addAll(stops, pointsEditor);
    }

    private void updateValue() {
        loaded = new RadialGradient(
                0,
                0,
                Math.min(1, Math.max(center.getX(), 0)),
                Math.min(1, Math.max(center.getY(), 0)),
                Math.min(1, Math.max(radius.getX(), 0)),
                proportional.isSelected(),
                cycleMethod.getValue(),
                stops.getStops()
        );
        setValue(loaded);
    }

    private void load(RadialGradient value) {
        loaded = value;
        stops.clear();
        stops.addAll(value.getStops());
        center.setX(value.getCenterX());
        center.setY(value.getCenterY());
        radius.setX(value.getRadius());
    }

    private static Node createPointDisplay(String text, boolean rectangle, double size) {
        Label label = new Label(text);
        label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 16));
        Shape shape = rectangle ? new Rectangle(size, size) : new Circle(size / 2);
        BorderPane pane = new BorderPane(label);
        if (text.isEmpty()) {
            pane.getChildren().remove(label);
            pane.setMaxSize(size, size);
        }
        pane.setMinSize(size, size);
        pane.setBackground(NodeMisc.simpleBackground(Color.DARKGRAY));
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
