package com.me.tmw.nodes.richtextfx.languages;

import com.me.tmw.nodes.richtextfx.EditorLanguageBase;
import com.me.tmw.nodes.richtextfx.SortableStyleSpan;
import com.me.tmw.resource.Resources;
import org.fxmisc.richtext.CodeArea;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class CSSLang extends EditorLanguageBase {

    private static final String[] KEYWORDS = {"italic", "bold", "!important", "bolder", "light", "lighter", "normal", "none", "solid", "middle", "auto", "inset"};
    private static final String[] UNITS =  { "px", "cm", "mm", "in", "pt", "pc", "em", "ex", "ch", "rem", "vw", "vh", "vmin", "vmax", "%" };

    private static final String TEXT_RECOGNITION_MAIN = "\\s:#.{}";
    private static final String TEXT_RECOGNITION_PATTERN = "[^" + TEXT_RECOGNITION_MAIN + "]";

    private static final String KEYWORDS_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String COMMENT_PATTERN = "/\\*(.|\n)*?\\*/";
    private static final String BRACE_PATTERN = "[{}]";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String PAREN_PATTERN = "[A-z-]+?\\(((\\n|.)*?)\\)";
    private static final String CLASS_PATTERN = "\\.[^" + TEXT_RECOGNITION_MAIN + "0-9](" + TEXT_RECOGNITION_PATTERN + ")+";
    private static final String VALUE_PATTERN = "(" + TEXT_RECOGNITION_PATTERN + "|-)+?:";
    private static final String COLOR_CODE_PATTERN = "#([A-z]|[0-9]){1,6}";
    private static final String PSEUDO_CLASS_PATTERN = ":(" + TEXT_RECOGNITION_PATTERN +"|-)+";
    private static final String NUMBER_PATTERN = "[0-9]+" + "(" + String.join("|", UNITS) + "|)";

    private final Function<String, List<SortableStyleSpan<Collection<String>>>> highlighter = LangHighlighting.simpleRegexMap(
            COMMENT_PATTERN, "comment",
            STRING_PATTERN, "string",
            NUMBER_PATTERN, "number",
            KEYWORDS_PATTERN, "keyword",
            CLASS_PATTERN, "style-class",
            PSEUDO_CLASS_PATTERN, "pseudo-class",
            VALUE_PATTERN, "value",
            COLOR_CODE_PATTERN, "color-code",
            PAREN_PATTERN, "paren",
            SEMICOLON_PATTERN, "semicolon",
            BRACE_PATTERN, "brace"
    );

    public CSSLang() {
        super(Resources.Nodes.LANGS.getCss("css"));
    }

    @Override
    public List<SortableStyleSpan<Collection<String>>> highlight(CodeArea area, String text) {
        return highlighter.apply(text);
    }

}
