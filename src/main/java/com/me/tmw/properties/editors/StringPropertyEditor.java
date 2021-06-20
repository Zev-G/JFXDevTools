package com.me.tmw.properties.editors;

import javafx.beans.property.Property;

public class StringPropertyEditor extends TextBasedPropertyEditor<String> {

    public StringPropertyEditor(Property<String> value) {
        this(value.getName(), value);
    }
    public StringPropertyEditor(String name, Property<String> value) {
        super(name, value);
    }

    @Override
    protected String convertToValue(String text) {
        return text;
    }

    @Override
    protected String convertToString(String value) {
        return value;
    }

}
