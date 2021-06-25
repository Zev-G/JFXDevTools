package com.me.tmw.nodes.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;

import java.util.function.DoubleConsumer;
import java.util.function.Predicate;

public final class Dragging {

    public static DragManager draggable(Node node, DoubleProperty x, DoubleProperty y) {
        DragManager manager = new DragManager(node, x, y);
        manager.attachListeners();
        return manager;
    }
    public static DragManager draggable(Node node, ObservableValue<Number> x, DoubleConsumer writeX, ObservableValue<Number> y, DoubleConsumer writeY) {
        DragManager manager = new DragManager(node, x, writeX, y, writeY);
        manager.attachListeners();
        return manager;
    }

    public static DragManager dragAsPopup(Node node) {
        Popup popup = new Popup();
        DoubleProperty writableXProperty = new SimpleDoubleProperty(popup.getX());
        DoubleProperty writableYProperty = new SimpleDoubleProperty(popup.getY());
        writableXProperty.addListener((observable, oldValue, newValue) -> popup.setX(newValue.doubleValue()));
        writableYProperty.addListener((observable, oldValue, newValue) -> popup.setY(newValue.doubleValue()));
        EventHandler<MouseEvent> mouseReleased = event -> {
            popup.hide();
        };
        DragManager manager = new DragManager(node, writableXProperty, writableYProperty) /* Only some-what used. */ {
            @Override
            public void attachListeners() {
                super.attachListeners();
                node.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleased);
            }

            @Override
            public void detachListeners() {
                super.detachListeners();
                node.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleased);
            }
        };

        manager.mousePressed = event -> {
            popup.getContent().setAll(new ImageView(node.snapshot(NodeMisc.TRANSPARENT_SNAPSHOT_PARAMETERS, null)));
            Bounds screenBounds = Layout.nodeOnScreen(node);
            manager.startX.set(screenBounds.getMinX());
            manager.startY.set(screenBounds.getMinY());
            manager.startScreenX.set(event.getScreenX());
            manager.startScreenY.set((event.getScreenY()));
        };

        manager.mouseDragged = event -> {
            if (!popup.isShowing() && node.getScene() != null && node.getScene().getWindow() != null) {
                popup.show(node.getScene().getWindow());
            }
            manager.writeX.accept((manager.startX.get() + (event.getScreenX() - manager.startScreenX.get())));
            manager.writeY.accept(manager.startY.get() + (event.getScreenY() - manager.startScreenY.get()));
        };

        manager.attachListeners();

        return manager;
    }

    public static class DragManager {

        private final DoubleProperty startX = new SimpleDoubleProperty(this, "startX");
        private final DoubleProperty startY = new SimpleDoubleProperty(this, "startX");
        private final DoubleProperty startScreenX = new SimpleDoubleProperty(this, "startScreenX", 0);
        private final DoubleProperty startScreenY = new SimpleDoubleProperty(this, "startScreenY", 0);

        private final Node node;

        private ObservableValue<Number> x;
        private ObservableValue<Number> y;
        private final DoubleConsumer writeX;
        private final DoubleConsumer writeY;

        private EventHandler<MouseEvent> mousePressed;
        private EventHandler<MouseEvent> mouseDragged;

        private Predicate<Node> nodeFilter = node -> true;

        public DragManager(Node node, DoubleProperty x, DoubleProperty y) {
            this(node, x, x::set, y, y::set);
        }
        public DragManager(Node node, ObservableValue<Number> x, DoubleConsumer writeX, ObservableValue<Number> y, DoubleConsumer writeY) {
            this.node = node;
            this.x = x;
            this.y = y;
            this.writeX = writeX;
            this.writeY = writeY;


            mousePressed = event -> {
                startX.set(x.getValue().doubleValue());
                startY.set(y.getValue().doubleValue());
                startScreenX.set(event.getScreenX());
                startScreenY.set(event.getScreenY());
            };

            mouseDragged = event -> {
                if (nodeFilter.test(event.getPickResult().getIntersectedNode())) {
                    writeX.accept(startX.get() + (event.getScreenX() - startScreenX.get()));
                    writeY.accept(startY.get() + (event.getScreenY() - startScreenY.get()));
                }
            };
        }

        public void attachListeners() {
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
            node.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
        }

        public void detachListeners() {
            node.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
            node.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
        }

        public void setNodeFilter(Predicate<Node> filter) {
            this.nodeFilter = filter;
        }

        public double getStartX() {
            return startX.get();
        }

        public DoubleProperty startXProperty() {
            return startX;
        }

        public void setStartX(double startX) {
            this.startX.set(startX);
        }

        public double getStartY() {
            return startY.get();
        }

        public DoubleProperty startYProperty() {
            return startY;
        }

        public void setStartY(double startY) {
            this.startY.set(startY);
        }

        public double getStartScreenX() {
            return startScreenX.get();
        }

        public DoubleProperty startScreenXProperty() {
            return startScreenX;
        }

        public void setStartScreenX(double startScreenX) {
            this.startScreenX.set(startScreenX);
        }

        public double getStartScreenY() {
            return startScreenY.get();
        }

        public DoubleProperty startScreenYProperty() {
            return startScreenY;
        }

        public void setStartScreenY(double startScreenY) {
            this.startScreenY.set(startScreenY);
        }

        public Node getNode() {
            return node;
        }

        public ObservableValue<Number> getX() {
            return x;
        }

        public void setX(DoubleProperty x) {
            this.x = x;
        }

        public ObservableValue<Number> getY() {
            return y;
        }

        public void setY(DoubleProperty y) {
            this.y = y;
        }

        public EventHandler<MouseEvent> getMousePressed() {
            return mousePressed;
        }

        public EventHandler<MouseEvent> getMouseDragged() {
            return mouseDragged;
        }

    }

}
