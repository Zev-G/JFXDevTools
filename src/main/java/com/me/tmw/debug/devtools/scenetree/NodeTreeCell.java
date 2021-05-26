package com.me.tmw.debug.devtools.scenetree;

import com.me.tmw.debug.devtools.DevUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.Property;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

public class NodeTreeCell extends TreeCell<Node> {

    private static final PseudoClass DARK = PseudoClass.getPseudoClass("dark");

    private BackgroundFill highlight = new BackgroundFill(Color.rgb(100, 100, 100, 0.05), new CornerRadii(10), Insets.EMPTY);

    private List<Binding<?>> toBeDisposed = new ArrayList<>();

    public NodeTreeCell(SceneTree sceneTree) {
        setOnMouseEntered(event -> {
            if (getItem() != null) {
                ((SceneTree) getTreeView()).getInspector().setExamined(getItem());
            }
        });
        setOnMouseExited(event -> {
            ((SceneTree) getTreeView()).getInspector().setExamined(null);
        });

        setContentDisplay(ContentDisplay.RIGHT);

        heightProperty().addListener((observable, oldValue, newValue) -> {
            if (getTreeView() instanceof SceneTree && ((SceneTree) getTreeView()).heightEstimationProperty().get() < newValue.doubleValue()) {
                ((SceneTree) getTreeView()).heightEstimationProperty().set(newValue.doubleValue());
            }
        });
        if (sceneTree.heightEstimationProperty().get() < getHeight()) {
            sceneTree.heightEstimationProperty().set(getHeight());
        }
    }

    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);
        toBeDisposed.forEach(Binding::dispose);
        toBeDisposed.clear();
        if (item != null) {
            String className = DevUtils.getSimpleClassName(item.getClass());
            setGraphic(genGraphic(item));
            setText(className);
            if (!getStyleClass().contains("real-tree-cell")) {
                getStyleClass().add("real-tree-cell");
            }
        } else {
            setGraphic(null);
            setText(null);
            getStyleClass().remove("real-tree-cell");
        }
    }

    private Node genGraphic(Node item) {
        StringBinding id = Bindings.createStringBinding(() -> {
            String idVal = item.getId();
            return idVal == null || idVal.isEmpty() ? "" : "\"" + idVal + "\" ";
        }, item.idProperty());
        StringBinding classes = Bindings.createStringBinding(() -> {
            List<String> stylesVal = item.getStyleClass();
            return stylesVal.isEmpty() ? "" : "\"" + String.join(" ", stylesVal) + "\" ";
        }, item.getStyleClass());
        toBeDisposed.add(id);
        toBeDisposed.add(classes);

        Text idText = new Text();
        idText.textProperty().bind(id);
        idText.getStyleClass().add("value-text");
        Text classText = new Text();
        classText.textProperty().bind(classes);
        classText.getStyleClass().add("value-text");

        Text classIndicator = new Text("class=");
        Text idIndicator = new Text("id=");
        classIndicator.getStyleClass().add("text-indicator");
        idIndicator.getStyleClass().add("text-indicator");

        Text[] themed = { idText, classText, classIndicator, idIndicator };

        TextFlow classFlow = new TextFlow(classIndicator, classText);
        TextFlow idFlow = new TextFlow(idIndicator, idText);
        TextFlow props = new TextFlow();
        InvalidationListener updateProps = (dontUse) -> {
            if (idText.getText().isEmpty()) {
                props.getChildren().remove(idFlow);
            } else if (!props.getChildren().contains(idFlow)) {
                props.getChildren().add(0, idFlow);
            }
            if (classText.getText().isEmpty()) {
                props.getChildren().remove(classFlow);
            } else if (!props.getChildren().contains(classFlow)) {
                props.getChildren().add(classFlow);
            }
        };
        updateProps.invalidated(null);
        classText.textProperty().addListener(updateProps);
        idText.textProperty().addListener(updateProps);

        selectedProperty().addListener((observable, oldValue, newValue) -> {
            for (Text text : themed) {
                text.pseudoClassStateChanged(DARK, newValue);
            }
        });
        if (isSelected()) {
            for (Text text : themed) {
                text.pseudoClassStateChanged(DARK, true);
                text.applyCss();
            }
        }

        return props;
    }

    private Node genPropertyFlow(Property<?> prop) {
        Text name = new Text(prop.getName() + "=");
        Text openQuotes = new Text("\"");
        Text value = new Text();
        StringExpression converted = Bindings.convert(prop);
        value.textProperty().bind(converted);
        Text closeQuotes = new Text("\"");

        return new TextFlow(name, openQuotes, value, closeQuotes);
    }

}
