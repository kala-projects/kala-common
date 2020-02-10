package asia.kala.collection.mutable;

import org.jetbrains.annotations.Contract;

public interface Growable<E> {
    @Contract("_ -> this")
    Growable<E> addOne(E element);

    void clear();
}
