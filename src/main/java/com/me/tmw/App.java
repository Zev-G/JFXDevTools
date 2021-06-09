package com.me.tmw;

import com.me.tmw.animations.Animations;
import com.me.tmw.animations.builder.grouping.AnimationGroupBuilder;
import com.me.tmw.debug.devtools.DevScene;
import com.me.tmw.examples.magis.Magis;
import com.me.tmw.nodes.richtextfx.LanguageCodeArea;
import com.me.tmw.nodes.richtextfx.languages.CSSLang;
import com.me.tmw.nodes.util.Dragging;
import com.me.tmw.nodes.util.Layout;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        Magis.run(primaryStage);

    }

    private static void testApp(Stage primaryStage) {
        VBox box = new VBox();
        box.setPadding(new Insets(45));
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);

        Label title = new Label("Title");
        Dragging.dragAsPopup(title);
        title.setFont(new Font(28));
        title.setTextFill(Color.WHITE);
        VBox layout = new VBox(new BorderPane(title), new Separator(), box);
        VBox layoutHolder = new VBox(layout);
        layoutHolder.setBackground(new Background(new BackgroundFill(Color.web("#9704d1"), CornerRadii.EMPTY, Insets.EMPTY)));
        layoutHolder.setOpacity(0);
        layout.setTranslateY(-20);

        for (int i = 0; i < 15; i++) {
            Button button = new Button("Label " + i);
            button.setBackground(null);
            button.setTextFill(Color.WHITE);
            button.setOpacity(0);
            button.setFont(new Font(18));
            button.setTranslateX(-20);
            Animations.bindAnimation(Animations.scale().setDuration(Duration.millis(100)).setToX(1.3).setToY(1.3).setFromX(1).setFromY(1).build(button), button.hoverProperty());
            Animations.bindAnimation(Animations.fade().setDuration(Duration.millis(100)).setToValue(1).setFromValue(0.7).build(button), button.armedProperty());
            Dragging.draggable(button, button.translateXProperty(), button.translateYProperty());
            button.rotateProperty().bind(Bindings.createDoubleBinding(() ->
                            (button.getTranslateX() != 0 ? Math.atan(button.getTranslateY() / button.getTranslateX()) : 0) * 100
                    , button.translateXProperty(), button.translateYProperty()));
            button.setOnMouseReleased(event -> {
                Animations.animatePropertyChange(button.translateYProperty(), 0, 100);
                Animations.animatePropertyChange(button.translateXProperty(), 0, 100);
            });
            box.getChildren().add(button);
        }

        Button load = new Button("Load");
        AnchorPane loadPane = new AnchorPane(load);

        load.setOnAction(event -> {

            loadPane.getChildren().setAll(layoutHolder);
            Layout.anchor(layoutHolder);
            Animations.sequential(
                    Animations.parallel(
                            Animations.animate(layout).translateY(0, 300).build(),
                            Animations.animate(layoutHolder).fadeTo(1, 300).build()
                    ),
                    Animations.animateNodesWithDelay(new AnimationGroupBuilder().fadeTo(0.7, 400).translateX(0, 400), box.getChildren(), 75, 2000)
            ).play();
        });
        Layout.anchor(load);
        primaryStage.setScene(new DevScene(loadPane));
        primaryStage.show();
    }

}
