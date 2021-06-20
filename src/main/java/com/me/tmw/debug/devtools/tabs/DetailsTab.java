package com.me.tmw.debug.devtools.tabs;

import com.me.tmw.properties.NodeIntrospector;
import javafx.scene.Node;
import javafx.scene.control.Tab;

public class DetailsTab extends Tab {

    private final StructureTab structureTab;

    public DetailsTab(StructureTab structureTab) {
        super("Details");
        this.structureTab = structureTab;
    }

    public void load(Node node) {
        var properties = NodeIntrospector.getDetailedProperties(node);

    }

}
