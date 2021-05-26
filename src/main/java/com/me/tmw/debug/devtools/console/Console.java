package com.me.tmw.debug.devtools.console;

import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.resource.Resources;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;

import javax.script.*;

public class Console extends StackPane {

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("console");

    private ScriptEngineManager manager;
    private ScriptContext context = new SimpleScriptContext();
    private ScriptEngine scriptEngine;

    private final String engineName;

    private final ConsoleLog log = new ConsoleLog(this);
    private final TextField input = new TextField();
    private final SVGPath inputSVG = NodeMisc.svgPath(ConsoleLogLine.ARROW);
    private final HBox inputLine = new HBox(NodeMisc.pad(inputSVG, new Insets(5)), input);
    private final Pane emptyArea = new VBox();
    private final VBox mainArea = new VBox(log, inputLine, emptyArea);

    private final ScrollPane scrollPane = new ScrollPane(mainArea);

    private final Parent root;

    public Console(String engineName, Parent root) {
        this.root = root;

        getStylesheets().add(STYLE_SHEET);
        emptyArea.setMinHeight(200);

        this.engineName = engineName;
        scrollPane.setFitToWidth(true);
        getChildren().add(scrollPane);

        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                log.log(new ConsoleLogLine.Input(input.getText()));
                try {
                    log.log(new ConsoleLogLine.Output(
                            getScriptEngine().eval(input.getText(), context)
                    ));
                } catch (ScriptException e) {
                    log.log(new ConsoleLogLine.Error(e));
                } finally {
                    input.setText("");
                    log.addSeparator();
                }
            }
        });

        input.getStyleClass().add("console-input");
        inputSVG.getStyleClass().add("input-svg");
        inputLine.getStyleClass().add("console-log-line");
        HBox.setHgrow(input, Priority.ALWAYS);
    }

    public ScriptEngineManager getManager() {
        if (manager == null) {
            manager = new ScriptEngineManager(getClass().getClassLoader());
        }
        return manager;
    }

    public ScriptEngine getScriptEngine() {
        if (scriptEngine == null) {
            scriptEngine = getManager().getEngineByName("JavaScript");
            context.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE);
            context.setAttribute("root", root, ScriptContext.ENGINE_SCOPE);
            context.setAttribute("scene", root.getScene(), ScriptContext.ENGINE_SCOPE);

            context.setAttribute("Button", Button.class, ScriptContext.ENGINE_SCOPE);
        }
        return scriptEngine;
    }

}
