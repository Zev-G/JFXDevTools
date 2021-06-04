package com.me.tmw.debug.devtools.inspectors;

import com.me.tmw.debug.devtools.DevUtils;
import com.me.tmw.nodes.util.Layout;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.stream.Collectors;

public class SimpleInspector extends InspectorBase {

    private final Label className = new Label();

    private final Text id = new Text();
    private final Text styleClasses = new Text();
    private final Text pseudoStates = new Text();
    private final TextFlow cssInfo = new TextFlow(id, styleClasses, pseudoStates);

    private final Label dimensions = new Label();

    private final HBox layout = new HBox(className, new Separator(Orientation.VERTICAL), cssInfo, dimensions);

    public SimpleInspector() {
        super();
        usingCSS = false;
        usingOverlay = true;
        getPopupContent().getChildren().add(layout);

        layout.getStyleClass().add("info-popup");
        styleClasses.getStyleClass().addAll("style-classes", "css-property");
        id.getStyleClass().addAll("id", "css-property");
        pseudoStates.getStyleClass().addAll("pseudo-states", "css-property");
        dimensions.getStyleClass().add("dimensions");
        className.getStyleClass().add("class-name");

        layout.setOnMouseEntered(event -> hidePopup());
    }

    @Override
    protected void layoutPopup(Node node) {
        Bounds boundsOnScreen = Layout.nodeOnScreen(node);
        if (boundsOnScreen != null) {
            getPopup().setX(boundsOnScreen.getMinX());
            getPopup().setY(boundsOnScreen.getMaxY());

            getOverlayPopup().setX(boundsOnScreen.getMinX());
            getOverlayPopup().setY(boundsOnScreen.getMinY());
        }
    }

    @Override
    protected void populatePopup(Node node) {
        // Unbind css properties
        styleClasses.textProperty().unbind();
        pseudoStates.textProperty().unbind();
        styleClasses.textProperty().unbind();
        // Bind css
        styleClasses.textProperty().bind(Bindings.createStringBinding(() ->
                        node.getStyleClass().stream().map(s -> "." + s).collect(Collectors.joining(", ")),
                styleClasses.getStyleClass()
        ));
        pseudoStates.textProperty().bind(Bindings.createStringBinding(() ->
                        node.getPseudoClassStates().stream().map(s -> ":" + s.getPseudoClassName()).collect(Collectors.joining("")),
                node.getPseudoClassStates()
        ));
        id.textProperty().bind(Bindings.createStringBinding(() -> node.getId() != null ? ("#" + node.getId()) : "", node.idProperty()));
        // Set class name
        className.setText(DevUtils.getSimpleClassName(node.getClass()));
        // Set text of dimensions
        Bounds bounds = node.getBoundsInLocal();
        dimensions.setText((int) bounds.getWidth() + " x " + (int) bounds.getHeight());

        overlayPopupContent.minWidthProperty().bind(Bindings.createDoubleBinding(() -> node.getBoundsInParent().getWidth(), node.boundsInParentProperty()));
        overlayPopupContent.minHeightProperty().bind(Bindings.createDoubleBinding(() -> node.getBoundsInParent().getHeight(), node.boundsInParentProperty()));
    }
}
