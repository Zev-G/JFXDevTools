package com.me.tmw.debug.devtools.nodeinfo.css.sheets;

import com.me.tmw.css.Sheets;
import javafx.css.Stylesheet;
import javafx.scene.Parent;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class SheetInfo extends TitledPane {

    private final Stylesheet stylesheet;

    private final VBox rules = new VBox();

    public SheetInfo(Stylesheet stylesheet, Parent node) {
        this.stylesheet = stylesheet;
        String[] urlPieces = stylesheet.getUrl().split("[/\\\\]");
        setText(urlPieces[urlPieces.length - 1]);

        stylesheet.getRules().stream().filter(rule -> Sheets.applies(node, rule)).forEach(rule -> {
            rules.getChildren().add(new SheetRule(rule, node));
        });

        setContent(rules);
    }

}
