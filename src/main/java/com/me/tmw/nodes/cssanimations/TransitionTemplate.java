package com.me.tmw.nodes.cssanimations;

import com.me.tmw.animations.Animations;
import com.me.tmw.animations.builder.timeline.KeyFrameBuilder;
import com.me.tmw.animations.builder.timeline.KeyValueBuilder;
import javafx.animation.*;
import javafx.beans.property.Property;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;

class TransitionTemplate<T> {

    private final Duration duration;
    private final String property;
    private final T from;
    private final T to;
    private final Interpolator interpolator;

    TransitionTemplate(Duration duration, String property, T from, T to, Interpolator interpolator) {
        this.duration = duration;
        this.property = property;
        this.from = from;
        this.to = to;
        this.interpolator = interpolator;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getProperty() {
        return property;
    }

    public String propertyProperty() {
        return property;
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    void play(Parent node) {
        if (to == null && from == null) return;
        makeAnimation(node)[0].play();
    }

    @SuppressWarnings("unchecked")
    Animation[] makeAnimation(Parent node) {
        StyleableProperty<?> property = ((CssMetaData<Styleable, ?>) node.getCssMetaData().stream()
                .filter(cssMetaData -> cssMetaData.getProperty() != null && !cssMetaData.getProperty().isEmpty())
                .filter(cssMetaData -> cssMetaData.getProperty().equalsIgnoreCase(getProperty().replaceAll(":", "")))
                .findFirst().orElseThrow(IllegalStateException::new)).getStyleableProperty(node);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(duration, keyValue(property, to)));

        Timeline reversed = new Timeline();
        reversed.getKeyFrames().add(new KeyFrame(duration, keyValue(property, property.getValue())));

        System.out.println("returning: " + timeline);
        return new Timeline[]{ timeline, reversed };
    }

    @SuppressWarnings("unchecked")
    private <A> KeyValue keyValue(WritableValue<A> property, Object value) {
        return new KeyValue(property, (A) value);
    }

}
