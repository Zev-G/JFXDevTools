package com.me.tmw.debug.devtools.inspectors;

import com.me.tmw.resource.Resources;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;
import javafx.stage.Popup;
import javafx.stage.Window;

public abstract class InspectorBase {

    public static final String DEFAULT_STYLE_SHEET = Resources.DEBUGGER.getCss("layout-inspector");
    private static final PseudoClass EXAMINED_PSEUDO_CLASS = PseudoClass.getPseudoClass("examined");

    private final ChangeListener<? super Number> screenPosListener = (observable, oldValue, newValue) -> layoutPopup(getExamined());
    private final ChangeListener<? super Transform> boundsListener = (observable, oldValue, newValue) -> layoutPopup(getExamined());

    private final ObjectProperty<Node> examined = new SimpleObjectProperty<>();

    private final Popup popup = new Popup();
    protected final StackPane popupContent = new StackPane();

    public InspectorBase() {
        this.popup.getContent().add(popupContent);
        this.popupContent.getStylesheets().add(DEFAULT_STYLE_SHEET);
        this.popupContent.setId("popup-content");

        examined.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.pseudoClassStateChanged(EXAMINED_PSEUDO_CLASS, false);
                oldValue.localToSceneTransformProperty().removeListener(boundsListener);

                if (oldValue.getScene() != null && oldValue.getScene().getWindow() != null) {
                    Window window = oldValue.getScene().getWindow();
                    window.xProperty().removeListener(screenPosListener);
                    window.yProperty().removeListener(screenPosListener);
                }
            }
            if (newValue != null) {
                newValue.pseudoClassStateChanged(EXAMINED_PSEUDO_CLASS, true);
                newValue.localToSceneTransformProperty().addListener(boundsListener);
                layoutPopup(newValue);
                populatePopup(newValue);
                if (newValue.getScene() != null && newValue.getScene().getWindow() != null) {
                    Window window = newValue.getScene().getWindow();
                    window.xProperty().addListener(screenPosListener);
                    window.yProperty().addListener(screenPosListener);
                }
                if (!isPopupShowing()) {
                    showPopup(newValue);
                }
            } else {
                hidePopup();
            }
        });
    }

    protected abstract void populatePopup(Node newValue);
    protected abstract void layoutPopup(Node node);
    protected boolean showPopup(Node node) {
        if (node != null && node.getScene() != null && node.getScene().getWindow() != null){
            popup.show(node.getScene().getWindow());
            return true;
        }
        return false;
    }
    protected void hidePopup() {
        popup.hide();
    }
    protected boolean isPopupShowing() {
        return popup.isShowing();
    }

    public Popup getPopup() {
        return popup;
    }

    public StackPane getPopupContent() {
        return popupContent;
    }

    public Node getExamined() {
        return examined.get();
    }
    public ObjectProperty<Node> examinedProperty() {
        return examined;
    }
    public void setExamined(Node node) {
        examinedProperty().set(node);
    }
}
