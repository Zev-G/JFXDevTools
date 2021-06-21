package com.me.tmw.properties.editors;

import com.me.tmw.nodes.control.FillWidthTextField;
import com.me.tmw.nodes.tooltips.SimpleTooltip;
import javafx.beans.property.Property;

public abstract class TextBasedPropertyEditor<T> extends PropertyEditorBase<T> {

    protected final FillWidthTextField textField = new FillWidthTextField();

    private String ignoreS = null;
    private T ignoreT = null;

    private boolean boundStatus = false;

    public TextBasedPropertyEditor(String name, Property<T> value) {
        super(name, value);

        textField.setText(convertToString(get()));

        addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(ignoreT)) {
                ignoreS = convertToString(newValue);
                textField.setText(ignoreS);
                ignoreS = null;

//                Animations.animator().scale(1.1, 250).thenWait(250).scale(1, 250).play(textField);

            }
        });

        testBound();
        textField.setOnMouseEntered(event -> testBound());
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (testBound()) return;
            if (!newValue.equals(ignoreS)) {
                try {
                    ignoreT = convertToValue(newValue);
                    set(ignoreT);
                    ignoreT = null;
                    removeError();
                } catch (RuntimeException e) {
                    addError();
                }
            } else {
                removeError();
            }
        });
        textField.setPromptText(value.getName());
        SimpleTooltip.install(textField, value.getName());

        setNode(textField);
    }

    private boolean testBound() {
        boolean bound = value.isBound();
        if (bound != boundStatus) {
            boundStatus = bound;
            textField.setDisable(bound);
        }
        return bound;
    }

    private void removeError() {
        textField.getStyleClass().remove("invalid-text");
    }
    private void addError() {
        if (!textField.getStyleClass().contains("invalid-text")) {
            textField.getStyleClass().add("invalid-text");
        }
    }

    protected abstract T convertToValue(String text);
    protected abstract String convertToString(T value);

}
