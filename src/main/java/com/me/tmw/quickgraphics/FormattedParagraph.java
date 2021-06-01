package com.me.tmw.quickgraphics;

import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.FlowPane;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FormattedParagraph extends FlowPane {

    public static class Parser {

        private static char startHyperlink = '[';
        private static char endHyperlink = ']';
        private static char startUrl = '(';
        private static char endUrl = ')';

        private static final Object HYPERLINK_TAG = new Object();
        private static final Object URL_TAG = new Object();

        private int at = 0;
        private final String text;

        private final List<Object> tags = new ArrayList<>();
        private final List<Object> args = new ArrayList<>();

        public Parser(String text, Collection<Object> args) {
            this.text = text;
            this.args.addAll(args);
        }

        public Node[] parse() {
            List<Node> nodes = new ArrayList<>();
            while (!atEnd()) {

            }
            return null;
        }

        private Hyperlink parseHyperlink() {
            if (!tags.isEmpty() || atEnd()) {
                return null;
            }
            if (text.charAt(at) == startHyperlink) {
                tags.add(HYPERLINK_TAG);
                int parsedTo = at + 1;
                int end = -1;
                while (parsedTo < text.length()) {
                    if (text.charAt(parsedTo) == endHyperlink) {
                        end = parsedTo;
                        break;
                    }
                    parsedTo++;
                }
                tags.remove(HYPERLINK_TAG);
                if (end != -1) {
                    int start = at;
                    at = end;
                    URI parsedUrl;
                    try {
                        parsedUrl = parseUrl();
                    } catch (URISyntaxException e) {
                        return null;
                    }
                    if (parsedUrl != null) {
                        Hyperlink hyperlink = new Hyperlink(text.substring(start, end - 1));
                        hyperlink.setOnAction(actionEvent -> {
                            try {
                                Desktop.getDesktop().browse(parsedUrl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        return hyperlink;
                    } else if (!tags.isEmpty() && tags.get(0) instanceof Runnable) {
                        Hyperlink hyperlink = new Hyperlink(text.substring(start, end - 1));
                        hyperlink.setOnAction(event -> ((Runnable) tags.get(0)).run());
                        tags.remove(0);
                        return hyperlink;
                    }
                }
            }
            return null;
        }

        private URI parseUrl() throws URISyntaxException {
            if (!tags.isEmpty() || atEnd()) {
                return null;
            }
            if (text.charAt(at) == startUrl) {
                tags.add(URL_TAG);
                int parsedTo = at + 1;
                int end = -1;
                while (parsedTo < text.length()) {
                    if (text.charAt(parsedTo) == endUrl) {
                        end = parsedTo;
                        break;
                    }
                    parsedTo++;
                }
                tags.remove(URL_TAG);
                if (end != -1) {
                    return new URI(text.substring(at, end - 1));
                }
            }
            return null;
        }

        private boolean atEnd() {
            return at >= text.length();
        }

    }

    public static Node[] parse(String parse) {
        // [Hyperlink](optional url)

        return null;
    }

//    public FormattedParagraph(String parse) {
//        this(parse(parse));
//    }

    public FormattedParagraph(Node... items) {
        getChildren().addAll(items);
    }

}
