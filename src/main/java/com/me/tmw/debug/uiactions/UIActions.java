package com.me.tmw.debug.uiactions;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.InputEvent;

public final class UIActions {


    public static UIAction<Parent> performOn(Stage stage) {
        return performOn(stage.getScene());
    }
    public static UIAction<Parent> performOn(Scene scene) {
        return performOn(scene.getRoot());
    }
    public static UIAction<Parent> performOn(Parent parent) {
        return new EmptyUIAction<>(parent);
    }

    public static void click(Node node) {
        Point2D location = node.localToScreen(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY());
        click(location.getX(), location.getY());
    }

    public static void click(double x, double y) {
        click((int) x, (int) y);
    }
    public static void click(int x, int y) {
        Point originalLocation = MouseInfo.getPointerInfo().getLocation();
        try {
            Robot robot = new Robot();
            robot.mouseMove(x, y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseMove((int) originalLocation.getX(), (int)originalLocation.getY());
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}
