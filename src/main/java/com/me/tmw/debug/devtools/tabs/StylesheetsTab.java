package com.me.tmw.debug.devtools.tabs;

import com.me.tmw.debug.devtools.nodeinfo.css.sheets.SheetsInfo;
import com.me.tmw.debug.devtools.tabs.FilesTab.Source;
import com.me.tmw.nodes.richtextfx.languages.CSSLang;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class StylesheetsTab extends Tab {

    private StructureTab structureTab;
    private final Map<Node, SheetsInfo> sheetsInfoMap = new HashMap<>();
    private final StackPane noStylesheets = new StackPane(new Label("Can't load style sheets."));

    private final ComboBox<String> typeComboBox = new ComboBox<>() {
        {
            this.getItems().addAll("File Path", "URL");
            this.getSelectionModel().select("File Path");
        }
    };
    private final TextField newStylesheetField = new TextField() {
        {
            promptTextProperty().bind(
                    Bindings.createStringBinding(() -> {
                        String selected = typeComboBox.getSelectionModel().getSelectedItem();
                        if (selected == null) {
                            selected = "";
                        }
                        if (selected.equals("File Path")) {
                            return "Location of the file, e.g. \"C:\\Windows\\stylesheet.css\"";
                        } else if (selected.equals("URL")) {
                            return "URL of the stylesheet, e.g. \"https://www.domain/files/stylesheet.css\"";
                        } else {
                            return "";
                        }
                    }, typeComboBox.getSelectionModel().selectedItemProperty())
            );
            HBox.setHgrow(this, Priority.ALWAYS);
        }
    };
    private final Button add = new Button("Add") {
        {
            setOnAction(event -> {
                URL result;
                String selected = typeComboBox.getSelectionModel().getSelectedItem();
                if (structureTab.getSceneTree().getSelectionModel().getSelectedItem() == null || !(structureTab.getSceneTree().getSelectionModel().getSelectedItem().getValue() instanceof Parent) || selected == null || newStylesheetField.getText().isEmpty()) {
                    return;
                }
                if (selected.equals("File Path")) {
                    try {
                        File file = new File(newStylesheetField.getText());
                        if (!file.exists()) {
                            if (!file.createNewFile()) {
                                return;
                            }
                        }
                        result = file.toURI().toURL();
                    } catch (IOException e) {
                        return;
                    }
                } else if (selected.equals("URL")) {
                    try {
                        result = new URL(newStylesheetField.getText());
                    } catch (MalformedURLException e) {
                        return;
                    }
                } else {
                    return;
                }
                ((Parent) structureTab.getSceneTree().getSelectionModel().getSelectedItem().getValue()).getStylesheets().add(result.toExternalForm());
                Source source = structureTab.getTools().getFilesTab().loadURL(result);
                source.setOnSaved(text -> {
                    Parent selectedParent = ((Parent) structureTab.getSceneTree().getSelectionModel().getSelectedItem().getValue());
                    selectedParent.getStylesheets().remove(result.toExternalForm());
                    selectedParent.getStylesheets().add(result.toExternalForm());
                });
            });
        }
    };
    private final HBox topBar = new HBox(typeComboBox, newStylesheetField, new Group(), add) {
        {
            setSpacing(2);
        }
    };

    private final StackPane stylesheetsPlaceHolder = new StackPane();
    private final VBox display = new VBox(topBar, stylesheetsPlaceHolder);

    public StylesheetsTab(StructureTab structureTab) {
        super("Stylesheets (Beta)");
        setContent(display);
        this.structureTab = structureTab;

        structureTab.getSceneTree().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue() instanceof Parent) {
                load((Parent) newValue.getValue());
            } else {
                stylesheetsPlaceHolder.getChildren().setAll(noStylesheets);
            }
        });
    }

    public void load(Parent value) {
        if (!sheetsInfoMap.containsKey(value)) {
            sheetsInfoMap.put(value, new SheetsInfo(value, url -> {
                structureTab.getTools().getFilesTab().loadURL(url, new CSSLang());
                structureTab.getTools().selectTab(structureTab.getTools().getFilesTab());
            }));
        }
        ScrollPane scrollPane = new ScrollPane(sheetsInfoMap.get(value));
        stylesheetsPlaceHolder.getChildren().setAll(scrollPane);
    }

}
