package com.me.tmw.nodes.control.paint;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

import java.util.*;
import java.util.stream.Collectors;

public class StopsPicker extends VBox implements List<Stop> {

    private final ObservableList<ObjectProperty<Stop>> stopProperties = FXCollections.observableArrayList();
    private final ObservableList<Stop> stops = FXCollections.observableArrayList();
    private final ObservableList<Stop> unmodifiableStops = FXCollections.unmodifiableObservableList(stops);
    private final List<ObjectProperty<Stop>> loadedStops = new ArrayList<>();

    private final Map<ObjectProperty<Stop>, StopView> stopMap = new HashMap<>();
    private final Map<ObjectProperty<Stop>, ChangeListener<Stop>> toBeRemoved = new HashMap<>();

    private final FlowPane stopsView = new FlowPane();
    private final Button addStop = new Button("Add Stop");

    public StopsPicker(Stop... stops) {
        this(Arrays.asList(stops));
    }
    public StopsPicker(Collection<Stop> initialStops) {
        // Implement functionality of stopProperties list
        stopProperties.addListener((InvalidationListener) observable -> {
            List<ObjectProperty<Stop>> removalQueue = new ArrayList<>(loadedStops);
            for (int i = 0, length = stopProperties.size(); i < length; i++) {
                ObjectProperty<Stop> stop = stopProperties.get(i);
                removalQueue.remove(stop);
                boolean contains = loadedStops.contains(stop);
                ObjectProperty<Stop> loadedStop = loadedStops.size() <= i ? null : loadedStops.get(i);
                if (!contains) {
                    addStop(stop, i);
                } else if (loadedStop != stop) {
                    moveStop(stop, i);
                }
            }
            removalQueue.forEach(this::removeStop);
        });

        // Add initial stopProperties
        stopProperties.addAll(initialStops.stream().map(SimpleObjectProperty::new).collect(Collectors.toList()));

        // CSS things
        getStyleClass().add("stopProperties");

        // Event handlers
        addStop.setOnAction(event -> stopProperties.add(new SimpleObjectProperty<>(new Stop(100, Color.WHITE))));

        // Populate self
        getChildren().addAll(stopsView, addStop);
    }

    private void moveStop(ObjectProperty<Stop> stop, int to) {
        StopView view = stopMap.computeIfAbsent(stop, StopView::new); // Probably could be replaced with a get call.
        loadedStops.remove(stop);
        stopsView.getChildren().remove(view);
        loadedStops.add(to, stop);
        getChildren().add(to, view);
    }
    private void addStop(ObjectProperty<Stop> stop, int to) {
        StopView view = stopMap.computeIfAbsent(stop, StopView::new);
        loadedStops.add(to, stop);
        stopsView.getChildren().add(to, view);
        ChangeListener<Stop> stopChanged = (observable, oldValue, newValue) -> {
            stops.remove(oldValue);
            stops.add(newValue);
        };
        stops.add(stop.get());
        view.stopProperty().addListener(stopChanged);
        toBeRemoved.put(view.stopProperty(), stopChanged);
    }
    private void removeStop(ObjectProperty<Stop> stop) {
        StopView view = stopMap.get(stop);
        loadedStops.remove(stop);
        stopsView.getChildren().remove(view);
        stop.removeListener(toBeRemoved.remove(stop));
        stops.remove(view.getStop());
    }

    public ObservableList<Stop> getStops() {
        return unmodifiableStops;
    }

    /*
       List implementation
     */

    private Optional<ObjectProperty<Stop>> findMatching(Object stop) {
        return stopProperties.stream()
                .filter(property -> stop == null ? property == null : stop.equals(property.get()))
                .findFirst();
    }
    private List<ObjectProperty<Stop>> findMatching(Collection<?> stops) {
        return stops.stream()
                .map(this::findMatching)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean add(Stop stop) {
        return stopProperties.add(new SimpleObjectProperty<>(stop));
    }

    @Override
    public boolean remove(Object o) {
        return stopProperties.remove(findMatching(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return stopProperties.containsAll(findMatching(c));
    }

    @Override
    public boolean addAll(Collection<? extends Stop> c) {
        return stopProperties.addAll(findMatching(c));
    }

    @Override
    public boolean addAll(int index, Collection<? extends Stop> c) {
        return stopProperties.addAll(index, findMatching(c));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return stopProperties.removeAll(findMatching(c));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return stopProperties.retainAll(findMatching(c));
    }

    @Override
    public void clear() {
        stopProperties.clear();
    }

    @Override
    public Stop get(int index) {
        return stopProperties.get(index).get();
    }

    @Override
    public Stop set(int index, Stop element) {
        Stop previous = stopProperties.get(index).get();
        stopProperties.get(index).set(element);
        return previous;
    }

    @Override
    public void add(int to, Stop stop) {
        stopProperties.add(to, new SimpleObjectProperty<>(stop));
    }

    @Override
    public Stop remove(int index) {
        return stopProperties.remove(index).get();
    }

    @Override
    public int indexOf(Object o) {
        return stopProperties.indexOf(findMatching(o));
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<Stop> listIterator() {
        return stopProperties.stream().map(ObservableObjectValue::get).collect(Collectors.toList()).listIterator();
    }

    @Override
    public ListIterator<Stop> listIterator(int index) {
        return stopProperties.stream().map(ObservableObjectValue::get).collect(Collectors.toList()).listIterator(index);
    }

    @Override
    public List<Stop> subList(int fromIndex, int toIndex) {
        return stopProperties.stream().map(ObservableObjectValue::get).collect(Collectors.toList()).subList(fromIndex, toIndex);
    }

    public int size() {
        return stopProperties.size();
    }

    @Override
    public boolean isEmpty() {
        return stopProperties.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return findMatching(o).isPresent();
    }

    @Override
    public Iterator<Stop> iterator() {
        return stopProperties.stream().map(ObservableObjectValue::get).collect(Collectors.toList()).iterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    @Override
    public <T> T[] toArray(T[] a) {
        // Borrowed from ArrayList#toArray(T[])
        Object[] nonGenericArray = toArray();
        if (a.length < stopProperties.size())
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(nonGenericArray, stopProperties.size(), a.getClass());
        System.arraycopy(nonGenericArray, 0, a, 0, stopProperties.size());
        if (a.length > stopProperties.size())
            a[stopProperties.size()] = null;
        return a;
    }

}
