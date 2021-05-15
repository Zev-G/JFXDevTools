package com.me.tmw.debug.devtools;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.stage.Popup;

public class Inspector {

    private final ObjectProperty<Node> examined = new SimpleObjectProperty<>();

    private final Popup popup = new Popup();

}
