package com.me.tmw.debug.uiactions;

import javafx.application.Platform;

public interface UIActionFunctions {

    void execute();

    default void executeLater() {
        executeLater(1);
    }
    default void executeLater(int times) {
        if (times == 0) {
            execute();
        } else {
            Platform.runLater(() -> executeLater(times - 1));
        }
    }

}
