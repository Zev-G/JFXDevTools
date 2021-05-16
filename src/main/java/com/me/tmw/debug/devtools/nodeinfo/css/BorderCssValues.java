package com.me.tmw.debug.devtools.nodeinfo.css;

import com.me.tmw.nodes.util.NodeMisc;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

import java.util.*;
import java.util.List;

public class BorderCssValues extends CssValues<Border> {

    private final Label borderColor  = new Label("-fx-border-color: ");
    private final Label borderRadii  = new Label("-fx-border-radius: ");
    private final Label borderInsets = new Label("-fx-border-insets: ");
    private final Label borderStyle  = new Label("-fx-border-style: ");
    private final Label borderWidth  = new Label("-fx-border-width: ");

    private final GridPane values = new GridPane();
    private final VBox names = new VBox();

    private final List<Map<Side, ColorCssValue>> colorEditors = new ArrayList<>();
    private final List<InsetsCssValue> radiiEditors = new ArrayList<>();
    private final List<InsetsCssValue> insetsEditors = new ArrayList<>();
    private final List<Map<Side, EnumCssValue<BorderStrokeStyles>>> styleEditors = new ArrayList<>();
    private final List<InsetsCssValue> widthEditors = new ArrayList<>();

    private final List<BorderStroke> borderStrokes;

    BorderCssValues(Border initialValue, Runnable updateNode, BooleanProperty expanded) {
        super(initialValue, updateNode, expanded);

        this.borderStrokes = initialValue.getStrokes();

        if (!borderStrokes.isEmpty()) {
            names.getChildren().addAll(
                    borderColor, borderRadii, borderInsets, borderStyle, borderWidth
            );
        }

        for (int i = 0; i < borderStrokes.size(); i++) {
            BorderStroke stroke = borderStrokes.get(i);
            loadPaints(stroke, i);
            loadRadiis(stroke, i);
            loadInsets(stroke, i);
            loadStyles(stroke, i);
            loadWidths(stroke, i);
        }

        values.setHgap(2);
        values.setVgap(5);
        names.setSpacing(6);
        names.setPadding(new Insets(3, 3, 3, 30));
        values.setPadding(new Insets(3, 0, 3, 0));
        init();
    }

    private void loadPaints(BorderStroke stroke, int i) {
        HashMap<Side, ColorCssValue> map = new HashMap<>();
        if (NodeMisc.allEqual(stroke.getTopStroke(), stroke.getBottomStroke(), stroke.getRightStroke(), stroke.getLeftStroke())) {
            ColorCssValue singular = new ColorCssValue(stroke.getTopStroke(), updateNode);
            for (Side side : Side.values()) {
                map.put(side, singular);
            }
        } else {
            ColorCssValue top = new ColorCssValue(stroke.getTopStroke(), updateNode);
            ColorCssValue right = new ColorCssValue(stroke.getRightStroke(), updateNode);
            ColorCssValue bottom = new ColorCssValue(stroke.getBottomStroke(), updateNode);
            ColorCssValue left = new ColorCssValue(stroke.getLeftStroke(), updateNode);

            map.put(Side.TOP,    top);
            map.put(Side.RIGHT,  right);
            map.put(Side.BOTTOM, bottom);
            map.put(Side.LEFT,   left);
        }
        colorEditors.add(map);

        HBox strokes = new HBox();
        for (ColorCssValue value : map.values()) {
            Node node = value.node();
            HBox.setHgrow(node, Priority.SOMETIMES);
            if (!strokes.getChildren().contains(node)) {
                strokes.getChildren().add(value.node());
            }
        }
        strokes.setSpacing(5);
        strokes.setPadding(new Insets(0, 5, 0, 0));
        values.add(strokes, i, 0);
    }
    private void loadRadiis(BorderStroke stroke, int i) {
        CornerRadii cornerRadii = stroke.getRadii();
        InsetsCssValue radiis = new InsetsCssValue(
                new Insets(cornerRadii.getTopLeftHorizontalRadius(), cornerRadii.getTopRightHorizontalRadius(), cornerRadii.getBottomRightHorizontalRadius(), cornerRadii.getBottomLeftHorizontalRadius())
                , updateNode
        );
        radiiEditors.add(radiis);
        values.add(radiis.node(), i, 1);
    }
    private void loadInsets(BorderStroke stroke, int i) {
        InsetsCssValue insets = new InsetsCssValue(stroke.getInsets(), updateNode);
        insetsEditors.add(insets);
        values.add(insets.node(), i, 2);
    }
    private void loadStyles(BorderStroke stroke, int i) {
        HashMap<Side, EnumCssValue<BorderStrokeStyles>> map = new HashMap<>();
        if (NodeMisc.allEqual(stroke.getTopStroke(), stroke.getRightStroke(), stroke.getBottomStroke(), stroke.getLeftStroke())) {
            EnumCssValue<BorderStrokeStyles> singular = new EnumCssValue<>(BorderStrokeStyles.class, BorderStrokeStyles.getFromBorderStrokeStyle(stroke.getTopStyle()), updateNode);
            for (Side side : Side.values()) {
                map.put(side, singular);
            }
        } else {
            EnumCssValue<BorderStrokeStyles> top = new EnumCssValue<>(BorderStrokeStyles.class, BorderStrokeStyles.getFromBorderStrokeStyle(stroke.getTopStyle()), updateNode);
            EnumCssValue<BorderStrokeStyles> right = new EnumCssValue<>(BorderStrokeStyles.class, BorderStrokeStyles.getFromBorderStrokeStyle(stroke.getRightStyle()), updateNode);
            EnumCssValue<BorderStrokeStyles> bottom = new EnumCssValue<>(BorderStrokeStyles.class, BorderStrokeStyles.getFromBorderStrokeStyle(stroke.getBottomStyle()), updateNode);
            EnumCssValue<BorderStrokeStyles> left = new EnumCssValue<>(BorderStrokeStyles.class, BorderStrokeStyles.getFromBorderStrokeStyle(stroke.getLeftStyle()), updateNode);

            map.put(Side.TOP,    top);
            map.put(Side.RIGHT,  right);
            map.put(Side.BOTTOM, bottom);
            map.put(Side.LEFT,   left);
        }
        styleEditors.add(map);

        HBox styles = new HBox();
        for (EnumCssValue<BorderStrokeStyles> value : map.values()) {
            Node node = value.node();
            HBox.setHgrow(node, Priority.SOMETIMES);
            if (!styles.getChildren().contains(node)) {
                styles.getChildren().add(value.node());
            }
        }
        styles.setSpacing(5);
        styles.setPadding(new Insets(0, 5, 0, 0));
        values.add(styles, i, 3);
    }
    private void loadWidths(BorderStroke stroke, int i) {
        BorderWidths width = stroke.getWidths();
        InsetsCssValue widths = new InsetsCssValue(
                new Insets(width.getTop(), width.getRight(), width.getBottom(), width.getLeft()),
                updateNode
        );
        widthEditors.add(widths);
        values.add(widths.node(), i, 4);
    }

