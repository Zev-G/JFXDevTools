package com.me.tmw.debug.devtools.nodeinfo.css;

import com.me.tmw.debug.devtools.nodeinfo.NodeInfo;
import com.me.tmw.resource.Resources;
import javafx.beans.Observable;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.geometry.Insets;
import javafx.scene.Parent;

import java.util.stream.Collectors;

public class NodeCss extends NodeInfo implements Styleable {

    public static final String STYLE_SHEET = Resources.DEBUGGER.getCss("node-css-editor");

    @SuppressWarnings("unchecked")
    public NodeCss(Parent node) {
        getStylesheets().add(STYLE_SHEET);
        getStyleClass().add("node-css");
        getChildren().add(new CssPropertiesView(
                node.getCssMetaData().stream().map(s -> ((CssMetaData<Styleable, ?>) s).getStyleableProperty(node)).filter(property -> property instanceof Observable).collect(Collectors.toList())
                , node));
        setPadding(new Insets(10));
    }

}
