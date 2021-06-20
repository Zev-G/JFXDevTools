package com.me.tmw.properties.editors;

import com.me.tmw.properties.NodeOrientationProperty;
import com.me.tmw.properties.NodeProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;

public abstract class PropertyEditorBase<T> implements PropertyEditor<T> {

    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final NodeProperty node = new NodeProperty(this);
    private final NodeProperty graphic = new NodeProperty(this, "graphic");
    private final NodeOrientationProperty nodeOrientation = new NodeOrientationProperty(this);

    private final ObservableList<PropertyEditor<T>> children = FXCollections.observableArrayList();

    private final Property<T> value;

    public PropertyEditorBase(String name, Property<T> value) {
        this(name, null, value);
    }
    public PropertyEditorBase(String name, Node node, Property<T> value) {
        this.name.set(name);
        this.node.set(node);
        this.value = value;
    }

    @Override
    public ObservableList<PropertyEditor<T>> getChildren() {
        return children;
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public NodeProperty nodeProperty() {
        return node;
    }

    @Override
    public NodeOrientationProperty nodeOrientationProperty() {
        return nodeOrientation;
    }
    public void setNodeOrientation(NodeOrientation orientation) {
        this.nodeOrientation.set(orientation);
    }

    @Override
    public NodeProperty graphicProperty() {
        return graphic;
    }


    @Override
    public void addListener(ChangeListener<? super T> listener) {
        value.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
        value.removeListener(listener);
    }

    @Override
    public T getValue() {
        return value.getValue();
    }

    public T get() {
        return value.getValue();
    }

    @Override
    public void setValue(T value) {
        this.value.setValue(value);
    }
    public void set(T value) {
        this.value.setValue(value);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        value.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        value.removeListener(listener);
    }
}
