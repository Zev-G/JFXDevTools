package com.me.tmw.debug.devtools.scenetree;

import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NodeTreeItem extends TreeItem<Node> {

    private static NodeTreeItem getEmpty() {
        return new NodeTreeItem();
    }

    private final List<NodeTreeItem> notExpandedChildren = new ArrayList<>();
    private final InvalidationListener listListener = list -> {
        Node value = getValue();
        notExpandedChildren.clear();
        if (value instanceof Parent) {
            Parent parent = (Parent) value;
            if (!parent.getChildrenUnmodifiable().isEmpty()) {
                notExpandedChildren.add(getEmpty());
            }
            if (!isExpanded()) {
                getChildren().setAll(notExpandedChildren);
            } else {
                getChildren().setAll(parent.getChildrenUnmodifiable().stream().map(NodeTreeItem::new).collect(Collectors.toList()));
            }
        }
    };

    public NodeTreeItem() {
        this(null);
    }
    public NodeTreeItem(Node node) {
        super(node);
        valueProperty().addListener((observable, oldValue, newValue) -> {
            update(newValue);
            if (oldValue instanceof Parent) {
                ((Parent) oldValue).getChildrenUnmodifiable().removeListener(listListener);
            }
        });
        update(node);

        expandedProperty().addListener((observable, oldValue, newValue) -> {
            Node value = getValue();
            if (newValue) {
                if (value instanceof Parent) {
                    fullyPopulate((Parent) value);
                } else {
                    getChildren().setAll(notExpandedChildren);
                }
            }
        });
    }

    private void fullyPopulate(Parent parent) {
        if (parent != null) {
            getChildren().setAll(parent.getChildrenUnmodifiable().stream().map(NodeTreeItem::new).collect(Collectors.toList()));
        } else {
            getChildren().setAll(notExpandedChildren);
        }
    }

    private void update(Node newValue) {
        getChildren().setAll(notExpandedChildren);
        if (newValue instanceof Parent) {
            ((Parent) newValue).getChildrenUnmodifiable().addListener(listListener);
        }
        listListener.invalidated(null);
    }

}
