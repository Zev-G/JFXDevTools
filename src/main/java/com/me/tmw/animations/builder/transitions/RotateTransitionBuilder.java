package com.me.tmw.animations.builder.transitions;

import javafx.animation.RotateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;

public final class RotateTransitionBuilder extends TransitionBuilderBase<RotateTransition, RotateTransitionBuilder, Double> {

    private final DoubleProperty fromAngle = new SimpleDoubleProperty(this, "fromAngle", -1);
    private final DoubleProperty toAngle = new SimpleDoubleProperty(this, "toAngle", -1);
    private final DoubleProperty byAngle = new SimpleDoubleProperty(this, "byAngle", -1);

    public RotateTransitionBuilder() {
        this(null);
    }

    public RotateTransitionBuilder(Node node) {
        super(node);
        loadInProperties(fromAngle, toAngle, byAngle);
    }

    @Override
    protected RotateTransitionBuilder getThis() {
        return this;
    }

    @Override
    public String getName() {
        return "Fade Transition";
    }

    public double getFromAngle() {
        return fromAngle.get();
    }

    public DoubleProperty fromAngleProperty() {
        return fromAngle;
    }

    public RotateTransitionBuilder setFromAngle(double fromAngle) {
        this.fromAngle.set(fromAngle);
        return this;
    }

    public double getToAngle() {
        return toAngle.get();
    }

    public DoubleProperty toAngleProperty() {
        return toAngle;
    }

    public RotateTransitionBuilder setToAngle(double toAngle) {
        this.toAngle.set(toAngle);
        return this;
    }

    public double getByAngle() {
        return byAngle.get();
    }

    public DoubleProperty byAngleProperty() {
        return byAngle;
    }

    public RotateTransitionBuilder setByAngle(double byAngle) {
        this.byAngle.set(byAngle);
        return this;
    }

    @Override
    public RotateTransition build(Node buildFor) {
        RotateTransition transition = new RotateTransition();
        super.applyProperties(transition);

        if (buildFor != null)
            transition.setNode(buildFor);
        if (toAngle.get() != -1)
            transition.setToAngle(toAngle.get());
        if (byAngle.get() != -1)
            transition.setByAngle(byAngle.get());
        if (fromAngle.get() != -1)
            transition.setFromAngle(fromAngle.get());

        return transition;
    }

}
