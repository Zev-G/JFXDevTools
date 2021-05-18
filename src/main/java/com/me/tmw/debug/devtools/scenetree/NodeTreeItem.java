package com.me.tmw.debug.devtools.scenetree;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;

import java.util.stream.Collectors;

public class NodeTreeItem extends TreeItem<Node> {

    public NodeTreeItem() {
        this(null);
    }
    public NodeTreeItem(Node node) {
        super(node);
        valueProperty().addListener((observable, oldValue, newValue) -> {
            update(newValue);
        });
        update(node);
    }

    private void update(Node newValue) {
        if (newValue != null) {
            if (newValue instanceof Parent) {
                getChildren().addAll(((Parent) newValue).getChildrenUnmodifiable().stream().map(NodeTreeItem::new).collect(Collectors.toList()));
            } else {
                getChildren().clear();
            }
        } else {
            getChildren().clear();
        }
    }

}
