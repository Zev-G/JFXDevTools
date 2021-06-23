package com.me.tmw.properties.editors;

import javafx.beans.property.Property;

public final class PropertyEditors {

    @SuppressWarnings("unchecked")
    public static <T> PropertyEditor<T> getEditor(Property<T> property, Class<T> type) {
        if (property == null) {
            return null;
        }
        if (type.isEnum()) {
            return OptionBasedPropertyEditor.fromArray(property, type.getEnumConstants());
        }
        Class<?> usedType = type;
        if (type.isPrimitive()) {
            usedType = toWrapper(type);
        }
        if (Number.class.isAssignableFrom(usedType)) {
            if (usedType == Double.class) {
                return (PropertyEditor<T>) new DoublePropertyEditor((Property<Number>) property);
            } else if (usedType == Integer.class) {
                return (PropertyEditor<T>) new IntegerPropertyEditor((Property<Number>) property);
            } else if (usedType == Long.class) {
                return (PropertyEditor<T>) new LongPropertyEditor((Property<Number>) property);
            } else if (usedType == Float.class) {
                return (PropertyEditor<T>) new FloatPropertyEditor((Property<Number>) property);
            } else {
                return (PropertyEditor<T>) new DoublePropertyEditor((Property<Number>) property);
            }
        }
        if (Boolean.class == usedType) {
            return (PropertyEditor<T>) new BooleanPropertyEditor((Property<Boolean>) property);
        }
        if (String.class == usedType) {
            return (PropertyEditor<T>) new StringPropertyEditor((Property<String>) property);
        }
        return null;
    }

    private static Class<?> toWrapper(Class<?> type) {
        if (type == int.class) {
            return Integer.class;
        } else if (type == byte.class) {
            return Byte.class;
        } else if (type == short.class) {
            return Short.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == double.class) {
            return Double.class;
        } else if (type == char.class) {
            return Character.class;
        } else if (type == boolean.class) {
            return Boolean.class;
        }
        throw new IllegalArgumentException("Type isn't primitive");
    }

}
