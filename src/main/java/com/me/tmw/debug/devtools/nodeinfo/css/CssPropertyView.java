package com.me.tmw.debug.devtools.nodeinfo.css;

import com.me.tmw.debug.devtools.nodeinfo.css.CssPropertiesView.ObservableStyleableProperty;
import com.me.tmw.nodes.control.FillWidthTextField;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.CssParser;
import javafx.css.StyleConverter;
import javafx.css.Stylesheet;
import javafx.css.converter.CursorConverter;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;

public class CssPropertyView extends HBox {

    private final CssParser parser = new CssParser();

    private final Label name = new Label();
    private final StackPane value = new StackPane();
    private final Label type = new Label(" } Type");

    private CssValue<?> cssValue;

    private final ObservableStyleableProperty<?> property;
    private final Runnable updateNode = this::updateNode;

    private final BooleanProperty expanded = new SimpleBooleanProperty(false);

    public CssPropertyView(ObservableStyleableProperty<?> property, Parent node) {
        super();
        getChildren().addAll(name, value, type);
        this.property = property;

        name.setText(property.getCssMetaData().getProperty() + ": ");

        property.addListener(observable -> {
            cssValue = cssNode(property.getValue(), property.getCssMetaData().getConverter(), this);
            value.getChildren().setAll(cssValue.node());
            setAlignment(cssValue.alignment());
        });
        cssValue = cssNode(property.getValue(), property.getCssMetaData().getConverter(), this);
        value.getChildren().setAll(cssValue.node());
        setAlignment(cssValue.alignment());

        String typeString = property.getCssMetaData().getConverter().getClass().getSimpleName().replaceAll("Converter", "");
        type.setText(": " + (
                typeString.isEmpty() ? "Unspecified" : typeString
        ));

        this.getStyleClass().add("property-view");
        name.getStyleClass().addAll("property-name", "css-property");
        value.getStyleClass().addAll("css-property");
        type.getStyleClass().addAll("property-type", "css-property");
    }

    @SuppressWarnings("unchecked")
    private void updateNode() {
        this.requestFocus();
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
            return new BackgroundCssValues((Background) obj, () -> System.out.println("asdf"), expanded);
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
