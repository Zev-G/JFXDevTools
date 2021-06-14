package com.me.tmw.collections;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface ImplementedList<T> extends List<T> {

    List<T> getList();

    @Override
    default int size() {
        return getList().size();
    }

    @Override
    default boolean isEmpty() {
        return getList().isEmpty();
    }

    @Override
    default boolean contains(Object o) {
        return getList().contains(o);
    }

    @Override
    default Iterator<T> iterator() {
        return getList().iterator();
    }

    @Override
    default Object[] toArray() {
        return getList().toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    default <T1> T1[] toArray(T1[] a) {
        return getList().toArray(a);
    }

    @Override
    default boolean add(T t) {
        return getList().add(t);
    }

    @Override
    default boolean remove(Object o) {
        return getList().remove(o);
    }

    @Override
    default boolean containsAll(Collection<?> c) {
        return getList().containsAll(c);
    }

    @Override
    default boolean addAll(Collection<? extends T> c) {
        return getList().addAll(c);
    }

    @Override
    default boolean addAll(int index, Collection<? extends T> c) {
        return getList().addAll(index, c);
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        return getList().removeAll(c);
    }

    @Override
    default boolean retainAll(Collection<?> c) {
        return getList().retainAll(c);
    }

    @Override
    default void clear() {
        getList().clear();
    }

    @Override
    default T get(int index) {
        return getList().get(index);
    }

    @Override
    default T set(int index, T element) {
        return getList().set(index, element);
    }

    @Override
    default void add(int index, T element) {
        getList().add(index, element);
    }

    @Override
    default T remove(int index) {
        return getList().remove(index);
    }

    @Override
    default int indexOf(Object o) {
        return getList().indexOf(o);
    }

    @Override
    default int lastIndexOf(Object o) {
        return getList().lastIndexOf(o);
    }

    @Override
    default ListIterator<T> listIterator() {
        return getList().listIterator();
    }

    @Override
    default ListIterator<T> listIterator(int index) {
        return getList().listIterator(index);
    }

    @Override
    default List<T> subList(int fromIndex, int toIndex) {
        return getList().subList(fromIndex, toIndex);
    }

}

