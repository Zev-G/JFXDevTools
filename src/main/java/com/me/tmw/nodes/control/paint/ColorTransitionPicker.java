package com.me.tmw.nodes.control.paint;

import com.me.tmw.nodes.control.Arrow;
import com.me.tmw.nodes.util.NodeMisc;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class ColorTransitionPicker extends GridPane {

    private final ObjectProperty<Color> fromValue = new SimpleObjectProperty<>(this, "from");
    private final ObjectProperty<Color> toValue   = new SimpleObjectProperty<>(this, "to");

    private final ColorPickerMiniView fromColor = new ColorPickerMiniView(fromValue.get());
    private final ColorPickerMiniView toColor = new ColorPickerMiniView(toValue.get());

    private final Arrow arrow = new Arrow(30, 4);

    public ColorTransitionPicker() {
        this(Color.WHITE, Color.WHITE);
    }
    public ColorTransitionPicker(Color from, Color to) {
        fromColor.setColor(from);
        toColor.setColor(from);

        fromValue.bind(fromColor.colorProperty());
        toValue.bind(toColor.colorProperty());

        fromColor.removeButton();
        toColor.removeButton();
        fromColor.prefWidthProperty().bind(fromColor.heightProperty());
        toColor.prefWidthProperty().bind(fromColor.heightProperty());

        arrow.setPadding(new Insets(5));

        setHgap(10);

        NodeMisc.addToGridPane(this, Arrays.asList(fromColor, arrow, toColor), x -> x + 1);
    }

    public Color getFromValue() {
        return fromValue.get();
    }
    public ObjectProperty<Color> fromValueProperty() {
        return fromValue;
    }
    public void setFromValue(Color fromValue) {
        this.fromValue.set(fromValue);
    }

    public Color getToValue() {
        return toValue.get();
    }
    public ObjectProperty<Color> toValueProperty() {
        return toValue;
    }
    public void setToValue(Color toValue) {
        this.toValue.set(toValue);
    }

}
