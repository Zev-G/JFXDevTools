package com.me.tmw.nodes.control.paint;

import com.me.tmw.nodes.control.LabeledNumberField;
import com.me.tmw.resource.Resources;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;

// TODO make this public once finished
/*public*/ class RadialGradientPicker extends VBox {

    private static final String STYLE_SHEET = Resources.NODES.getCss("radial-gradient-picker");

    private final ObjectProperty<RadialGradient> value = new SimpleObjectProperty<>(this, "value");

    /* ********************************************************
    *          Radial Gradient Components
     ******************************************************** */

    private final LabeledNumberField focusAngle = new LabeledNumberField("Focus Angle: ");
    private final LabeledNumberField focusDistance = new LabeledNumberField("Focus Distance: ");
    private final LabeledNumberField centerX = new LabeledNumberField("Center X: ");
    private final LabeledNumberField centerY = new LabeledNumberField("Center Y: ");
    private final LabeledNumberField radius = new LabeledNumberField("Radius: ");

    private final CheckBox proportional = new CheckBox("Proportional: ");
    private final ComboBox<CycleMethod> cycleMethod = new ComboBox<>(FXCollections.observableArrayList(CycleMethod.values()));
    private final StopsPicker stops = new StopsPicker();

    /* ********************************************************
     *          Layout
     ******************************************************** */


    private final Pane preview = new Pane();



    // double: focusAngle, focusDistance, centerX, centerY, radius
    // boolean: proportional
    // CycleMethod: cycleMethod
    // List<Stop>: stops



}
