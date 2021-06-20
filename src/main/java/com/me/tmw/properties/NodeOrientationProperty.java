package com.me.tmw.properties;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.NodeOrientation;

public class NodeOrientationProperty extends SimpleObjectProperty<NodeOrientation> {

    public NodeOrientationProperty() {
        super();
    }

    public NodeOrientationProperty(NodeOrientation initialValue) {
        super(initialValue);
    }

    public NodeOrientationProperty(Object bean) {
        super(bean, "nodeOrientation");
    }

    public NodeOrientationProperty(String name) {
        super(null, name);
    }

    public NodeOrientationProperty(Object bean, String name) {
        super(bean, name);
    }

    public NodeOrientationProperty(Object bean, String name, NodeOrientation initialValue) {
        super(bean, name, initialValue);
    }

}
