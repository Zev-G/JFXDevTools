package com.me.tmw.debug.devtools.scenetree;

import com.me.tmw.nodes.util.NodeMisc;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class NodeTreeCell extends TreeCell<Node> {

    private BackgroundFill highlight = new BackgroundFill(Color.rgb(100, 100, 100, 0.05), new CornerRadii(10), Insets.EMPTY);

    public NodeTreeCell(SceneTree sceneTree) {
        setOnMouseEntered(event -> {
            if (getItem() != null) {
                ((SceneTree) getTreeView()).getInspector().setExamined(getItem());
            }
        });
        setOnMouseExited(event -> {
            ((SceneTree) getTreeView()).getInspector().setExamined(null);
        });
    }

    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.getClass().getSimpleName());
            if (!getStyleClass().contains("real-tree-cell")) {
                getStyleClass().add("real-tree-cell");
            }
        } else {
            setText(null);
            getStyleClass().remove("real-tree-cell");
        }
    }
}
