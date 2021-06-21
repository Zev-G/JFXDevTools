package com.me.tmw.debug.devtools.tabs;

import com.me.tmw.properties.NodeIntrospector;
import com.me.tmw.properties.editors.PropertyEditor;
import com.me.tmw.properties.editors.PropertyEditors;
import com.me.tmw.properties.editors.PropertyEditorsView;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.effect.Glow;

import java.util.ArrayList;
import java.util.List;

public class DetailsTab extends Tab {

    private final StructureTab structureTab;

    public DetailsTab(StructureTab structureTab) {
        super("Details");
        this.structureTab = structureTab;

        structureTab.getSceneTree().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (structureTab.getInfoTabPane().getSelectionModel().getSelectedItem() == this) {
                load(newValue.getValue());
            }
        });

        setClosable(false);
    }

    public void load(Node node) {
        var properties = NodeIntrospector.getHighDetailedProperties(node);
        List<PropertyEditor<?>> editors = new ArrayList<>();
        for (NodeIntrospector.DetailedProperty detailedProperty : properties) {
            PropertyEditor<?> editor = getCastedEditor(detailedProperty.getProperty(), detailedProperty.getGetter().getReturnType());
            if (editor != null) {
                editor.setPropertyType(detailedProperty.getGetter().getReturnType());
                editors.add(editor);
            }
        }
        setContent(new ScrollPane(new PropertyEditorsView(editors)));
    }

    @SuppressWarnings("unchecked")
    private <T> PropertyEditor<?> getCastedEditor(Property<T> property, Class<?> propertyClass) {
        return PropertyEditors.getEditor(property, (Class<T>) propertyClass);
    }

}
