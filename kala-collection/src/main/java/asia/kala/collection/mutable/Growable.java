package asia.kala.collection.mutable;

import org.jetbrains.annotations.Contract;

public interface Growable<E> {

    @Contract(mutates = "this")
    void add(E element);

    @Contract(mutates = "this")
    void clear();
}
