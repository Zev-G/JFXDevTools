package com.me.tmw.debug.devtools.scenetree;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;

public class SceneTree extends TreeView<Node> {

    public SceneTree(Scene scene) {
        this(scene.rootProperty());
    }
    public SceneTree(ObjectProperty<Parent> root) {

    }

}
