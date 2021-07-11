package com.me.tmw.debug.devtools.nodeinfo;

import javafx.css.Selector;
import javafx.scene.text.Text;

public class SheetSelector extends Text {

    private final Selector selector;

    public SheetSelector(Selector selector) {
        this.selector = selector;

        setText(selector.toString());
    }

}
