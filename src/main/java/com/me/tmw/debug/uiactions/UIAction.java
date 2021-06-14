package com.me.tmw.debug.uiactions;

import com.me.tmw.nodes.util.NodeMisc;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class UIAction<T extends Node> implements UIActionFunctions {

    private final UIAction<?> parentAction;
    private Supplier<T> parent;

    UIAction(T parent) {
        this(parent, null);
    }
    UIAction(T parent, UIAction<?> parentAction) {
        this(() -> parent, parentAction);
    }

    protected UIAction(Supplier<T> parent, UIAction<?> parentAction) {
        this.parent = () -> {
            T node = parent.get();
            this.parent = () -> node;
            return node;
        };
        this.parentAction = parentAction;
    }

    public final UIAction<T> clickOn(Point2D point) {
        return clickOn(point.getX(), point.getY());
    }
    public final UIAction<T> clickOn(double x, double y) {
        if (parent == null) {
            return this;
        } else {
            return run(() -> UIActions.click(x, y));
        }
    }
    public final UIAction<T> clickOn() {
        if (parent == null) {
            return this;
        } else {
            return run(() -> UIActions.click(parent.get()));
        }
    }

    public final UIAction<T> fireMouseEvent(EventType<? extends MouseEvent> eventType) {
        if (parent == null) {
            return this;
        } else {
            return run(() -> parent.get().fireEvent(NodeMisc.generateMouseEvent(parent.get(), eventType)));
        }
    }

    public final UIAction<T> run(Runnable runnable) {
        return new UIAction<>(parent, this) {
            @Override
            protected void run() {
                runnable.run();
            }
        };
    }

    public final UIAction<Node> lookup(String lookup) {
        return new EmptyUIAction<Node>(() -> parent.get().lookup(lookup), this);
    }
    public final UIAction<Node> lookup(String lookup, int index) {
        return new EmptyUIAction<Node>(() -> parent.get().lookupAll(lookup).toArray(new Node[0])[index], this);
    }

    /**
     * @param lookup a string which gets parsed as a CSS selector, this selector is used to find valid children of this node.
     * @throws NullPointerException if the Parent property of this UIAction is null.
     * @see Node#lookupAll(String)
     */
    public final UIAction<Node> lookupAll(String lookup, Function<UIAction<Node>, UIAction<?>> configure) {
        return processor(new ArrayList<>(parent.get().lookupAll(lookup)), this, configure);
    }

    private static <T extends Node> UIAction<Node> processor(List<T> children, UIAction<?> parent, Function<UIAction<T>, UIAction<?>> configure) {
        List<UIAction<?>> uiActions = children.stream()
                .map(node -> configure.apply(new EmptyUIAction<>(node, parent) {
                    @Override
                    public void execute() {
                        // executing this child shouldn't cause anything to happen to the parent.
                        if (this.isInExecution()) return;
                        setInExecution(true);
                        run();
                        setInExecution(false);
                    }
                })).collect(Collectors.toList());
        //noinspection Convert2Diamond
        return new UIAction<Node /* Won't compile w/out */>(() -> (Node) parent.parent, parent) {
            @Override
            protected void run() {
                uiActions.forEach(UIAction::execute);
            }
        };
    }

    public final UIAction<Node> get(int i) {
        return new EmptyUIAction<Node>(() -> {
            Node node = parent.get();
            if (node instanceof Parent) {
                Optional<List<Node>> expectedResult = NodeMisc.getChildren(node);
                if (expectedResult.isPresent()) {
                    return expectedResult.get().get(i);
                } else {
                    return ((Parent) node).getChildrenUnmodifiable().get(i);
                }
            }
            return node;
        }, this);
    }

    /**
     * Uses {@link Parent#getChildrenUnmodifiable()} 100% of the time. Useful in some cases, but generally {@link UIAction#get(int)} should be used.
     */
    public final UIAction<Node> simpleGetChild(int i) {
        return new EmptyUIAction<Node>(() -> {
            Node node = parent.get();
            if (node instanceof Parent) {
                return ((Parent) node).getChildrenUnmodifiable().get(i);
            }
            return node;
        }, this);
    }

    private boolean inExecution = false; // Used to stop an infinite loop.

    public void execute() {
        if (inExecution) return;
        inExecution = true;
        if (parentAction != null) parentAction.execute();
        run();
        inExecution = false;
    }

    protected abstract void run();

    protected boolean isInExecution() {
        return inExecution;
    }
    protected void setInExecution(boolean inExecution) {
        this.inExecution = inExecution;
    }

    public Supplier<T> getNode() {
        return parent;
    }

    public UIAction<?> getParentAction() {
        return parentAction;
    }

}
