package com.me.tmw.properties.editors;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;

public class EnumPropertyEditor<T extends Enum<T>> extends OptionBasedPropertyEditor<T> {

    public EnumPropertyEditor(Property<T> value, Class<T> type) {
        super(value, FXCollections.observableArrayList(type.getEnumConstants()));
    }

}
