package com.me.tmw.quickgraphics;

import javafx.scene.control.Label;

public class Heading extends Label implements QuickGraphic<Heading> {

    private final int degree;

    public Heading(String text, int degree) {
        super(text);
        this.degree = degree;
    }

    @Override
    public String htmlTag() {
        return "<h" + degree + ">";
    }

    @Override
    public String mdSyntax() {
        return "#".repeat(degree);
    }

    @Override
    public Heading duplicate() {
        return new Heading(getText(), degree);
    }

    @Override
    public String getStringRepresentation() {
        return mdSyntax() + " " + getText();
    }



}
