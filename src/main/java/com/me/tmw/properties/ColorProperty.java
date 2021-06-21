package com.me.tmw.properties;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class ColorProperty extends SimpleObjectProperty<Color> {

    public ColorProperty() {
    }

    public ColorProperty(Object bean) {
        super(bean, "color");
    }

    public ColorProperty(String name) {
        super(null, name);
    }

    public ColorProperty(Color initialValue) {
        super(initialValue, "color");
    }

    public ColorProperty(Object bean, String name) {
        super(bean, name);
    }

    public ColorProperty(Object bean, String name, Color initialValue) {
        super(bean, name, initialValue);
    }

}
