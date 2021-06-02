package com.me.tmw.debug.devtools;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DevScene extends Scene {

    private final SplitPane root;

    private DevTools tools;

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
                if (tools == null) {
                    tools = new DevTools(root);
                }
                Node intersected = event.getPickResult().getIntersectedNode();
                tools.getStructureTab().getSceneTree().tryToFocus(intersected);
                if (!this.root.getItems().contains(tools)) {
                    this.root.getItems().add(tools);
                }
            });
            menu.show(getWindow(), event.getScreenX(), event.getScreenY());
        });
        addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.F12) {
                if (tools == null) {
                    tools = new DevTools(root);
                    tools.getStructureTab().getSceneTree().tryToFocus(root);
                }
                if (!this.root.getItems().contains(tools)) {
                    this.root.getItems().add(tools);
                } else {
                    this.root.getItems().remove(tools);
                }
            }
        });
    }

    private static SplitPane root(Parent subRoot) {
        return new SplitPane(subRoot);
    }

}
