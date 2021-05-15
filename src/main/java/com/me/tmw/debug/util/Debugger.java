package com.me.tmw.debug.util;

import com.me.tmw.nodes.util.Layout;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class Debugger {

    private static final PseudoClass EXAMINED = PseudoClass.getPseudoClass("examined");

    public static void showProperty(ObservableValue<?> observable, Node showFor) {
        Popup popup = new Popup();
        String styleSheet = Objects.requireNonNull(Debugger.class.getClassLoader().getResource("debugger/property.css")).toExternalForm();

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
        String styleSheet = Objects.requireNonNull(Debugger.class.getClassLoader().getResource("debugger/layout-examiner.css")).toExternalForm();
        parent.getStylesheets().add(styleSheet);

        Popup infoPopup = new Popup();
        infoPopup.hide();
        Label dimensions = new Label();
        Text id = new Text();
        Text styleClasses = new Text();
        Text pseudoStates = new Text();
        TextFlow cssInfo = new TextFlow(id, styleClasses, pseudoStates);
        Label className = new Label();
        HBox info = new HBox(className, new Separator(Orientation.VERTICAL), cssInfo, dimensions);
        info.getStyleClass().add("info-popup");
        info.getStylesheets().add(styleSheet);
        styleClasses.getStyleClass().addAll("style-classes", "css-property");
        id.getStyleClass().addAll("id", "css-property");
        pseudoStates.getStyleClass().addAll("pseudo-states", "css-property");
        dimensions.getStyleClass().add("dimensions");
        className.getStyleClass().add("class-name");
        infoPopup.getContent().add(info);

        info.setOnMouseEntered(event -> infoPopup.hide());

        AtomicReference<Node> lastNode = new AtomicReference<>();
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (lastNode.get() != event.getPickResult().getIntersectedNode()) {
                if (lastNode.get() != null) {
                    lastNode.get().pseudoClassStateChanged(EXAMINED, false);
                }
                if (event.getPickResult().getIntersectedNode() != null) {
                    Node intersectedTemp = event.getPickResult().getIntersectedNode();
                    Parent intersected;
                    if (!(intersectedTemp instanceof Parent)) {
                        intersected = intersectedTemp.getParent();
                    } else {
                        intersected = (Parent) intersectedTemp;
                    }
                    if (intersected.getScene() != null && intersected.getScene().getWindow() != null) {
                        styleClasses.textProperty().unbind();
                        pseudoStates.textProperty().unbind();
                        styleClasses.textProperty().unbind();
                        styleClasses.textProperty().bind(Bindings.createStringBinding(() ->
                                intersected.getStyleClass().stream().map(s -> "." + s).collect(Collectors.joining(", ")),
                                styleClasses.getStyleClass()
                        ));
                        pseudoStates.textProperty().bind(Bindings.createStringBinding(() ->
                                intersected.getPseudoClassStates().stream().map(s -> ":" + s.getPseudoClassName()).collect(Collectors.joining("")),
                                intersected.getPseudoClassStates()
                        ));
                        id.textProperty().bind(Bindings.createStringBinding(() -> intersected.getId() != null ? ("#" + intersected.getId()) : "", intersected.idProperty()));
                        className.setText(intersected.getClass().getSimpleName());
                        Bounds bounds = intersected.getBoundsInLocal();
                        dimensions.setText((int) bounds.getWidth() + " x " + (int) bounds.getHeight());
                        Bounds boundsInScreen = Layout.nodeOnScreen(intersected);
                        infoPopup.setX(boundsInScreen.getMinX());
                        infoPopup.setY(boundsInScreen.getMaxY());
                        infoPopup.show(intersected.getScene().getWindow());
                    }
                    if (!intersected.getStyleClass().contains("examinable")) {
                        intersected.getStyleClass().add("examinable");
                    }
                    lastNode.set(intersected);
                    intersected.pseudoClassStateChanged(EXAMINED, true);
                } else {
                    infoPopup.hide();
                    lastNode.set(null);
                }
            }
        };
        parent.addEventFilter(MouseEvent.MOUSE_MOVED, mouseEventEventHandler);
        parent.addEventFilter(MouseEvent.MOUSE_EXITED, mouseEventEventHandler);
    }

}
