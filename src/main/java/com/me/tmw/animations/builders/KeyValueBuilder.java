package com.me.tmw.animations.builders;

import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;

import java.util.function.Function;

public final class KeyValueBuilder<T> {

    private final ObjectProperty<Function<Node, WritableValue<T>>> writableValue = new SimpleObjectProperty<>(this, "writableValue");
    private final ObjectProperty<T> endValue = new SimpleObjectProperty<>(this, "endValue");
    private final ObjectProperty<Interpolator> interpolator = new SimpleObjectProperty<>(this, "interpolator", Interpolator.EASE_BOTH);

    public KeyValueBuilder() {

    }

    public Function<Node, WritableValue<T>> getWritableValue() {
        return writableValue.get();
    }

    public ObjectProperty<Function<Node, WritableValue<T>>> writableValueProperty() {
        return writableValue;
    }

    public KeyValueBuilder<T> setWritableValue(Function<Node, WritableValue<T>> writableValue) {
        this.writableValue.set(writableValue);
        return this;
    }

    public T getEndValue() {
        return endValue.get();
    }

    public ObjectProperty<T> endValueProperty() {
        return endValue;
    }

    public KeyValueBuilder<T> setEndValue(T endValue) {
        this.endValue.set(endValue);
        return this;
    }

    public Interpolator getInterpolator() {
        return interpolator.get();
    }

    public ObjectProperty<Interpolator> interpolatorProperty() {
        return interpolator;
    }

    public KeyValueBuilder<T> setInterpolator(Interpolator interpolator) {
        this.interpolator.set(interpolator);
        return this;
    }

    public KeyValue build(Node node) {
        return new KeyValue(writableValue.get().apply(node), endValue.get(), interpolator.get());
    }

}
