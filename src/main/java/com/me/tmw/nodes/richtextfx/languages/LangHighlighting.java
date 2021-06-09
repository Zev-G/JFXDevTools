package com.me.tmw.nodes.richtextfx.languages;

import com.me.tmw.nodes.richtextfx.SortableStyleSpan;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangHighlighting {

    public static Function<String, List<SortableStyleSpan<Collection<String>>>> simpleRegexMap(String... array) {
        if (array.length % 2 != 0) {
            throw new IllegalArgumentException("Array must come in pairs.");
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < array.length; i += 2) {
            map.put(array[i], array[i + 1]);
        }
        return simpleRegexMap(map);
    }

    public static Function<String, List<SortableStyleSpan<Collection<String>>>> simpleRegexMap(Map<String, String> map) {
        Map<Pattern, Collection<String>> compiled = new HashMap<>();
        map.forEach((s, strings) -> compiled.put(Pattern.compile(s), Collections.singleton(strings)));
        return regexMap(compiled);
    }

    public static Function<String, List<SortableStyleSpan<Collection<String>>>> nonCompiledRegexMap(Map<String, Collection<String>> map) {
        Map<Pattern, Collection<String>> compiled = new HashMap<>();
        map.forEach((s, strings) -> compiled.put(Pattern.compile(s), strings));
        return regexMap(compiled);
    }

    public static Function<String, List<SortableStyleSpan<Collection<String>>>> regexMap(Map<Pattern, Collection<String>> map) {
        return text -> {
            List<SortableStyleSpan<Collection<String>>> styleSpans = new ArrayList<>();
            for (Map.Entry<Pattern, Collection<String>> item : map.entrySet()) {
                Matcher matcher = item.getKey().matcher(text);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    styleSpans.add(new SortableStyleSpan<>(item.getValue(), start, end));
                }
            }
            return styleSpans;
        };
    }

}
