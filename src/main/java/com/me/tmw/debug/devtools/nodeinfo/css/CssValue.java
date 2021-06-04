package com.me.tmw.debug.devtools.nodeinfo.css;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;

public abstract class CssValue<T> {

    protected final Runnable updateNode;
    protected final T initialValue;

    protected final BooleanProperty editable = new SimpleBooleanProperty(true);

    CssValue(T initialValue, Runnable updateNode) {
        this.updateNode = updateNode;
        this.initialValue = initialValue;
    }

    public abstract String toCssString();
    public abstract Parent node();

    protected boolean isUsingAltGen() {
        return false;
    }
    public T genAlt() {
        return null;
    }

    public Pos alignment() {
        return Pos.CENTER_LEFT;
    }

    public Node belowName() {
        return null;
    }

    public Node belowValue() {
        return null;
    }

    public boolean isEditable() {
        return editable.get();
    }

    public BooleanProperty editableProperty() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }
}
