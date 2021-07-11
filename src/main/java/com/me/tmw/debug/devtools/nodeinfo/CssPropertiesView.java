package com.me.tmw.debug.devtools.nodeinfo;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CssPropertiesView extends GridPane {

    private static final List<String> BANNED_PROPERTIES = Arrays.asList("visibility", "-fx-effect");

    private final List<ObservableStyleableProperty<?>> properties = new ArrayList<>();

    private final List<CssPropertyView> propertyViews = new ArrayList<>();

    public CssPropertiesView(List<StyleableProperty<?>> list, Parent node) {

        properties.addAll(
                list.stream()
                        .sorted(Comparator.comparing(o -> o.getCssMetaData().getProperty()))
                        .filter(s -> !BANNED_PROPERTIES.contains(s.getCssMetaData().getProperty())).map(t -> new ObservableStyleableProperty<>((StyleableProperty<?>) t)).collect(Collectors.toList())
        );

        List<CssPropertyView> propertyViews = properties.stream().map(s -> new CssPropertyView(s, node)).collect(Collectors.toList());

        for (int i = 0; i < propertyViews.size(); i++) {
            CssPropertyView view = propertyViews.get(i);
            add(view.left(), 0, i);
            add(view.right(), 1, i);
        }
    }

    public List<CssPropertyView> getCssPropertyViews() {
        return propertyViews;
    }

    static class ObservableStyleableProperty<T> implements ObservableValue<T>, StyleableProperty<T> {

        private final ObservableValue<T> observable;
        private final StyleableProperty<T> styleableProperty;

        @SuppressWarnings("unchecked")
        public ObservableStyleableProperty(StyleableProperty<T> styleable) {
            this.styleableProperty = styleable;
            if (styleable instanceof ObservableValue<?>) {
                this.observable = (ObservableValue<T>) styleable;
            } else {
                this.observable = null;
            }
        }

        public ObservableValue<T> getObservable() {
            return observable;
        }

        public StyleableProperty<T> getBackingStyleableProperty() {
            return styleableProperty;
        }

        @Override
        public void addListener(InvalidationListener listener) {
            if (observable != null) {
                observable.addListener(listener);
            }
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            if (observable != null) {
                observable.removeListener(listener);
            }
        }

        @Override
        public void addListener(ChangeListener<? super T> listener) {
            if (observable != null) {
                observable.addListener(listener);
            }
        }

        @Override
        public void removeListener(ChangeListener<? super T> listener) {
            if (observable != null) {
                observable.removeListener(listener);
            }
        }

        @Override
        public void applyStyle(StyleOrigin origin, T value) {
            styleableProperty.applyStyle(origin, value);
        }

        @Override
        public StyleOrigin getStyleOrigin() {
            return styleableProperty.getStyleOrigin();
        }

        @Override
        public CssMetaData<? extends Styleable, T> getCssMetaData() {
            return styleableProperty.getCssMetaData();
        }

        @Override
        public T getValue() {
            return styleableProperty.getValue();
        }

        @Override
        public void setValue(T value) {
            styleableProperty.setValue(value);
        }

        @SuppressWarnings("unchecked")
        public void setValueObj(Object obj) {
            setValue((T) obj);
        }

    }

}
