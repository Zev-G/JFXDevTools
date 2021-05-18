package com.me.tmw.debug.devtools.nodeinfo.css;

import com.me.tmw.nodes.control.FillWidthTextField;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.Arrays;

public class InsetsCssValue extends CssValue<Insets> {

    private final TextField value = new FillWidthTextField();

    InsetsCssValue(Insets initialValue, Runnable updateNode) {
        super(initialValue, updateNode);
        value.disableProperty().bind(Bindings.not(editable));
        value.getStyleClass().addAll("property-value", "css-property");

        value.setText(insetsToString(initialValue));

        value.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                updateNode.run();
            }
        });
    }

    public static String insetsToString(Insets insets) {
        if (insets.getLeft() == insets.getRight() && insets.getTop() == insets.getBottom()) {
            if (insets.getLeft() == insets.getTop()) {
                return String.valueOf(insets.getLeft());
            } else {
                return insets.getLeft() + " " + insets.getTop();
            }
        } else {
            return insets.getTop() + " " + insets.getRight() + " " + insets.getBottom() + " " + insets.getLeft();
        }
    }

    public static Insets insetsFromString(String string) {
        string = string.replaceAll(",", " ").replaceAll(" +", " ");
        Double[] values = Arrays.stream(string.split(" ")).map(Double::parseDouble).toArray(Double[]::new);
        if (values.length == 1) {
            return new Insets(values[0]);
        } else if (values.length == 2) {
            return new Insets(values[0], values[1], values[0], values[1]);
        } else if (values.length == 3) {
            return new Insets(values[0], values[1], values[2], 0);
        } else if (values.length >= 4) {
            return new Insets(values[0], values[1], values[2], values[3]);
        } else {
            return Insets.EMPTY;
        }
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
    public Insets genAlt() {
        return insetsFromString(node().getText());
    }
}
