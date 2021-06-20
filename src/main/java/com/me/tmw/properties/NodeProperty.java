package com.me.tmw.properties;


import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;
import javafx.scene.Parent;

public class NodeProperty extends ObjectPropertyBase<Node> {

    private final Object bean;
    private final String name;

    public NodeProperty() {
        this(null, "node");
    }
    public NodeProperty(String name) {
        this(null, name);
    }
    public NodeProperty(Object bean) {
        this(bean, "node");
    }
    public NodeProperty(Object bean, String name) {
        this.bean = bean;
        this.name = name;
    }

    public NodeProperty(Node initialValue) {
        this(initialValue, null, "node");
    }
    public NodeProperty(Node initialValue, Object bean) {
        this(initialValue, bean, "node");
    }
    public NodeProperty(Node initialValue, String name) {
        this(initialValue, null, name);
    }
    public NodeProperty(Node initialValue, Object bean, String name) {
        super(initialValue);
        this.bean = bean;
        this.name = name;
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    public Parent createVisualizer() {
        return new Parent() {

            {
                InvalidationListener thisChanged = observable -> getChildren().setAll(get());
                thisChanged.invalidated(NodeProperty.this);
                addListener(thisChanged);
            }

        };
    }

}
