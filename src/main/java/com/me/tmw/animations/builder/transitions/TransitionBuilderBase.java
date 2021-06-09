package com.me.tmw.animations.builder.transitions;

import com.me.tmw.animations.AnimationBuilderBase;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;

import java.lang.reflect.InvocationTargetException;

public abstract class TransitionBuilderBase<A extends Transition, T extends TransitionBuilderBase<A, T, V>, V> extends AnimationBuilderBase<A, T> implements TransitionBuilder<A, T, V> {

    private final ObjectProperty<Duration> duration = new SimpleObjectProperty<>(this, "duration");
    private final ObjectProperty<Interpolator> interpolator = new SimpleObjectProperty<>(this, "interpolator", Interpolator.EASE_BOTH);

    public TransitionBuilderBase() {
        this(null);
    }

    public TransitionBuilderBase(Node node) {
        super(node);
        loadInProperties(duration, interpolator);
    }

    @Override
    public Duration getDuration() {
        return duration.get();
    }

    @Override
    public ObjectProperty<Duration> durationProperty() {
        return duration;
    }

    @Override
    public T setDuration(Duration duration) {
        this.duration.set(duration);
        return getThis();
    }

    @Override
    public Interpolator getInterpolator() {
        return interpolator.get();
    }

    @Override
    public ObjectProperty<Interpolator> interpolatorProperty() {
        return interpolator;
    }

    @Override
    public T setInterpolator(Interpolator interpolator) {
        this.interpolator.set(interpolator);
        return getThis();
    }

    protected void applyProperties(A transition) {
        super.applyProperties(transition);
        transition.setInterpolator(interpolator.get());
        try {
            transition.getClass().getMethod("setDuration", Duration.class).invoke(transition, duration.get());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
        }
    }

}
