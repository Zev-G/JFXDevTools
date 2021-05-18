package com.me.tmw.debug.devtools.scenetree;

import com.me.tmw.debug.devtools.inspectors.SimpleInspector;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;

public class SceneTree extends TreeView<Node> {

    private final SimpleInspector inspector = new SimpleInspector();

    public SceneTree(Scene scene) {
        this(scene.rootProperty());
    }
    public SceneTree(ObjectProperty<Parent> root) {
        rootProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && oldValue.getValue() instanceof Parent) {
                ((Parent) oldValue.getValue()).getStylesheets().remove(SimpleInspector.DEFAULT_STYLE_SHEET);
            }
            if (newValue != null && newValue.getValue() instanceof Parent) {
                ((Parent) newValue.getValue()).getStylesheets().add(SimpleInspector.DEFAULT_STYLE_SHEET);
            }
        });

        setCellFactory(param -> new NodeTreeCell(this));
        setRoot(new NodeTreeItem(root.get()));
    }

    public SimpleInspector getInspector() {
        return inspector;
    }
}
