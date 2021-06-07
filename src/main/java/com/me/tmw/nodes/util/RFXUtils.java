package com.me.tmw.nodes.util;

import com.me.tmw.nodes.richtextfx.SortableStyleSpan;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;
import java.util.function.Function;

public final class RFXUtils {

    public static final Function<Collection<SortableStyleSpan<Collection<String>>>, Collection<String>> JOIN_STRINGS = spans -> {
        if (spans.isEmpty())
            return Collections.emptyList();
        List<String> styles = new ArrayList<>();
        for (SortableStyleSpan<Collection<String>> span : spans) {
            styles.addAll(span.getStyle());
        }
        return styles;
    };

    public static final Collection<String> STRINGS_EMPTY = Collections.emptyList();

    public static <T> StyleSpansBuilder<T> joinSpans(Collection<SortableStyleSpan<T>> styleSpans, Function<Collection<SortableStyleSpan<T>>, T> combine, T empty, int length) {
        StyleSpansBuilder<T> builder = new StyleSpansBuilder<>();

        List<SortableStyleSpan<T>> sortedByStartSpans = new ArrayList<>(styleSpans);
        List<SortableStyleSpan<T>> sortedByEndSpans = new ArrayList<>(styleSpans);

        sortedByStartSpans.sort(Comparator.comparingInt(SortableStyleSpan::getStart));
        sortedByEndSpans.sort(Comparator.comparingInt(SortableStyleSpan::getEnd));

        List<SortableStyleSpan<T>> withinSpans = new ArrayList<>();

        int current = 0;
        while (!sortedByEndSpans.isEmpty()) {

            SortableStyleSpan<T> firstStart = sortedByStartSpans.isEmpty() ? null : sortedByStartSpans.get(0);
            SortableStyleSpan<T> firstEnd = sortedByEndSpans.get(0);

            SortableStyleSpan<T> first;
            int firstVal;
            if (firstStart != null && firstStart.getStart() <= firstEnd.getEnd()) {
                first = firstStart;
                firstVal = firstStart.getStart();
            } else {
                first = firstEnd;
                firstVal = firstEnd.getEnd();
            }
            boolean firstIsStart = first == firstStart;

            if (current < firstVal) {
                builder.add(new StyleSpan<>(combine.apply(withinSpans), firstVal - current));
                current = firstVal;
            }

            if (firstIsStart) {
                sortedByStartSpans.remove(first);
                withinSpans.add(first);
            } else {
                sortedByEndSpans.remove(first);
                withinSpans.remove(first);
            }
        }

        if (current < length || length == 0) {
            builder.add(new StyleSpan<>(empty, length - current));
        }

        return builder;
    }

}
