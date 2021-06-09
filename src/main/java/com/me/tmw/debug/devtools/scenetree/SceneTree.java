package com.me.tmw.debug.devtools.scenetree;

import com.me.tmw.debug.devtools.inspectors.SimpleInspector;
import com.me.tmw.resource.Resources;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SceneTree extends TreeView<Node> {

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("scene-tree");

    private final SimpleInspector inspector = new SimpleInspector();

    private final DoubleProperty heightEstimation = new SimpleDoubleProperty(20);

    public SceneTree(Scene scene) {
        this(scene.rootProperty());
    }

    public SceneTree(ObjectProperty<Parent> root) {
        getStylesheets().add(STYLE_SHEET);
        getStyleClass().add("scene-tree");

        setCellFactory(param -> new NodeTreeCell(this));
        setRoot(new NodeTreeItem(root.get()));
    }

    public DoubleProperty heightEstimationProperty() {
        return heightEstimation;
    }

    public int count(TreeItem<?> root) {
        return 1 + root.getChildren().stream().filter(child -> child.getValue() != null).mapToInt(this::count).sum();
    }

    public SimpleInspector getInspector() {
        return inspector;
    }

    public final boolean tryToFocus(Node node) {
        List<Node> parents = new ArrayList<>();
        Node at = node;
        parents.add(at);
        while (at.getParent() != null && at != getRoot().getValue()) {
            at = at.getParent();
            parents.add(at);
        }
        TreeItem<Node> on = getRoot();
        for (int i = parents.size() - 2; i >= 0; i--) {
            at = parents.get(i);
            if (!on.isExpanded()) {
                on.setExpanded(true);
            }
            Node finalAt = at;
            Optional<TreeItem<Node>> treeItemOptional = on.getChildren().stream().filter(nodeTreeItem -> nodeTreeItem.getValue() == finalAt).findFirst();
            if (treeItemOptional.isPresent()) {
                on = treeItemOptional.get();
            } else {
                return false;
            }
        }
        if (on != null) {
            getSelectionModel().select(on);
            requestFocus();
        }
        return true;
    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }
}
