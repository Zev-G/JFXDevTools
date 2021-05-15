package com.me.tmw.css;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;

import java.util.concurrent.atomic.AtomicReference;

public final class Pseudos {

    public static BooleanProperty pseudoClassProperty(Node node, String pseudoClass) {
        if (pseudoClass.startsWith(":")) {
            pseudoClass = pseudoClass.substring(1);
        }
        return pseudoClassProperty(node, PseudoClass.getPseudoClass(pseudoClass));
    }
    public static BooleanProperty pseudoClassProperty(Node node, PseudoClass pseudoClass) {
        var pseudos = new Object() {
            private ObservableSet<PseudoClass> states;
            private SetChangeListener<PseudoClass> changeListener;
        };
        pseudos.states = node.getPseudoClassStates();

        BooleanProperty active = new SimpleBooleanProperty(pseudos.states.contains(pseudoClass));

        pseudos.changeListener = change -> {
            System.out.println("2");
            if (pseudos.states != node.getPseudoClassStates()) {
                pseudos.states.removeListener(pseudos.changeListener);
                pseudos.states = node.getPseudoClassStates();
                pseudos.states.addListener(pseudos.changeListener);
            }
            active.set(pseudos.states.contains(pseudoClass));
        };
        pseudos.states.addListener(pseudos.changeListener);

        return active;
    }

}
