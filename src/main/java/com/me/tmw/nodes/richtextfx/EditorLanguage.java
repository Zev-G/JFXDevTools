package com.me.tmw.nodes.richtextfx;

import org.fxmisc.richtext.GenericStyledArea;

import java.util.List;

public interface EditorLanguage<PS, SEG, S, A extends GenericStyledArea<PS, SEG, S>> {

    void addedTo(A area);
    void removedFrom(A area);

    List<SortableStyleSpan<PS>> highlight(A area, String text);

}
