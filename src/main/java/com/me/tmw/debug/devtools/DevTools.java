package com.me.tmw.debug.devtools;

import com.me.tmw.css.Sheets;
import com.me.tmw.debug.devtools.tabs.ConsoleTab;
import com.me.tmw.debug.devtools.tabs.FilesTab;
import com.me.tmw.debug.devtools.tabs.StructureTab;
import com.me.tmw.nodes.control.svg.SVG;
import com.me.tmw.nodes.tooltips.SimpleTooltip;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.resource.Resources;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static com.me.tmw.css.Sheets.Essentials.*;

public class DevTools extends StackPane {

    public static boolean listenToStylesheets = true;
    public static boolean propertiesInSceneTree = true;

    public static boolean displayPossibleClutterInSceneTree = false;

    public static void enableImprovedPerformanceMode() {
        listenToStylesheets = false;
        propertiesInSceneTree = false;
    }
    public static void disableImprovedPerformanceMode() {
        listenToStylesheets = true;
        propertiesInSceneTree = true;
    }

    private static final Object LISTENING_TO_STYLESHEETS = new Object() {
        @Override
        public String toString() {
            return "DevTools stylesheet listener marker. Can be removed but doing so will slightly hurt performance.";
        }
    };

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("dev-tools");
    private static final String TAB_STYLE_SHEET = Resources.DEBUGGER.getCss("flat-tab");

    private final ObservableSet<String> allStyleSheets = FXCollections.observableSet();

    private final StructureTab structureTab;
    private final ConsoleTab consoleTab;
    private final FilesTab filesTab;
    private final TabPane tabPane = new TabPane();

    private final Button close = new Button("", NodeMisc.svgPath(SVG.X, 0.5));
    private final Button detach = new Button("", NodeMisc.svgPath(SVG.OPEN, 0.8));
    private final Pane closePlaceHolder = new Pane(close);
    private final Pane detachPlaceHolder = new Pane(detach);

    private final HBox buttons = new HBox(detachPlaceHolder, closePlaceHolder);

    private final DevToolsContainer container;
    private boolean attachedToContainer = true;

    private Stage detachedStage;

    /*
         Properties
     */
    private final BooleanProperty closable = new SimpleBooleanProperty(true) {
        @Override
        protected void invalidated() {
            boolean val = closable.get();
            if (val) {
                if (closePlaceHolder.getChildren().isEmpty()) {
                    closePlaceHolder.getChildren().add(close);
                }
            } else {
                closePlaceHolder.getChildren().clear();
            }
        }
    };
    private final BooleanProperty detachable = new SimpleBooleanProperty(true) {
        @Override
        protected void invalidated() {
            boolean val = detachable.get();
            if (val) {
                if (detachPlaceHolder.getChildren().isEmpty()) {
                    detachPlaceHolder.getChildren().add(detach);
                }
            } else {
                detachPlaceHolder.getChildren().clear();
            }
        }
    };

    public DevTools(Parent root, DevToolsContainer container) {
        this.container = container;
        getStylesheets().addAll(TAB_STYLE_SHEET, Sheets.Essentials.STYLE_SHEET, STYLE_SHEET);
        getStyleClass().add(SMALL_DIVIDER_CLASS);

        buttons.setPickOnBounds(false);
        closePlaceHolder.setPickOnBounds(false);
        detachPlaceHolder.setPickOnBounds(false);

        recursivelyListenToStylesheetsOf(root);

        close.getStyleClass().addAll(TRANSPARENT_BUTTON_CLASS, LIGHT_SVG_BUTTON_CLASS, HAND_CURSOR_CLASS);
        detach.getStyleClass().addAll(TRANSPARENT_BUTTON_CLASS, LIGHT_SVG_BUTTON_CLASS, HAND_CURSOR_CLASS);
        SimpleTooltip.apply(close, "Closes dev tools.");
        SimpleTooltip.apply(detach, "Opens dev tools in a new window.");
        close.setOnAction(event -> hide());
        detach.setOnAction(event -> {
            attachedToContainer = !attachedToContainer;
            if (attachedToContainer) {
                container.attach(this);
                if (detachedStage != null) {
                    detachedStage.close();
                    detachedStage = null;
                }
            } else {
                container.remove(this);
                detachedStage = new Stage();
                detachedStage.setScene(new Scene(this));
                detachedStage.show();
            }
        });

        buttons.setAlignment(Pos.TOP_RIGHT);
        buttons.setPadding(new Insets(5, 7.5, 5, 0));
        buttons.setSpacing(5);
        buttons.setPickOnBounds(false);

        filesTab = new FilesTab(root, this);
        structureTab = new StructureTab(root, this);
        consoleTab = new ConsoleTab(root);
        structureTab.setClosable(false);
        consoleTab.setClosable(false);

        tabPane.getTabs().addAll(structureTab, consoleTab, filesTab);

        getChildren().addAll(tabPane, buttons);
    }

    private void recursivelyListenToStylesheetsOf(Parent parent) {
        if (!listenToStylesheets) return;

        parent.getProperties().put(LISTENING_TO_STYLESHEETS, true);

        InvalidationListener stylesheetsListener = observable -> allStyleSheets.addAll(parent.getStylesheets());
        stylesheetsListener.invalidated(parent.getStylesheets());
        parent.getStylesheets().addListener(stylesheetsListener);

        InvalidationListener childrenListener = observable ->
                parent.getChildrenUnmodifiable().stream()
                .filter(node -> node instanceof Parent)
                .filter(node -> !node.getProperties().containsKey(LISTENING_TO_STYLESHEETS))
                .map(node -> (Parent) node)
                .forEach(this::recursivelyListenToStylesheetsOf);

        childrenListener.invalidated(parent.getChildrenUnmodifiable());
        parent.getChildrenUnmodifiable().addListener(childrenListener);
    }

    public ObservableSet<String> getAllStyleSheets() {
        return allStyleSheets;
    }

    public StructureTab getStructureTab() {
        return structureTab;
    }

    public ConsoleTab getConsoleTab() {
        return consoleTab;
    }

    public FilesTab getFilesTab() {
        return filesTab;
    }

    public void selectTab(Tab tab) {
        tabPane.getSelectionModel().select(tab);
    }

    public boolean isClosable() {
        return closable.get();
    }

    public BooleanProperty closableProperty() {
        return closable;
    }

    public void setClosable(boolean closable) {
        this.closable.set(closable);
    }

    public boolean isDetachable() {
        return detachable.get();
    }

    public BooleanProperty detachableProperty() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        this.detachable.set(detachable);
    }

    public void hide() {
        if (attachedToContainer) {
            container.remove(this);
        } else {
            attachedToContainer = true;
            detachedStage.hide();
            detachedStage = null;
        }
    }

    public boolean isShown() {
        return container.isShowing(this) || (detachedStage != null && detachedStage.isShowing());
    }

}
