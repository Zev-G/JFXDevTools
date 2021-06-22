package com.me.tmw.nodes.control.paint;

import com.me.tmw.nodes.control.Point;
import com.me.tmw.nodes.control.PointsEditor;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.resource.Resources;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LinearGradientPicker extends VBox {

    private static final String STYLE_SHEET = Resources.NODES.getCss("linear-gradient-picker");

    private final ComboBox<CycleMethod> cycleMethods = new ComboBox<>();

    private final StopsPicker stops = new StopsPicker();

    private final PointsEditor resultPreview = new PointsEditor();
    private final Point startY = new Point(0, 0, true, true, false, createPointDisplay(false, true));
    private final Point startX = new Point(0, 0, true, false, true, createPointDisplay(true, true));
    private final Point endY = new Point(1, 1, true, true, false, createPointDisplay(false, false));
    private final Point endX = new Point(1, 1, true, false, true, createPointDisplay(true, false));

    private final ObjectProperty<LinearGradient> value = new SimpleObjectProperty<>(this, "value");

    public LinearGradientPicker() {
        this(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(100, Color.WHITE)));
    }

    public LinearGradientPicker(LinearGradient originalValue) {
        getStyleClass().add("linear-gradient-picker");
        getStylesheets().add(STYLE_SHEET);

        value.set(originalValue);

        cycleMethods.getItems().addAll(CycleMethod.values());
        cycleMethods.valueProperty().addListener(observable -> updateValue());

        resultPreview.backgroundProperty().bind(
                Bindings.createObjectBinding(
                        () -> new Background(new BackgroundFill(value.get(), CornerRadii.EMPTY, Insets.EMPTY)),
                        value
                )
        );
        resultPreview.getPoints().addAll(startX, startY, endX, endY);

        resultPreview.connectPoints(startX, startY);
        resultPreview.connectPoints(endX, endY);

        stops.getStops().addListener((InvalidationListener) observable -> updateValue());
        startX.xProperty().addListener(observable -> updateValue());
        startY.yProperty().addListener(observable -> updateValue());
        endX.xProperty().addListener(observable -> updateValue());
        endY.yProperty().addListener(observable -> updateValue());


        resultPreview.getStyleClass().add("result-preview");

        VBox.setVgrow(resultPreview, Priority.ALWAYS);

        getChildren().addAll(stops, resultPreview);
        stops.getFooter().getChildren().add(cycleMethods);

        loadGradient(originalValue);
    }

    private void loadGradient(LinearGradient gradient) {
        startX.setX(gradient.getStartX());
        startY.setY(gradient.getStartY());
        endX.setX(gradient.getEndX());
        endY.setY(gradient.getEndY());
        stops.getStopProperties().clear();
        stops.addAll(gradient.getStops());
        cycleMethods.getSelectionModel().select(gradient.getCycleMethod());
    }

    private void updateValue() {
        try {
            value.set(new LinearGradient(startX.getClampedX(), startY.getClampedY(), endX.getClampedX(), endY.getClampedY(), true, cycleMethods.getValue(), this.stops.getStops()));
        } catch (Exception ignored) {
            value.set(new LinearGradient(0, 0, 100, 100, true, CycleMethod.NO_CYCLE, new Stop(100, Color.WHITE)));
//            resultPreview.getChildren().add(new Label("Error in generating linear gradient"));
        }
    }

    public ObjectProperty<LinearGradient> valueProperty() {
        return value;
    }

    public LinearGradient getValue() {
        return value.get();
    }

    private static Node createPointDisplay(boolean x, boolean start) {
        Label text = new Label(x ? "X" : "Y");
        text.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 16));
        double size = 10;
        Shape shape = start ? new Rectangle(size, size) : new Circle(size / 2);
        BorderPane pane = new BorderPane(text);
        pane.setBackground(NodeMisc.simpleBackground(Color.DARKGRAY));
        pane.setShape(shape);
        return pane;
    }

}
