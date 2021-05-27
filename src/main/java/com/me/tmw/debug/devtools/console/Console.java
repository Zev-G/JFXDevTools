package com.me.tmw.debug.devtools.console;

import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.resource.Resources;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.Match;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import jdk.dynalink.beans.StaticClass;
import org.fxmisc.richtext.InlineCssTextArea;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.script.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Console extends StackPane {

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("console");
    private static final boolean PRINT_CONSOLE_EXCEPTION = true;

    private static final String initialScript =
            "load(\"fx:base.js\");\n" +
            "load(\"fx:controls.js\");\n" +
            "load(\"fx:graphics.js\");";

    private ScriptEngineManager manager;
    private final ScriptContext context = new SimpleScriptContext();
    private ScriptEngine scriptEngine;

    private final String engineName;

    private final ConsoleLog log = new ConsoleLog(this);
    private final InlineCssTextArea input = new InlineCssTextArea();
    private final SVGPath inputSVG = NodeMisc.svgPath(ConsoleLogLine.ARROW);
    private final HBox inputLine = new HBox(NodeMisc.pad(inputSVG, new Insets(5)), input);
    private final Pane emptyArea = new VBox();
    private final VBox mainArea = new VBox(log, inputLine, emptyArea);

    private final ScrollPane scrollPane = new ScrollPane(mainArea);

    private final List<StringProperty> history = new ArrayList<>();
    private final StringProperty current = new SimpleStringProperty();
    private int offset = 0;

    private final Parent root;

    public Console(String engineName, Parent root) {
        this.root = root;

        getStylesheets().add(STYLE_SHEET);
        emptyArea.setMinHeight(200);

        this.engineName = engineName;
        scrollPane.setFitToWidth(true);
        getChildren().add(scrollPane);

        history.add(current);
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (offset == 0) {
                current.set(newValue);
            }
        });

        input.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!event.isShiftDown()) {
                    event.consume();
                    InlineCssTextArea uneditableArea = new InlineCssTextArea();
                    makeInlineCssTextAreaJSCompatible(uneditableArea);
                    String text = input.getText();
//                    text = text.substring(0, input.getCaretPosition() - 1) + text.substring(input.getCaretPosition());
                    uneditableArea.replaceText(text);
                    uneditableArea.getStyleClass().add("console-input");
                    uneditableArea.setEditable(false);

                    uneditableArea.maxHeightProperty().bind(uneditableArea.totalHeightEstimateProperty());
                    uneditableArea.prefHeightProperty().bind(uneditableArea.totalHeightEstimateProperty());
                    uneditableArea.setMinHeight(-1);
                    uneditableArea.maxWidthProperty().bind(uneditableArea.totalWidthEstimateProperty());
                    uneditableArea.prefWidthProperty().bind(uneditableArea.totalWidthEstimateProperty());

                    log.log(new ConsoleLogLine.Input(uneditableArea));
                    try {
                        log.log(new ConsoleLogLine.Output(
                                getScriptEngine().eval(text, context)
                        ));
                    } catch (ScriptException e) {
                        log.log(new ConsoleLogLine.Error(e));
                    } catch (Exception e) {
                        log.log(new ConsoleLogLine.Error(e));
                        if (PRINT_CONSOLE_EXCEPTION) {
                            e.printStackTrace();
                        }
                    } finally {
                        history.add(history.size() - 1, new SimpleStringProperty(input.getText()));
                        offset = 0;
                        input.replaceText("");
                        log.addSeparator();
                    }
                } else {
                    input.insertText(input.getCaretPosition(), "\n");
                }
            } else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
                int delta = event.getCode() == KeyCode.UP ? 1 : -1;
                int newOffset = offset + delta;
                if (newOffset >= 0 && newOffset < history.size()) {
                    offset = newOffset;
                    String val = history.get((history.size() - offset) - 1).get();
                    input.replaceText(val);
                    input.displaceCaret(val.length());
                }
            }
        });

        makeInlineCssTextAreaJSCompatible(input);

        input.getStyleClass().add("console-input");
        inputSVG.getStyleClass().add("input-svg");
        inputLine.getStyleClass().add("console-log-line");
        HBox.setHgrow(input, Priority.ALWAYS);
        VBox.setVgrow(inputLine, Priority.ALWAYS);
    }

    public ScriptEngineManager getManager() {
        if (manager == null) {
            manager = new ScriptEngineManager(getClass().getClassLoader());
        }
        return manager;
    }

    private static void makeInlineCssTextAreaJSCompatible(InlineCssTextArea input) {
        final HashMap<Pattern, String> regexCssMap  = new HashMap<>();
        final String keywordCss = "-fx-fill: purple";
        String keywords = "abstract\targuments\tawait\tboolean\n" +
                "break\tbyte\tcase\tcatch\n" +
                "char\tclass\tcontinue\n" +
                "debugger\tdefault\tdelete\tdo\n" +
                "double\telse\tenum\teval\n" +
                "export\textends\tfalse\tfinal\n" +
                "finally\tfloat\tfor\tfunction\n" +
                "goto\tif\timplements\timport*\n" +
                "in\tinstanceof\tint\tinterface\n" +
                "long\tnative\tnew\n" +
                "null\tpackage\tprivate\tprotected\n" +
                "public\treturn\tshort\tstatic\n" +
                "super\tswitch\tsynchronized\tthis\n" +
                "throw\tthrows\ttransient\ttrue\n" +
                "try\ttypeof\tvar\tvoid\n" +
                "volatile\twhile\twith\tyield";
        for (String keyword : keywords.split("\\s")) {
            if (!keyword.isEmpty())
                regexCssMap.put(Pattern.compile("\\b" + Pattern.quote(keyword.replaceAll("\\s", "")) + "\\b"), keywordCss);
        }
        input.plainTextChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(change -> {
            String text = input.getText();
            input.setStyle(0, text.length(), "");
            for (Pattern pattern : regexCssMap.keySet()) {
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end   = matcher.end();
                    input.setStyle(start, end, regexCssMap.get(pattern));
                }
            }
        });
    }

    public ScriptEngine getScriptEngine() {
        if (scriptEngine == null) {
            log.log(new ConsoleLogLine.Input("Starting up JS engine..."));

            input.setDisable(true);

            new Thread(() -> {

                scriptEngine = getManager().getEngineByName(engineName);
                context.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE);
                context.setAttribute("root", root, ScriptContext.ENGINE_SCOPE);
                context.setAttribute("scene", root.getScene(), ScriptContext.ENGINE_SCOPE);

                Platform.runLater(() -> {
                    log.log(new ConsoleLogLine.Input("Successfully started JS engine."));
                    log.log(new ConsoleLogLine.Input("Initializing jfx classes..."));
                });

                Reflections jfxSceneReflections = new Reflections("javafx.scene", new SubTypesScanner(false));
                Reflections jfxStageReflections = new Reflections("javafx.stage", new SubTypesScanner(false));

                Set<Class<?>> allClasses = new HashSet<>(jfxSceneReflections.getSubTypesOf(Object.class));
                allClasses.addAll(jfxStageReflections.getSubTypesOf(Object.class));

                for (Class<?> aClass : allClasses) {
                    if (!aClass.isAnonymousClass() && !aClass.isLocalClass() && aClass.getDeclaringClass() == null) {
                        context.setAttribute(aClass.getSimpleName(), StaticClass.forClass(aClass), ScriptContext.ENGINE_SCOPE);
                    }
                }

                Platform.runLater(() -> {
                    log.log(new ConsoleLogLine.Input("Successfully initialized classes."));
                    log.addSeparator();
                    input.setDisable(false);
                });


            }).start();

        }
        return scriptEngine;
    }

}