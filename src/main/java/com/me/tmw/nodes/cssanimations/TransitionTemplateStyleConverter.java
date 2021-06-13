package com.me.tmw.nodes.cssanimations;

import javafx.animation.Interpolator;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class TransitionTemplateStyleConverter extends StyleConverter<ParsedValue[], TransitionTemplate<?>> {

    // lazy, thread-safe instantiation
    private static class Holder {
        static final TransitionTemplateStyleConverter INSTANCE = new TransitionTemplateStyleConverter();
    }

    public static TransitionTemplateStyleConverter getInstance() {
        return Holder.INSTANCE;
    }

    public static TransitionTemplate<Object> defaultValues = new TransitionTemplate<>(
            Duration.seconds(1), null, null, null, Interpolator.EASE_BOTH
    );

    @Override
    public TransitionTemplate<?> convert(ParsedValue<ParsedValue[], TransitionTemplate<?>> value, Font font) {
        ParsedValue[] values = value.getValue();
        if (values.length < 1 || values.length > 4) {
            throw new IllegalArgumentException();
        }

        String property = defaultValues.getProperty();
        Duration duration = defaultValues.getDuration();
        Object to = defaultValues.getTo();
        Object from = defaultValues.getFrom();

        boolean setTo = false;
        for (ParsedValue loopValue : values) {
            Object converted = loopValue.convert(font);
            if (converted instanceof String) {
                property = (String) converted;
            } else if (converted instanceof Duration) {
                duration = (Duration) converted;
            } else {
                if (!setTo) {
                    to = converted;
                    setTo = true;
                } else {
                    from = converted;
                }
            }
        }

        return new TransitionTemplate<>(duration, property, from, to, defaultValues.getInterpolator());
    }

    @Override
    public TransitionTemplate<?> convert(Map<CssMetaData<? extends Styleable, ?>, Object> convertedValues) {
        return super.convert(convertedValues);
    }

    public TransitionTemplate<?> convert(String text) {
        if (CACHE.containsKey(text)) return CACHE.get(text);

        String[] args = text.split("[ ,]");
        String toAsString = null;
        String fromAsString = null;
        String property = null;
        String durationAsString = null;
        for (String arg : args) {
            String argName;
            String value;
            if (arg.contains("=")) {
                String[] equalSplit = arg.split("=");
                argName = equalSplit[0];
                value = equalSplit[1];
            } else {
                value = arg;
                if (arg.startsWith("-")) {
                    argName = "property";
                } else if (couldBeDuration(arg)) {
                    argName = "duration";
                } else if (toAsString == null) {
                    argName = "to";
                } else {
                    argName = "from";
                }
            }
            switch (argName) {
                case "property" -> property = value;
                case "duration" -> durationAsString = value;
                case "to" -> toAsString = value;
                case "from" -> fromAsString = value;
            }
        }
        Object to = toAsString == null ? 0 : Double.parseDouble(toAsString);
        Object from = fromAsString == null ? 0 : Double.parseDouble(fromAsString);
        Duration duration;
        if (durationAsString == null) {
            duration = Duration.millis(1000);
        } else {
            durationAsString = durationAsString.trim();
            double durationAsMillis = Double.parseDouble(durationAsString.replaceAll("[^0-9.]", ""));
            String ending = durationAsString.replaceAll("[0-9.]", "");
            if (!ending.isEmpty()) {
                durationAsMillis *= switch (ending) {
                    case "s" -> 1000;
                    case "m" -> 60000;
                    default -> 1;
                };
            }
            duration = Duration.millis(durationAsMillis);
        }
        TransitionTemplate<?> result = new TransitionTemplate<>(
                duration, property, from, to, defaultValues.getInterpolator()
        );
        CACHE.put(text, result);
        return result;
    }

    private boolean couldBeDuration(String test) {
        int endOfNums = -1;
        boolean pastDot = false;
        boolean atDot = false;
        char[] charArray = test.toCharArray();
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char c = charArray[i];
            if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9') {
                atDot = false;
                continue;
            }
            if (c == '.') {
                if (pastDot) return false;
                pastDot = true;
                atDot = true;
                continue;
            }
            endOfNums = i;
            break;
        }
        if (atDot) return false;
        if (endOfNums == -1) return true; // i think durations can default to ms.
        return test.substring(endOfNums).matches("ms|s|m");
    }

    private static final Map<String, TransitionTemplate<?>> CACHE = new HashMap<>();

}
