package com.me.tmw.quickgraphics;

import javafx.scene.control.Label;

public class Heading extends Label implements QuickGraphic<Heading> {

    private final int degree;

    public Heading(int degree, String text) {
        super(text);
        this.degree = degree;
        getStyleClass().add(htmlTag());
    }

    // h1, h2, h3...
    @Override
    public String htmlTag() {
        return "h" + degree;
    }

    // #, ##, ###...
    @Override
    public String mdSyntax() {
        return "#".repeat(degree);
    }

    @Override
    public Heading duplicate() {
        return new Heading(degree, getText());
    }

    // ## Text, # Title, ### Variables...
    @Override
    public String getStringRepresentation() {
        return mdSyntax() + " " + getText();
    }

    // Text, Title, Variables...
    @Override
    public String getRawText() {
        return getText();
    }
}
