package com.me.tmw.animations.builder.grouping;

import com.me.tmw.animations.AnimationBuilderBase;
import com.me.tmw.animations.builder.AnimationBuilder;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public final class ParallelBuilder extends AnimationBuilderBase<ParallelTransition, ParallelBuilder> {

    private final List<AnimationBuilder<?, ?>> children = new ArrayList<>();

    public ParallelBuilder add(AnimationBuilder<?, ?> builder) {
        children.add(builder);
        return this;
    }

    public ParallelBuilder remove(AnimationBuilder<?, ?> builder) {
        children.remove(builder);
        return this;
    }

    public List<AnimationBuilder<?, ?>> getChildren() {
        return children;
    }

    @Override
    protected ParallelBuilder getThis() {
        return this;
    }

    @Override
    public String getName() {
        return "Parallel";
    }

    @Override
    public ParallelTransition build(Node buildFor) {
        Animation[] builtChildren = new Animation[children.size()];
        for (int i = 0; i < children.size(); i++) {
            builtChildren[i] = children.get(i).build(buildFor);
        }
        return new ParallelTransition(builtChildren);
    }

}
