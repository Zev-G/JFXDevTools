package com.me.tmw.resource;

public final class Resources {

    public static final ResourceFolder DEBUGGER = new ResourceFolder("debugger/");
    public static final Nodes NODES    = new Nodes();
    public static final ResourceFolder CSS      = new ResourceFolder("css/");

    public static class Nodes extends ResourceFolder {
        public static final ResourceFolder LANGS = new ResourceFolder("nodes/langs/");

        public Nodes() {
            super("nodes/");
        }
    }

}
