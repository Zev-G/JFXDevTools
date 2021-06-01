package com.me.tmw.debug.visualize;

import com.me.tmw.debug.devtools.DevUtils;
import com.me.tmw.resource.Resources;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ObjectVisualizer extends TitledPane {

    private static final String STYLE_SHEET = Resources.DEBUGGER.getCss("object-visualizer");

    private final Object object;
    private final Field[] fields;

    private final VBox items = new VBox();

    public ObjectVisualizer(Object object) {
        this(object, null);
    }
    public ObjectVisualizer(Object object, String name) {
        this.object = object;
        setContent(items);
        setText("");

        Text classNameTextNode = new Text(DevUtils.getSimpleClassName(object.getClass()));
        Text nameTextNode = name == null ? null : new Text(name);
        Text valueTextNode = new Text(object.toString());
        classNameTextNode.getStyleClass().add("class-name");
        valueTextNode.getStyleClass().add("value-label");
        TextFlow flow = new TextFlow();
        flow.getChildren().add(classNameTextNode);
        if (nameTextNode != null) {
            flow.getChildren().add(new Text(" "));
            flow.getChildren().add(nameTextNode);
        }
        flow.getChildren().add(new Text(": "));
        flow.getChildren().add(valueTextNode);

        getStylesheets().add(STYLE_SHEET);

        setGraphic(flow);

        this.fields = object.getClass().getDeclaredFields();

        setExpanded(false);

        if (isPrimitiveOrPrimitiveWrapperOrString(object.getClass())) {
            setCollapsible(false);
        }

        expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (items.getChildren().isEmpty() && newValue && !object.getClass().isPrimitive() && !(object instanceof String)) {
                try {
                    Map<String, Object> nameValMap = Arrays.stream(
                            Introspector.getBeanInfo(object.getClass(), Object.class).getPropertyDescriptors())
                            .filter(descriptor -> descriptor.getReadMethod() != null)
                            .collect(Collectors.toMap(
                                    PropertyDescriptor::getName,
                                    descriptor -> {
                                        try {
                                            Object result = descriptor.getReadMethod().invoke(object);
                                            if (result != null) {
                                                return result;
                                            }
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                            return "can't access";
                                        }
                                        return "null";
                                    }
                            ));

                    for (Map.Entry<String, Object> entry : nameValMap.entrySet()) {
                        items.getChildren().add(
                                new ObjectVisualizer(
                                        entry.getValue(), entry.getKey()
                                )
                        );
                    }
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean isPrimitiveOrPrimitiveWrapperOrString(Class<?> type) {
        return type.isPrimitive() ||
                type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class || type == String.class;
    }

}
