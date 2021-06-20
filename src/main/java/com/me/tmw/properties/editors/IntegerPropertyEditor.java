package com.me.tmw.properties.editors;

import javafx.beans.property.Property;

public class IntegerPropertyEditor extends NumberPropertyEditor<Integer> {

    public IntegerPropertyEditor(Property<Integer> value) {
        super(value);
    }

    @Override
    protected Integer convertToValue(String text) {
        return Integer.parseInt(text);
    }

}
