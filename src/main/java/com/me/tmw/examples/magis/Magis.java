package com.me.tmw.examples.magis;

import com.me.tmw.animations.Animations;
import com.me.tmw.debug.devtools.DevScene;
import com.me.tmw.debug.util.Debugger;
import com.me.tmw.examples.magis.home.LessonShortcut;
import com.me.tmw.nodes.util.Dragging;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Magis {

    public static void run(Stage primaryStage) {

        VBox lessons = new VBox();
        lessons.setPadding(new Insets(30));
        lessons.setSpacing(30);
        lessons.setAlignment(Pos.CENTER);

        lessons.setOnKeyPressed(event -> {
            lessons.getChildren().clear();
            loadOntoVBox(lessons);
        });

        primaryStage.setScene(DevScene.getInstance(lessons));
        loadOntoVBox(lessons);
        primaryStage.show();
        primaryStage.setMaximized(true);
    }

    public static void loadOntoVBox(VBox lessons) {
        for (int i = 0; i < 5; i++) {
            LessonShortcut shortcut = new LessonShortcut("Comments", "Become familiar with the three types of Java comments", 100);
//            Debugger.showProperty(shortcut.translateXProperty(), shortcut);
            shortcut.setIndex(i + 1);
            shortcut.setOpacity(0);
            shortcut.setTranslateY(-50);
            shortcut.setScaleX(0.95);
            shortcut.setScaleY(0.95);
            shortcut.setMouseTransparent(true);
            shortcut.setBorder(new Border(new BorderStroke(Color.ORANGE, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(4))));
            lessons.getChildren().add(shortcut);
            Dragging.draggable(shortcut, shortcut.translateXProperty(), shortcut.translateYProperty());
            shortcut.setOnMouseReleased(event -> {
                Animations.animatePropertyChange(shortcut.translateXProperty(), 0, 100);
                Animations.animatePropertyChange(shortcut.translateYProperty(), 0, 100);
            });
        }

        double delay = 335;
        Platform.runLater(() -> {
            Animations.animateNodesWithDelay(
                    Animations.animator().fadeTo(1, delay).scale(1, delay).setOnFinished(event -> lessons.getChildren().forEach(node -> node.setMouseTransparent(false))).translateY(1, delay),
                    lessons.getChildren(), 120, 3000
            ).play();
        });
    }

}
