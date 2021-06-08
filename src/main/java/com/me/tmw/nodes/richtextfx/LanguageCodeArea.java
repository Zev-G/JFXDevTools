package com.me.tmw.nodes.richtextfx;

import com.me.tmw.nodes.util.RFXUtils;
import com.me.tmw.resource.Resources;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class LanguageCodeArea extends CodeArea {

    private static final String STYLE_SHEET = Resources.NODES.getCss("code-area");

    private final ObjectProperty<EditorLanguage<Collection<String>, String, Collection<String>, CodeArea>> language = new SimpleObjectProperty<>(this, "language");

    private HighlightingThread stylingThread;

    public LanguageCodeArea(EditorLanguage<Collection<String>, String, Collection<String>, CodeArea> language) {
        getStylesheets().add(STYLE_SHEET);

        this.plainTextChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(plainTextChange -> highlight());
        this.language.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removedFrom(this);
            }
            if (newValue != null) {
                newValue.addedTo(this);
                highlight();
            }
        });

        this.language.set(language);
    }

    public LanguageCodeArea addLineNumbers() {
        setParagraphGraphicFactory(SafeLineNumberFactory.get(this));
        return this;
    }

    private void highlight() {
        if (stylingThread != null && stylingThread.isAlive()) {
            stylingThread.interrupt();
        }
        stylingThread = new HighlightingThread();
        stylingThread.start();
    }

    private class HighlightingThread extends Thread {

        private final String text;
        private final EditorLanguage<Collection<String>, String, Collection<String>, CodeArea> language;

        public HighlightingThread() {
            setDaemon(false);
            this.text = getText();
            this.language = LanguageCodeArea.this.language.get();
        }

        @Override
        public void run() {
            if (language != null) {
                StyleSpansBuilder<Collection<String>> styleSpans = RFXUtils.joinSpans(language.highlight(LanguageCodeArea.this, text), RFXUtils.JOIN_STRINGS, RFXUtils.STRINGS_EMPTY, text.length());
                if (!isInterrupted()) {
                    Platform.runLater(() -> {
                        if (!isInterrupted()) {
                            try {
                                setStyleSpans(0, styleSpans.create());
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                        }
                    });
                }
            } else {
                Platform.runLater(() -> {
                    if (!isInterrupted()) {
                        clearStyle(0, text.length());
                    }
                });
            }
        }

    }

    @Override
    public Predicate<Collection<String>> getFoldStyleCheck() {
        return super.getFoldStyleCheck();
    }

    @Override
    public UnaryOperator<Collection<String>> getRemoveFoldStyle() {
        return super.getRemoveFoldStyle();
    }

    @Override
    public void unfoldParagraphs(int startingFrom, Predicate<Collection<String>> isFolded, UnaryOperator<Collection<String>> styleMixin) {
        super.unfoldParagraphs(startingFrom, isFolded, styleMixin);
    }
}
