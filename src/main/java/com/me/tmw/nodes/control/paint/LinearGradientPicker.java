package com.me.tmw.nodes.control.paint;

import com.me.tmw.nodes.control.FillWidthTextField;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.resource.Resources;
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
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;

public class LinearGradientPicker extends VBox {

    private static final String STYLE_SHEET = Resources.NODES.getCss("linear-gradient-picker");

    private final TextField position = new FillWidthTextField("from 0% 0% to 100% 100%");
    private final ComboBox<CycleMethod> cycleMethods = new ComboBox<>();
    private final HBox topHBox = new HBox(position, cycleMethods);

    private final FlowPane stops = new FlowPane();
    private final Pane resultPreview = new StackPane();

    private final List<ColorPickerMiniView> colorPickers = new ArrayList<>();
    private final List<StringProperty> stopProperties = new ArrayList<>();

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
        stops.getStyleClass().add("stops");
        topHBox.getStyleClass().add("top-layout-hbox");

        VBox.setVgrow(resultPreview, Priority.ALWAYS);

        Button addStop = new Button("Add Stop");
        addStop.setOnAction(event -> addStop());

        getChildren().addAll(topHBox, stops, addStop, resultPreview);

        loadGradient(originalValue);
    }

    private void loadGradient(LinearGradient gradient) {
        position.setText("from " + gradient.getStartX() + "% " + gradient.getStartY() + "% to " + gradient.getEndX() + "% " + gradient.getEndY() + "%");
        for (Stop stop : gradient.getStops()) {
            addStop(stop.getColor(), (stop.getOffset() * 100) + "%");
        }
        cycleMethods.getSelectionModel().select(gradient.getCycleMethod());
    }

    private void updateValue() {
        List<String> stops = new ArrayList<>();
        for (int i = 0; i < colorPickers.size(); i++) {
            stops.add(
                    NodeMisc.colorToCss(colorPickers.get(i).getColor()) + " " + stopProperties.get(i).get()
            );
        }
        CycleMethod cycleMethod = cycleMethods.getValue();
        String text = "linear-gradient(" + (position.getText().isEmpty() ? "" : position.getText() + ", ") + (cycleMethod != null && cycleMethod != CycleMethod.NO_CYCLE ? cycleMethod.toString().toLowerCase() + ", " : "") + String.join(", ", stops) + ")";
        try {
            value.set(LinearGradient.valueOf(text));
            resultPreview.getChildren().clear();
        } catch (Exception ignored) {
            value.set(new LinearGradient(0, 0, 100, 100, true, CycleMethod.NO_CYCLE, new Stop(100, Color.WHITE)));
            resultPreview.getChildren().add(new Label("Error in generating linear gradient"));
        }
    }

    public void addStop() {
        addStop(colorPickers.size(), Color.WHITE, "");
    }

    public void addStop(Color initialColor, String stopRule) {
        addStop(colorPickers.size(), initialColor, stopRule);
    }

    public void addStop(int i, Color initialColor, String stopRule) {
        ColorPickerMiniView colorPicker = new ColorPickerMiniView(initialColor);
        colorPicker.setPrefSize(100, 100);
        colorPicker.colorProperty().addListener(observable -> updateValue());
        colorPickers.add(colorPicker);

        TextField stopString = new TextField(stopRule);
        stopString.textProperty().addListener(observable -> updateValue());
        stopString.setPromptText("Stop point (10%, 20)");
        stopProperties.add(stopString.textProperty());

        Button removeStop = new Button("Remove");

        HBox belowBox = new HBox(stopString, removeStop);
        belowBox.getStyleClass().add("stop-line");
        VBox stop = new VBox(colorPicker, belowBox);
        stop.getStyleClass().add("stop-line");

        removeStop.setOnAction(event -> {
            stopProperties.remove(stopString.textProperty());
            colorPickers.remove(colorPicker);
            stops.getChildren().remove(stop);
            updateValue();
        });

        updateValue();

        stops.getChildren().add(stop);

//        stops.add(colorPicker, 0, i + arrows);
//        stops.add(stopString, 1, i + arrows);
    }

}
