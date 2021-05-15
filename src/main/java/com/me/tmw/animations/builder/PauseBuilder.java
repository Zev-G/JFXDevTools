package com.me.tmw.animations.builder;

import com.me.tmw.animations.AnimationBuilderBase;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;

public class PauseBuilder extends AnimationBuilderBase<PauseTransition, PauseBuilder> {

    private static final Duration DEFAULT_DUR = new Duration(0);

    private final ObjectProperty<Duration> duration = new SimpleObjectProperty<>(this, "duration", DEFAULT_DUR);

    @Override
    public String getName() {
        return "PauseBuilder";
    }

    public PauseBuilder setDuration(Duration duration) {
        this.duration.set(duration);
        return this;
    }

    public Duration getDuration() {
        return duration.get();
    }

    public ObjectProperty<Duration> durationProperty() {
        return duration;
    }

    @Override
    public PauseTransition build(Node buildFor) {
        PauseTransition pauseTransition = new PauseTransition(duration.get());
        super.applyProperties(pauseTransition);
        return pauseTransition;
    }

    @Override
    public PauseBuilder getThis() {
        return this;
    }
}
