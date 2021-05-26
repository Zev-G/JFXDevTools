package com.me.tmw.debug.devtools.tabs;

import com.me.tmw.debug.devtools.nodeinfo.css.NodeCss;
import com.me.tmw.debug.devtools.scenetree.SceneTree;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class StructureTab extends Tab {

    private final ObjectProperty<Parent> root = new SimpleObjectProperty<>();
    private final Map<Node, NodeCss> cssPropertiesMap = new HashMap<>();

    private final SceneTree sceneTree;
    private final StackPane noCssProperties = new StackPane(new Label("No Css Properties"));
    private final Tab cssTab = new Tab("Css Properties", noCssProperties);
    private final TabPane infoTabPane = new TabPane(cssTab);

    private final SplitPane split = new SplitPane();

    public StructureTab(Parent root) {
        this.root.set(root);
        sceneTree = new SceneTree(this.root);

        sceneTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue() instanceof Parent) {
                if (!cssPropertiesMap.containsKey(newValue.getValue())) {
                    cssPropertiesMap.put(newValue.getValue(), new NodeCss((Parent) newValue.getValue()));
                }
                cssTab.setContent(new ScrollPane(cssPropertiesMap.get(newValue.getValue())));
            } else {
                cssTab.setContent(noCssProperties);
            }
        });

        setContent(split);
        setText("Structure");

        split.getItems().addAll(sceneTree, infoTabPane);
    }

    public SceneTree getSceneTree() {
        return sceneTree;
    }
}
