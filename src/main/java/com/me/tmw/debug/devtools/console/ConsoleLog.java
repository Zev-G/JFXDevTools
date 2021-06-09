package com.me.tmw.debug.devtools.console;

import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class ConsoleLog extends VBox {

    private final Console console;

    public ConsoleLog(Console console) {
        this.console = console;
        getStyleClass().add("console-log");
    }

    public void log(ConsoleLogLine item) {
        getChildren().add(item);
    }

    public void log(String log) {
        log(new ConsoleLogLine.Output(log));
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
    }

    public void addSeparator() {
        getChildren().add(new Separator());
    }

    public void clear() {
        getChildren().clear();
    }
}
