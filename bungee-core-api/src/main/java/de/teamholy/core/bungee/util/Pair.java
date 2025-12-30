package de.teamholy.core.bungee.util;

public class Pair<T, U> {
    private final T left;
    private final U right;

    public Pair(T left, U right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return this.left;
    }

    public U getRight() {
        return this.right;
    }
}
