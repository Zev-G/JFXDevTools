package com.me.tmw.debug.util;

import com.me.tmw.debug.devtools.inspectors.InspectorBase;
import com.me.tmw.debug.devtools.inspectors.SimpleInspector;
import com.me.tmw.debug.devtools.nodeinfo.NodeCss;
import com.me.tmw.nodes.util.Layout;
import com.me.tmw.resource.Resources;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class Debugger {

    private static final PseudoClass EXAMINED = PseudoClass.getPseudoClass("examined");

    public static void showProperty(ObservableValue<?> observable, Node showFor) {
        Popup popup = new Popup();
        String styleSheet = Resources.DEBUGGER.getCss("property");

        Label propertyViewer = new Label();
        propertyViewer.textProperty().bind(Bindings.convert(observable));

        BorderPane popupParent = new BorderPane(propertyViewer);
        popupParent.setMouseTransparent(true);
        popupParent.getStylesheets().add(styleSheet);
        popupParent.getStyleClass().add("popup-scene");

        popup.getContent().add(popupParent);

        Runnable listener = () -> {
            Bounds boundsOnScreen = Layout.nodeOnScreen(showFor);
            if (boundsOnScreen != null) {
                popup.setX(boundsOnScreen.getMinX() + boundsOnScreen.getWidth() / 2 - (popupParent.getWidth() / 2));
                popup.setY(boundsOnScreen.getMaxY());
            }
        };

        showFor.boundsInParentProperty().addListener((observable1, oldValue, newValue) -> listener.run());
        showFor.sceneProperty().addListener((observable1, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.windowProperty().addListener((observable2, oldValue1, newValue1) -> {
                    if (newValue1 != null) {
                        newValue1.xProperty().addListener((observable3, oldValue2, newValue2) -> listener.run());
                        newValue1.yProperty().addListener((observable3, oldValue2, newValue2) -> listener.run());
                    }
                });
            }
        });

        Supplier<TimerTask> taskSupplier = () -> new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(popup::hide);
            }
        };

        AtomicReference<TimerTask> taskAtomicReference = new AtomicReference<>(taskSupplier.get());

        Timer timer = new Timer(true);

        observable.addListener((observable1, oldValue, newValue) -> {
            taskAtomicReference.get().cancel();
            timer.purge();
            taskAtomicReference.set(taskSupplier.get());
            timer.schedule(taskAtomicReference.get(), 1000);
            if (showFor.getScene() != null && showFor.getScene().getWindow() != null) {
                popup.show(showFor.getScene().getWindow());
            }
        });
    }

    public static void examineLayout(Parent parent) {
        String styleSheet = InspectorBase.DEFAULT_STYLE_SHEET;
        parent.getStylesheets().add(styleSheet);

        SimpleInspector inspector = new SimpleInspector();

        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            Node intersected = event.getPickResult().getIntersectedNode();
            if (intersected != null && !(intersected instanceof Parent)) {
                intersected = intersected.getParent();
            }
            inspector.setExamined(intersected);
        };
        parent.addEventFilter(MouseEvent.MOUSE_MOVED, mouseEventEventHandler);
        parent.addEventFilter(MouseEvent.MOUSE_EXITED, mouseEventEventHandler);
    }

    public static Stage liveViewCss(Parent node) {
        NodeCss examiner = new NodeCss(node);
        Stage stage = new Stage();
        stage.setScene(new Scene(examiner));
        stage.setTitle("CSS Properties of: " + node);
        return stage;
    }

}
