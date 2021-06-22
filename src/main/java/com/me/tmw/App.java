package com.me.tmw;

import com.me.tmw.animations.Animations;
import com.me.tmw.animations.builder.grouping.AnimationGroupBuilder;
import com.me.tmw.debug.devtools.DevScene;
import com.me.tmw.debug.devtools.DevTools;
import com.me.tmw.debug.devtools.DevToolsContainer;
import com.me.tmw.nodes.control.Point;
import com.me.tmw.nodes.control.PointsEditor;
import com.me.tmw.nodes.control.paint.LinearGradientPicker;
import com.me.tmw.nodes.util.Dragging;
import com.me.tmw.nodes.util.Layout;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

//        Magis.run(primaryStage);

//        Label label = new Label("Label");
//        StringPropertyEditor editor = new StringPropertyEditor(label.textProperty());
//        DoublePropertyEditor heightEditor = new DoublePropertyEditor(label.minHeightProperty());
//        OptionBasedPropertyEditor<Cursor> cursors = OptionBasedPropertyEditor.fromArray(label.cursorProperty(), Stream.concat(Arrays.stream(Cursor.class.getDeclaredFields()).filter(field -> Cursor.class.isAssignableFrom(field.getType())).map(field -> {
//            try {
//                return (Cursor) field.get(null);
//            } catch (IllegalAccessException e) {
//                return null;
//            }
//        }), Stream.of((Cursor) null)).toArray(Cursor[]::new));
//        PaintPropertyEditor colorEditor = new PaintPropertyEditor(label.textFillProperty());
//        Button button = new Button("Set to: \"hi\"");
//        button.setOnAction(event -> label.setText("hi"));
//        primaryStage.setScene(new DevScene(new VBox(button, label, editor.getNode(), heightEditor.getNode(), cursors.getNode(), colorEditor.getNode())));

        Point a = new Point(0, 0);
        a.setXLocked(true);
        a.setCentered(false);
        Point b = new Point(0, 0);
        b.setYLocked(true);
        b.setCentered(false);
        PointsEditor editor = new PointsEditor(a, b);
        editor.connectPoints(a, b);
        primaryStage.setScene(new DevScene(editor));

        primaryStage.show();
        primaryStage.setTitle("Test");

//        UIActions.performOn(primaryStage).lookupAll(".button", nodeUIAction -> nodeUIAction.fireMouseEvent(MouseEvent.MOUSE_ENTERED)).execute();
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

    private static void benchmarkDevTools(int times) {
        new DevTools(new LinearGradientPicker(), new DevToolsContainer() {
            @Override
            public void attach(DevTools tools) {

            }

            @Override
            public void remove(DevTools tools) {

            }

            @Override
            public boolean isShowing(DevTools tools) {
                return false;
            }
        });
        DevToolsContainer container = new DevToolsContainer() {
            @Override
            public void attach(DevTools tools) {

            }

            @Override
            public void remove(DevTools tools) {

            }

            @Override
            public boolean isShowing(DevTools tools) {
                return true;
            }
        };
        long[] totalLowTimes = new long[times];
        long[] totalHighTimes = new long[times];

        long totalHighPerformanceTime = 0;
        long totalLowPerformanceTime = 0;
        for (int i = 0; i < times; i++) {
            Parent parent = new LinearGradientPicker(LinearGradient.valueOf("linear-gradient(from 0px 0px to 200px 0px, #00ff00 0%, 0xff0000 50%, 0x1122ff40 100%)"));
            DevTools.disableImprovedPerformanceMode();
            long highPerformanceTime = timeToMakeDevTools(parent, container);
            totalHighPerformanceTime += highPerformanceTime;

            parent = new LinearGradientPicker(LinearGradient.valueOf("linear-gradient(from 0px 0px to 200px 0px, #00ff00 0%, 0xff0000 50%, 0x1122ff40 100%)"));
            DevTools.enableImprovedPerformanceMode();
            long lowPerformanceTime = timeToMakeDevTools(parent, container);
            totalLowPerformanceTime += lowPerformanceTime;

            totalLowTimes[i] = lowPerformanceTime;
            totalHighTimes[i] = highPerformanceTime;
        }
        System.out.println("-".repeat(15) + " ".repeat(3) + "Benchmark Results" + " ".repeat(3) + "-".repeat(15));
        System.out.println("Total time: " + (totalHighPerformanceTime + totalLowPerformanceTime) + "ms");
        System.out.println("\tWith node tree search time: " + totalLowPerformanceTime + "ms");
        System.out.println("\tWithout node tree search time: " + totalHighPerformanceTime + "ms");
        System.out.println();
        System.out.println("Average time: " + ((totalHighPerformanceTime + totalLowPerformanceTime) / times) + "ms");
        System.out.println("\tWith node tree search average time: " + (totalLowPerformanceTime / times) + "ms");
        System.out.println("\tWithout node tree search average time: " + (totalHighPerformanceTime / times) + "ms");

        System.out.println("Graph:");
        for (int i = 0; i < times; i++) {
            System.out.println("-".repeat((int) totalHighTimes[i]) + " (" + totalHighTimes[i] +"ms)");
            System.out.println("=".repeat((int) totalLowTimes[i]) + " (" + totalLowTimes[i] +"ms)");
        }
        System.out.println("-".repeat(36 + "Benchmark Results".length()));
    }

    private static long timeToMakeDevTools(Parent parent, DevToolsContainer container) {
        long time = System.currentTimeMillis();
        new DevTools(parent, container);
        return System.currentTimeMillis() - time;
    }

}
