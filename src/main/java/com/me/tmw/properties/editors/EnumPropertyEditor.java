package com.me.tmw.properties.editors;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumPropertyEditor<T extends Enum<T>> extends OptionBasedPropertyEditor<T> {

    public EnumPropertyEditor(Property<T> value, Class<T> type) {
        super(value, FXCollections.observableArrayList(getConstantsAndNull(type)));
    }

    private static <T> List<T> getConstantsAndNull(Class<T> type) {
        List<T> constants = new ArrayList<>(Arrays.asList(type.getEnumConstants()));
        constants.add(null);
        return constants;
    }

}
