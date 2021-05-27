package com.me.tmw.resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public String read(String s) {
        return new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path + s))))
                .lines().collect(Collectors.joining("\n"));
    }
}
