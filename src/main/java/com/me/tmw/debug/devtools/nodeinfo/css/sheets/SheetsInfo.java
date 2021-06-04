package com.me.tmw.debug.devtools.nodeinfo.css.sheets;

import com.me.tmw.debug.devtools.nodeinfo.NodeInfo;
import com.me.tmw.resource.Resources;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.css.CssParser;
import javafx.css.Stylesheet;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetsInfo extends NodeInfo {

    private Parent node;

    private final Accordion sheets = new Accordion();
    private final List<String> viewedSheets = new ArrayList<>();
    private final Map<String, SheetInfo> viewedSheetsSheetInfoMap = new HashMap<>();

    private final CssParser parser = new CssParser();
    private final InvalidationListener sheetsInvalidated = observable -> {
        List<String> invalidSheets = new ArrayList<>(viewedSheets);
        List<String> newSheets = new ArrayList<>();
        for (String sheet : node.getStylesheets()) {
            invalidSheets.remove(sheet);
            if (!viewedSheets.contains(sheet)) {
                newSheets.add(sheet);
            }
        }
        for (String invalidSheet : invalidSheets) {
            viewedSheets.remove(invalidSheet);
            sheets.getPanes().remove(
                    viewedSheetsSheetInfoMap.remove(invalidSheet)
            );
        }
        for (String newSheet : newSheets) {
            URL url;
            try {
                url = new URL(newSheet);
            } catch (MalformedURLException e) {
                continue;
            }
            Stylesheet stylesheet;
            try {
                stylesheet = parser.parse(url);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            SheetInfo info = new SheetInfo(stylesheet, node);
            viewedSheetsSheetInfoMap.put(newSheet, info);
            sheets.getPanes().add(info);
        }
    };

    public SheetsInfo(Parent parent) {
        this.node = parent;

        parent.getStylesheets().addListener(sheetsInvalidated);
        sheetsInvalidated.invalidated(null);

        getChildren().add(sheets);
    }

}
