package com.me.tmw.debug.devtools.nodeinfo.css.sheets;

import com.me.tmw.css.Sheets;
import javafx.css.Rule;
import javafx.css.Stylesheet;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SheetInfo extends TitledPane {

    private final Stylesheet stylesheet;

    private final VBox rules = new VBox();
    private final BorderPane layout = new BorderPane();

    private final Hyperlink viewMore = new Hyperlink("View full style sheet");

    public SheetInfo(Stylesheet stylesheet, Parent node) {
        this.stylesheet = stylesheet;
        String[] urlPieces = stylesheet.getUrl().split("[/\\\\]");
        setText(urlPieces[urlPieces.length - 1]);

        List<Rule> validRules = stylesheet.getRules().stream().filter(this::shouldAddRule).collect(Collectors.toList());

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

    private boolean shouldAddRule(Rule rule) {
        return rule.getDeclarations().stream()
                .filter(declaration -> declaration.getProperty() != null)
                .anyMatch(declaration -> declaration.getParsedValue().getConverter() != null);
    }

}
