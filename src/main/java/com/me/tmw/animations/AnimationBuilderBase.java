package com.me.tmw.animations;

import com.me.tmw.animations.builders.AnimationBuilder;
import javafx.animation.Animation;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public abstract class AnimationBuilderBase<A extends Animation, T extends AnimationBuilderBase<A, T>> implements AnimationBuilder<A, T> {

    private final Map<String, Property<?>> propertyMap = new HashMap<>();
    private final ObjectProperty<Node> node = new SimpleObjectProperty<>(this, "node");

    private final ObjectProperty<EventHandler<ActionEvent>> onFinished = new SimpleObjectProperty<>(this, "onFinished");
    private final ObjectProperty<Duration> delay = new SimpleObjectProperty<>(this, "delay");
    private final BooleanProperty autoReverse = new SimpleBooleanProperty(this, "autoReverse");
    private final IntegerProperty cycleCount = new SimpleIntegerProperty(this, "cycleCount", 1);
    private final DoubleProperty rate = new SimpleDoubleProperty(this, "rate", 1D);

    public AnimationBuilderBase() {
        this(null);
    }

    public AnimationBuilderBase(Node initialNode) {
        this.node.set(initialNode);
        loadInProperties(node, onFinished, delay, autoReverse, cycleCount, rate);
    }

    protected abstract T getThis();

    @Override
    public Node getNode() {
        return node.get();
    }

    public T setNode(Node node) {
        this.node.set(node);
        return getThis();
    }

    @Override
    public ObjectProperty<Node> nodeProperty() {
        return node;
    }

    @Override
    public EventHandler<ActionEvent> getOnFinished() {
        return onFinished.get();
    }

    @Override
    public ObjectProperty<EventHandler<ActionEvent>> onFinishedProperty() {
        return onFinished;
    }

    @Override
    public T setOnFinished(EventHandler<ActionEvent> onFinished) {
        this.onFinished.set(onFinished);
        return getThis();
    }

    @Override
    public Duration getDelay() {
        return delay.get();
    }

    @Override
    public ObjectProperty<Duration> delayProperty() {
        return delay;
    }

    @Override
    public T setDelay(Duration delay) {
        this.delay.set(delay);
        return getThis();
    }

    @Override
    public boolean isAutoReverse() {
        return autoReverse.get();
    }

    @Override
    public BooleanProperty autoReverseProperty() {
        return autoReverse;
    }

    @Override
    public T setAutoReverse(boolean autoReverse) {
        this.autoReverse.set(autoReverse);
        return getThis();
    }

    @Override
    public int getCycleCount() {
        return cycleCount.get();
    }

    @Override
    public IntegerProperty cycleCountProperty() {
        return cycleCount;
    }

    @Override
    public T setCycleCount(int cycleCount) {
        this.cycleCount.set(cycleCount);
        return getThis();
    }

    @Override
    public double getRate() {
        return rate.get();
    }

    @Override
    public DoubleProperty rateProperty() {
        return rate;
    }

    @Override
    public T setRate(double rate) {
        this.rate.set(rate);
        return getThis();
    }

    @Override
    public Map<String, Property<?>> getProperties() {
        return propertyMap;
    }

    protected void loadInProperties(Property<?>... properties) {
        for (Property<?> property : properties) {
            propertyMap.put(property.getName(), property);
        }
    }

    protected void applyProperties(A transition) {
        if (onFinished.get() != null)
            transition.setOnFinished(onFinished.get());
        if (delay.get() != null)
            transition.setDelay(delay.get());
        transition.setAutoReverse(autoReverse.get());
        transition.setCycleCount(cycleCount.get());
        transition.setRate(rate.get());
    }

}
