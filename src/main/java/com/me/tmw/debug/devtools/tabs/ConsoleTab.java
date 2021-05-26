package com.me.tmw.debug.devtools.tabs;

import com.me.tmw.debug.devtools.console.Console;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

public class ConsoleTab extends Tab {

    private final Console console;

    public ConsoleTab(Parent root) {
        super("Console");
        console = new Console("JavaScript", root);
        setContent(console);
    }

}
