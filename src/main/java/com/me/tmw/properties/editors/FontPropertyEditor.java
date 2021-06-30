package com.me.tmw.properties.editors;

import com.me.tmw.nodes.control.FontEditor;
import com.me.tmw.resource.Resources;
import javafx.beans.property.Property;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.util.Objects;

public class FontPropertyEditor extends PreviewBasedPropertyEditor<Font> {

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("font-property-editor");

    private final FontEditor editor = new FontEditor();

    public FontPropertyEditor(Property<Font> value) {
        this(value.getName(), value);
    }
    public FontPropertyEditor(String name, Property<Font> value) {
        super(name, value);
        editor.setValue(get());

        editor.valueProperty().addListener(observable -> {
            if (!value.isBound() && !Objects.equals(get(), editor.getValue())) {
                set(editor.getValue());
            }
        });
        value.addListener(observable -> {
            if (!Objects.equals(get(), editor.getValue())) {
                editor.setValue(get());
            }
        });

        editorRoot.getStylesheets().add(STYLE_SHEET);

        Label preview = new Label("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz 0123456789");
        preview.fontProperty().bind(value);

        setPreview(preview);
        setEditor(editor);
    }

}
