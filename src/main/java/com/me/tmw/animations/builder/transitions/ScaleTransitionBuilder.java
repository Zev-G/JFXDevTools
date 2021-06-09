package com.me.tmw.animations.builder.transitions;

import javafx.animation.ScaleTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;

public final class ScaleTransitionBuilder extends TransitionBuilderBase<ScaleTransition, ScaleTransitionBuilder, Double> {

    private final DoubleProperty toX = new SimpleDoubleProperty(this, "toX", -1);
    private final DoubleProperty fromX = new SimpleDoubleProperty(this, "fromX", -1);
    private final DoubleProperty byX = new SimpleDoubleProperty(this, "byX", -1);

    private final DoubleProperty toY = new SimpleDoubleProperty(this, "toY", -1);
    private final DoubleProperty fromY = new SimpleDoubleProperty(this, "fromY", -1);
    private final DoubleProperty byY = new SimpleDoubleProperty(this, "byY", -1);

    private final DoubleProperty toZ = new SimpleDoubleProperty(this, "toZ", -1);
    private final DoubleProperty fromZ = new SimpleDoubleProperty(this, "fromZ", -1);
    private final DoubleProperty byZ = new SimpleDoubleProperty(this, "byZ", -1);

    public ScaleTransitionBuilder() {
        this(null);
    }

    public ScaleTransitionBuilder(Node node) {
        super(node);
        loadInProperties(toX, fromX, byX, toY, fromY, byY, toZ, fromZ, byZ);
    }

    public double getToX() {
        return toX.get();
    }

    public DoubleProperty toXProperty() {
        return toX;
    }

    public ScaleTransitionBuilder setToX(double toX) {
        this.toX.set(toX);
        return this;
    }

    public double getFromX() {
        return fromX.get();
    }

    public DoubleProperty fromXProperty() {
        return fromX;
    }

    public ScaleTransitionBuilder setFromX(double fromX) {
        this.fromX.set(fromX);
        return this;
    }

    public double getByX() {
        return byX.get();
    }

    public DoubleProperty byXProperty() {
        return byX;
    }

    public ScaleTransitionBuilder setByX(double byX) {
        this.byX.set(byX);
        return this;
    }

    public double getToY() {
        return toY.get();
    }

    public DoubleProperty toYProperty() {
        return toY;
    }

    public ScaleTransitionBuilder setToY(double toY) {
        this.toY.set(toY);
        return this;
    }

    public double getFromY() {
        return fromY.get();
    }

    public DoubleProperty fromYProperty() {
        return fromY;
    }

    public ScaleTransitionBuilder setFromY(double fromY) {
        this.fromY.set(fromY);
        return this;
    }

    public double getByY() {
        return byY.get();
    }

    public DoubleProperty byYProperty() {
        return byY;
    }

    public ScaleTransitionBuilder setByY(double byY) {
        this.byY.set(byY);
        return this;
    }

    public double getToZ() {
        return toZ.get();
    }

    public DoubleProperty toZProperty() {
        return toZ;
    }

    public ScaleTransitionBuilder setToZ(double toZ) {
        this.toZ.set(toZ);
        return this;
    }

    public double getFromZ() {
        return fromZ.get();
    }

    public DoubleProperty fromZProperty() {
        return fromZ;
    }

    public ScaleTransitionBuilder setFromZ(double fromZ) {
        this.fromZ.set(fromZ);
        return this;
    }

    public double getByZ() {
        return byZ.get();
    }

    public DoubleProperty byZProperty() {
        return byZ;
    }

    public ScaleTransitionBuilder setByZ(double byZ) {
        this.byZ.set(byZ);
        return this;
    }

    @Override
    protected ScaleTransitionBuilder getThis() {
        return this;
    }

    @Override
    public String getName() {
        return "Scale Transition";
    }

    @Override
    public ScaleTransition build(Node buildFor) {
        ScaleTransition transition = new ScaleTransition();
        super.applyProperties(transition);

        if (buildFor != null)
            transition.setNode(buildFor);

        if (toX.get() != -1)
            transition.setToX(toX.get());
        if (toY.get() != -1)
            transition.setToY(toY.get());
        if (toZ.get() != -1)
            transition.setToZ(toZ.get());

        if (fromX.get() != -1)
            transition.setFromX(fromX.get());
        if (fromY.get() != -1)
            transition.setFromY(fromY.get());
        if (fromZ.get() != -1)
            transition.setFromZ(fromZ.get());

        if (byX.get() != -1)
            transition.setByX(byX.get());
        if (byY.get() != -1)
            transition.setByY(byY.get());
        if (byZ.get() != -1)
            transition.setByZ(byZ.get());

        return transition;
    }

}
