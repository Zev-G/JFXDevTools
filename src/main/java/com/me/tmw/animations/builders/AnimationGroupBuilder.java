package com.me.tmw.animations.builders;

import com.me.tmw.animations.AnimationBuilderBase;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class AnimationGroupBuilder extends AnimationBuilderBase<SequentialTransition, AnimationGroupBuilder> {

    private final List<AnimationBuilder<?, ?>> animations = new ArrayList<>();
    private ParallelBuilder currentSegment = new ParallelBuilder();

    public AnimationGroupBuilder() {
        this(null);
    }

    public AnimationGroupBuilder(Node node) {
        super(node);
    }

    private void recycleCurrentTransition() {
        if (currentSegment != null && !currentSegment.getChildren().isEmpty()) {
            if (currentSegment.getChildren().size() == 1) {
                animations.add(currentSegment.getChildren().get(0));
            } else {
                animations.add(currentSegment);
            }
            currentSegment = new ParallelBuilder();
        }
    }

    public AnimationGroupBuilder add(AnimationBuilder<?, ?> animation) {
        currentSegment.getChildren().add(animation);
        return this;
    }

    public AnimationGroupBuilder thenWait(double millis) {
        return thenWait(Duration.millis(millis));
    }

    public AnimationGroupBuilder thenWait(Duration duration) {
        recycleCurrentTransition();
        add(new PauseBuilder().setDuration(duration));
        recycleCurrentTransition();
        return this;
    }

    public AnimationGroupBuilder thenFinish() {
        recycleCurrentTransition();
        return this;
    }

    public AnimationGroupBuilder fadeBy(double fadeBy, double millis) {
        return fadeBy(fadeBy, Duration.millis(millis));
    }

    public AnimationGroupBuilder fadeBy(double fadeBy, Duration duration) {
        return fadeBy(fadeBy, duration, Interpolator.EASE_BOTH);
    }

    public AnimationGroupBuilder fadeBy(double fadeBy, double millis, Interpolator interpolator) {
        return fadeBy(fadeBy, Duration.millis(millis), interpolator);
    }

    public AnimationGroupBuilder fadeBy(double fadeBy, Duration duration, Interpolator interpolator) {
        return add(new FadeTransitionBuilder().setDuration(duration).setByValue(fadeBy).setInterpolator(interpolator));
    }

    public AnimationGroupBuilder fadeTo(double fadeTo, double millis) {
        return fadeTo(fadeTo, Duration.millis(millis));
    }

    public AnimationGroupBuilder fadeTo(double fadeTo, Duration duration) {
        return fadeTo(fadeTo, duration, Interpolator.EASE_BOTH);
    }

    public AnimationGroupBuilder fadeTo(double fadeTo, double millis, Interpolator interpolator) {
        return fadeTo(fadeTo, Duration.millis(millis), interpolator);
    }

    public AnimationGroupBuilder fadeTo(double fadeTo, Duration duration, Interpolator interpolator) {
        return add(new FadeTransitionBuilder().setToValue(fadeTo).setDuration(duration).setInterpolator(interpolator));
    }

    public AnimationGroupBuilder translateX(double to, double millis) {
        return translateX(to, Duration.millis(millis));
    }

    public AnimationGroupBuilder translateX(double to, Duration duration) {
        return translateX(to, duration, Interpolator.EASE_BOTH);
    }

    public AnimationGroupBuilder translateX(double to, double millis, Interpolator interpolator) {
        return translateX(to, Duration.millis(millis), interpolator);
    }

    public AnimationGroupBuilder translateX(double to, Duration duration, Interpolator interpolator) {
        return add(new TranslateTransitionBuilder().setDuration(duration).setToX(to).setInterpolator(interpolator));
    }

    public AnimationGroupBuilder translateY(double to, double millis) {
        return translateY(to, Duration.millis(millis));
    }

    public AnimationGroupBuilder translateY(double to, Duration duration) {
        return translateY(to, duration, Interpolator.EASE_BOTH);
    }

    public AnimationGroupBuilder translateY(double to, double millis, Interpolator interpolator) {
        return translateY(to, Duration.millis(millis), interpolator);
    }

    public AnimationGroupBuilder translateY(double to, Duration duration, Interpolator interpolator) {
        TranslateTransition translateY = new TranslateTransition(duration);
        translateY.setToY(to);
        translateY.setInterpolator(interpolator);
        return add(new TranslateTransitionBuilder().setDuration(duration).setToY(to).setInterpolator(interpolator));
    }

    public AnimationGroupBuilder scale(double to, double millis) {
        return scale(to, Duration.millis(millis));
    }

    public AnimationGroupBuilder scale(double to, Duration duration) {
        return scale(to, duration, Interpolator.EASE_BOTH);
    }

    public AnimationGroupBuilder scale(double to, double millis, Interpolator interpolator) {
        return scale(to, Duration.millis(millis), interpolator);
    }

    public AnimationGroupBuilder scale(double to, Duration duration, Interpolator interpolator) {
        return add(new ScaleTransitionBuilder().setDuration(duration).setToX(to).setToY(to).setInterpolator(interpolator));
    }

    public AnimationGroupBuilder scaleX(double to, double millis) {
        return scaleX(to, Duration.millis(millis));
    }

    public AnimationGroupBuilder scaleX(double to, Duration duration) {
        return scaleX(to, duration, Interpolator.EASE_BOTH);
    }

    public AnimationGroupBuilder scaleX(double to, double millis, Interpolator interpolator) {
        return scaleX(to, Duration.millis(millis), interpolator);
    }

    public AnimationGroupBuilder scaleX(double to, Duration duration, Interpolator interpolator) {
        return add(new ScaleTransitionBuilder().setDuration(duration).setToX(to).setInterpolator(interpolator));
    }

    public AnimationGroupBuilder scaleY(double to, double millis) {
        return scaleY(to, Duration.millis(millis));
    }

    public AnimationGroupBuilder scaleY(double to, Duration duration) {
        return scaleY(to, duration, Interpolator.EASE_BOTH);
    }

    public AnimationGroupBuilder scaleY(double to, double millis, Interpolator interpolator) {
        return scaleY(to, Duration.millis(millis), interpolator);
    }

    public AnimationGroupBuilder scaleY(double to, Duration duration, Interpolator interpolator) {
        return add(new ScaleTransitionBuilder().setDuration(duration).setToY(to).setInterpolator(interpolator));
    }

    public AnimationGroupBuilder rotate(double to, double millis) {
        return rotate(to, Duration.millis(millis));
    }

    public AnimationGroupBuilder rotate(double to, Duration duration) {
        return rotate(to, duration, Interpolator.EASE_BOTH);
    }

    public AnimationGroupBuilder rotate(double to, double millis, Interpolator interpolator) {
        return rotate(to, Duration.millis(millis), interpolator);
    }

    public AnimationGroupBuilder rotate(double to, Duration duration, Interpolator interpolator) {
        return add(new RotateTransitionBuilder().setDuration(duration).setToAngle(to).setInterpolator(interpolator));
    }

    public <T> AnimationGroupBuilder property(Function<Node, WritableValue<T>> property, T to, double millis) {
        return property(property, to, Duration.millis(millis));
    }

    public <T> AnimationGroupBuilder property(Function<Node, WritableValue<T>> property, T to, Duration duration) {
        return property(property, to, duration, Interpolator.EASE_BOTH);
    }

    public <T> AnimationGroupBuilder property(Function<Node, WritableValue<T>> property, T to, double millis, Interpolator interpolator) {
        return property(property, to, Duration.millis(millis), interpolator);
    }

    public <T> AnimationGroupBuilder property(Function<Node, WritableValue<T>> property, T to, Duration duration, Interpolator interpolator) {
        TimelineBuilder builder = new TimelineBuilder().add(
                new KeyFrameBuilder().setDuration(duration).add(
                        new KeyValueBuilder<T>().setEndValue(to).setInterpolator(interpolator).setWritableValue(property)
                ));
        return add(builder);
    }

    @Override
    public String getName() {
        return "Group";
    }

    @Override
    public SequentialTransition build(Node buildFor) {
        recycleCurrentTransition();
        Animation[] builtChildren = new Animation[animations.size()];
        for (int i = 0; i < animations.size(); i++) {
            builtChildren[i] = animations.get(i).build(buildFor);
        }
        SequentialTransition transition = new SequentialTransition(buildFor, builtChildren);
        super.applyProperties(transition);
        return transition;
    }

    @Override
    public AnimationGroupBuilder getThis() {
        return this;
    }

}
