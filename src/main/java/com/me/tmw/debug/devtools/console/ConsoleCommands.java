package com.me.tmw.debug.devtools.console;

public class ConsoleCommands {

    private final Console console;

    public ConsoleCommands(Console console) {
        this.console = console;
    }

    public void clear() {
        console.getLog().clear();
    }

    public void log(Object obj) {
        console.getLog().log(new ConsoleLogLine.Output(obj));
        console.getLog().addSeparator();
    }

}
