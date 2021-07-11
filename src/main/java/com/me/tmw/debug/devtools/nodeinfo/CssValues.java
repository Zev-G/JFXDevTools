package com.me.tmw.debug.devtools.nodeinfo;


import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public abstract class CssValues<T> extends CssValue<T> {

    private final VBox items = new VBox();

    private final Label expand = new Label("+");
    private final VBox layout = new VBox(expand);

    protected final BooleanProperty expanded;

    CssValues(T initialValue, Runnable updateNode, BooleanProperty expanded) {
        super(initialValue, updateNode);
        this.expanded = expanded;

        expand.getStyleClass().add("property-expander");

        expanded.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                expanded();
            } else {
                minimized();
            }
        });

        if (expanded.get()) {
            Platform.runLater(this::expanded);
        } else {
            Platform.runLater(this::minimized);
        }

        expand.setOnMousePressed(event -> expanded.set(!expanded.get()));
    }

    protected void init() {
        if (expanded.get()) {
            expanded();
        } else {
            minimized();
        }
    }

    private void expanded() {
        if (items.getChildren().isEmpty()) {
            addNodes(items);
        }
        if (!layout.getChildren().contains(items)) {
            layout.getChildren().add(items);
        }
        expand.setText("-");
    }

    private void minimized() {
        layout.getChildren().remove(items);
        expand.setText("+");
    }

    public abstract void addNodes(VBox vBox);

    @Override
    public Parent node() {
        return layout;
    }

    @Override
    public Pos alignment() {
        return Pos.TOP_LEFT;
    }
}
