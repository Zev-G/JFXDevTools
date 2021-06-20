package com.me.tmw.properties.editors;

import com.me.tmw.nodes.tooltips.SimpleTooltip;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public abstract class OptionBasedPropertyEditor<T> extends PropertyEditorBase<T> {

    private final ObservableList<T> items;

    protected final ComboBox<T> selector = new ComboBox<>();

    public OptionBasedPropertyEditor(Property<T> value, ObservableList<T> items) {
        this(value.getName(), value, items);
    }
    public OptionBasedPropertyEditor(String name, Property<T> value, ObservableList<T> items) {
        super(name, value);
        this.items = items;

        selector.getSelectionModel().selectedItemProperty().addListener(observable -> {
            T selected = selector.getSelectionModel().getSelectedItem();
            if (get() != selected) {
                set(selected);
            }
        });

        addListener(observable -> {
            T newValue = get();
            if (items.contains(newValue)) {
                selector.getSelectionModel().select(newValue);
            } else {
                throw new IllegalStateException("Value: [" + newValue + "] does not exist in list of possible items.");
            }
        });

        Callback<ListView<T>, ListCell<T>> cellFactory = param -> {
            ListCell<T> cell = new ListCell<>();
            cell.itemProperty().addListener(observable -> cell.setText(String.valueOf(cell.getItem())));
            cell.setText(String.valueOf(cell.getItem()));
            return cell;
        };
        selector.setCellFactory(cellFactory);
        selector.setButtonCell(cellFactory.call(null));
        selector.setItems(items);
        selector.getSelectionModel().select(value.getValue());

        selector.setPromptText(value.getName());
        SimpleTooltip.install(selector, value.getName());
        setNode(selector);
    }

    public static <T> OptionBasedPropertyEditor<T> fromArray(Property<T> property, T[] array) {
        return new OptionBasedPropertyEditor<T>(property, FXCollections.observableArrayList(array)) { };
    }

    public ObservableList<T> getItems() {
        return items;
    }
}
