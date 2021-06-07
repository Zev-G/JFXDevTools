package com.me.tmw.debug.devtools.nodeinfo.css.sheets;

import com.me.tmw.css.Sheets;
import com.me.tmw.debug.devtools.nodeinfo.css.CssPropertiesView;
import com.me.tmw.debug.devtools.nodeinfo.css.NodeCss;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SheetRule extends VBox {

    private final Rule rule;

    private final TextFlow selectors = new TextFlow();
    private final CssPropertiesView declarations;
    private final HBox bottom = new HBox();

    @SuppressWarnings("unchecked")
    public SheetRule(Rule rule, Parent node) {
        this.rule = rule;

        List<StyleableProperty<?>> styleableProperties = rule.getDeclarations().stream()
//                .filter(declaration -> declaration.getProperty() != null)
//                .filter(declaration -> declaration.getParsedValue().getConverter() != null)
                .map(declaration -> {
                    try {
                        return new SimpleStyleableObjectProperty<>(new CssMetaData<Styleable, Object>(
                                declaration.getProperty() == null ? "no-property" : declaration.getProperty(),
                                declaration.getParsedValue().getConverter() == null ? new StyleConverter<>() : declaration.getParsedValue().getConverter()) {
                            @Override
                            public boolean isSettable(Styleable styleable) {
                                return false;
                            }

                            @Override
                            public StyleableProperty<Object> getStyleableProperty(Styleable styleable) {
                                return null;
                            }
                        }, declaration.getParsedValue().convert(new Font(10)));
                    } catch (ClassCastException error) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        declarations = new CssPropertiesView(styleableProperties, node);
        declarations.setDisable(true);
        declarations.getStyleClass().add(Sheets.Essentials.SILENT_DISABLE_CLASS);
        declarations.getStylesheets().addAll(NodeCss.STYLE_SHEET, Sheets.Essentials.STYLE_SHEET);

        ObservableList<Selector> ruleSelectors = rule.getSelectors();
        for (int i = 0, ruleSelectorsSize = ruleSelectors.size(); i < ruleSelectorsSize; i++) {
            Selector selector = ruleSelectors.get(i);
            selectors.getChildren().add(new SheetSelector(selector));
            if (i + 1 < ruleSelectorsSize) {
                Text comma = new Text(", ");
                selectors.getChildren().add(comma);
            }
        }
        Text openBracket = new Text(" {");
        selectors.getChildren().add(openBracket);

        Text closeBracket = new Text("}");
        bottom.getChildren().add(closeBracket);

        selectors.setStyle("-fx-font-size: 14;");

        declarations.setPadding(new Insets(0, 0, 0, 30));

        getChildren().addAll(selectors, declarations, bottom);
    }

}
