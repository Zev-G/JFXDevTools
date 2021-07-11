package com.me.tmw.debug.devtools.nodeinfo;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class SheetInfo extends TitledPane {

    private final Stylesheet stylesheet;

    private final VBox rules = new VBox();
    private final BorderPane layout = new BorderPane();

    private final Hyperlink viewMore = new Hyperlink("View full style sheet");

    private final Button openButton = new Button("", NodeMisc.svgPath(SVG.OPEN, 0.8));
    private final Button removeButton = new Button("", NodeMisc.svgPath(SVG.TRASH, 0.5));
    private final StackPane removeButtonPlaceHolder = new StackPane(removeButton);
    private final HBox content = new HBox(openButton, removeButtonPlaceHolder);

    private final String baseText;

    public SheetInfo(Stylesheet stylesheet, Parent node, Runnable open) {
        this.stylesheet = stylesheet;
        String[] urlPieces = stylesheet.getUrl().split("[/\\\\]");
        baseText = urlPieces[urlPieces.length - 1];
        setText(baseText);

        openButton.setOnAction(event -> open.run());
        removeButton.setOnAction(event -> node.getStylesheets().remove(stylesheet.getUrl()));
        content.setSpacing(3);
        setGraphic(content);
        setContentDisplay(ContentDisplay.RIGHT);
        Sheets.Essentials.makeSmoothButton(removeButton, openButton);

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

    public void setInherited(boolean b) {
        if (b) {
            setText(baseText + " (Inherited)");
            removeButtonPlaceHolder.getChildren().clear();
        } else {
            setText(baseText);
            if (!removeButtonPlaceHolder.getChildren().contains(removeButton)) {
                removeButtonPlaceHolder.getChildren().add(removeButton);
            }
        }
    }

}
