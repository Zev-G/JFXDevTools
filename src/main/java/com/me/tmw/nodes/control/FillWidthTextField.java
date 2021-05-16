package com.me.tmw.nodes.control;

import com.sun.javafx.scene.control.skin.Utils;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class FillWidthTextField extends TextField {

    public FillWidthTextField() {
        this("");
    }

    public FillWidthTextField(String text) {
        textProperty().addListener((ov, prevText, currText) -> {
            double calc = calculateWidth();
            setPrefWidth(calc);
            positionCaret(getCaretPosition()); // If you remove this line, it flashes a little bit
        });
        prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            double calc = calculateWidth();
            if (newValue.doubleValue() != calc) {
                setPrefWidth(calc);
            }
        });
        fontProperty().addListener((observable, oldValue, newValue) -> {
            double calc = calculateWidth();
            setPrefWidth(calc);
        });
        HBox.setHgrow(this, Priority.SOMETIMES);
        setText(text);
    }

    public double calculateWidth() {
        return Utils.computeTextWidth(getFont(), getText(), 0D) + getFont().getSize();
    }

}

