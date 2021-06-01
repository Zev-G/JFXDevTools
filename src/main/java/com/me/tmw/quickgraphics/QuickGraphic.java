package com.me.tmw.quickgraphics;


public interface QuickGraphic<T> {

    String htmlTag();
    String mdSyntax();
    T duplicate();
    String getStringRepresentation();
    String getRawText();

}
