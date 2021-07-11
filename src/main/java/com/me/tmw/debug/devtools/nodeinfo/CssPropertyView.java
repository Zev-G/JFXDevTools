package com.me.tmw.debug.devtools.nodeinfo;

import com.me.tmw.debug.devtools.nodeinfo.CssPropertiesView.ObservableStyleableProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.CssParser;
import javafx.css.StyleConverter;
import javafx.css.Stylesheet;
import javafx.css.converter.CursorConverter;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private final BooleanProperty expanded = new SimpleBooleanProperty(this, "expanded", false);
    private final BooleanProperty editable = new SimpleBooleanProperty(this, "editable", false);

    public CssPropertyView(ObservableStyleableProperty<?> property, Parent node) {
        super();
        this.property = property;

        if (property.getBackingStyleableProperty() instanceof Property<?>) {
            EventHandler<MouseEvent> hoverListener = event -> editable.set(!((Property<?>) property.getBackingStyleableProperty()).isBound());
            hoverListener.handle(null);
            left.setOnMouseEntered(hoverListener);
            right.setOnMouseEntered(hoverListener);
        }

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
        cssValue.editable.bind(editable);

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

    public boolean isExpanded() {
        return expanded.get();
    }

    public BooleanProperty expandedProperty() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded.set(expanded);
    }

    public boolean isEditable() {
        return editable.get();
    }

    public BooleanProperty editableProperty() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }

    public CssValue<?> getCssValue() {
        return cssValue;
    }

    @SuppressWarnings("unchecked")
    private void updateNode() {
        if (property.getBackingStyleableProperty() instanceof Property<?> && ((Property<?>) property.getBackingStyleableProperty()).isBound()) {
            return;
        }
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

    public static <T> CssValue<?> cssNode(T obj, StyleConverter<?, ?> converter, CssPropertyView view) {
        return cssNode(obj, converter, view.updateNode, view.expanded);
    }

    public static <T> CssValue<?> cssNode(T obj, StyleConverter<?, ?> converter, Runnable updateNode, BooleanProperty expanded) {
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
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            HBox result = new HBox();
            result.setSpacing(5);
            List<CssValue<?>> values = Arrays.stream(array).map(o -> cssNode(o, converter, updateNode, expanded)).collect(Collectors.toList());
            result.getChildren().addAll(values.stream().map(CssValue::node).collect(Collectors.toList()));
            return new CssValue<>(array, updateNode) {
                @Override
                public String toCssString() {
                    StringBuilder builder = new StringBuilder();
                    for (CssValue<?> value : values) {
                        builder.append(", ").append(value.toCssString());
                    }
                    return builder.toString().isEmpty() ? "" : builder.substring(2);
                }

                @Override
                public Parent node() {
                    return result;
                }
            };
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return new StringCssValue(obj.toString(), updateNode);
        } else if (obj instanceof String || obj instanceof URL) {
            return new StringCssValue('"' + obj.toString() + '"', updateNode);
        } else if (obj instanceof Insets) {
            Insets insets = (Insets) obj;
            return new InsetsCssValue(insets, updateNode);
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
        } else if (obj instanceof Paint) {
            return new ColorCssValue((Paint) obj, updateNode);
        } else if (obj instanceof BorderStrokeStyle) {
            if (obj == BorderStrokeStyle.DASHED) {
                return new StringCssValue("dashed", updateNode);
            } else if (obj == BorderStrokeStyle.DOTTED) {
                return new StringCssValue("dotted", updateNode);
            } else if (obj == BorderStrokeStyle.SOLID) {
                return new StringCssValue("solid", updateNode);
            } else if (obj == BorderStrokeStyle.NONE) {
                return new StringCssValue("none", updateNode);
            } else {
                BorderStrokeStyle strokeStyle = (BorderStrokeStyle) obj;
                String buffer = strokeStyle.getType() + " " +
                        strokeStyle.getLineJoin() + " " +
                        strokeStyle.getLineCap() + " " +
                        strokeStyle.getMiterLimit() + " " +
                        strokeStyle.getDashOffset() + (strokeStyle.getDashArray() != null ?
                        " " + strokeStyle.getDashArray()
                        : "");
                return new StringCssValue(buffer, updateNode);
            }
        } else if (obj.getClass().getSimpleName().equals("Margins")) {
            return new StringCssValue(obj.toString(), updateNode);
        } else if (obj instanceof CornerRadii) {
            CornerRadii fillRadii = (CornerRadii) obj;
            return new InsetsCssValue(new Insets(fillRadii.getTopLeftHorizontalRadius(), fillRadii.getTopRightHorizontalRadius(), fillRadii.getBottomRightHorizontalRadius(), fillRadii.getBottomLeftHorizontalRadius())
                    , updateNode);
        }
//        System.out.println("[CssPropertyView] no check for: {" + obj.getClass() + "} " + obj);
        return new StringCssValue(String.valueOf(obj).toLowerCase(), updateNode);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T>> EnumCssValue<T> getEnumCssValue(Enum<?> base, Runnable updater) {
        Enum<T> enumObj = (Enum<T>) base;
        return new EnumCssValue<>((Class<T>) enumObj.getClass(), (T) enumObj, updater);
    }

}
