package com.me.tmw.nodes.cssanimations;

import javafx.animation.Animation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimatedWrapper extends Parent {

    private final Map<String, Animation[]> propertyAnimationMap = new HashMap<>();

    private static final CssMetaData<AnimatedWrapper, String> TEMPLATE_CSS_META_DATA = new CssMetaData<>("-fxp-transition", StyleConverter.getStringConverter()) {
        @Override
        public boolean isSettable(AnimatedWrapper styleable) {
            return styleable != null;
        }

        @Override
        public StyleableProperty<String> getStyleableProperty(AnimatedWrapper styleable) {
            return styleable.template;
        }
    };

    private final StyleableObjectProperty<String> template = new SimpleStyleableObjectProperty<>(TEMPLATE_CSS_META_DATA, this, "template", "reset") {
        @Override
        protected void invalidated() {
            String template = get();
            for (String command : template.split(",( |)")) {
                processCommand(command);
            }
        }
    };

    private final ObjectProperty<Parent> content = new SimpleObjectProperty<>(this, "content") {
        @Override
        protected void invalidated() {
            if (content.get() != null) {
                getChildren().setAll(content.get());
            } else {
                getChildren().clear();
            }
        }
    };

    public AnimatedWrapper() {
        this(null);
    }
    public AnimatedWrapper(Parent node) {
        content.set(node);
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> metaData = new ArrayList<>(Parent.getClassCssMetaData());
        metaData.add(TEMPLATE_CSS_META_DATA);
        return metaData;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    private void processCommand(String command) {
        if (command.equals("reset")) {
            propertyAnimationMap.values().forEach(animations -> {
                animations[0].stop();
                animations[1].play();
            });
            propertyAnimationMap.clear();
        } else if (command.startsWith("reset")) {
            String property = command.substring("reset".length()).trim();
            if (propertyAnimationMap.containsKey(property)) {
                Animation[] animations = propertyAnimationMap.get(property);
                animations[0].stop();
                animations[1].play();
            }
        } else if (content.get() != null && command.contains("-")) {
            TransitionTemplate<?> transitionTemplate = TransitionTemplateStyleConverter.getInstance().convert(command);
            Animation[] animations = transitionTemplate.makeAnimation(content.get());
            animations[0].play();
            if (propertyAnimationMap.containsKey(transitionTemplate.getProperty())) {
                propertyAnimationMap.get(transitionTemplate.getProperty())[0].stop();
                propertyAnimationMap.get(transitionTemplate.getProperty())[1].stop();
            }
            propertyAnimationMap.put(transitionTemplate.getProperty(), animations);
        }
    }
}
