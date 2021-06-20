package com.me.tmw.properties.editors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;

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

    ObjectProperty<Node> nodeProperty();
    default Node getNode() {
        return nodeProperty().get();
    }
    default void setNode(Node node) {
        nodeProperty().set(node);
    }

    ReadOnlyProperty<NodeOrientation> nodeOrientationProperty();
    default NodeOrientation getNodeOrientation() {
        return nodeOrientationProperty().getValue();
    }

}
