package com.me.tmw.debug.devtools.nodeinfo;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackgroundCssValues extends CssValues<Background> {

    private final Label backgroundFill = new Label("-fx-background-fill: ");
    private final Label cornerRadii = new Label("-fx-background-radius: ");
    private final Label insets = new Label("-fx-background-insets: ");

    private final GridPane values = new GridPane();
    private final VBox names = new VBox();

    private final List<ColorCssValue> fillPreviews = new ArrayList<>();
    private final List<InsetsCssValue> radiiPreviews = new ArrayList<>();
    private final List<InsetsCssValue> insetsPreviews = new ArrayList<>();

    private final List<BackgroundFill> strokes;

    BackgroundCssValues(Background initialValue, Runnable updateNode, BooleanProperty expanded) {
        super(initialValue, updateNode, expanded);

        values.disableProperty().bind(Bindings.not(editable));

        strokes = initialValue != null ? initialValue.getFills() : Collections.emptyList();

        names.getChildren().addAll(
                backgroundFill, cornerRadii, insets
        );

        int i = 0;
        for (BackgroundFill fill : strokes) {
            ColorCssValue colorCssValue = new ColorCssValue(fill.getFill()
                    , updateNode);
            fillPreviews.add(colorCssValue);
            values.add(colorCssValue.node(), i, 0);

            CornerRadii fillRadii = fill.getRadii();
            InsetsCssValue radii = new InsetsCssValue(new Insets(fillRadii.getTopLeftHorizontalRadius(), fillRadii.getTopRightHorizontalRadius(), fillRadii.getBottomRightHorizontalRadius(), fillRadii.getBottomLeftHorizontalRadius())
                    , updateNode);
            radiiPreviews.add(radii);
            values.add(radii.node(), i, 1);

            InsetsCssValue insets = new InsetsCssValue(fill.getInsets(),
                    updateNode);
            insetsPreviews.add(insets);
            values.add(insets.node(), i, 2);
            i++;
        }

        values.setHgap(2);
        values.setVgap(5);
        names.setSpacing(5);
        names.setPadding(new Insets(3, 3, 3, 30));
        values.setPadding(new Insets(3, 0, 3, 0));
        init();
    }


    @Override
    public String toCssString() {
        // Not used
        return null;
    }

    @Override
    protected boolean isUsingAltGen() {
        return true;
    }

    @Override
    public Background genAlt() {
        List<BackgroundFill> backgroundFills = new ArrayList<>();
        for (int i = 0; i < strokes.size(); i++) {
            Paint color = i < fillPreviews.size() ? fillPreviews.get(i).genAlt() : Color.TRANSPARENT;

            Insets radiiAsInsets = i < radiiPreviews.size() ? radiiPreviews.get(i).genAlt() : Insets.EMPTY;
            CornerRadii radii = new CornerRadii(radiiAsInsets.getTop(), radiiAsInsets.getRight(), radiiAsInsets.getBottom(), radiiAsInsets.getLeft(), false);

            Insets insets = i < insetsPreviews.size() ? insetsPreviews.get(i).genAlt() : Insets.EMPTY;

            backgroundFills.add(new BackgroundFill(color, radii, insets));
        }
        return new Background(backgroundFills, Collections.emptyList());
    }

    @Override
    public void addNodes(VBox vBox) {
        // not used
    }

    @Override
    public Node belowName() {
        return names;
    }

    @Override
    public Node belowValue() {
        return values;
    }

}
