package com.me.tmw.nodes.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;

public final class Dragging {

    public static DragManager draggable(Node node, DoubleProperty x, DoubleProperty y) {
        DragManager manager = new DragManager(node, x, y);
        manager.attachListeners();
        return manager;
    }

    public DragManager dragAsPopup(Node node) {
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
            manager.x.set((manager.startX.get() + (event.getScreenX() - manager.startScreenX.get())));
            manager.y.set(manager.startY.get() + (event.getScreenY() - manager.startScreenY.get()));
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

        private DoubleProperty x;
        private DoubleProperty y;

        private EventHandler<MouseEvent> mousePressed;
        private EventHandler<MouseEvent> mouseDragged;

        public DragManager(Node node, DoubleProperty x, DoubleProperty y) {
            this.node = node;
            this.x = x;
            this.y = y;

            mousePressed = event -> {
                startX.set(x.get());
                startY.set(y.get());
                startScreenX.set(event.getScreenX());
                startScreenY.set(event.getScreenY());
            };

            mouseDragged = event -> {
                x.set(startX.get() + (event.getScreenX() - startScreenX.get()));
                y.set(startY.get() + (event.getScreenY() - startScreenY.get()));
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

        public DoubleProperty getX() {
            return x;
        }
        public void setX(DoubleProperty x) {
            this.x = x;
        }

        public DoubleProperty getY() {
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
