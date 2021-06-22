package com.me.tmw.nodes.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class NumberField extends TextField {

    private final DoubleProperty value = new SimpleDoubleProperty(this, "value");

    private double numPassKey;
    private String textPassKey;

    private final List<Function<String, ConversionResult>> conversionFunctions = new ArrayList<>();

    public NumberField() {
        this(0);
    }
    public NumberField(double initialValue) {
        getStyleClass().add("number-field");

        value.set(initialValue);
        numPassKey = initialValue;
        textPassKey = String.valueOf(initialValue);
        setText(textPassKey);

        addConverter(text -> {
            try {
                return new ConversionResult(Double.parseDouble(text));
            } catch (NumberFormatException exception) {
                return ConversionResult.UNSUCCESSFUL;
            }
        });

        setOnKeyTyped(event -> {
            if (value.isBound()) {
                event.consume();
            }
        });

        value.addListener(observable -> {
            double newValue = value.get();
            if (newValue != numPassKey) {
                textPassKey = String.valueOf(newValue);
                setText(textPassKey);
                numPassKey = newValue;
            }
        });

        textProperty().addListener(observable -> {
            String newValue = getText();
            if (!newValue.equals(textPassKey)) {
                calculateText(newValue);
            } else {
                getStyleClass().remove("conversion-error");
            }
        });
    }

    private void calculateText(String newValue) {
        boolean success = false;
        for (Function<String, ConversionResult> attempter : conversionFunctions) {
            ConversionResult result = attempter.apply(newValue);
            if (result.isSuccessful()) {
                double val = result.getValue();
                if (val != value.get()) {
                    numPassKey = val;
                    value.set(val);
                }
                success = true;
                break;
            }
        }
        if (success) {
            getStyleClass().remove("conversion-error");
        } else if (!getStyleClass().contains("conversion-error")) {
            getStyleClass().add("conversion-error");
        }
    }

    public void addConverter(Function<String, ConversionResult> converter) {
        conversionFunctions.add(converter);
        calculateText(getText());
    }
    public void removeConverter(Function<String, ConversionResult> converter) {
        conversionFunctions.remove(converter);
        calculateText(getText());
    }

    public double getValue() {
        return value.get();
    }
    public DoubleProperty valueProperty() {
        return value;
    }
    public void setValue(double value) {
        this.value.set(value);
    }

    public static class ConversionResult {

        public static final ConversionResult UNSUCCESSFUL = new ConversionResult();

        private final boolean successful;
        private final double value;

        private ConversionResult() {
            this.successful = false;
            this.value = -1;
        }
        public ConversionResult(double value) {
            this.successful = true;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConversionResult that = (ConversionResult) o;
            return successful == that.successful && Double.compare(that.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(successful, value);
        }

        public boolean isSuccessful() {
            return successful;
        }

        public double getValue() {
            return value;
        }

    }

}
