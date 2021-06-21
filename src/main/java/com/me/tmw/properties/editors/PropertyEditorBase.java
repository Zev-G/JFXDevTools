package com.me.tmw.properties.editors;

import com.me.tmw.properties.NodeProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;

import java.util.Optional;

public abstract class PropertyEditorBase<T> implements PropertyEditor<T> {

    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final NodeProperty node = new NodeProperty(this);
    private final NodeProperty graphic = new NodeProperty(this, "graphic");
    private final ObjectProperty<ContentDisplay> contentDisplay = new SimpleObjectProperty<>(this, "contentDisplay", ContentDisplay.RIGHT);

    private final ObservableList<PropertyEditor<T>> children = FXCollections.observableArrayList();
    protected final Property<T> value;

    private Class<?> propertyType;

    public PropertyEditorBase(Property<T> value) {
        this(value.getName(), value);
    }
    public PropertyEditorBase(Node node, Property<T> value) {
        this(value.getName(), node, value);
    }
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
    public ObjectProperty<ContentDisplay> contentDisplayProperty() {
        return contentDisplay;
    }
    public void setContentDisplay(ContentDisplay orientation) {
        this.contentDisplay.set(orientation);
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
        setValue(value);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        value.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        value.removeListener(listener);
    }

    @Override
    public Optional<Class<?>> getPropertyType() {
        return propertyType == null ? Optional.empty() : Optional.of(propertyType);
    }

    @Override
    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

}
