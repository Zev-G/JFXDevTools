package com.me.tmw.debug.devtools.nodeinfo.css;

import com.me.tmw.nodes.control.ColorPicker;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.stage.Popup;

public class ColorCssValue extends CssValue<Paint> {

    private final ColorPicker picker = new ColorPicker(true);
    private final Button chooseColor = new Button("Use");
    private final VBox layout = new VBox(chooseColor);
    private final Popup pickerPopup = new Popup();

    private final Pane pickerPreview = new Pane();

    private String paintType = "not-set";

    ColorCssValue(Paint initialValue, Runnable updateNode) {
        super(initialValue, updateNode);

        pickerPreview.setBackground(new Background(new BackgroundFill(initialValue, CornerRadii.EMPTY, Insets.EMPTY)));
        pickerPreview.setPrefSize(15, 15);
        pickerPreview.getStyleClass().add("color-property");
        pickerPreview.setOnMousePressed(event -> {
            pickerPopup.show(pickerPreview.getScene().getWindow());
            pickerPopup.setX(event.getScreenX());
            pickerPopup.setY(event.getScreenY());
        });
        pickerPopup.setAutoHide(true);
        pickerPopup.setHideOnEscape(true);
        layout.setSpacing(10);
        layout.setPadding(new Insets(15, 15, 20, 15));
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(15), Insets.EMPTY)));

        chooseColor.setOnAction(event -> {
            pickerPopup.hide();
            updateNode.run();
        });

        pickerPopup.getContent().add(layout);

        if (initialValue instanceof Color) {
            paintType = "color";
            picker.setCurrentColor((Color) initialValue);
            pickerPreview.backgroundProperty().bind(Bindings.createObjectBinding(
                    () -> new Background(new BackgroundFill(picker.getCustomColor(), CornerRadii.EMPTY, Insets.EMPTY)),
                    picker.customColorProperty()
            ));
            layout.getChildren().add(0, picker);
            layout.setPadding(new Insets(0, 0, 20, 0));
        } else {
            chooseColor.setDisable(true);
            Pane bigPreview = new Pane();
            bigPreview.setPrefSize(300, 300);
            bigPreview.setBackground(pickerPreview.getBackground());
            layout.getChildren().add(0, bigPreview);
            Label uneditableNotice = new Label("Later versions will support editing more paint types");
            uneditableNotice.setWrapText(true);
            layout.getChildren().add(0, uneditableNotice);
        }
    }

    @Override
    public String toCssString() {
        if (paintType.equals("color")) {
            Color color = picker.getCustomColor();
            int red = (int) (color.getRed() * 255);
            int green = (int) (color.getGreen() * 255);
            int blue = (int) (color.getBlue() * 255);
            double opacity = color.getOpacity();
            return "rgba(" + red + ", " + green + ", " + blue + ", " + opacity + ")";
        } else {
            return "transparent";
        }
    }

    @Override
    public Paint genAlt() {
        if (paintType.equals("color")) {
            return picker.getCustomColor();
        } else {
            return initialValue;
        }
    }

    @Override
    public Parent node() {
        return pickerPreview;
    }

}
