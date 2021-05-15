package com.me.tmw.animations.builder;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.Map;

public interface AnimationBuilder<A extends Animation, T extends AnimationBuilder<A, T>> {

    EventHandler<ActionEvent> getOnFinished();
    ObjectProperty<EventHandler<ActionEvent>> onFinishedProperty();
    T setOnFinished(EventHandler<ActionEvent> onFinished);

    Duration getDelay();
    ObjectProperty<Duration> delayProperty();
    T setDelay(Duration duration);

    String getName();
    Map<String, Property<?>> getProperties();

    T setNode(Node node);
    Node getNode();
    ObjectProperty<Node> nodeProperty();

    boolean isAutoReverse();
    BooleanProperty autoReverseProperty();
    T setAutoReverse(boolean autoReverse);

    int getCycleCount();
    IntegerProperty cycleCountProperty();
    T setCycleCount(int cycleCount);

    double getRate();
    DoubleProperty rateProperty();
    T setRate(double rate);

    default void play(Node play) {
        build(play).play();
    }
    default void play() {
        build(getNode()).play();
    }
    A build(Node buildFor);
    default A build() {
        return build(getNode());
    }

}
