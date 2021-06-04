package com.me.tmw.css;

import com.me.tmw.resource.Resources;
import javafx.css.Rule;
import javafx.scene.Parent;

public final class Sheets {

    public static boolean applies(Parent node, Rule rule) {
        return rule.getSelectors().stream().anyMatch(selector -> selector.applies(node));
    }

    public static class Essentials {

        public static String STYLE_SHEET = Resources.CSS.getCss("essentials");;

        public static String TRANSPARENT_BUTTON_CLASS = "transparent-button";
        public static String LIGHT_SVG_BUTTON_CLASS = "light-svg-button";
        public static String HAND_CURSOR_CLASS = "hand-cursor";
        public static String SMALL_DIVIDER_CLASS = "small-divider";
        public static final String SILENT_DISABLE_CLASS = "silent-disable";

    }

}
