package com.me.tmw.nodes.control.paint;

import com.me.tmw.debug.util.Debugger;
import com.me.tmw.nodes.control.svg.SVG;
import com.me.tmw.nodes.util.NodeMisc;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

import java.util.Objects;
import java.util.Optional;

public class StopView extends HBox {

    public static final EventType<ActionEvent> REMOVED = ActionEvent.ACTION;

    private final ObjectProperty<EventHandler<ActionEvent>> onRemoved = new SimpleObjectProperty<>(this, "onRemoved");
    private final ObjectProperty<Stop> stop;
    private final BooleanProperty usingSlider = new SimpleBooleanProperty(this, "usingSlider", true);
    private Stop lastStop = null;

    private final ColorPickerMiniView colorPicker = new ColorPickerMiniView();

    private final TextField stopPoint = new TextField();
    private final Slider rangePoint = new Slider(0, 100, 0);
    private final Label sliderVal = new Label();
    private final VBox sliderLayout = new VBox(rangePoint, sliderVal);
    private final StackPane pointPlace = new StackPane(sliderLayout);
    private final Button removeStop = new Button("", NodeMisc.svgPath(SVG.X, 0.3));

    public StopView() {
        this(new SimpleObjectProperty<>());
    }
    public StopView(Stop initialValue) {
        this(new SimpleObjectProperty<>(initialValue));
    }
    public StopView(ObjectProperty<Stop> stop) {
        this.stop = stop;

        // Add children
        getChildren().addAll(colorPicker, pointPlace, removeStop);

        // Make stop settable
        NodeMisc.runAndAddListener(stop, observable -> {
            Stop currentStop = getStop();
            if (!Objects.equals(currentStop, lastStop)) {
                loadStop(currentStop);
            }
        });

        // CSS things
        getStyleClass().addAll("stop", "stop-line");

        // Configure components
        stopPoint.setPromptText("Stop point, i.e. 10%");
        VBox.setVgrow(colorPicker, Priority.ALWAYS);
        HBox.setHgrow(stopPoint, Priority.ALWAYS);
        colorPicker.setPrefSize(30, 30);
        sliderVal.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(((int) (rangePoint.getValue() * 1000)) / 1000D), rangePoint.valueProperty()));
        sliderLayout.setAlignment(Pos.CENTER);

        // Event handlers and listeners
        removeStop.setOnAction(event -> fireEvent(new ActionEvent(this, this)));
        InvalidationListener anyRelevantChanged = observable -> setStopSilently(tryAndGenerateStop().orElse(null));
        stopPoint.textProperty().addListener(anyRelevantChanged);
        colorPicker.colorProperty().addListener(anyRelevantChanged);
        rangePoint.valueProperty().addListener(anyRelevantChanged);

        // Add onRemoved property's functionality
        onRemoved.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                removeEventFilter(REMOVED, oldValue);
            }
            if (newValue != null) {
                addEventFilter(REMOVED, newValue);
            }
        });
    }

    public static double ruleFromString(String stopRule) {
        return Double.parseDouble(stopRule.strip().replace("%", "")) / 100;
    }

    private Optional<Stop> tryAndGenerateStop() {
        Color color = colorPicker.getColor();
        try {
            double offset;
            if (usingSlider.get()) {
                offset = rangePoint.getValue() / 100;
            } else {
                offset = ruleFromString(stopPoint.getText());
            }
            return Optional.of(new Stop(offset, color));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void setStopSilently(Stop stop) {
        if (!Objects.equals(stop, lastStop)) {
            lastStop = stop;
            setStop(stop);
        }
    }
    private void loadStop(Stop stop) {
        lastStop = stop;
        stopPoint.setText(String.valueOf(stop.getOffset() * 100));
        rangePoint.setValue(stop.getOffset() * 100);
        colorPicker.setColor(stop.getColor());
    }

    public ObjectProperty<EventHandler<ActionEvent>> onRemovedProperty() {
        return onRemoved;
    }
    public EventHandler<ActionEvent> getOnRemoved() {
        return onRemoved.get();
    }
    public void setOnRemoved(EventHandler<ActionEvent> onRemoved) {
        this.onRemoved.set(onRemoved);
    }

    public ObjectProperty<Stop> stopProperty() {
        return stop;
    }
    public Stop getStop() {
        return stop.get();
    }
    public void setStop(Stop stop) {
        this.stop.set(stop);
    }

}
