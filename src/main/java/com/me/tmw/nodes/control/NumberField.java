package com.me.tmw.nodes.control;

import com.sun.javafx.scene.control.skin.Utils;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class NumberField extends TextField {

    private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");

    private final DoubleProperty value = new SimpleDoubleProperty(this, "value");
    private final BooleanProperty forceIntegers = new SimpleBooleanProperty(this, "forceIntegers");
    private final BooleanProperty autoWidth = new SimpleBooleanProperty(this, "autoWidth");

    private double numPassKey;
    private String textPassKey;

    private final ObservableList<Function<String, ConversionResult>> conversionFunctions = FXCollections.observableArrayList();

    public NumberField() {
        this(0);
    }
    public NumberField(Number initialValue) {
        getStyleClass().add("number-field");

        value.set(initialValue.doubleValue());
        numPassKey = initialValue.doubleValue();
        textPassKey = String.valueOf(initialValue);
        setText(textPassKey);

        // Init conversion functions.
        conversionFunctions.addListener((InvalidationListener) observable -> calculateText(getText()));
        conversionFunctions.add(text -> {
            try {
                if (isForceIntegers()) {
                    return new ConversionResult(Integer.parseInt(text));
                } else {
                    return new ConversionResult(Double.parseDouble(text));
                }
            } catch (NumberFormatException exception) {
                return ConversionResult.UNSUCCESSFUL;
            }
        });

        forceIntegers.addListener(observable -> {
            textPassKey = null;
            calculateText(getText());
        });

        List<Runnable> removeAutoWidth = new ArrayList<>();
        autoWidth.addListener(observable -> {
            boolean autoWidth = isAutoWidth();
            if (autoWidth) {
                InvalidationListener textListener = changedProp -> {
                    double calc = Utils.computeTextWidth(getFont(), getText() + " ", 0D);;
                    setPrefWidth(calc);
                    positionCaret(getCaretPosition()); // If you remove this line, it flashes a little bit
                };
                textListener.invalidated(null);
                textProperty().addListener(textListener);
                removeAutoWidth.add(() -> textProperty().removeListener(textListener));

                InvalidationListener fontListener = changedProp -> {
                    double calc = Utils.computeTextWidth(getFont(), getText() + " ", 0D);;
                    setPrefWidth(calc);
                };
                fontProperty().addListener(fontListener);
                removeAutoWidth.add(() -> fontProperty().removeListener(fontListener));
            } else {
                removeAutoWidth.forEach(Runnable::run);
                removeAutoWidth.clear();
                setPrefWidth(USE_COMPUTED_SIZE);
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
                textPassKey = String.valueOf(value.getValue());
                setText(textPassKey);
                numPassKey = newValue;
            }
        });

        textProperty().addListener(observable -> {
            String newValue = getText();
            if (!newValue.equals(textPassKey)) {
                calculateText(newValue);
            } else {
                pseudoClassStateChanged(INVALID, false);
            }
        });
    }

    public boolean isForceIntegers() {
        return forceIntegers.get();
    }
    public BooleanProperty forceIntegersProperty() {
        return forceIntegers;
    }
    public void setForceIntegers(boolean forceIntegers) {
        this.forceIntegers.set(forceIntegers);
    }

    public boolean isAutoWidth() {
        return autoWidth.get();
    }
    public BooleanProperty autoWidthProperty() {
        return autoWidth;
    }
    public void setAutoWidth(boolean autoWidth) {
        this.autoWidth.set(autoWidth);
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
        pseudoClassStateChanged(INVALID, !success);
    }

    public ObservableList<Function<String, ConversionResult>> getConversionFunctions() {
        return conversionFunctions;
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
