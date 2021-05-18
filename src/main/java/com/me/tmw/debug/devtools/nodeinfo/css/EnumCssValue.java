package com.me.tmw.debug.devtools.nodeinfo.css;

import javafx.beans.binding.Bindings;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;

public class EnumCssValue<T extends Enum<T>> extends CssValue<T> {

    protected final ComboBox<T> comboBox = new ComboBox<>();

    EnumCssValue(Class<T> enumClass, T initialValue, Runnable update) {
        super(initialValue, update);
        comboBox.disableProperty().bind(Bindings.not(editable));

        comboBox.getItems().addAll(enumClass.getEnumConstants());
        comboBox.getSelectionModel().select(initialValue);

        comboBox.getStyleClass().add("enum-property");

        comboBox.selectionModelProperty().get().selectedItemProperty().addListener((observable, oldValue, newValue) -> update.run());
    }

    @Override
    public String toCssString() {
        return comboBox.getValue().toString().toLowerCase().replaceAll("_", "-");
    }

    @Override
    public Parent node() {
        return comboBox;
    }

    @Override
    public T genAlt() {
        return comboBox.getValue();
    }
}
