package com.me.tmw.animations.builder.timeline;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class KeyFrameBuilder {

    private static final Duration DEFAULT_DURATION = Duration.seconds(1);

    private final ObjectProperty<Duration> duration = new SimpleObjectProperty<>(this, "duration", DEFAULT_DURATION);
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final ObjectProperty<EventHandler<ActionEvent>> onFinished = new SimpleObjectProperty<>(this, "onFinished");

    private final List<KeyValueBuilder<?>> keyValueBuilders = new ArrayList<>();

    public KeyFrameBuilder() {

    }

    public Duration getDuration() {
        return duration.get();
    }

    public ObjectProperty<Duration> durationProperty() {
        return duration;
    }

    public KeyFrameBuilder setDuration(Duration duration) {
        this.duration.set(duration);
        return this;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public KeyFrameBuilder setName(String name) {
        this.name.set(name);
        return this;
    }

    public EventHandler<ActionEvent> getOnFinished() {
        return onFinished.get();
    }

    public ObjectProperty<EventHandler<ActionEvent>> onFinishedProperty() {
        return onFinished;
    }

    public KeyFrameBuilder setOnFinished(EventHandler<ActionEvent> onFinished) {
        this.onFinished.set(onFinished);
        return this;
    }

    public List<KeyValueBuilder<?>> getKeyValues() {
        return keyValueBuilders;
    }

    public KeyFrameBuilder add(KeyValueBuilder<?> keyValue) {
        keyValueBuilders.add(keyValue);
        return this;
    }

    public KeyFrameBuilder addAll(KeyValueBuilder<?>... keyValues) {
        return addAll(Arrays.asList(keyValues));
    }

    public KeyFrameBuilder addAll(Collection<KeyValueBuilder<?>> keyValues) {
        keyValueBuilders.addAll(keyValues);
        return this;
    }

    public KeyFrameBuilder removeAll(KeyValueBuilder<?>... keyValues) {
        return removeAll(Arrays.asList(keyValues));
    }

    public KeyFrameBuilder removeAll(Collection<KeyValueBuilder<?>> keyValues) {
        keyValueBuilders.removeAll(keyValues);
        return this;
    }

    public KeyFrameBuilder remove(KeyValueBuilder<?> keyValue) {
        keyValueBuilders.remove(keyValue);
        return this;
    }

    public KeyFrame build(Node node) {
        KeyValue[] keyValues = new KeyValue[keyValueBuilders.size()];
        for (int i = 0; i < keyValueBuilders.size(); i++) {
            keyValues[i] = keyValueBuilders.get(i).build(node);
        }
        return new KeyFrame(duration.get(), name.get(), onFinished.get(), keyValues);
    }

}
