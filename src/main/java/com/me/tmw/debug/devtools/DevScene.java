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

    private final Parent subRoot;

    public DevScene(Parent root) {
        super(root(root));
        this.subRoot = root;
        this.splitPane = (SplitPane) getRoot();

        this.splitPane.setOnContextMenuRequested(event -> {
            MenuItem inspect = new MenuItem("Inspect");
            ContextMenu menu = new ContextMenu(inspect);
            inspect.setOnAction(actionEvent -> {
                Node intersected = event.getPickResult().getIntersectedNode();
                getDevTools().getStructureTab().getSceneTree().tryToFocus(intersected);
                if (!getDevTools().isShown()) {
                    attach(getDevTools());
                }
            });
            menu.show(getWindow(), event.getScreenX(), event.getScreenY());
        });
        addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.F12) {
                if (!getDevTools().isShown()) {
                    attach(getDevTools());
                } else {
                    getDevTools().hide();
                }
            }
        });
    }

    private DevTools getDevTools() {
        if (tools == null) {
            tools = new DevTools(subRoot, this);
            tools.getStructureTab().getSceneTree().tryToFocus(subRoot);
        }
        return tools;
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
