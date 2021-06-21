package com.me.tmw.properties.editors;

import javafx.beans.property.Property;

public class IntegerPropertyEditor extends NumberPropertyEditor<Number> {

    public IntegerPropertyEditor(Property<Number> value) {
        super(value);
    }

    @Override
    protected Integer convertToValue(String text) {
        return Integer.parseInt(text);
    }

}
