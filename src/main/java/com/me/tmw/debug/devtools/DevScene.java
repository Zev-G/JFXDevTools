package com.me.tmw.debug.devtools;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DevScene extends Scene implements DevToolsContainer {

    private final SplitPane splitPane;

    private DevTools tools;

    public static DevScene getInstance(Parent root) {
        return new DevScene(root, root(root));
    }

    private DevScene(Parent root, SplitPane splitPane) {
        super(splitPane);
        this.splitPane = splitPane;

        this.splitPane.setOnContextMenuRequested(event -> {
            MenuItem inspect = new MenuItem("Inspect");
            ContextMenu menu = new ContextMenu(inspect);
            inspect.setOnAction(actionEvent -> {
                if (tools == null) {
                    tools = new DevTools(root, this);
                }
                Node intersected = event.getPickResult().getIntersectedNode();
                tools.getStructureTab().getSceneTree().tryToFocus(intersected);
                if (!tools.isShown()) {
                    attach(tools);
                }
            });
            menu.show(getWindow(), event.getScreenX(), event.getScreenY());
        });
        addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.F12) {
                if (tools == null) {
                    tools = new DevTools(root, this);
                    tools.getStructureTab().getSceneTree().tryToFocus(root);
                }
                if (!tools.isShown()) {
                    attach(tools);
                } else {
                    tools.hide();
                }
            }
        });
    }

    private static SplitPane root(Parent subRoot) {
        return new SplitPane(subRoot);
    }

    @Override
    public void attach(DevTools tools) {
        if (!this.splitPane.getItems().contains(tools)) {
            this.splitPane.getItems().add(tools);
        }
    }

    @Override
    public void remove(DevTools tools) {
        this.splitPane.getItems().remove(tools);
    }

    @Override
    public boolean isShowing(DevTools tools) {
        return this.splitPane.getItems().contains(tools);
    }
}
