package com.me.tmw.properties.editors;

import javafx.beans.property.Property;

public class FloatPropertyEditor extends NumberPropertyEditor<Number> {

    public FloatPropertyEditor(Property<Number> value) {
        super(value);
    }

    @Override
    protected Float convertToValue(String text) {
        return Float.parseFloat(text);
    }

}
