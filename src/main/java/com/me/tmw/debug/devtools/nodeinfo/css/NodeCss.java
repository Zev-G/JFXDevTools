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
import javafx.geometry.Insets;
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
        setPadding(new Insets(10));
    }

}
