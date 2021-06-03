package com.me.tmw.debug.devtools.nodeinfo.css;

import javafx.beans.binding.Bindings;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

class FontCssValue extends CssValue<Font> {

    private final StringCssValue style;
    private final StringCssValue size;
    private final StringCssValue family;

    private final HBox items;

    FontCssValue(Font initialValue, Runnable updater) {
        super(initialValue, updater);

        style = new StringCssValue(initialValue.getStyle().replaceAll("Regular", "Normal"), updater);
        size = new StringCssValue(String.valueOf(initialValue.getSize()), updater);
        family = new StringCssValue(initialValue.getFamily(), updater);

        items = new HBox(style.node(), size.node(), family.node());
        items.disableProperty().bind(Bindings.not(editable));
    }

    @Override
    public String toCssString() {
        return style.toCssString() + " " + size.toCssString() + " " + family.toCssString();
    }

    @Override
    public Parent node() {
        return items;
    }
}
