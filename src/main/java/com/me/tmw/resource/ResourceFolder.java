package com.me.tmw.resource;

import java.util.Objects;

public class ResourceFolder {

    private final String path;

    public ResourceFolder(ResourceFolder parent, String subPath) {
        this(parent.getPath() + subPath);
    }
    public ResourceFolder(String path) {
        this.path = path;
    }

    public String getCss(String name) {
        if (!name.endsWith(".css")) {
            name = name + ".css";
        }
        return Objects.requireNonNull(getClass().getClassLoader().getResource(path + name)).toExternalForm();
    }

    public String getPath() {
        return path;
    }

    public String getPng(String png) {
        if (!png.endsWith(".png")) {
            png = png + ".png";
        }
        return Objects.requireNonNull(getClass().getClassLoader().getResource(path + png)).toExternalForm();
    }
}
