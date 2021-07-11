package com.me.tmw.animations.builders;

import com.me.tmw.animations.AnimationBuilderBase;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class TimelineBuilder extends AnimationBuilderBase<Timeline, TimelineBuilder> {

    private final List<KeyFrameBuilder> keyFrames = new ArrayList<>();

    public TimelineBuilder() {
        this(null);
    }

    public TimelineBuilder(Node node) {
        super(node);
    }

    public List<KeyFrameBuilder> getKeyFrames() {
        return keyFrames;
    }

    public TimelineBuilder add(KeyFrameBuilder builder) {
        keyFrames.add(builder);
        return this;
    }

    public TimelineBuilder addAll(KeyFrameBuilder... builders) {
        return addAll(Arrays.asList(builders));
    }

    public TimelineBuilder addAll(Collection<KeyFrameBuilder> builders) {
        keyFrames.addAll(builders);
        return this;
    }

    public TimelineBuilder remove(KeyFrameBuilder builder) {
        keyFrames.remove(builder);
        return this;
    }

    public TimelineBuilder removeAll(KeyFrameBuilder... builders) {
        return removeAll(Arrays.asList(builders));
    }

    public TimelineBuilder removeAll(Collection<KeyFrameBuilder> builders) {
        keyFrames.removeAll(builders);
        return this;
    }

    @Override
    protected TimelineBuilder getThis() {
        return this;
    }

    @Override
    public String getName() {
        return "Timeline";
    }

    @Override
    public Timeline build(Node buildFor) {
        KeyFrame[] builtKeyFrames = new KeyFrame[this.keyFrames.size()];
        for (int i = 0; i < keyFrames.size(); i++) {
            builtKeyFrames[i] = keyFrames.get(i).build(buildFor);
        }
        Timeline timeline = new Timeline(builtKeyFrames);
        super.applyProperties(timeline);
        return timeline;
    }

}
