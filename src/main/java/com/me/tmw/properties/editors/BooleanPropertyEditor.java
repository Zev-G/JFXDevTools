package com.me.tmw.properties.editors;

import javafx.beans.property.Property;
import javafx.scene.control.CheckBox;

public class BooleanPropertyEditor extends PropertyEditorBase<Boolean> {

    private final CheckBox checkBox = new CheckBox();

    public BooleanPropertyEditor(Property<Boolean> value) {
        this(value.getName(), value);
    }

    public BooleanPropertyEditor(String name, Property<Boolean> value) {
        super(name, value);
        checkBox.setSelected(get());
        checkBox.selectedProperty().addListener(observable -> {
            boolean val = checkBox.isSelected();
            if (val != get()) {
                set(val);
            }
        });
        value.addListener(observable -> {
            boolean val = get();
            if (val != get()) {
                checkBox.setSelected(val);
            }
        });
        setNode(checkBox);
    }

}
