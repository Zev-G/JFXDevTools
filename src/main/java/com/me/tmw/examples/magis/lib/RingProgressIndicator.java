package com.me.tmw.examples.magis.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Progress indicator showing a filling arc.
 *
 * @author Andrea Vacondio
 *
 */
public class RingProgressIndicator extends ProgressCircleIndicator {
    public RingProgressIndicator() {
//        this.getStylesheets().add(RingProgressIndicator.class.getResource("ringprogress.css").toExternalForm());
        this.getStyleClass().add("ringindicator");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RingProgressIndicatorSkin(this);
    }

    public final void setRingWidth(int value) {
        ringWidthProperty().set(value);
    }

    public final DoubleProperty ringWidthProperty() {
        return ringWidth;
    }

    public final double getRingWidth() {
        return ringWidthProperty().get();
    }

    /**
     * thickness of the ring indicator.
     */
    private DoubleProperty ringWidth = new StyleableDoubleProperty(22) {
        @Override
        public Object getBean() {
            return RingProgressIndicator.this;
        }

        @Override
        public String getName() {
            return "ringWidth";
        }

        @Override
        public CssMetaData<RingProgressIndicator, Number> getCssMetaData() {
            return StyleableProperties.RING_WIDTH;
        }
    };

    private static class StyleableProperties {
        private static final CssMetaData<RingProgressIndicator, Number> RING_WIDTH = new CssMetaData<RingProgressIndicator, Number>(
                "-fx-ring-width", SizeConverter.getInstance(), 22) {

            @Override
            public boolean isSettable(RingProgressIndicator n) {
                return n.ringWidth == null || !n.ringWidth.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(RingProgressIndicator n) {
                return (StyleableProperty<Number>) n.ringWidth;
            }
        };

        public static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            styleables.addAll(ProgressCircleIndicator.getClassCssMetaData());
            styleables.add(RING_WIDTH);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
}
