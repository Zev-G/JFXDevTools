package com.me.tmw.debug.devtools.tabs;

import com.me.tmw.debug.devtools.DevTools;
import com.me.tmw.debug.devtools.DevUtils;
import com.me.tmw.debug.devtools.nodeinfo.css.NodeCss;
import com.me.tmw.debug.devtools.scenetree.SceneTree;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureTab extends Tab {

    private final ObjectProperty<Parent> root = new SimpleObjectProperty<>();
    private final Map<Node, NodeCss> cssPropertiesMap = new HashMap<>();

    private final SceneTree sceneTree;
    private final TextFlow classChain = new TextFlow();
    private final VBox left;

    private final StackPane noCssProperties = new StackPane(new Label("No Css Properties"));
    private final Tab cssTab = new Tab("Css Properties", noCssProperties);
    private final StylesheetsTab stylesheetsTab;
    private final DetailsTab detailsTab;
    private final DevTools tools;
    private final TabPane infoTabPane = new TabPane(cssTab);

    private final SplitPane split = new SplitPane();

    public StructureTab(Parent root, DevTools tools) {
        this.root.set(root);
        this.tools = tools;
        sceneTree = new SceneTree(this.root);
        VBox.setVgrow(sceneTree, Priority.ALWAYS);

        detailsTab = new DetailsTab(this);
        stylesheetsTab = new StylesheetsTab(this);

        infoTabPane.getTabs().addAll(stylesheetsTab, detailsTab);

        classChain.setPadding(new Insets(10));
        classChain.getStyleClass().add("class-chain");
        ScrollPane classChainScrollPane = new ScrollPane(classChain);
        classChainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        classChainScrollPane.getStyleClass().add("class-chain");
        left = new VBox(sceneTree, classChainScrollPane);

        cssTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && sceneTree.getSelectionModel().getSelectedItem() != null && sceneTree.getSelectionModel().getSelectedItem().getValue() instanceof Parent) {
                loadCssTab((Parent) sceneTree.getSelectionModel().getSelectedItem().getValue());
            }
        });
        stylesheetsTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && sceneTree.getSelectionModel().getSelectedItem() != null && sceneTree.getSelectionModel().getSelectedItem().getValue() instanceof Parent) {
                stylesheetsTab.load((Parent) sceneTree.getSelectionModel().getSelectedItem().getValue());
            }
        });
        detailsTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                detailsTab.load(sceneTree.getSelectionModel().getSelectedItem().getValue());
            }
        });

        sceneTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            tools.getConsoleTab().getConsole().requestBinding("selected", newValue == null ? null : newValue.getValue());

            if (newValue != null && newValue.getValue() != null) {
                List<Text> texts = new ArrayList<>();
                for (Class<?> aClass = newValue.getValue().getClass(); aClass.getSuperclass() != null; aClass = aClass.getSuperclass()) {
                    Text text = new Text(DevUtils.getSimpleClassName(aClass));
                    text.getStyleClass().add("class-representative");
                    if (aClass.isInterface() || Modifier.isAbstract(aClass.getModifiers())) {
                        text.getStyleClass().add("abstract-representative");
                    } else {
                        text.getStyleClass().add("concrete-representative");
                    }
                    texts.add(text);
                    texts.add(new Text("   "));
                }
                classChain.getChildren().setAll(texts);
            } else {
                classChain.getChildren().clear();
            }

            if (newValue != null && newValue.getValue() instanceof Parent) {
                if (infoTabPane.getSelectionModel().getSelectedItem() == cssTab) {
                    loadCssTab((Parent) newValue.getValue());
                }
            } else {
                cssTab.setContent(noCssProperties);
            }
        });

        setContent(split);
        setText("Structure");

        cssTab.setClosable(false);

        split.getItems().addAll(left, infoTabPane);
    }

    private void loadCssTab(Parent value) {
        if (!cssPropertiesMap.containsKey(value)) {
            cssPropertiesMap.put(value, new NodeCss(value));
        }
        ScrollPane scrollPane = new ScrollPane(cssPropertiesMap.get(value));
        scrollPane.getStyleClass().add("node-css");
        scrollPane.getStylesheets().add(NodeCss.STYLE_SHEET);
        cssTab.setContent(scrollPane);
    }

    public SceneTree getSceneTree() {
        return sceneTree;
    }

    public DevTools getTools() {
        return tools;
    }

    public Parent getRoot() {
        return root.get();
    }

    public TabPane getInfoTabPane() {
        return infoTabPane;
    }
}
