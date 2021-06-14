package com.me.tmw.debug.uiactions;

import javafx.scene.Node;

import java.util.function.Supplier;

public class EmptyUIAction<T extends Node> extends UIAction<T> {

    EmptyUIAction(T parent) {
        super(parent);
    }

    EmptyUIAction(T parent, UIAction<?> parentAction) {
        super(parent, parentAction);
    }

    public EmptyUIAction(Supplier<T> parent, UIAction<?> parentAction) {
        super(parent, parentAction);
    }

    @Override
    public void run() {

    }

}
