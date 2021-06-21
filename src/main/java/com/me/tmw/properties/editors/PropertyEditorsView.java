package com.me.tmw.properties.editors;

import com.me.tmw.nodes.tooltips.SimpleTooltip;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.resource.Resources;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.*;

/**
 * This class loads a set of property editors. This view is generated once with its supplied property editors and is never loaded again.
 */
public class PropertyEditorsView extends GridPane {

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("property-editors");

    private final List<PropertyEditor<?>> editors = new ArrayList<>();

    public PropertyEditorsView(PropertyEditor<?>... editors) {
        this(Arrays.asList(editors));
    }
    public PropertyEditorsView(Collection<PropertyEditor<?>> editors) {
        this.editors.addAll(editors);
        loadAll();
        setHgap(7.5);
        getStylesheets().add(STYLE_SHEET);
    }

    private void loadAll() {
        editors.sort(Comparator.comparing(PropertyEditor::getName));
        int i = 0;
        for (PropertyEditor<?> topEditor : editors) {
            i = loadEditor(topEditor, i, 0);
        }
    }

    private int loadEditor(PropertyEditor<?> editor, int i, int depth) {
        Label name = new Label();
        name.textProperty().bind(editor.nameProperty());
        name.graphicProperty().bind(editor.graphicProperty());
        name.contentDisplayProperty().bind(editor.contentDisplayProperty());

        StackPane nodeWrapper = new StackPane();
        nodeWrapper.setAlignment(Pos.CENTER_LEFT);
        NodeMisc.runAndAddListener(editor.nodeProperty(), observable -> nodeWrapper.getChildren().setAll(editor.getNode()));

        if (editor.getPropertyType().isPresent()) {
            Label type = new Label();
            type.setText(editor.getPropertyType().get().getSimpleName());
            SimpleTooltip.install(type, editor.getPropertyType().get().toString());
            add(type, 0, i);
        }
        add(name, 1, i);
        add(nodeWrapper, 2, i);

        for (PropertyEditor<?> child : editor.getChildren()) {
            i = loadEditor(child, i, depth + 1);
        }

        return i + 1;
    }

}