    @Override
    public Border genAlt() {
        List<BorderStroke> newBorderStrokes = new ArrayList<>();
        for (int i = 0; i < borderStrokes.size(); i++) {
            Map<Side, ColorCssValue> colorMap = colorEditors.get(i);
            Paint topColor = colorMap.get(Side.TOP).genAlt();
            Paint rightColor = colorMap.get(Side.RIGHT).genAlt();
            Paint bottomColor = colorMap.get(Side.BOTTOM).genAlt();
            Paint leftColor = colorMap.get(Side.LEFT).genAlt();

            Map<Side, EnumCssValue<BorderStrokeStyles>> stylesMap = styleEditors.get(i);
            BorderStrokeStyle topStroke = stylesMap.get(Side.TOP).genAlt().getStyle();
            BorderStrokeStyle rightStroke = stylesMap.get(Side.RIGHT).genAlt().getStyle();
            BorderStrokeStyle bottomStroke = stylesMap.get(Side.BOTTOM).genAlt().getStyle();
            BorderStrokeStyle leftStroke = stylesMap.get(Side.LEFT).genAlt().getStyle();

            Insets radiiAsInsets = radiiEditors.get(i).genAlt();
            CornerRadii radii = new CornerRadii(radiiAsInsets.getTop(), radiiAsInsets.getRight(), radiiAsInsets.getBottom(), radiiAsInsets.getLeft(), false);

            Insets widthAsInsets = widthEditors.get(i).genAlt();
            BorderWidths borderWidths = new BorderWidths(widthAsInsets.getTop(), widthAsInsets.getRight(), widthAsInsets.getBottom(), widthAsInsets.getLeft());

            Insets insets = insetsEditors.get(i).genAlt();

            newBorderStrokes.add(
                    new BorderStroke(
                            topColor, rightColor, bottomColor, leftColor,
                            topStroke, rightStroke, bottomStroke, leftStroke,

                            radii, borderWidths, insets
                    )
            );
        }
        return new Border(newBorderStrokes, Collections.emptyList()); // TODO
    }

    @Override
    public String toCssString() {
        return null; // Un used
    }

    @Override
    public void addNodes(VBox vBox) {
        // Un used
    }

    @Override
    public Node belowName() {
        return names;
    }

    @Override
    public Node belowValue() {
        return values;
    }

    @Override
    protected boolean isUsingAltGen() {
        return true;
    }

    protected enum BorderStrokeStyles {
        NONE(BorderStrokeStyle.NONE),
        DOTTED(BorderStrokeStyle.DOTTED),
        DASHED(BorderStrokeStyle.DASHED),
        SOLD(BorderStrokeStyle.SOLID)
        ;

        private static BorderStrokeStyles getFromBorderStrokeStyle(BorderStrokeStyle strokeStyle){
            if (strokeStyle == null) return NONE;
            for (BorderStrokeStyles enumEquiv : values()) {
                if (enumEquiv.getStyle() == strokeStyle) {
                    return enumEquiv;
                }
            }
            return NONE;
        }

        private final BorderStrokeStyle style;

        BorderStrokeStyles(BorderStrokeStyle style) {
            this.style = style;
        }

        public BorderStrokeStyle getStyle() {
            return style;
        }
    }

}
