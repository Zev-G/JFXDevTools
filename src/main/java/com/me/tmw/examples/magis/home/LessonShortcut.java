package com.me.tmw.examples.magis.home;

import com.me.tmw.animations.Animations;
import com.me.tmw.examples.magis.lib.RingProgressIndicator;
import com.me.tmw.nodes.util.Layout;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Objects;

public class LessonShortcut extends HBox {

    private final ImageView image = new ImageView(new Image("https://static.thenounproject.com/png/146003-200.png"));
    private final Separator separator = new Separator(Orientation.VERTICAL);

    private final RingProgressIndicator progressIndicator = new RingProgressIndicator();
    private final Label titleLabel = new Label();
    private final AnchorPane padProgressIndicator = new AnchorPane(progressIndicator);
    private final AnchorPane titleLayout = new AnchorPane(titleLabel, padProgressIndicator);
    private final Label paragraph = new Label();
    private final VBox rightVerticalLayout = new VBox(titleLayout, paragraph);

    private final IntegerProperty index = new SimpleIntegerProperty(0);

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    public LessonShortcut(String title, String description, int completionPercentage) {
        this.title.set(title);
        this.description.set(description);

        VBox.setVgrow(this, Priority.NEVER);

        titleLayout.setMinWidth(450);

        this.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("examples/css/magis.css")).toExternalForm());

        this.getStyleClass().add("lesson-shortcut");
        titleLabel.getStyleClass().addAll("title", "consolas");

        paragraph.textProperty().bind(this.description);
        titleLabel.textProperty().bind(Bindings.concat(this.title, " - ", index));

        progressIndicator.setProgress(completionPercentage);

        getChildren().addAll(image, separator, rightVerticalLayout);

        Layout.anchorTop(0, titleLabel);
        Layout.anchorRight(0, titleLabel);
        Layout.anchorLeft(15, progressIndicator);
//        Dragging.draggable(this, layoutXProperty(), layoutYProperty());

        Animations.bindAnimation(Animations.animate(this).add(
                Animations.scale().setFromX(1).setFromY(1).setToX(1.02).setToY(1.02).setDuration(Duration.millis(100))
        ).build(this), hoverProperty());

        double size = 135;
        image.setFitWidth(size);
        image.setFitHeight(size);
    }

    public int getIndex() {
        return index.get();
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public void setIndex(int index) {
        this.index.set(index);
    }
}
