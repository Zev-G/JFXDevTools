package com.me.tmw.nodes.control.paint;

import com.me.tmw.nodes.control.NumberField;
import com.me.tmw.nodes.control.NumberField.ConversionResult;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.properties.ColorProperty;
import com.me.tmw.resource.Resources;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.function.Function;

public abstract class ColorValuesEditor extends HBox {

    private static final String STYLE_SHEET = Resources.NODES.getCss("text-color-editor");

    protected final ColorProperty value = new ColorProperty();
    private boolean colorLock = false;

    public ColorValuesEditor() {
        getStyleClass().add("color-values-editor");
        getStylesheets().add(STYLE_SHEET);
    }

    protected abstract void loadValue(Color color); // Implementations should probably be synchronized
    protected abstract Color genColor(); // Implementations should probably be synchronized

    private synchronized void updateColor() {
        System.out.println("Updating");
        colorLock = true;
        Color newValue = genColor();
        if (!Objects.equals(newValue, value.get())) {
            value.set(newValue);
        }
        setBackground(NodeMisc.simpleBackground(newValue)); // TODO: 2021-06-30 remove this line once RGBPicker is done
        colorLock = false;
    }

    protected final void loadProperty(Property<Number> property, Function<String, ConversionResult> convertor) {
        boolean isIntProperty = property instanceof IntegerProperty;

        var lock = new Object() {
            boolean locked;
            void lock() { locked = true; }
            void unlock() { locked = false; }
            boolean isLocked() { return locked; }
        };

        Label propertyName = new Label(property.getName());
        NumberField editor = new NumberField(property.getValue());
        if (convertor != null) {
            editor.getConversionFunctions().clear();
            editor.getConversionFunctions().add(convertor);
        }
        editor.valueProperty().addListener(observable -> {
            if (!lock.isLocked() && editor.getValue() != property.getValue().doubleValue()) {
                lock.lock();
                property.setValue(editor.getValue());
                lock.unlock();
            }
        });
        property.addListener(observable -> {
            if (!lock.isLocked() && editor.getValue() != property.getValue().doubleValue()) {
                lock.lock();
                editor.setValue(property.getValue().doubleValue());
                lock.unlock();
            }
            if (!colorLock) {
                updateColor();
            }
        });
        editor.setAutoWidth(true);
        propertyName.getStyleClass().addAll("color-property-name", "color-property");
        editor.getStyleClass().addAll("color-property-editor", "color-property");

        if (isIntProperty) editor.setForceIntegers(true);

        getChildren().addAll(propertyName, editor);
    }

    @SafeVarargs
    protected final void loadProperties(Property<Number>... properties) {
        for (Property<Number> property : properties) {
            loadProperty(property, null);
        }
    }

    @SafeVarargs
    protected final void loadProperties(Function<String, ConversionResult> convertor, Property<Number>... properties) {
        for (Property<Number> property : properties) {
            loadProperty(property, convertor);
        }
    }

    public final ColorProperty valueProperty() {
        return value;
    }
    public final void setValue(Color value) {
        this.value.set(value);
    }
    public final Color getValue() {
        return value.get();
    }

}
