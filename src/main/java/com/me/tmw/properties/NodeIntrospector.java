package com.me.tmw.properties;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

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

    public static List<DetailedProperty> getHighDetailedProperties(Object obj) {
        List<Method> methods = Arrays.asList(obj.getClass().getMethods());

        Map<Method, Property<?>> properties = getDetailedProperties(obj);
        List<DetailedProperty> detailedProperties = new ArrayList<>();
        for (Map.Entry<Method, Property<?>> entry : properties.entrySet()) {
            String tempName = entry.getKey().getName().toLowerCase(Locale.ROOT);
            final String name = tempName.substring(0, tempName.length() - "property".length());
            List<Method> possibleMethods = methods.stream().filter(method -> method.getName().toLowerCase(Locale.ROOT).endsWith(name)).collect(Collectors.toList());
            Optional<Method> getter = possibleMethods.stream().filter(method -> method.getName().startsWith("get") || method.getName().startsWith("is")).findFirst();
            if (getter.isEmpty()) continue;
            Optional<Method> setter = possibleMethods.stream().filter(method -> method.getName().startsWith("set")).findFirst();
            detailedProperties.add(new DetailedProperty(setter.orElse(null), getter.get(), entry.getKey(), entry.getValue()));
        }
        return detailedProperties;
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

    public static class PropertyAccessor {

        private final Method setter;
        private final Method getter;
        private final Method propertyMethod;

        public PropertyAccessor(Method setter, Method getter, Method propertyMethod) {
            this.setter = setter;
            this.getter = getter;
            this.propertyMethod = propertyMethod;
        }

        public Method getSetter() {
            return setter;
        }
        public Method getGetter() {
            return getter;
        }
        public Method getPropertyMethod() {
            return propertyMethod;
        }

    }

    public static class DetailedProperty extends PropertyAccessor {

        private final Property<?> property;

        public DetailedProperty(Method setter, Method getter, Method propertyMethod, Property<?> property) {
            super(setter, getter, propertyMethod);
            this.property = property;
        }

        public Property<?> getProperty() {
            return property;
        }

        @Override
        public String toString() {
            return "DetailedProperty{" +
                    "setter=" + getSetter() +
                    ", getter=" + getGetter() +
                    ", propertyMethod=" + getPropertyMethod() +
                    ", property=" + property +
                    '}';
        }

    }

}
