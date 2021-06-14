package com.me.tmw.nodes.control.svg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SVG {

    public static final String PLUS = "M 27.2836 13.6423 c 0 1.3188 -1.0688 2.3874 -2.3882 2.3874 H 16.0291 v 8.8667 c 0 1.3191 -1.0691 2.3878 -2.3876 2.3874 c -0.6594 0 -1.2562 -0.2668 -1.688 -0.6988 c -0.4322 -0.4325 -0.6994 -1.0291 -0.6991 -1.6881 l -0.0004 -8.8677 H 2.3867 c -0.659 0 -1.2553 -0.2668 -1.688 -0.6994 c -0.4315 -0.4315 -0.6985 -1.0283 -0.6985 -1.6878 C 0 12.3238 1.0688 11.2549 2.388 11.2549 h 8.8664 V 2.388 C 11.2545 1.0691 12.3238 0 13.6425 0 c 1.3182 0.0004 2.3865 1.0684 2.3872 2.3871 v 8.8677 h 8.8673 C 26.2156 11.2555 27.2829 12.3238 27.2836 13.6423 z";
    public static final String TRASH = "M 0 8.1013 l 0 -1.9686 q 0.0562 -1.0687 0.8437 -1.7999 t 1.8561 -0.7312 l 2.6998 0 l 0 -0.8999 q 0 -1.1249 0.7874 -1.9124 t 1.9124 -0.7874 l 8.9994 0 q 1.1249 0 1.9124 0.7874 t 0.7874 1.9124 l 0 0.8999 l 2.6998 0 q 1.0687 0 1.8561 0.7312 t 0.8437 1.7999 l 0 1.9686 q 0 0.7312 -0.5343 1.2656 t -1.2656 0.5343 l 0 15.299 q 0 1.5187 -1.0405 2.5592 t -2.5592 1.0405 l -14.3991 0 q -1.5187 0 -2.5592 -1.0405 t -1.0405 -2.5592 l 0 -15.299 q -0.7312 0 -1.2656 -0.5343 t -0.5343 -1.2656 z m 1.7999 0 l 21.5986 0 l 0 -1.7999 q 0 -0.3937 -0.2531 -0.6468 t -0.6468 -0.2531 l -19.7987 0 q -0.3937 0 -0.6468 0.2531 t -0.2531 0.6468 l 0 1.7999 z m 1.7999 17.0989 q 0 0.7312 0.5343 1.2656 t 1.2656 0.5343 l 14.3991 0 q 0.7312 0 1.2656 -0.5343 t 0.5343 -1.2656 l 0 -15.299 l -17.9988 0 l 0 15.299 z m 1.7999 -0.8999 l 0 -11.6993 q 0 -0.3937 0.2531 -0.6468 t 0.6468 -0.2531 l 1.7999 0 q 0.3937 0 0.6468 0.2531 t 0.2531 0.6468 l 0 11.6993 q 0 0.3937 -0.2531 0.6468 t -0.6468 0.2531 l -1.7999 0 q -0.3937 0 -0.6468 -0.2531 t -0.2531 -0.6468 z m 0.8999 0 l 1.7999 0 l 0 -11.6993 l -1.7999 0 l 0 11.6993 z m 0.8999 -20.6987 l 10.7993 0 l 0 -0.8999 q 0 -0.3937 -0.2531 -0.6468 t -0.6468 -0.2531 l -8.9994 0 q -0.3937 0 -0.6468 0.2531 t -0.2531 0.6468 l 0 0.8999 z m 3.5998 20.6987 l 0 -11.6993 q 0 -0.3937 0.2531 -0.6468 t 0.6468 -0.2531 l 1.7999 0 q 0.3937 0 0.6468 0.2531 t 0.2531 0.6468 l 0 11.6993 q 0 0.3937 -0.2531 0.6468 t -0.6468 0.2531 l -1.7999 0 q -0.3937 0 -0.6468 -0.2531 t -0.2531 -0.6468 z m 0.8999 0 l 1.7999 0 l 0 -11.6993 l -1.7999 0 l 0 11.6993 z m 4.4997 0 l 0 -11.6993 q 0 -0.3937 0.2531 -0.6468 t 0.6468 -0.2531 l 1.7999 0 q 0.3937 0 0.6468 0.2531 t 0.2531 0.6468 l 0 11.6993 q 0 0.3937 -0.2531 0.6468 t -0.6468 0.2531 l -1.7999 0 q -0.3937 0 -0.6468 -0.2531 t -0.2531 -0.6468 z m 0.8999 0 l 1.7999 0 l 0 -11.6993 l -1.7999 0 l 0 11.6993 z";
    public static final String ARROW = "M 8.122 24 l -4.122 -4 8 -8 -8 -8 4.122 -4 11.878 12 z";
    public static final String BURGER = "M 8.94 0 h 105 c 4.92 0 8.94 4.02 8.94 8.94 l 0 0 c 0 4.92 -4.02 8.94 -8.94 8.94 h -105 C 4.02 17.88 0 13.86 0 8.94 l 0 0 C 0 4.02 4.02 0 8.94 0 L 8.94 0 z M 8.94 78.07 h 105 c 4.92 0 8.94 4.02 8.94 8.94 l 0 0 c 0 4.92 -4.02 8.94 -8.94 8.94 h -105 C 4.02 95.95 0 91.93 0 87.01 l 0 0 C 0 82.09 4.02 78.07 8.94 78.07 L 8.94 78.07 z M 8.94 39.03 h 105 c 4.92 0 8.94 4.02 8.94 8.94 l 0 0 c 0 4.92 -4.02 8.94 -8.94 8.94 h -105 C 4.02 56.91 0 52.89 0 47.97 l 0 0 C 0 43.06 4.02 39.03 8.94 39.03 L 8.94 39.03 z";
    public static final String WARNING = "M 504 256 c 0 136.997 -111.043 248 -248 248 S 8 392.997 8 256 C 8 119.083 119.043 8 256 8 s 248 111.083 248 248 z m -248 50 c -25.405 0 -46 20.595 -46 46 s 20.595 46 46 46 s 46 -20.595 46 -46 s -20.595 -46 -46 -46 z m -43.673 -165.346 l 7.418 136 c 0.347 6.364 5.609 11.346 11.982 11.346 h 48.546 c 6.373 0 11.635 -4.982 11.982 -11.346 l 7.418 -136 c 0.375 -6.874 -5.098 -12.654 -11.982 -12.654 h -63.383 c -6.884 0 -12.356 5.78 -11.981 12.654 z";
    public static final String CHECK_MARK = "M 173.898 439.404 l -166.4 -166.4 c -9.997 -9.997 -9.997 -26.206 0 -36.204 l 36.203 -36.204 c 9.997 -9.998 26.207 -9.998 36.204 0 L 192 312.69 L 432.095 72.596 c 9.997 -9.997 26.207 -9.997 36.204 0 l 36.203 36.204 c 9.997 9.997 9.997 26.206 0 36.204 l -294.4 294.401 c -9.998 9.997 -26.207 9.997 -36.204 -0.001 z";
    public static final String X = "M 24 20.188 l -8.315 -8.209 l 8.2 -8.282 l -3.697 -3.697 l -8.212 8.318 l -8.31 -8.203 l -3.666 3.666 l 8.321 8.24 l -8.206 8.313 l 3.666 3.666 l 8.237 -8.318 l 8.285 8.203 z";
    public static final String OPEN = "M 12.96 9.6 H 12 a 0.48 0.48 90 0 0 -0.48 0.48 V 13.44 H 1.92 V 3.84 H 6.24 a 0.48 0.48 90 0 0 0.48 -0.48 V 2.4 a 0.48 0.48 90 0 0 -0.48 -0.48 H 1.44 A 1.44 1.44 90 0 0 0 3.36 V 13.92 a 1.44 1.44 90 0 0 1.44 1.44 H 12 a 1.44 1.44 90 0 0 1.44 -1.44 V 10.08 A 0.48 0.48 90 0 0 12.96 9.6 Z M 14.64 0 h -3.84 c -0.6411 0 -0.9615 0.7773 -0.51 1.23 l 1.0719 1.0719 L 4.05 9.6111 a 0.72 0.72 90 0 0 0 1.02 L 4.7301 11.31 a 0.72 0.72 90 0 0 1.02 0 L 13.0584 3.9996 L 14.13 5.07 c 0.45 0.45 1.23 0.135 1.23 -0.51 V 0.72 A 0.72 0.72 90 0 0 14.64 0 Z";
    public static final String FOLDER = "M 29.232 8.064 H 17.136 l -3.4417 -3.4417 c -0.378 -0.378 -0.8908 -0.5903 -1.4257 -0.5903 H 3.024 C 1.3539 4.032 0 5.3859 0 7.056 v 18.144 c 0 1.6701 1.3539 3.024 3.024 3.024 h 26.208 c 1.6701 0 3.024 -1.3539 3.024 -3.024 V 11.088 c 0 -1.6701 -1.3539 -3.024 -3.024 -3.024 z m 0 17.136 H 3.024 V 7.056 h 8.8276 l 3.4417 3.4417 c 0.378 0.378 0.8908 0.5903 1.4257 0.5903 H 29.232 v 14.112 z";
    public static final String SEARCH = "M 50.5 44.27 L 40.53 34.3 c -0.45 -0.45 -1.06 -0.7 -1.7 -0.7 H 37.2 c 2.76 -3.53 4.4 -7.97 4.4 -12.8 C 41.6 9.31 32.29 0 20.8 0 S 0 9.31 0 20.8 s 9.31 20.8 20.8 20.8 c 4.83 0 9.27 -1.64 12.8 -4.4 v 1.63 c 0 0.64 0.25 1.25 0.7 1.7 l 9.97 9.97 c 0.94 0.94 2.46 0.94 3.39 0 l 2.83 -2.83 c 0.94 -0.94 0.94 -2.46 0.01 -3.4 z M 20.8 33.6 c -7.07 0 -12.8 -5.72 -12.8 -12.8 c 0 -7.07 5.72 -12.8 12.8 -12.8 c 7.07 0 12.8 5.72 12.8 12.8 c 0 7.07 -5.72 12.8 -12.8 12.8 z";

    public static String resizePath(String path, double multiply) {
        StringBuilder builder = new StringBuilder();
        for (PathElement element : PathElement.parse(path)) {
            builder.append(" ");
            Number[] values = element.values;
            for (int i = 0; i < values.length; i++) values[i] = values[i].doubleValue() * multiply;
            builder.append(element);
        }
        return builder.toString().trim();
    }

    private static class PathElement {

        private static final double DECIMAL_CAPPER = 1000D;

        private final String command;
        private final Number[] values;

        private PathElement(String command, Number... values) {
            this.command = command;
            this.values = values;
        }

        private static PathElement[] parse(String text) {
            List<PathElement> elements = new ArrayList<>();

            var lambdaRef = new Object() {
                StringBuilder numBuilder = new StringBuilder();
                String command = null;
            };

            Runnable addCommand = () -> {
                List<Number> items = new ArrayList<>();
                for (String num : lambdaRef.numBuilder.toString().split(" +")) {
                    if (num.isEmpty()) continue;
                    double val = Double.parseDouble(num);
                    if ((int) val == val) {
                        items.add((int) val);
                    } else {
                        items.add(val);
                    }
                }
                elements.add(new PathElement(lambdaRef.command, items.toArray(new Number[0])));
                lambdaRef.numBuilder = new StringBuilder();
            };

            for (String charString : text.split("")) {
                if (charString.matches("[A-z]")) {
                    if (lambdaRef.command != null) {
                        addCommand.run();
                    }
                    lambdaRef.command = charString;
                } else {
                    lambdaRef.numBuilder.append(charString);
                }
            }
            if (lambdaRef.command != null) {
                addCommand.run();
            }

            return elements.toArray(new PathElement[0]);
        }

        @Override
        public String toString() {
            return command + " " + Arrays.stream(values).map(i -> {
                if (i.doubleValue() == i.intValue()) {
                    return String.valueOf(i.intValue());
                } else {
                    return String.valueOf(((int) (i.doubleValue() * DECIMAL_CAPPER)) / DECIMAL_CAPPER);
                }
            }).collect(Collectors.joining(" "));
        }

    }

}
