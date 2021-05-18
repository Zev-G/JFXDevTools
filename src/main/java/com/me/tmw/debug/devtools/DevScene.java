package com.me.tmw.debug.devtools;

import com.me.tmw.debug.devtools.nodeinfo.css.NodeCss;
import com.me.tmw.debug.devtools.scenetree.SceneTree;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;

public class DevScene extends Scene {

    private final SplitPane root;

    public static DevScene getInstance(Parent root) {
        return new DevScene(root, root(root));
    }

    private DevScene(Parent root, SplitPane splitPane) {
        super(splitPane);
        this.root = splitPane;

        this.root.setOnContextMenuRequested(event -> {
            MenuItem inspect = new MenuItem("Inspect");
            ContextMenu menu = new ContextMenu(inspect);
            inspect.setOnAction(actionEvent -> {
                Node intersectedTemp = event.getPickResult().getIntersectedNode();
                Parent intersected = intersectedTemp instanceof Parent ? (Parent) intersectedTemp : intersectedTemp.getParent();
                /*this.root.getItems().add(new NodeCss(intersected));*/
                this.root.getItems().add(new SceneTree(this));
            });
            menu.show(getWindow(), event.getScreenX(), event.getScreenY());
        });
    }

    private static SplitPane root(Parent subRoot) {
        return new SplitPane(subRoot);
    }

}
