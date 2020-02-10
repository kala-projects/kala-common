package asia.kala.collection.mutable;

public interface Growable<E> {
    void append(E element);

    void clear();
}
