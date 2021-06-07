package com.me.tmw.nodes.richtextfx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import org.fxmisc.richtext.CodeArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class EditorLanguageBase implements EditorLanguage<Collection<String>, String, Collection<String>, CodeArea> {

    protected static final String NUMBER_PATTERN = "[0-9]+";
    protected static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

    private final StringProperty stylesheet = new SimpleStringProperty(this, "stylesheet");
    private final List<Consumer<CodeArea>> onceRemoved = new ArrayList<>();

    protected EditorLanguageBase(String stylesheet) {
        this.stylesheet.set(stylesheet);
    }

    @Override
    public void addedTo(CodeArea area) {
        ChangeListener<String> stylesheetChanged = (observableValue, oldValue, newValue) -> {
            area.getStylesheets().add(newValue);
            area.getStylesheets().remove(oldValue);
        };
        stylesheetChanged.changed(stylesheet, null, stylesheet.get());
        stylesheet.addListener(stylesheetChanged);

        onceRemoved.add(codeArea -> stylesheet.removeListener(stylesheetChanged));
    }

    @Override
    public void removedFrom(CodeArea area) {
        onceRemoved.forEach(consumer -> consumer.accept(area));
        onceRemoved.clear();
        area.getStylesheets().remove(stylesheet.get());
    }

}
