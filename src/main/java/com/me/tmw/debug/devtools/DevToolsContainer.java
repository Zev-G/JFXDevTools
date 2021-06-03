package com.me.tmw.debug.devtools;

public interface DevToolsContainer {

    void attach(DevTools tools);
    void remove(DevTools tools);
    boolean isShowing(DevTools tools);
}
