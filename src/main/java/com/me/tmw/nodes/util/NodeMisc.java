package com.me.tmw.nodes.util;

import com.me.tmw.nodes.control.svg.SVG;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
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
}
