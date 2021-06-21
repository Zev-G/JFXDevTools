package com.me.tmw.properties;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Paint;

public class PaintProperty extends SimpleObjectProperty<Paint> {

    public PaintProperty() {
    }

    public PaintProperty(String name) {
        super(null, name);
    }
    public PaintProperty(Object bean) {
        super(bean, "paint");
    }
    public PaintProperty(Paint initialValue) {
        super(initialValue, "paint");
    }

    public PaintProperty(Object bean, String name) {
        super(bean, name);
    }

    public PaintProperty(Object bean, String name, Paint initialValue) {
        super(bean, name, initialValue);
    }

}
