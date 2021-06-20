package com.me.tmw.properties.editors;

import javafx.beans.property.Property;

public class DoublePropertyEditor extends NumberPropertyEditor<Number> {

    public DoublePropertyEditor(Property<Number> value) {
        super(value);
    }

    @Override
    protected Double convertToValue(String text) {
        return Double.parseDouble(text);
    }

}
