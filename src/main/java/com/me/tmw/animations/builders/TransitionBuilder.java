package com.me.tmw.animations.builders;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.util.Duration;

public interface TransitionBuilder<A extends Transition, T extends TransitionBuilder<A, T, V>, V> extends AnimationBuilder<A, T> {

    Duration getDuration();

    ObjectProperty<Duration> durationProperty();

    T setDuration(Duration duration);

    Interpolator getInterpolator();

    ObjectProperty<Interpolator> interpolatorProperty();

    T setInterpolator(Interpolator interpolator);

}
