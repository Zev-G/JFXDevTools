package com.me.tmw.nodes.control;

import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.properties.editors.*;
import com.me.tmw.resource.Resources;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

import java.util.Objects;

public class FontEditor extends VBox {

    private static final String STYLE_SHEET = Resources.NODES.getCss("font-editor");

    private final ObjectProperty<Font> value = new SimpleObjectProperty<>(this, "value");
    private boolean pause;
    private Font loaded;

    private final OptionBasedPropertyEditor<String> family = OptionBasedPropertyEditor.fromArray(new SimpleStringProperty(this, "Family"), Font.getFamilies().toArray(new String[0]));
    private final EnumPropertyEditor<FontWeight> fontWeight = new EnumPropertyEditor<>(new SimpleObjectProperty<>(this, "Font Weight"), FontWeight.class);
    private final BooleanPropertyEditor italic = new BooleanPropertyEditor(new SimpleBooleanProperty(this, "Italic"));
    private final DoublePropertyEditor size = new DoublePropertyEditor(new SimpleDoubleProperty(this, "Size"));

    private final TextArea preview = new TextArea();
    private final GridPane propertyDisplay = new GridPane();

    public FontEditor() {
        this(Font.getDefault());
    }
    public FontEditor(Font defaultVal) {

        // Listen to value
        value.addListener(observable -> {
            Font val = value.get();
            if (!Objects.equals(loaded, val)) {
                loadFont(val);
            }
        });
        value.set(defaultVal);

        // CSS Things
        getStylesheets().add(STYLE_SHEET);
        getStyleClass().add("font-editor");
        propertyDisplay.getStyleClass().add("font-properties");

        // Listen to properties.
        family.addListener(observable -> updateFont());
        fontWeight.addListener(observable -> updateFont());
        italic.addListener(observable -> updateFont());
        size.addListener(observable -> updateFont());

        // Init property display.
        initPropertyDisplay();

        // Init family editor.
        Callback<ListView<String>, ListCell<String>> cellFactory = param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (item != null) {
                    setFont(Font.font(item, 12));
                }
            }
        };
        family.getNode().setCellFactory(cellFactory);
        family.getNode().setButtonCell(cellFactory.call(null));

        // Init preview
        preview.fontProperty().bind(value);
        preview.setWrapText(true);
        preview.getStyleClass().add("font-preview");
        //noinspection SpellCheckingInspection
        preview.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        // Set children.
        getChildren().addAll(propertyDisplay, new Separator(), preview);
    }

    private void initPropertyDisplay() {
        initProperty(family, 0);
        initProperty(size, 1);
        initProperty(fontWeight, 2);
        initProperty(italic, 3);
    }
    private void initProperty(PropertyEditor<?> editor, int i) {
        Label label = new Label();
        label.textProperty().bind(editor.nameProperty());

        propertyDisplay.add(label, 0, i);
        propertyDisplay.add(editor.getNode(), 1, i);
    }

    private synchronized void loadFont(Font font) {
        pause = true;
        loaded = font;

        family.setValue(font.getFamily());
        boolean italic = font.getStyle().toLowerCase().contains("italic");
        FontWeight weight = FontWeight.findByName(font.getStyle().replace("Italic", "").trim());
        if (weight == null) weight = FontWeight.NORMAL;
        this.italic.set(italic);
        this.fontWeight.set(weight);
        size.set(font.getSize());

        pause = false;
    }
    private synchronized void updateFont() {
        if (!pause) {
            loaded = Font.font(family.get(), fontWeight.get(), italic.get() ? FontPosture.ITALIC : FontPosture.REGULAR, size.get().doubleValue());
            value.set(loaded);
        }
    }

    public Font getValue() {
        return value.get();
    }

    public ObjectProperty<Font> valueProperty() {
        return value;
    }

    public void setValue(Font value) {
        this.value.set(value);
    }

}
