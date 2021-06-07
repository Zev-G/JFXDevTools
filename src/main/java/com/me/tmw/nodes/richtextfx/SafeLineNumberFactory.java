package com.me.tmw.nodes.richtextfx;


import java.util.Collection;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

/**
 * Graphic factory that produces labels containing line numbers and a "+" to indicate folded paragraphs.
 * To customize appearance, use {@code .lineno} and {@code .fold-indicator} style classes in CSS stylesheets.
 */
public class SafeLineNumberFactory implements IntFunction<Node> {

    private static final Insets DEFAULT_INSETS = new Insets(0.0, 5.0, 0.0, 5.0);
    private static final Paint DEFAULT_TEXT_FILL = Color.web("#666");
    private static final Font DEFAULT_FONT = Font.font("monospace", FontPosture.ITALIC, 13);
    private static final Font DEFAULT_FOLD_FONT = Font.font("monospace", FontWeight.BOLD, 13);
    private static final Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.web("#ddd"), null, null));

    public static IntFunction<Node> get(LanguageCodeArea area) {
        return get(area, digits -> "%1$" + digits + "s");
    }

    public static IntFunction<Node> get(LanguageCodeArea area, IntFunction<String> format )
    {
        return get(area, format, area.getFoldStyleCheck(), area.getRemoveFoldStyle() );
    }

    /**
     * Use this if you extended GenericStyledArea for your own text area and you're using paragraph folding.
     *
     * @param format Given an int convert to a String for the line number.
     * @param isFolded Given a paragraph style PS check if it's folded.
     * @param removeFoldStyle Given a paragraph style PS, return a <b>new</b> PS that excludes fold styling.
     */
    public static IntFunction<Node> get(
            LanguageCodeArea area,
            IntFunction<String> format,
            Predicate<Collection<String>> isFolded,
            UnaryOperator<Collection<String>> removeFoldStyle )
    {
        return new SafeLineNumberFactory(area, format, isFolded, removeFoldStyle );
    }

    private final Val<Integer> nParagraphs;
    private final IntFunction<String> format;
    private final LanguageCodeArea area;
    private final UnaryOperator<Collection<String>> removeFoldStyle;
    private final Predicate<Collection<String>> isFoldedCheck;

    private SafeLineNumberFactory(
            LanguageCodeArea area,
            IntFunction<String> format,
            Predicate<Collection<String>> isFolded,
            UnaryOperator<Collection<String>> removeFoldStyle )
    {
        nParagraphs = LiveList.sizeOf(area.getParagraphs());
        this.removeFoldStyle = removeFoldStyle;
        this.isFoldedCheck = isFolded;
        this.format = format;
        this.area = area;
    }

    @Override
    public Node apply(int idx) {
        Val<String> formatted = nParagraphs.map(n -> format(idx+1, n));

        Label lineNo = new Label();
        lineNo.setFont(DEFAULT_FONT);
        lineNo.setBackground(DEFAULT_BACKGROUND);
        lineNo.setTextFill(DEFAULT_TEXT_FILL);
        lineNo.setPadding(DEFAULT_INSETS);
        lineNo.setAlignment(Pos.TOP_RIGHT);
        lineNo.getStyleClass().add("lineno");

        // bind label's text to a Val that stops observing area's paragraphs
        // when lineNo is removed from scene
        lineNo.textProperty().bind(formatted.conditionOnShowing(lineNo));

        if ( isFoldedCheck != null )
        {
            Label foldIndicator = new Label( " " );
            foldIndicator.setTextFill( Color.BLUE ); // Prevents CSS errors
            foldIndicator.setFont( DEFAULT_FOLD_FONT );

            lineNo.setContentDisplay( ContentDisplay.RIGHT );
            lineNo.setGraphic( foldIndicator );

            if ( area.getParagraphs().size() > idx+1 ) {
                if ( isFoldedCheck.test( area.getParagraph( idx+1 ).getParagraphStyle() )
                        && ! isFoldedCheck.test( area.getParagraph( idx ).getParagraphStyle() ) ) {
                    foldIndicator.setOnMouseClicked( ME -> area.unfoldParagraphs
                            (
                                    idx, isFoldedCheck, removeFoldStyle
                            ));
                    foldIndicator.getStyleClass().add( "fold-indicator" );
                    foldIndicator.setCursor( Cursor.HAND );
                    foldIndicator.setText( "+" );
                }
            }
        }

        return lineNo;
    }

    private String format(int x, int max) {
        int digits = (int) Math.floor(Math.log10(max)) + 1;
        return String.format(format.apply(digits), x);
    }

}

