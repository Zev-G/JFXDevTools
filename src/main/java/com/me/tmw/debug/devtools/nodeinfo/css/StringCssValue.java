package com.me.tmw.debug.devtools.nodeinfo.css;

import com.me.tmw.nodes.control.FillWidthTextField;
import javafx.beans.binding.Bindings;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

class StringCssValue extends CssValue<String> {

    private final TextField value = new FillWidthTextField();

    public StringCssValue(Runnable updater) {
        this("", updater);
    }

    public StringCssValue(String initialValue, Runnable updater) {
        super(initialValue, updater);
        value.getStyleClass().addAll("property-value", "css-property");
        value.disableProperty().bind(Bindings.not(editable));

        value.textProperty().addListener((observable, oldValue, newValue) -> {
            value.getStyleClass().remove("string");
            if (newValue.trim().matches("\".+?\"")) {
                value.getStyleClass().add("string");
            }
        });

        value.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                updateNode.run();
            }
        });

        value.setText(initialValue);
    }

    @Override
    public String toCssString() {
        return value.getText();
    }

    @Override
    public TextField node() {
        return value;
    }

    @Override
    public String genAlt() {
        return value.getText();
    }
}
