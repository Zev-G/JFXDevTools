package com.me.tmw.nodes.control.paint;

import com.me.tmw.resource.Resources;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

public class ColorPickerMiniView extends Pane {

    private static final String STYLE_SHEET = Resources.NODES.getCss("color-picker-mini-view");

    private final StringProperty text = new SimpleStringProperty(this, "text", "Use Color");
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this, "color");

    private final Button chooseColor = new Button();
    private final ColorPicker picker = new ColorPicker(true);
    private final BorderPane layout = new BorderPane(null, picker, chooseColor, null, null);
    private final Popup popup = new Popup();

    public ColorPickerMiniView() {
        this(Color.WHITE);
    }
    public ColorPickerMiniView(Color ogColor) {
        color.set(ogColor);
        getStylesheets().add(STYLE_SHEET);

        color.bind(picker.customColorProperty());
        chooseColor.textProperty().bind(text);

        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        layout.setPadding(new Insets(5, 5, 20, 5));
        layout.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(15), Insets.EMPTY)));

        popup.getContent().add(layout);

        getStyleClass().add("color-picker-mini-view");
        setPrefSize(15, 15);
        backgroundProperty().bind(Bindings.createObjectBinding(
                () -> new Background(new BackgroundFill(color.get(), CornerRadii.EMPTY, Insets.EMPTY)),
                color
        ));
        setOnMousePressed(event -> {
            popup.show(getScene().getWindow());
            popup.setX(event.getScreenX());
            popup.setY(event.getScreenY());
        });

        chooseColor.setOnAction(event -> popup.hide());
    }

    public void removeButton() {
        layout.getChildren().remove(chooseColor);
    }

    public String getText() {
        return text.get();
    }
    public StringProperty textProperty() {
        return text;
    }
    public void setText(String text) {
        this.text.set(text);
    }

    public Color getColor() {
        return color.get();
    }
    public ObjectProperty<Color> colorProperty() {
        return color;
    }
    public void setColor(Color color) {
        this.picker.setCurrentColor(color);
    }
}
