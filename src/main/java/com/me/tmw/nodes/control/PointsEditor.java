package com.me.tmw.nodes.control;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PointsEditor extends Region {

    private final ObservableList<Point> points = FXCollections.observableArrayList();

    public PointsEditor(Point... configurations) {
        this(Arrays.asList(configurations));
    }
    public PointsEditor(Collection<Point> configurations) {
        // Initialize points list.
        points.addListener((ListChangeListener<Point>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    List<Node> nodes = new ArrayList<>();
                    for (Point added : c.getAddedSubList()) {
                        nodes.add(added);
                        if (added.getEditor() != null) {
                            added.getEditor().getPoints().remove(added);
                        }
                        added.setEditor(this);
                    }
                    getChildren().addAll(nodes);
                }
                if (c.wasRemoved()) {
                    List<Node> nodes = new ArrayList<>();
                    for (Point removed : c.getRemoved()) {
                        nodes.add(removed);
                        removed.clearConnectors();
                        if (removed.getEditor() == this) {
                            removed.setEditor(null);
                        }
                    }
                    getChildren().removeAll(nodes);
                }
            }
        });
        points.addAll(configurations);
    }

    public PointConnector connectPoints(Point a, Point b) {
        PointConnector connector = new PointConnector(a, b);
        getChildren().add(connector);
        connector.toBack();
        return connector;
    }

    public ObservableList<Point> getPoints() {
        return points;
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth();
        double height = getHeight();

        for (Point point : points) {
            point.layout(width, height);
        }
    }

}
