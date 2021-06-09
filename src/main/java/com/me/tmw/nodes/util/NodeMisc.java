package com.me.tmw.nodes.util;

import com.me.tmw.nodes.control.svg.SVG;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

public final class NodeMisc {

    public static final SnapshotParameters TRANSPARENT_SNAPSHOT_PARAMETERS = new SnapshotParameters();

    static {
        TRANSPARENT_SNAPSHOT_PARAMETERS.setFill(Color.TRANSPARENT);
    }

    @SafeVarargs
    public static <T> boolean allEqual(T... vals) {
        if (vals.length <= 1) return true;
        T first = vals[0];
        for (int i = 1; i < vals.length; i++) {
            if (vals[i] != first) {
                return false;
            }
        }
        return true;
    }

    public static Border simpleBorder(Paint paint, double width) {
        return new Border(new BorderStroke(paint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(width)));
    }

    public static void addToGridPane(GridPane gridPane, Collection<? extends Node> nodes, IntFunction<Integer> xConverter) {
        addToGridPane(gridPane, nodes, xConverter, 0, x -> 0, 0);
    }
    public static void addToGridPane(GridPane gridPane, Collection<? extends Node> nodes, IntFunction<Integer> xConverter, int startX, IntFunction<Integer> yConverter, int startY) {
        for (Node node : nodes) {
            gridPane.add(node, startX, startY);
            startX = xConverter.apply(startX);
            startY = yConverter.apply(startY);
        }
    }

    public static String colorToCss(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        double opacity = color.getOpacity();
        return "rgba(" + red + ", " + green + ", " + blue + ", " + opacity + ")";
    }

    public static SVGPath svgPath(String s, double multiplier) {
        return svgPath(SVG.resizePath(s, multiplier));
    }
    public static SVGPath svgPath(String s) {
        SVGPath svgPath = new SVGPath();
        svgPath.getStyleClass().addAll("svg-path", "svg");
        svgPath.setContent(s);
        return svgPath;
    }

    public static Background addToBackground(Background background, BackgroundFill fill) {
        List<BackgroundFill> fills = new ArrayList<>(background.getFills());
        fills.add(fill);
        return new Background(fills, background.getImages());
    }

    public static Background removeFromBackground(Background background, BackgroundFill fill) {
        List<BackgroundFill> fills = new ArrayList<>(background.getFills());
        fills.remove(fill);
        return new Background(fills, background.getImages());
    }

    public static ImageView snapshot(Node node) {
        return new ImageView(node.snapshot(TRANSPARENT_SNAPSHOT_PARAMETERS, null));
    }

    public static Node center(Node inputSVG) {
        VBox wrapper = new VBox(inputSVG);
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }

    public static Node pad(Node node, Insets pad) {
        VBox padder = new VBox(node);
        padder.setPadding(pad);
        return padder;
    }

    public static Node vGrow(Node input, Priority priority) {
        VBox box = new VBox(input);
        VBox.setVgrow(input, priority);
        return box;
    }

    public static Background simpleBackground(Color red) {
        return new Background(new BackgroundFill(red, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public static MenuItem makeMenuItem(String text, EventHandler<ActionEvent> eventHandler) {
        MenuItem item = new MenuItem(text);
        item.setOnAction(eventHandler);
        return item;
    }

    /**
     * Creates an array of text nodes where each object passed into this method either adds a new text node (in the case of a String, Text, or StringBinding), or changes the current preset (in all other cases).
     * @param objects must be either a {@link String}, {@link StringBinding}, {@link Text} node,{@link Paint}, or a {@link Font}.
     * @return the parsed text nodes.
     */
    public static Collection<Text> createTexts(Object... objects) {
        List<Text> texts = new ArrayList<>();
        Font currentFont = null;
        Paint currentFill = null;
        for (int i = 0, objectsLength = objects.length; i < objectsLength; i++) {
            Object obj = objects[i];
            if (obj instanceof String || obj instanceof StringBinding) {
                Text newText = new Text();
                if (currentFill != null)
                    newText.setFill(currentFill);
                if (currentFont != null)
                    newText.setFont(currentFont);
                if (obj instanceof String) {
                    newText.setText(obj.toString());
                } else {
                    newText.textProperty().bind((StringBinding) obj);
                }
                texts.add(newText);
            } else if (obj instanceof Text) {
                texts.add((Text) obj);
            } else if (obj instanceof Paint) {
                currentFill = (Paint) obj;
            } else if (obj instanceof Font) {
                currentFont = (Font) obj;
            } else {
                throw new IllegalArgumentException("Invalid argument: " + obj + " at position: " + i +".");
            }
        }
        return texts;
    }

}
