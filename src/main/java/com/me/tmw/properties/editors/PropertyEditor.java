package com.me.tmw.properties.editors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;

import java.util.Optional;

public interface PropertyEditor<T> extends ObservableValue<T>, WritableValue<T> {

    ObservableList<PropertyEditor<T>> getChildren();

    StringProperty nameProperty();
    default String getName() {
        return nameProperty().get();
    }
    default void setName(String name) {
        nameProperty().set(name);
    }

    ObjectProperty<Node> graphicProperty();
    default Node getGraphic() {
        return graphicProperty().get();
    }
    default void setGraphic(Node node) {
        graphicProperty().set(node);
    }

    Node getNode();

    ReadOnlyProperty<ContentDisplay> contentDisplayProperty();
    default ContentDisplay getContentDisplay() {
        return contentDisplayProperty().getValue();
    }

    Optional<Class<?>> getPropertyType();
    void setPropertyType(Class<?> propertyType);

}
