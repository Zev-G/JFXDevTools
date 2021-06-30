package com.me.tmw.nodes.control.paint;

import com.me.tmw.nodes.control.NumberField;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

public class RGBPicker extends ColorValuesEditor {

    private final IntegerProperty red = new SimpleIntegerProperty(this, "R");
    private final IntegerProperty green = new SimpleIntegerProperty(this, "G");
    private final IntegerProperty blue = new SimpleIntegerProperty(this, "B");
    private final IntegerProperty alpha = new SimpleIntegerProperty(this, "A");

    public RGBPicker() {
        loadProperties(s -> {
            try {
                int val = Integer.parseInt(s);
                if (val < 0 || val > 255) {
                    return NumberField.ConversionResult.UNSUCCESSFUL;
                } else {
                    return new NumberField.ConversionResult(val);
                }
            } catch (NumberFormatException e) {
                return NumberField.ConversionResult.UNSUCCESSFUL;
            }
        }, red, green, blue);
        loadProperty(alpha ,s -> {
            try {
                int val = Integer.parseInt(s);
                if (val < 0 || val > 100) {
                    return NumberField.ConversionResult.UNSUCCESSFUL;
                } else {
                    return new NumberField.ConversionResult(val);
                }
            } catch (NumberFormatException e) {
                return NumberField.ConversionResult.UNSUCCESSFUL;
            }
        });
    }

    @Override
    protected void loadValue(Color color) {
        red.set((int) (color.getRed() * 255));
        green.set((int) (color.getGreen() * 255));
        blue.set((int) (color.getBlue() * 255));
        alpha.set((int) (color.getOpacity() * 100));
    }

    @Override
    protected Color genColor() {
        return Color.rgb(
                Math.max(0, Math.min(255, red.get())),
                Math.max(0, Math.min(255, green.get())),
                Math.max(0, Math.min(255, blue.get())),
                Math.max(0, Math.min(1, alpha.get() / 100D))
                );
    }

}
