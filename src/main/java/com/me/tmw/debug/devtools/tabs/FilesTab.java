package com.me.tmw.debug.devtools.tabs;

import com.me.tmw.css.Sheets;
import com.me.tmw.debug.devtools.DevTools;
import com.me.tmw.nodes.control.svg.SVG;
import com.me.tmw.nodes.richtextfx.EditorLanguageBase;
import com.me.tmw.nodes.richtextfx.LanguageCodeArea;
import com.me.tmw.nodes.richtextfx.languages.CSSLang;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.nodes.util.Popups;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FilesTab extends Tab {

    private final ObservableSet<URL> unloadedURLs = FXCollections.observableSet();
    private final ObservableSet<Path> unloadedFiles = FXCollections.observableSet();

    private final Parent root;
    private final DevTools tools;

    private final ObservableMap<Object, SourceTab> sourceTabMap = FXCollections.observableHashMap();

    private final GridPane backdrop = new GridPane() {
        {
            add(new Label("Open a file in the left view for it to show up here."), 0, 0);
        }
    };
    private final TabPane openFiles = new TabPane() {
        {
            sceneProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    newValue.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> {
                        if (getSelectionModel().getSelectedItem() instanceof SourceTab) {
                            ((SourceTab) getSelectionModel().getSelectedItem()).save();
                        }
                    });
                }
            });
            setTabDragPolicy(TabDragPolicy.REORDER);
        }
    };
    private final StackPane tabPaneBg = new StackPane(backdrop, openFiles);

    private final ListView<Object> files = new ListView<>() {
        {
            setCellFactory(param -> {
                ListCell<Object> cell = new ListCell<>();
                cell.itemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        if (sourceTabMap.containsKey(newValue)) {
                            cell.setText(sourceTabMap.get(newValue).getSource().getName());
                            cell.setContextMenu(new ContextMenu(
                                    NodeMisc.makeMenuItem("Copy Path", actionEvent ->
                                            Toolkit.getDefaultToolkit().getSystemClipboard()
                                                    .setContents(
                                                            new StringSelection(sourceTabMap.get(newValue).getSource().getKey().toString())
                                                            , null))
                            ));
                        } else {
                            if (newValue instanceof Path && unloadedFiles.contains(newValue)) {
                                cell.setText(((Path) newValue).getFileName().toString());
                            } else if (newValue instanceof URL && unloadedURLs.contains(newValue)) {
                                String[] urlPieces = newValue.toString().split("[/\\\\]");
                                if (urlPieces.length != 0) {
                                    cell.setText(urlPieces[urlPieces.length - 1]);
                                } else {
                                    cell.setText(newValue.toString());
                                }
                            }
                            cell.setContextMenu(null);
                            cell.setGraphic(null);
                        }
                    } else {
                        cell.setText("");
                        cell.setGraphic(null);
                        cell.setContextMenu(null);
                    }
                });
                cell.setOnMousePressed(event -> {
                    if (event.getClickCount() == 2 && cell.getItem() != null) {
                        Object item = cell.getItem();
                        if (sourceTabMap.containsKey(item)) {
                            loadSource(sourceTabMap.get(item).source);
                        } else if (item instanceof URL && unloadedURLs.contains(item)) {
                            loadURL(item.toString());
                        }
                    }
                });
                return cell;
            });
            Label placeHolder = new Label();
            placeHolder.textProperty().bind(
                    Bindings.createStringBinding(() -> {
                        if (sourceTabMap.isEmpty()) {
                            return "No files loaded yet.";
                        } else {
                            return "No files for filter.";
                        }
                    }, Bindings.size(sourceTabMap))
            );
            setPlaceholder(placeHolder);
            VBox.setVgrow(this, Priority.ALWAYS);
        }
    };
    private final Button openFile = new Button("", NodeMisc.svgPath(SVG.FOLDER, 0.8)) {
        {
            Sheets.Essentials.makeSmoothButton(this);

            setOnAction(event -> {
                FileChooser chooser = new FileChooser();

                File result;
                if ((result = chooser.showOpenDialog(getScene().getWindow())) != null) {
                    loadFile(result.toPath());
                }
            });
        }
    };
    private final Button search = new Button("", NodeMisc.svgPath(SVG.SEARCH, 0.4)) {
        {
            Sheets.Essentials.makeSmoothButton(this);

            setOnAction(event ->
                    Popups.showInputPopup("Input a URL", FilesTab.this::loadURL)
            );
        }
    };
    private final TextField filter = new TextField() {
        {
            setPromptText("Filter displayed sources.");
            HBox.setHgrow(this, Priority.ALWAYS);

            textProperty().addListener((observable, oldValue, newValue) -> {
                List<Source> sources = sourceTabMap.values().stream()
                        .map(SourceTab::getSource)
                        .filter(source -> source.getName().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT)))
                        .collect(Collectors.toList());

                files.getItems().setAll(sources.stream().map(Source::getKey).collect(Collectors.toList()));
            });
        }
    };
    private final HBox topLeft = new HBox(filter, openFile, search) {
        {
            setAlignment(Pos.CENTER_LEFT);
            setSpacing(5);
        }
    };
    private final VBox left = new VBox(topLeft, files);

    private final SplitPane split = new SplitPane(left, tabPaneBg);

    public FilesTab(Parent root, DevTools tools) {
        super("Files");
        this.root = root;
        this.tools = tools;

        InvalidationListener stylesheetsChanged = observable -> {
            for (String stylesheet : tools.getAllStyleSheets()) {
                addUnloadedURL(stylesheet);
            }
        };
        tools.getAllStyleSheets().addListener(stylesheetsChanged);
        stylesheetsChanged.invalidated(tools.getAllStyleSheets());

        setClosable(false);
        backdrop.setAlignment(Pos.CENTER);
        SplitPane.setResizableWithParent(files, false);

        setContent(split);
    }

    public boolean loadURL(String url) {
        return loadURL(url, deriveLanguage(url));
    }

    public boolean loadURL(String url, EditorLanguageBase langChoice) {
        try {
            loadURL(new URL(url), langChoice);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public Source loadURL(URL url) {
        removeUnloadedURL(url);
        Source pathResult;
        if ((pathResult = tryURLasPath(url, null, false)) != null) {
            return pathResult;
        } else {
            return loadURL(url, deriveLanguage(url.toString()));
        }
    }

    public Source loadURL(URL url, EditorLanguageBase langChoice) {
        removeUnloadedURL(url);
        Source pathResult;
        if ((pathResult = tryURLasPath(url, langChoice, true)) != null) {
            return pathResult;
        }
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
        return source;
    }

    private Source tryURLasPath(URL url, EditorLanguageBase langChoice, boolean force) {
        if (!url.getFile().isEmpty()) {
            Path path;
            try {
                URI uri = url.toURI();
                if (uri.getPath() != null && uri.getScheme().equals("file")) {
                    path = new File(uri).toPath();
                    if (force) {
                        return loadFile(path, langChoice);
                    } else {
                        return loadFile(path);
                    }
                }
            } catch (URISyntaxException e) {
                return null;
            }
        }
        return null;
    }

    private EditorLanguageBase deriveLanguage(String text) {
        if (text.endsWith(".css")) {
            return new CSSLang();
        } else {
            return null;
        }
    }

    public Source loadFile(Path file) {
        return loadFile(file, deriveLanguage(file.toString()));
    }

    public Source loadFile(Path file, EditorLanguageBase langChoice) {
        removeUnloadedFile(file);
        Supplier<String> reader = () -> {
            try {
                return String.join("\n", Files.readAllLines(file));
            } catch (IOException e) {
                e.printStackTrace();
                return "Error while reading file: " + file;
            }
        };
        Consumer<String> writer = null;
        if (Files.isWritable(file)) {
            writer = text -> {
                try {
                    Files.writeString(file, text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        }

        Source source = new Source(
                reader, writer, file, file.getFileName().toString()
        );
        source.setPreferredLanguage(langChoice);
        loadSource(source);
        return source;
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

    public void addUnloadedFile(Path file) {
        if (unloadedFiles.contains(file) && !sourceTabMap.containsKey(file)) {
            return;
        }
        unloadedFiles.add(file);
        files.getItems().add(file);
    }
    public void addUnloadedURL(String path) {
        try {
            URL url = new URL(path);
            if (unloadedURLs.contains(url) && !sourceTabMap.containsKey(url.toString())) {
                return;
            }
            unloadedURLs.add(url);
            files.getItems().add(url);
        } catch (MalformedURLException ignored) { }
    }
    public void removeUnloadedFile(Path file) {
        unloadedFiles.remove(file);
        files.getItems().remove(file);
    }
    public void removeUnloadedURL(String path) {
        try {
            removeUnloadedURL(new URL(path));
        } catch (MalformedURLException ignored) { }
    }
    public void removeUnloadedURL(URL url) {
        unloadedURLs.remove(url);
        files.getItems().remove(url);
    }

    public static class Source {

        private final Object key;

        private final String name;
        private final Supplier<String> reader;
        private final Consumer<String> writer;

        private EditorLanguageBase preferredLanguage;

        private Consumer<String> onSaved;

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

        public void setOnSaved(Consumer<String> onSaved) {
            this.onSaved = onSaved;
        }

        public Consumer<String> getOnSaved() {
            return onSaved;
        }

    }

    private static class SourceTab extends Tab {

        private final Source source;
        private final LanguageCodeArea area;

        private String originalText;

        public SourceTab(Source source) {
            this.source = source;

            setText(source.getName());

            area = new LanguageCodeArea(source.preferredLanguage).addLineNumbers();

            originalText = source.getReader().get();
            area.replaceText(originalText);

            area.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals(originalText)) {
                    setText(source.getName() + "*");
                } else {
                    setText(source.getName());
                }
            });

            setContent(new VirtualizedScrollPane<>(area));

            MenuItem save = NodeMisc.makeMenuItem("Save", event -> this.save());

            if (source.writer == null) {
                area.setEditable(false);
            } else {
                setContextMenu(new ContextMenu(
                        save
                ));
            }

        }

        public Source getSource() {
            return source;
        }

        public void save() {
            if (source.writer != null) {
                source.writer.accept(area.getText());
                originalText = area.getText();
                setText(source.getName());
                if (source.getOnSaved() != null) {
                    source.getOnSaved().accept(area.getText());
                }
            }
        }

    }

}
