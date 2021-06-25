package com.me.tmw.properties.editors;

import com.me.tmw.nodes.util.NodeMisc;
import javafx.beans.property.Property;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

public abstract class PaintEditorBase<T extends Paint> extends PreviewBasedPropertyEditor<T> {

    public PaintEditorBase(Property<T> value) {
        this(value.getName(), value);
    }

    public PaintEditorBase(String name, Property<T> value) {
        super(name, value);

        Pane previewColor = new Pane();
        previewColor.setMinSize(35, 20);
        previewColor.backgroundProperty().bind(NodeMisc.backgroundFromProperty(value));
        setPreview(previewColor);
    }

}
