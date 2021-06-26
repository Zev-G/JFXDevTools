package com.me.tmw.properties.editors;

import javafx.beans.property.Property;
import javafx.scene.text.Font;

public class FontPropertyEditor extends PropertyEditorBase<Font> {

    public FontPropertyEditor(Property<Font> value) {
        this(value.getName(), value);
    }
    public FontPropertyEditor(String name, Property<Font> value) {
        super(name, value);
    }

}
