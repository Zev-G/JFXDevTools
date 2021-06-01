package com.me.tmw.debug.devtools.console;

import com.me.tmw.debug.devtools.DevUtils;
import com.me.tmw.debug.devtools.scenetree.SceneTree;

import com.me.tmw.debug.visualize.ObjectVisualizer;
import com.me.tmw.nodes.control.svg.SVG;
import com.me.tmw.nodes.util.NodeMisc;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import org.fxmisc.richtext.InlineCssTextArea;

public class ConsoleLogLine extends HBox {

    private static final Object ERROR_MARKER = new Object();

    public static final String ARROW = SVG.resizePath(SVG.ARROW, 0.45);

    private final Node graphic;
    public ConsoleLogLine(Object log, String graphic) {
        this(log, NodeMisc.pad(NodeMisc.svgPath(graphic), new Insets(5)));
    }
    public ConsoleLogLine(Object log, Node graphic) {
        this.graphic = graphic;
        getChildren().add(graphic);
        if (log != ERROR_MARKER) {
            if (log == null) {
                getChildren().add(undefined());
            } else if (log instanceof InlineCssTextArea) {
                getChildren().add((Node) log);
            } else if (log instanceof Parent) {
                SceneTree tree = new SceneTree(new SimpleObjectProperty<>((Parent) log));
                tree.setShowRoot(false);
                TitledPane reveal = new TitledPane(DevUtils.getSimpleClassName(log.getClass()), tree);
                reveal.setOnMouseEntered(event -> tree.getInspector().setExamined((Node) log));
                reveal.setOnMouseExited(event -> tree.getInspector().setExamined(null));
                reveal.setExpanded(false);
                HBox.setHgrow(reveal, Priority.ALWAYS);
                getChildren().add(reveal);
            } else {
                if (ObjectVisualizer.isPrimitiveOrPrimitiveWrapperOrString(log.getClass())) {
                    getChildren().add(stringValue(log));
                } else {
                    getChildren().add(new ObjectVisualizer(log));
                }
            }
        }
        getStyleClass().add("console-log-line");
    }

    public Node getGraphic() {
        return graphic;
    }

    private Node stringValue(Object log) {
        Label label = new Label(log.toString());
        label.setPadding(new Insets(1, 0, 0, 0));
        return label;
    }

    private Node undefined() {
        return new Text("undefined");
    }

    public static class Input extends ConsoleLogLine {

        public Input(Object log) {
            super(log, ARROW);
        }
    }

    public static class Output extends ConsoleLogLine {

        public Output(Object log) {
            super(log, NodeMisc.pad(flipped(), new Insets(5)));
        }

        private static SVGPath flipped() {
            SVGPath path = NodeMisc.svgPath(ARROW);
            path.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            return path;
        }

    }

    public static class Error extends ConsoleLogLine {

        public Error(Exception log) {
            super(ERROR_MARKER, SVG.resizePath(SVG.WARNING, 0.03));
            Label errorText = new Label(log.getMessage());
            errorText.getStyleClass().add("error-text");
            getChildren().add(errorText);
            getStyleClass().add("error-log-line");
        }

    }
}
