package com.me.tmw.nodes.control;

import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.fxmisc.richtext.InlineCssTextField;

public class InlineCssFillWidthTextField extends InlineCssTextField {

    public InlineCssFillWidthTextField() {
        this("");
    }
    public InlineCssFillWidthTextField(String text) {
        textProperty().addListener((ov, prevText, currText) -> {
            Text textNode = new Text(currText + "  ");
            textNode.setFont(new Font(18)); // Set the same font, so the size is the same
            double width = textNode.getLayoutBounds().getWidth() // This is the size of the Text in the TextField
                    + getPadding().getLeft() + getPadding().getRight() // Add the padding of the TextField
                    + 2d; // Add some spacing
            setPrefWidth(width); // Set the width
            displaceCaret(getCaretPosition()); // If you remove this line, it flashes a little bit
        });
        setText(text);
    }

}
