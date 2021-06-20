package com.me.tmw.debug.devtools.tabs;

import com.me.tmw.properties.NodeIntrospector;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.Tab;

import java.util.List;

public class DetailsTab extends Tab {

    private final StructureTab structureTab;

    public DetailsTab(StructureTab structureTab) {
        super("Details");
        this.structureTab = structureTab;
    }

    public void load(Node node) {
        var properties = NodeIntrospector.getDetailedProperties(node);
        System.out.println(properties);
    }

}
