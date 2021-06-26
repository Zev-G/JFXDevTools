package com.me.tmw.properties.editors;

import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.properties.NodeProperty;
import com.me.tmw.resource.Resources;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;

public abstract class PreviewBasedPropertyEditor<T> extends PropertyEditorBase<T> {

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("preview-based-editor");

    protected final NodeProperty preview  = new NodeProperty(this, "preview");
    protected final NodeProperty editor   = new NodeProperty(this, "editor");
    protected final StackPane previewRoot = new StackPane();
    protected final StackPane editorRoot  = new StackPane();

    protected final Popup editorDisplay = new Popup();

    public PreviewBasedPropertyEditor(Property<T> value) {
        this(value.getName(), value);
    }
    public PreviewBasedPropertyEditor(String name, Property<T> value) {
        super(name, value);

        // Sync preview to previewRoot and editor to editorRoot
        NodeMisc.runAndAddListener(preview, observable -> {
            if (preview.get() == null) previewRoot.getChildren().clear();
            else previewRoot.getChildren().setAll(preview.get());
        });
        NodeMisc.runAndAddListener(editor, observable -> {
            if (editor.get() == null) editorRoot.getChildren().clear();
            else editorRoot.getChildren().setAll(editor.get());
        });

        // Populate and configure popup
        editorDisplay.getContent().setAll(editorRoot);
        editorDisplay.setHideOnEscape(true);
        editorDisplay.setAutoHide(true);

        // Style: sheets, classes and ids
        editorRoot.setId("editor-root");
        previewRoot.getStyleClass().add("preview-root");
        editorRoot.getStylesheets().add(STYLE_SHEET);
        previewRoot.getStylesheets().add(STYLE_SHEET);

        // Mouse Events
        previewRoot.setOnMouseReleased(event -> {
            if (!value.isBound()) {
                editorDisplay.show(previewRoot.getScene().getWindow());
                editorDisplay.setX(event.getScreenX() - editorRoot.getWidth() / 2);
                editorDisplay.setY(event.getScreenY() + 5);
            }
        });
        previewRoot.setOnMouseEntered(event -> previewRoot.pseudoClassStateChanged(BOUND, value.isBound()));

        // Finalize initialization
        setNode(previewRoot);
    }

    public Node getPreview() {
        return preview.get();
    }
    public NodeProperty previewProperty() {
        return preview;
    }
    public void setPreview(Node preview) {
        this.preview.set(preview);
    }

    public Node getEditor() {
        return editor.get();
    }
    public NodeProperty editorProperty() {
        return editor;
    }
    public void setEditor(Node editor) {
        this.editor.set(editor);
    }

}
