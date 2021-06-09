package com.me.tmw.nodes.util;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class Popups {

    public static void showInputPopup(String prompt, Consumer<String> receiver) {
        TextField input = new TextField();
        input.setPromptText(prompt);

        Stage stage = new Stage();
        stage.setScene(new Scene(input));
        stage.setTitle(prompt);
        stage.show();

        AtomicReference<Boolean> success = new AtomicReference<>(false);
        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                success.set(true);
                stage.hide();
                receiver.accept(input.getText());
            }
        });

        stage.setOnHidden(event -> {
            if (!success.get()) {
                receiver.accept(null);
            }
        });
    }

}
