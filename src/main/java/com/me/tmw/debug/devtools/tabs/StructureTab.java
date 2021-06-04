package com.me.tmw.debug.devtools.tabs;

import com.me.tmw.debug.devtools.DevTools;
import com.me.tmw.debug.devtools.inspectors.SimpleInspector;
import com.me.tmw.debug.devtools.nodeinfo.css.NodeCss;
import com.me.tmw.debug.devtools.scenetree.SceneTree;
import com.me.tmw.nodes.util.NodeMisc;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class StructureTab extends Tab {

    private final ObjectProperty<Parent> root = new SimpleObjectProperty<>();
    private final Map<Node, NodeCss> cssPropertiesMap = new HashMap<>();

    private final SceneTree sceneTree;
    private final StackPane noCssProperties = new StackPane(new Label("No Css Properties"));
    private final Tab cssTab = new Tab("Css Properties", noCssProperties);
    private final DevTools tools;
    private final TabPane infoTabPane = new TabPane(cssTab);

    private final SplitPane split = new SplitPane();

    public StructureTab(Parent root, DevTools tools) {
        this.root.set(root);
        this.tools = tools;
        sceneTree = new SceneTree(this.root);

        sceneTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            tools.getConsoleTab().getConsole().requestBinding("selected", newValue == null ? null : newValue.getValue());
            if (newValue != null && newValue.getValue() instanceof Parent) {
                if (!cssPropertiesMap.containsKey(newValue.getValue())) {
                    cssPropertiesMap.put(newValue.getValue(), new NodeCss((Parent) newValue.getValue()));
                }
                ScrollPane scrollPane = new ScrollPane(cssPropertiesMap.get(newValue.getValue()));
                scrollPane.getStyleClass().add("node-css");
                scrollPane.getStylesheets().add(NodeCss.STYLE_SHEET);
                cssTab.setContent(scrollPane);
            } else {
                cssTab.setContent(noCssProperties);
            }
        });

        setContent(split);
        setText("Structure");

        cssTab.setClosable(false);

        split.getItems().addAll(sceneTree, infoTabPane);
    }

    public SceneTree getSceneTree() {
        return sceneTree;
    }
}
