package com.me.tmw.debug.devtools.tabs;

import com.me.tmw.debug.devtools.DevTools;
import com.me.tmw.nodes.richtextfx.EditorLanguageBase;
import com.me.tmw.nodes.richtextfx.LanguageCodeArea;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.fxmisc.richtext.CodeArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FilesTab extends Tab {

    private final Parent root;
    private final DevTools tools;

    private final Map<Object, SourceTab> sourceTabMap = new HashMap<>();

    private final GridPane backdrop = new GridPane() {
        { add(new BorderPane(new Label("Open a file in the left view for it to show up here.")), 0, 0); }
    };
    private final TabPane openFiles = new TabPane();
    private final StackPane tabPaneBg = new StackPane(backdrop, openFiles);

    private final ListView<Object> files = new ListView<>();
    private final SplitPane split = new SplitPane(files, tabPaneBg);

    public FilesTab(Parent root, DevTools tools) {
        super("Files");
        this.root = root;
        this.tools = tools;

        SplitPane.setResizableWithParent(files, false);

        setContent(split);
        files.setCellFactory(param -> {
            ListCell<Object> cell = new ListCell<>();
            cell.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && sourceTabMap.containsKey(newValue)) {
                    cell.setText(sourceTabMap.get(newValue).getSource().getName());
                } else {
                    cell.setText("");
                    cell.setGraphic(null);
                }
            });
            cell.setOnMousePressed(event -> {
                if (event.getClickCount() == 2 && cell.getItem() != null) {
                    loadSource(sourceTabMap.get(cell.getItem()).source);
                }
            });
            return cell;
        });
    }

    public boolean loadURL(String url) {
        return loadURL(url, null);
    }
    public boolean loadURL(String url, EditorLanguageBase langChoice) {
        try {
            loadURL(new URL(url), langChoice);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
    public void loadURL(URL url) {
        loadURL(url, null);
    }
    public void loadURL(URL url, EditorLanguageBase langChoice) {
        Supplier<String> reader = () -> {
            StringBuilder urlText = new StringBuilder();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    urlText.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error reading text of url: " + url;
            }
            return urlText.toString();
        };

        String[] urlPieces = url.getPath().split("[/\\\\]");

        Source source = new Source(
                reader, null, url.toString(), urlPieces[urlPieces.length - 1]
        );
        source.setPreferredLanguage(langChoice);
        loadSource(source);
    }

    public void loadSource(Source source) {
        this.loadSource(source, false);
    }
    public void loadSource(Source source, boolean silent) {
        if (source == null) {
            throw new NullPointerException();
        }
        SourceTab tab;
        if (sourceTabMap.containsKey(source.getKey())) {
            tab = sourceTabMap.get(source.getKey());
        } else {
            tab = new SourceTab(source);
            sourceTabMap.put(source.getKey(), tab);
        }

        if (!openFiles.getTabs().contains(tab)) {
            openFiles.getTabs().add(tab);
        }
        if (!silent && !openFiles.getSelectionModel().getSelectedItem().equals(tab)) {
            openFiles.getSelectionModel().select(tab);
        }
        if (!files.getItems().contains(source.getKey())) {
            files.getItems().add(source.getKey());
        }
    }

    public static class Source {

        private final Object key;

        private final String name;
        private final Supplier<String> reader;
        private final Consumer<String> writer;

        private EditorLanguageBase preferredLanguage;

        private Source(Supplier<String> reader, Consumer<String> writer, Object key, String name) {
            if (key == null) {
                throw new IllegalArgumentException("Key can't be null");
            }

            this.reader = reader;
            this.writer = writer;

            this.key = key;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Object getKey() {
            return key;
        }

        public Consumer<String> getWriter() {
            return writer;
        }

        public Supplier<String> getReader() {
            return reader;
        }

        public void setPreferredLanguage(EditorLanguageBase preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
        }

    }

    private static class SourceTab extends Tab {

        private final Source source;

        public SourceTab(Source source) {
            this.source = source;

            setText(source.getName());
            CodeArea area = new LanguageCodeArea(source.preferredLanguage).addLineNumbers();
            area.replaceText(source.getReader().get());
            setContent(area);

            if (source.writer == null) {
                area.setEditable(false);
            }
        }

        public Source getSource() {
            return source;
        }

    }

}
