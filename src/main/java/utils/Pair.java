package main.java.utils;

public class Pair<K, V> {

    private final K xCoordinate;
    private final V yCoordinate;

    public static <K, V> Pair<K, V> createPair(K xCoordinate, V yCoordinate) {
        return new Pair<K, V>(xCoordinate, yCoordinate);
    }

    public Pair(K element0, V element1) {
        this.xCoordinate = element0;
        this.yCoordinate = element1;
    }

    public K getLeft() {
        return xCoordinate;
    }

    public V getRight() {
        return yCoordinate;
    }

}
