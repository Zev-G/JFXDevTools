package com.me.tmw.properties.editors;

import com.me.tmw.nodes.control.paint.ColorPicker;
import com.me.tmw.nodes.control.paint.LinearGradientPicker;
import com.me.tmw.nodes.control.paint.RadialGradientPicker;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.resource.Resources;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PaintPropertyEditor extends PaintEditorBase<Paint> {

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("paint-property-editor");

    private final ChoiceBox<PaintType> typeSelector = new ChoiceBox<>(FXCollections.observableArrayList(PaintType.values()));
    private final Map<PaintType, Parent> editors = new HashMap<>();
    private final Map<PaintType, Property<? extends Paint>> editorProperties = new HashMap<>();

    private final BorderPane header = new BorderPane(typeSelector);
    private final StackPane content = new StackPane();
    private final VBox layout = new VBox(header, new Separator(), content);

    private final ColorPicker colorEditor = new ColorPicker();
    private final LinearGradientPicker linearGradientEditor = new LinearGradientPicker();
    private final RadialGradientPicker radialGradientPicker = new RadialGradientPicker();

    public PaintPropertyEditor(Property<Paint> value) {
        this(value.getName(), value);
    }

    public PaintPropertyEditor(String name, Property<Paint> value) {
        super(name, value);

        // CSS data initialization
        layout.setId("popup-root");
        editorRoot.getStylesheets().add(STYLE_SHEET);
        previewRoot.getStylesheets().add(STYLE_SHEET);

        // Editor listeners
        colorEditor.customColorProperty().addListener(observable -> {
            if (typeSelector.getValue() == PaintType.COLOR) {
                Color colorVal = colorEditor.getCustomColor();
                if (!colorVal.equals(get())) {
                    set(colorVal);
                }
            }
        });
        linearGradientEditor.valueProperty().addListener(observable -> {
            if (typeSelector.getValue() == PaintType.LINEAR_GRADIENT) {
                LinearGradient gradientVal = linearGradientEditor.getValue();
                if (!gradientVal.equals(get())) {
                    set(gradientVal);
                }
            }
        });
        radialGradientPicker.valueProperty().addListener(observable -> {
            if (typeSelector.getValue() == PaintType.RADIAL_GRADIENT) {
                RadialGradient gradientVal = radialGradientPicker.getValue();
                if (!gradientVal.equals(get())) {
                    set(gradientVal);
                }
            }
        });

        // Load editors
        editors.put(PaintType.COLOR, colorEditor);
        editorProperties.put(PaintType.COLOR, colorEditor.customColorProperty());
        editors.put(PaintType.LINEAR_GRADIENT, linearGradientEditor);
        editorProperties.put(PaintType.LINEAR_GRADIENT, linearGradientEditor.valueProperty());
        editors.put(PaintType.RADIAL_GRADIENT, radialGradientPicker);
        editorProperties.put(PaintType.RADIAL_GRADIENT, radialGradientPicker.valueProperty());

        // Tab selection.
        NodeMisc.runAndAddListener(typeSelector.getSelectionModel().selectedItemProperty(),
                observable -> {
                    PaintType selected = typeSelector.getSelectionModel().getSelectedItem();
                    if (editors.containsKey(selected)) {
                        content.getChildren().setAll(editors.get(selected));
                        if (editorProperties.containsKey(selected)) {
                            Property<? extends Paint> selectedProperty = editorProperties.get(selected);
                            if (!selectedProperty.getValue().equals(get())) {
                                set(selectedProperty.getValue());
                            }
                        }
                    } else {
                        content.getChildren().clear();
                    }
                }
        );
        // Property listener
        NodeMisc.runAndAddListener(value,
                observable -> {
                    Paint val = get();
                    if (val == null) {
                        val = Color.TRANSPARENT;
                    }
                    if (val instanceof Color) {
                        colorEditor.setCurrentColor((Color) val);
                        typeSelector.setValue(PaintType.COLOR);
                    } else if (val instanceof LinearGradient) {
                        linearGradientEditor.setValue((LinearGradient) val);
                        typeSelector.setValue(PaintType.LINEAR_GRADIENT);
                    } else if (val instanceof RadialGradient) {
                        radialGradientPicker.setValue((RadialGradient) val);
                        typeSelector.setValue(PaintType.RADIAL_GRADIENT);
                    } else if (val instanceof ImagePattern) {
                        // TODO Load onto image patter editor.
                        typeSelector.setValue(PaintType.IMAGE_PATTERN);
                    }
                    typeSelector.getSelectionModel().select(PaintType.fromClass(val.getClass()).orElseThrow());
                }
        );

        setEditor(layout);
    }

    private enum PaintType {
        COLOR("Color", Color.class),
        LINEAR_GRADIENT("Linear Gradient", LinearGradient.class),
        RADIAL_GRADIENT("Radial Gradient", RadialGradient.class),
        IMAGE_PATTERN("Image Pattern", ImagePattern.class);

        static Optional<PaintType> fromClass(Class<? extends Paint> type) {
            return Arrays.stream(values()).filter(paintType -> paintType.getType() == type).findFirst();
        }

        private final String asString;
        private final Class<? extends Paint> type;

        PaintType(String asString, Class<? extends Paint> type) {
            this.asString = asString;
            this.type = type;
        }

        @Override
        public String toString() {
            return asString;
        }

        public Class<? extends Paint> getType() {
            return type;
        }
    }

}
