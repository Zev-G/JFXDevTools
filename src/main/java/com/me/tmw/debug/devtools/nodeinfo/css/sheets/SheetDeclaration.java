package com.me.tmw.debug.devtools.nodeinfo.css.sheets;

import com.me.tmw.debug.devtools.nodeinfo.css.CssPropertyView;
import com.me.tmw.debug.devtools.nodeinfo.css.CssValue;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.Declaration;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;

public class SheetDeclaration extends FlowPane {

    private final Declaration declaration;

    private final Label property = new Label();
    private final CssValue<?> value;

    public SheetDeclaration(Declaration declaration) {
        this.declaration = declaration;

        property.setText(declaration.getProperty());
        value = CssPropertyView.cssNode(declaration.getParsedValue().convert(new Font(10)), declaration.getParsedValue().getConverter(), () -> {}, new SimpleBooleanProperty(true));

        getChildren().addAll(property, value.node());
    }

}
