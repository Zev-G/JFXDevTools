package com.me.tmw.debug.devtools.nodeinfo.css.sheets;

import com.me.tmw.css.Sheets;
import com.me.tmw.nodes.control.svg.SVG;
import com.me.tmw.nodes.util.NodeMisc;
import javafx.css.Rule;
import javafx.css.Stylesheet;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static com.me.tmw.css.Sheets.Essentials.*;

public class SheetInfo extends TitledPane {

    private final Stylesheet stylesheet;

    private final VBox rules = new VBox();
    private final BorderPane layout = new BorderPane();

    private final Hyperlink viewMore = new Hyperlink("View full style sheet");

    private final Button openButton = new Button("", NodeMisc.svgPath(SVG.OPEN, 0.8));

    public SheetInfo(Stylesheet stylesheet, Parent node, Runnable open) {
        this.stylesheet = stylesheet;
        String[] urlPieces = stylesheet.getUrl().split("[/\\\\]");
        setText(urlPieces[urlPieces.length - 1]);

        openButton.setOnAction(event -> open.run());
        setGraphic(this.openButton);
        setContentDisplay(ContentDisplay.RIGHT);
        this.openButton.getStyleClass().addAll(TRANSPARENT_BUTTON_CLASS, LIGHT_SVG_BUTTON_CLASS, HAND_CURSOR_CLASS);

        List<Rule> validRules = new ArrayList<>(stylesheet.getRules());

        List<Rule> shownRules = new ArrayList<>();

        validRules.stream().filter(rule -> Sheets.applies(node, rule)).forEach(rule -> {
            shownRules.add(rule);
            SheetRule sheetRule = new SheetRule(rule, node);
            rules.getChildren().add(sheetRule);
        });

        layout.setLeft(rules);

        if (shownRules.size() < stylesheet.getRules().size()) {
            layout.setBottom(viewMore);

            viewMore.setOnAction(event -> {
                int i = 0;
                for (Rule rule : validRules) {
                    if (!shownRules.contains(rule)) {
                        SheetRule sheetRule = new SheetRule(rule, node);
                        rules.getChildren().add(i, sheetRule);
                        shownRules.add(rule);
                    }
                    i++;
                }
                layout.setBottom(null);
            });
        }

        setContent(layout);
    }

}
