package com.me.tmw.nodes.control.paint;

import com.me.tmw.nodes.util.NodeMisc;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

import java.util.Optional;

public class StopView extends VBox {

    public static final EventType<ActionEvent> REMOVED = new EventType<>(Event.ANY, "removed");

    private final ObjectProperty<EventHandler<ActionEvent>> onRemoved = new SimpleObjectProperty<>(this, "onRemoved");
    private Stop lastStop = null;
    private final ObjectProperty<Stop> stop;

    private final ColorPickerMiniView colorPicker = new ColorPickerMiniView();

    private final TextField stopPoint = new TextField();
    private final Button removeStop = new Button("Remove");
    private final HBox below = new HBox(stopPoint, removeStop);

    public StopView() {
        this(new SimpleObjectProperty<>());
    }
    public StopView(Stop initialValue) {
        this(new SimpleObjectProperty<>(initialValue));
    }
    public StopView(ObjectProperty<Stop> stop) {
        this.stop = stop;

        // Add children
        getChildren().addAll(colorPicker, below);

        // Make stop settable
        NodeMisc.runAndAddListener(stop, observable -> {
            Stop currentStop = getStop();
            if (currentStop != lastStop) {
                loadStop(currentStop);
            }
        });

        // CSS things
        getStyleClass().add("stop-line");
        below.getStyleClass().add("stop-line");

        // Configure components
        stopPoint.setPromptText("Stop point, i.e. 10%");

        // Configure self
        setPrefSize(75, 75);

        // Event handlers and listeners
        removeStop.setOnAction(event -> fireEvent(new ActionEvent(this, this)));
        InvalidationListener anyRelevantChanged = observable -> setStopSilently(tryAndGenerateStop().orElse(null));
        stopPoint.textProperty().addListener(anyRelevantChanged);
        colorPicker.colorProperty().addListener(anyRelevantChanged);

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

    private Optional<Stop> tryAndGenerateStop() {
        Color color = colorPicker.getColor();
        try {
            double offset = Double.parseDouble(stopPoint.getText().strip().replace("%", ""));
            return Optional.of(new Stop(offset, color));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void setStopSilently(Stop stop) {
        if (stop == null || !stop.equals(lastStop)) {
            lastStop = stop;
            setStop(stop);
        }
    }
    private void loadStop(Stop stop) {
        lastStop = stop;
        stopPoint.setText(String.valueOf(stop.getOffset()));
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
