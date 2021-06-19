package com.me.tmw.debug.devtools.scenetree;

import com.me.tmw.debug.devtools.DevTools;
import com.me.tmw.debug.devtools.DevUtils;
import com.me.tmw.debug.devtools.nodeinfo.css.ColorCssValue;
import com.me.tmw.nodes.control.paint.ColorPickerMiniView;
import javafx.beans.binding.*;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class NodeTreeCell extends TreeCell<Node> {

    private static final PseudoClass DARK = PseudoClass.getPseudoClass("dark");

    private final List<Binding<?>> toBeDisposed = new ArrayList<>();

    public NodeTreeCell(SceneTree sceneTree) {
        setOnMouseEntered(event -> {
            if (getItem() != null) {
                ((SceneTree) getTreeView()).getInspector().setExamined(getItem());
            }
        });
        setOnMouseExited(event -> ((SceneTree) getTreeView()).getInspector().setExamined(null));

        setContentDisplay(ContentDisplay.RIGHT);

        heightProperty().addListener((observable, oldValue, newValue) -> {
            if (getTreeView() instanceof SceneTree && ((SceneTree) getTreeView()).heightEstimationProperty().get() < newValue.doubleValue()) {
                ((SceneTree) getTreeView()).heightEstimationProperty().set(newValue.doubleValue());
            }
        });
        if (sceneTree.heightEstimationProperty().get() < getHeight()) {
            sceneTree.heightEstimationProperty().set(getHeight());
        }
    }

    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);
        toBeDisposed.forEach(Binding::dispose);
        toBeDisposed.clear();
        if (item != null) {
            String className = DevUtils.getSimpleClassName(item.getClass());
            if (DevTools.propertiesInSceneTree) {
                setGraphic(genGraphic(item));
            }
            setText(className);
            if (!getStyleClass().contains("real-tree-cell")) {
                getStyleClass().add("real-tree-cell");
            }
        } else {
            setGraphic(null);
            setText(null);
            getStyleClass().remove("real-tree-cell");
        }
    }

    private Node genGraphic(Node item) {
        HBox props = new HBox();
        props.setSpacing(5);

        var generator = new Object(){
            <T> Node generate(ReadOnlyProperty<T> prop, Predicate<T> isDefaultVal) {
                return generate(prop.getName(), prop, isDefaultVal);
            }
            <T> Node generate(String propName, ObservableValue<T> prop, Predicate<T> isDefaultVal) {
                return genStringPropertyView(propName, prop, isDefaultVal, node -> props.getChildren().remove(node), node -> {
                    if (node != null && node.getParent() != props) {
                        props.getChildren().add(node);
                    }
                });
            }

            <P extends Paint> Node generatePaintView(ReadOnlyProperty<P> prop, Predicate<P> isDefaultVal) {
                return generatePaintView(prop.getName(), prop, isDefaultVal);
            }
            <P extends Paint> Node generatePaintView(String propName, ObservableValue<P> prop, Predicate<P> isDefaultVal) {
                Region color = new Region();
                color.setStyle("-fx-min-width: 1em; -fx-min-height: 1em; -fx-border-color: black; -fx-border-radius: 1;");
                ObjectBinding<Background> binding = Bindings.createObjectBinding(() ->
                                new Background(new BackgroundFill(prop.getValue(), CornerRadii.EMPTY, Insets.EMPTY))
                        , prop);
                toBeDisposed.add(binding);
                color.backgroundProperty().bind(binding);
                return genPropertyView(color, propName, prop, isDefaultVal, node -> props.getChildren().remove(node), node -> {
                    if (node != null && node.getParent() != props) {
                        props.getChildren().add(node);
                    }
                });
            }
        };

        generator.generate("styleClass", Bindings.createStringBinding(() -> String.join(" ", item.getStyleClass()), item.getStyleClass()), String::isEmpty);

        Predicate<String> isStringEmpty = string -> string == null || string.isEmpty();
        generator.generate(item.idProperty(), isStringEmpty);
        generator.generate(item.accessibleHelpProperty(), isStringEmpty);
        generator.generate(item.accessibleTextProperty(), isStringEmpty);
        generator.generate(item.accessibleRoleDescriptionProperty(), isStringEmpty);

        Predicate<Number> isZero = number -> number.doubleValue() == 0;
        Predicate<Number> isOne = number -> number.doubleValue() == 1;
        Predicate<Number> isNegativeOne = number -> number.doubleValue() == -1;
        generator.generate(item.rotateProperty(), isZero);
        generator.generate(item.translateXProperty(), isZero);
        generator.generate(item.translateYProperty(), isZero);
        generator.generate(item.translateZProperty(), isZero);

        generator.generate(item.scaleXProperty(), isOne);
        generator.generate(item.scaleYProperty(), isOne);
        generator.generate(item.scaleZProperty(), isOne);
        generator.generate(item.opacityProperty(), isOne);

        Predicate<Boolean> isTrue = bool -> bool;
        Predicate<Boolean> isFalse = bool -> !bool;
        generator.generate(item.disableProperty(), isFalse);
        generator.generate(item.focusedProperty(), isFalse);
        generator.generate(item.hoverProperty(), isFalse);
        generator.generate(item.mouseTransparentProperty(), isFalse);
        generator.generate(item.pressedProperty(), isFalse);

        generator.generate(item.visibleProperty(), isTrue);

        if (item instanceof Shape) {
            Shape asShape = (Shape) item;
            generator.generatePaintView(asShape.fillProperty(), Objects::isNull);

            if (DevTools.displayPossibleClutterInSceneTree) {
                generator.generate(asShape.smoothProperty(), isTrue);

                generator.generate(asShape.strokeDashOffsetProperty(), isZero);
                generator.generate(asShape.strokeWidthProperty(), isOne);
                generator.generate(asShape.strokeMiterLimitProperty(), num -> num.doubleValue() == 10.0);

                generator.generatePaintView(asShape.strokeProperty(), color -> color.equals(Color.BLACK));
            }
        }
        if (item instanceof Region) {
            Region asRegion = (Region) item;
            generator.generate(asRegion.maxWidthProperty(), isNegativeOne);
            generator.generate(asRegion.maxHeightProperty(), isNegativeOne);

            generator.generate(asRegion.minWidthProperty(), isNegativeOne);
            generator.generate(asRegion.minHeightProperty(), isNegativeOne);

            generator.generate(asRegion.prefWidthProperty(), isNegativeOne);
            generator.generate(asRegion.prefHeightProperty(), isNegativeOne);

            if (DevTools.displayPossibleClutterInSceneTree) {
                generator.generate(asRegion.heightProperty(), a -> true);
                generator.generate(asRegion.widthProperty(), a -> true);
            }
        }
        if (item instanceof Labeled) {
            Labeled labeled = (Labeled) item;

            generator.generatePaintView(labeled.textFillProperty(), color -> true);
            // font implementation here
            generator.generate(labeled.textProperty(), isStringEmpty);

            generator.generate(labeled.underlineProperty(), isFalse);
            generator.generate(labeled.wrapTextProperty(), isFalse);

            if (item instanceof ButtonBase) {
                generator.generate(((ButtonBase) item).armedProperty(), isFalse);
            }
        }
        if (item instanceof ImageView) {
            ImageView imageView = (ImageView) item;
            generator.generate("image", Bindings.createStringBinding(() -> imageView.getImage().getUrl()), Objects::isNull);
            generator.generate(imageView.preserveRatioProperty(), isFalse);

            generator.generate(imageView.fitHeightProperty(), isZero);
            generator.generate(imageView.fitWidthProperty(), isZero);

            if (DevTools.displayPossibleClutterInSceneTree) {
                generator.generate(imageView.xProperty(), isZero);
                generator.generate(imageView.yProperty(), isZero);
            }
        }

        if (DevTools.displayPossibleClutterInSceneTree) {
            generator.generate(item.layoutXProperty(), isZero);
            generator.generate(item.layoutYProperty(), isZero);
        }



        return props;
    }

    private <T> Node genStringPropertyView(String propName, ObservableValue<T> prop, Predicate<T> isDefaultVal, Consumer<Node> remover, Consumer<Node> adder) {

        Text value = new Text();
        value.getStyleClass().add("value-text");
        StringBinding converted;
        if (prop instanceof StringExpression) {
            converted = Bindings.createStringBinding(((StringExpression) prop)::get, prop);
        } else if (prop instanceof ObservableNumberValue) {
            converted = Bindings.createStringBinding(() -> String.valueOf(((int) (((Number) prop.getValue()).doubleValue() * 10000)) / 10000D), prop);
        } else {
            converted = (StringBinding) Bindings.convert(prop);
        }
        toBeDisposed.add(converted);
        value.textProperty().bind(converted);

        return genPropertyView(value, propName, prop, isDefaultVal, remover, adder);
    }

    private <T, N extends Node> Node genPropertyView(N value, String propName, ObservableValue<T> prop, Predicate<T> isDefaultVal, Consumer<Node> remover, Consumer<Node> adder) {
        Text name = new Text(propName + "=");
        name.getStyleClass().addAll("text-indicator");
        Text openQuotes = new Text("\"");
        BooleanBinding display = Bindings.createBooleanBinding(() -> !isDefaultVal.test(prop.getValue()), prop);
        toBeDisposed.add(display);
        Text closeQuotes = new Text("\"");

        HBox result = new HBox(name, openQuotes, value, closeQuotes);

        if (!display.get()) {
            remover.accept(result);
        } else {
            adder.accept(result);
        }

        display.addListener(observable -> {
            if (display.get()) {
                adder.accept(result);
            } else {
                remover.accept(result);
            }
        });

        return result;
    }

}
