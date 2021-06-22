package com.me.tmw.nodes.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class LabeledNumberField extends Region {

    private final Label label = new Label();
    private final NumberField field = new NumberField();

    public LabeledNumberField() {
        this("", 0);
    }
    public LabeledNumberField(String text) {
        this(text, 0);
    }
    public LabeledNumberField(double value) {
        this("", value);
    }
    public LabeledNumberField(String text, double value) {
        setText(text);
        setValue(value);

        getChildren().addAll(label, field);
    }

    public Label getLabel() {
        return label;
    }
    public NumberField getField() {
        return field;
    }

    public StringProperty textProperty() {
        return label.textProperty();
    }
    public void setText(String text) {
        label.setText(text);
    }
    public String getText() {
        return label.getText();
    }

    public DoubleProperty valueProperty() {
        return field.valueProperty();
    }
    public void setValue(double value) {
        field.setValue(value);
    }
    public double getValue() {
        return field.getValue();
    }

}
