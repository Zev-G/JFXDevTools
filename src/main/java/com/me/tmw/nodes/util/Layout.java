package com.me.tmw.nodes.util;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public final class Layout {

    public static Bounds nodeOnScreen(Node node) {
        return node.localToScreen(node.getBoundsInLocal());
    }

    public static void anchor(Node node) {
        anchor(node, 0);
    }
    public static void anchor(Node node, double amount) {
        AnchorPane.setTopAnchor(node, amount);
        AnchorPane.setLeftAnchor(node, amount);
        AnchorPane.setBottomAnchor(node, amount);
        AnchorPane.setRightAnchor(node, amount);
    }

    public static void anchorTop(double amount, Node... nodes) {
        for (Node node : nodes) {
            AnchorPane.setTopAnchor(node, amount);
        }
    }
    public static void anchorLeft(double amount, Node... nodes) {
        for (Node node : nodes) {
            AnchorPane.setLeftAnchor(node, amount);
        }
    }
    public static void anchorBottom(double amount, Node... nodes) {
        for (Node node : nodes) {
            AnchorPane.setBottomAnchor(node, amount);
        }
    }
    public static void anchorRight(double amount, Node... nodes) {
        for (Node node : nodes) {
            AnchorPane.setRightAnchor(node, amount);
        }
    }

}
