package com.me.tmw.properties.editors;

import javafx.beans.property.Property;

public abstract class NumberPropertyEditor<T extends Number> extends TextBasedPropertyEditor<T> {

    public NumberPropertyEditor(Property<T> value) {
        this(value.getName() ,value);
    }
    public NumberPropertyEditor(String name, Property<T> value) {
        super(name, value);
    }

    @Override
    protected String convertToString(T value) {
        return String.valueOf(value);
    }
}
