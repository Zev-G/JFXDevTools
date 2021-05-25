package com.me.tmw.debug.devtools;

import com.me.tmw.debug.devtools.tabs.StructureTab;
import com.me.tmw.resource.Resources;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

public class DevTools extends StackPane {

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("dev-tools");

    private final StructureTab structureTab;
    private final TabPane tabPane = new TabPane();

    public DevTools(Parent root) {
        structureTab = new StructureTab(root);

        tabPane.getTabs().add(structureTab);

        getChildren().add(tabPane);
    }

    public StructureTab getStructureTab() {
        return structureTab;
    }
}
