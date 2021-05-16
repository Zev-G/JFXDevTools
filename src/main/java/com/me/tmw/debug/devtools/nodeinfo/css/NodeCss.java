package com.me.tmw.debug.devtools.nodeinfo.css;

import com.me.tmw.debug.devtools.nodeinfo.NodeInfo;
import com.me.tmw.resource.Resources;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.stream.Collectors;

public class NodeCss extends NodeInfo implements Styleable {

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("node-css-editor");

    @SuppressWarnings("unchecked")
    public NodeCss(Parent node) {
        getStylesheets().add(STYLE_SHEET);
        getChildren().add(new CssPropertiesView(
                node.getCssMetaData().stream().map(s -> ((CssMetaData<Styleable, ?>) s).getStyleableProperty(node)).filter(property -> property instanceof Observable).collect(Collectors.toList())
        , node));
    }

    private static class PropertiesTable extends TableView<StyleableProperty<?>> {

        private final TableColumn<StyleableProperty<?>, String> nameColumn = new TableColumn<>("Name");
        private final TableColumn<StyleableProperty<?>, String> valueColumn = new TableColumn<>("Value");

        @SuppressWarnings("unchecked")
        public PropertiesTable(Node node) {
            super();
            getItems().setAll(
                    node.getCssMetaData().stream().map(s -> ((CssMetaData<Styleable, ?>) s).getStyleableProperty(node)).filter(property -> property instanceof Observable).collect(Collectors.toList())
            );
            getColumns().addAll(nameColumn, valueColumn);

            nameColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getCssMetaData().getProperty()));
            valueColumn.setCellValueFactory(param -> Bindings.convert((ObservableValue<?>) param.getValue()));
        }

    }

}
