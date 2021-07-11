package com.me.tmw.animations.builders;

import javafx.animation.FadeTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;

public final class FadeTransitionBuilder extends TransitionBuilderBase<FadeTransition, FadeTransitionBuilder, Double> {

    private final DoubleProperty fromValue = new SimpleDoubleProperty(this, "fromValue", -1);
    private final DoubleProperty toValue = new SimpleDoubleProperty(this, "toValue", -1);
    private final DoubleProperty byValue = new SimpleDoubleProperty(this, "byValue", -1);

    public FadeTransitionBuilder() {
        this(null);
    }

    public FadeTransitionBuilder(Node node) {
        super(node);
        loadInProperties(fromValue, toValue, byValue);
    }

    @Override
    protected FadeTransitionBuilder getThis() {
        return this;
    }

    @Override
    public String getName() {
        return "Fade Transition";
    }

    public double getFromValue() {
        return fromValue.get();
    }

    public DoubleProperty fromValueProperty() {
        return fromValue;
    }

    public FadeTransitionBuilder setFromValue(double fromValue) {
        this.fromValue.set(fromValue);
        return this;
    }

    public double getToValue() {
        return toValue.get();
    }

    public DoubleProperty toValueProperty() {
        return toValue;
    }

    public FadeTransitionBuilder setToValue(double toValue) {
        this.toValue.set(toValue);
        return this;
    }

    public double getByValue() {
        return byValue.get();
    }

    public DoubleProperty byValueProperty() {
        return byValue;
    }

    public FadeTransitionBuilder setByValue(double byValue) {
        this.byValue.set(byValue);
        return this;
    }

    @Override
    public FadeTransition build(Node buildFor) {
        FadeTransition transition = new FadeTransition();
        super.applyProperties(transition);

        if (buildFor != null)
            transition.setNode(buildFor);
        if (toValue.get() != -1)
            transition.setToValue(toValue.get());
        if (byValue.get() != -1)
            transition.setByValue(byValue.get());
        if (fromValue.get() != -1)
            transition.setFromValue(fromValue.get());

        return transition;
    }

}
