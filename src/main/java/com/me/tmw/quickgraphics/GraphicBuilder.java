package com.me.tmw.quickgraphics;

import java.util.ArrayList;
import java.util.List;

public final class GraphicBuilder {

    public static GraphicBuilder create() {
        return new GraphicBuilder();
    }

    private final List<QuickGraphic<?>> graphics = new ArrayList<>();

    public GraphicBuilder add(QuickGraphic<?> graphic) {
        graphics.add(graphic);
        return this;
    }

    public GraphicBuilder addHeading(int degree, String text) {
        return add(new Heading(degree, text));
    }

}
