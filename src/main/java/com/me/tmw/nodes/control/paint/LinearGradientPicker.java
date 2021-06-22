package com.me.tmw.nodes.control.paint;

import com.me.tmw.nodes.control.FillWidthTextField;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.resource.Resources;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

import java.util.ArrayList;
import java.util.List;

public class LinearGradientPicker extends VBox {

    private static final String STYLE_SHEET = Resources.NODES.getCss("linear-gradient-picker");

    private final TextField position = new FillWidthTextField("from 0% 0% to 100% 100%");
    private final ComboBox<CycleMethod> cycleMethods = new ComboBox<>();
    private final HBox topHBox = new HBox(position, cycleMethods);

    private final StopsPicker stops = new StopsPicker();
    private final Pane resultPreview = new StackPane();

    private final ObjectProperty<LinearGradient> value = new SimpleObjectProperty<>(this, "value");

    public LinearGradientPicker() {
        this(new LinearGradient(0, 0, 100, 100, true, CycleMethod.NO_CYCLE, new Stop(100, Color.WHITE)));
    }

    public LinearGradientPicker(LinearGradient originalValue) {
        getStyleClass().add("linear-gradient-picker");
        getStylesheets().add(STYLE_SHEET);

        value.set(originalValue);

        cycleMethods.getItems().addAll(CycleMethod.values());
        cycleMethods.valueProperty().addListener(observable -> updateValue());

        resultPreview.backgroundProperty().bind(
                Bindings.createObjectBinding(
                        () -> new Background(new BackgroundFill(value.get(), CornerRadii.EMPTY, Insets.EMPTY)),
                        value
                )
        );

        position.textProperty().addListener(observable -> updateValue());

        resultPreview.getStyleClass().add("result-preview");
        topHBox.getStyleClass().add("top-layout-hbox");

        VBox.setVgrow(resultPreview, Priority.ALWAYS);

        stops.getStops().addListener((InvalidationListener) observable -> updateValue());

        getChildren().addAll(topHBox, stops, resultPreview);

        loadGradient(originalValue);
    }

    private void loadGradient(LinearGradient gradient) {
        position.setText("from " + gradient.getStartX() + "% " + gradient.getStartY() + "% to " + gradient.getEndX() + "% " + gradient.getEndY() + "%");
        for (Stop stop : gradient.getStops()) {
            stops.add(new Stop(StopView.ruleFromString((stop.getOffset() * 100) + "%"), stop.getColor()));
        }
        cycleMethods.getSelectionModel().select(gradient.getCycleMethod());
    }

    private void updateValue() {
        List<String> stopStrings = new ArrayList<>();
        for (Stop stop : this.stops.getStops()) {
            stopStrings.add(
                    NodeMisc.colorToCss(stop.getColor()) + " " + stop.getOffset() + "%"
            );
        }
        CycleMethod cycleMethod = cycleMethods.getValue();
        String text = "linear-gradient(" + (position.getText().isEmpty() ? "" : position.getText() + ", ") + (cycleMethod != null && cycleMethod != CycleMethod.NO_CYCLE ? cycleMethod.toString().toLowerCase() + ", " : "") + String.join(", ", stopStrings) + ")";
        try {
            value.set(LinearGradient.valueOf(text));
            resultPreview.getChildren().clear();
        } catch (Exception ignored) {
            value.set(new LinearGradient(0, 0, 100, 100, true, CycleMethod.NO_CYCLE, new Stop(100, Color.WHITE)));
            resultPreview.getChildren().add(new Label("Error in generating linear gradient"));
        }
    }

    public ObjectProperty<LinearGradient> valueProperty() {
        return value;
    }

    public LinearGradient getValue() {
        return value.get();
    }

}
