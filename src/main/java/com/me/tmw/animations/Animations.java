package com.me.tmw.animations;

import com.me.tmw.animations.builder.AnimationBuilder;
import com.me.tmw.animations.builder.grouping.AnimationGroupBuilder;
import com.me.tmw.animations.builder.timeline.KeyFrameBuilder;
import com.me.tmw.animations.builder.timeline.KeyValueBuilder;
import com.me.tmw.animations.builder.timeline.TimelineBuilder;
import com.me.tmw.animations.builder.transitions.FadeTransitionBuilder;
import com.me.tmw.animations.builder.transitions.RotateTransitionBuilder;
import com.me.tmw.animations.builder.transitions.ScaleTransitionBuilder;
import com.me.tmw.animations.builder.transitions.TranslateTransitionBuilder;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.WritableValue;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.List;

public final class Animations {

    public static AnimationGroupBuilder animate(Node node) {
        return new AnimationGroupBuilder(node);
    }

    public static AnimationGroupBuilder animator() {
        return new AnimationGroupBuilder();
    }

    public static ChangeListener<Boolean> bindAnimation(Animation animation, ObservableBooleanValue property) {
        if (property.get()) {
            animation.stop();
            animation.setRate(1);
            animation.play();
        }
        ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
            if (newValue) {
                animation.setRate(1);
            } else {
                animation.setRate(-1);
            }
            animation.play();
        };
        property.addListener(listener);
        return listener;
    }

    public static FadeTransitionBuilder fade() {
        return new FadeTransitionBuilder();
    }

    public static RotateTransitionBuilder rotate() {
        return new RotateTransitionBuilder();
    }

    public static ScaleTransitionBuilder scale() {
        return new ScaleTransitionBuilder();
    }

    public static TranslateTransitionBuilder translate() {
        return new TranslateTransitionBuilder();
    }

    public static TimelineBuilder timeline() {
        return new TimelineBuilder();
    }

    public static void fadeOn(EventType<?> eventType, Node node, double to, double millis) {
        fadeOn(eventType, node, to, Duration.millis(millis));
    }

    public static void fadeOn(EventType<?> action, Node node, double to, Duration duration) {
        FadeTransition transition = new FadeTransition(duration, node);
        transition.setToValue(to);
        on(action, node, transition);
    }

    public static void on(EventType<?> eventType, Node node, Animation animation) {
        node.addEventHandler(eventType, event -> animation.play());
    }

    public static Animation animateNodesWithDelay(AnimationBuilder<?, ?> animation, List<Node> nodes, double delay, double maxTotal) {
        double total = maxTotal != -1 ? Math.min(maxTotal, nodes.size() * delay) : delay;
        delay = total / nodes.size();
        Animation[] delayedAnimations = new Animation[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            SequentialTransition transition = new SequentialTransition(nodes.get(i), new PauseTransition(Duration.millis(delay * i)), animation.build(nodes.get(i)));
            delayedAnimations[i] = transition;
        }
        return new ParallelTransition(delayedAnimations);
    }

    public static SequentialTransition sequential(Animation... animations) {
        return new SequentialTransition(animations);
    }

    public static ParallelTransition parallel(Animation... animations) {
        return new ParallelTransition(animations);
    }

    public static <T> void animatePropertyChange(WritableValue<T> translateYProperty, T v, double millis) {
        animatePropertyChange(translateYProperty, v, Duration.millis(millis));
    }

    public static <T> void animatePropertyChange(WritableValue<T> translateYProperty, T v, Duration duration) {
        timeline().add(new KeyFrameBuilder().setDuration(duration).add(new KeyValueBuilder<T>().setWritableValue(node -> translateYProperty).setEndValue(v))).play();
    }
}
