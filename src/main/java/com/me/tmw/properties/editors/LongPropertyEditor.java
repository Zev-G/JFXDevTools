package com.me.tmw.properties.editors;

import javafx.beans.property.Property;

public class LongPropertyEditor extends NumberPropertyEditor<Number> {

    public LongPropertyEditor(Property<Number> value) {
        super(value);
    }

    @Override
    protected Long convertToValue(String text) {
        return Long.parseLong(text);
    }

}
