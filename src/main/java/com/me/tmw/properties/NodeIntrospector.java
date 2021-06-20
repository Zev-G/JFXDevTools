package com.me.tmw.properties;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class NodeIntrospector {

    public static Consumer<ReflectiveOperationException> reflectionHandler = Throwable::printStackTrace;
    public static Consumer<ClassCastException> castHandler = Throwable::printStackTrace;

    private static <V> Function<Method, V> invokeMapper(Object object) {
        return method -> invoke(method, object);
    }
    @SuppressWarnings("unchecked")
    private static <V> V invoke(Method method, Object obj) {
        try {
            return (V) method.invoke(obj);
        } catch (ReflectiveOperationException e) {
            reflectionHandler.accept(e);
            return null;
        } catch (ClassCastException e) {
            castHandler.accept(e);
            return null;
        }
    }

    public static List<Property<?>> getProperties(Object obj) {
        return getProperties(obj, Requirements.PROPERTY);
    }

    public static <T extends ObservableValue<?>> List<T> getProperties(Object obj, RequirementBase<T> requirement) {
        return getValidMethodsStream(obj, requirement)
                .map(NodeIntrospector.<T>invokeMapper(obj))
                .collect(Collectors.toList());
    }

    public static Map<Method, Property<?>> getDetailedProperties(Object obj) {
        return getDetailedProperties(obj, Requirements.PROPERTY);
    }
    public static <T extends ObservableValue<?>> Map<Method, T> getDetailedProperties(Object obj, RequirementBase<T> requirement) {
        return getValidMethodsStream(obj, requirement)
                .collect(HashMap::new, (map, method) -> {
                    T result = invoke(method, obj);
                    if (result != null) {
                        map.put(method, result);
                    }
                }, HashMap::putAll);
    }

    private static Stream<Method> getValidMethodsStream(Object obj, RequirementBase<?> requirement) {
        return Arrays.stream(obj.getClass().getMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.canAccess(obj))
                .filter(method -> method.getName().toLowerCase(Locale.ROOT).endsWith("property"))
                .filter(requirement);
    }

    public static abstract class RequirementBase<T extends ObservableValue<?>> implements Predicate<Method> {

    }

    public static class Requirements {

        public static RequirementBase<ObservableValue<?>> OBSERVABLE_VALUE = fromPredicate(method -> ObservableValue.class.isAssignableFrom(method.getReturnType()));
        public static RequirementBase<Property<?>> PROPERTY = fromPredicate(method -> Property.class.isAssignableFrom(method.getReturnType()));

        public static <T extends ObservableValue<?>> RequirementBase<T> fromPredicate(Predicate<Method> predicate) {
            return new RequirementBase<>() {
                @Override
                public boolean test(Method method) {
                    return predicate.test(method);
                }
            };
        }

    }

}
