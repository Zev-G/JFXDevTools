package com.me.tmw.properties.editors;

import com.me.tmw.nodes.control.paint.ColorPicker;
import javafx.beans.property.Property;
import javafx.scene.paint.Color;

public class ColorPropertyEditor extends PaintEditorBase<Color> {

    public ColorPropertyEditor(Property<Color> value) {
        this(value.getName(), value);
    }
    public ColorPropertyEditor(String name, Property<Color> value) {
        super(name, value);

        ColorPicker picker = new ColorPicker();
        if (value.getValue() != null) picker.setCustomColor(value.getValue());

        picker.customColorProperty().addListener(observable -> {
            if (!picker.getCustomColor().equals(get())) {
                set(picker.getCustomColor());
            }
        });

        addListener(observable -> {
            if (!picker.getCustomColor().equals(get())) {
                picker.setCurrentColor(get());
            }
        });

        setEditor(picker);
    }

}
