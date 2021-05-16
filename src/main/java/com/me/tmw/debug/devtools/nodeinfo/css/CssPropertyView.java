package com.me.tmw.debug.devtools.nodeinfo.css;

import com.me.tmw.debug.devtools.nodeinfo.css.CssPropertiesView.ObservableStyleableProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.CssParser;
import javafx.css.StyleConverter;
import javafx.css.Stylesheet;
import javafx.css.converter.CursorConverter;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;

public class CssPropertyView {

    private final CssParser parser = new CssParser();

    private final Label name = new Label();
    private final VBox nameSubItems = new VBox();
    private final VBox left = new VBox(name);

    private final StackPane value = new StackPane();
    private final Label type = new Label(" } Type");
    private final HBox valueTypeBox = new HBox(value, type);
    private final VBox valueSubItems = new VBox();
    private final VBox right = new VBox(valueTypeBox);

    private CssValue<?> cssValue;

    private final ObservableStyleableProperty<?> property;
    private final Runnable updateNode = this::updateNode;

    private final BooleanProperty expanded = new SimpleBooleanProperty(false);

    public CssPropertyView(ObservableStyleableProperty<?> property, Parent node) {
        super();
        this.property = property;

        expanded.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (!left.getChildren().contains(nameSubItems)) {
                    left.getChildren().add(nameSubItems);
                }
                if (!right.getChildren().contains(valueSubItems)) {
                    right.getChildren().add(valueSubItems);
                }
            } else {
                left.getChildren().remove(nameSubItems);
                right.getChildren().remove(valueSubItems);
            }
        });
        if (expanded.get()) {
            left.getChildren().add(nameSubItems);
            right.getChildren().add(valueSubItems);
        }

        name.setText(property.getCssMetaData().getProperty() + ": ");

        property.addListener(observable -> propertyChanged());
        propertyChanged();

        String typeString = property.getCssMetaData().getConverter().getClass().getSimpleName().replaceAll("Converter", "");
        type.setText(": " + (
                typeString.isEmpty() ? "Unspecified" : typeString
        ));

        nameSubItems.getStyleClass().add("css-property");
        name.getStyleClass().addAll("property-name", "css-property");
        value.getStyleClass().addAll("css-property");
        type.getStyleClass().addAll("property-type", "css-property");
    }

    private void propertyChanged() {
        cssValue = cssNode(property.getValue(), property.getCssMetaData().getConverter(), this);
        value.getChildren().setAll(cssValue.node());
        valueTypeBox.setAlignment(cssValue.alignment());
        nameSubItems.getChildren().clear();
        Node belowName = cssValue.belowName();
        if (belowName != null) {
            nameSubItems.getChildren().setAll(belowName);
        }
        Node belowValue = cssValue.belowValue();
        if (belowValue != null) {
            valueSubItems.getChildren().setAll(belowValue);
        }
    }

    public Parent left() {
        return left;
    }
    public Parent right() {
        return right;
    }

    @SuppressWarnings("unchecked")
    private void updateNode() {
//        this.requestFocus();
        if (cssValue.isUsingAltGen()) {
            property.setValueObj(cssValue.genAlt());
        } else {
            Stylesheet lastStyleSheet = parser.parse("* { " + this + "; }");
            if (!lastStyleSheet.getRules().isEmpty() && !lastStyleSheet.getRules().get(0).getDeclarations().isEmpty()) {
                property.setValueObj(
                        property.getCssMetaData().getConverter().convert(lastStyleSheet.getRules().get(0).getDeclarations().get(0).getParsedValue(), null)
                );
            }
        }
    }

    @Override
    public String toString() {
        return name.getText() + cssValue.toCssString();
    }

    private static <T> CssValue<?> cssNode(T obj, StyleConverter<?, ?> converter, CssPropertyView view) {
        Runnable updateNode = view.updateNode;
        BooleanProperty expanded = view.expanded;
        if (converter instanceof CursorConverter) {
            return new CursorCssValue((Cursor) obj, updateNode);
        }
        if (converter.getClass().getSimpleName().equals("BackgroundConverter")) {
            return new BackgroundCssValues((Background) obj, updateNode, expanded);
        }
        if (converter.getClass().getSimpleName().equals("BorderConverter")) {
            return new BorderCssValues((Border) obj, updateNode, expanded);
        }
        if (obj == null) {
            return new StringCssValue("null", updateNode);
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return new StringCssValue(obj.toString(), updateNode);
        } else if (obj instanceof String || obj instanceof URL) {
            return new StringCssValue('"' + obj.toString() + '"', updateNode);
        } else if (obj instanceof Insets) {
            Insets insets = (Insets) obj;
            return new StringCssValue(insets.getTop() + " " + insets.getRight() + " " + insets.getBottom() + " " + insets.getLeft(), updateNode);
        } else if (obj instanceof Duration) {
            return new StringCssValue(((Duration) obj).toMillis() + "ms", updateNode);
        } else if (obj instanceof Color) {
            Color color = (Color) obj;
            return new ColorCssValue(color, updateNode);
        } else if (obj instanceof Font) {
            return new FontCssValue((Font) obj, updateNode);
        } else if (obj instanceof Enum<?>) {
            Enum<?> enumObj = (Enum<?>) obj;
            return getEnumCssValue(enumObj, updateNode);
        }
        return new StringCssValue(String.valueOf(obj).toLowerCase(), updateNode);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T>> EnumCssValue<T> getEnumCssValue(Enum<?> base, Runnable updater) {
        Enum<T> enumObj = (Enum<T>) base;
        return new EnumCssValue<>((Class<T>) enumObj.getClass(), (T) enumObj, updater);
    }

}
