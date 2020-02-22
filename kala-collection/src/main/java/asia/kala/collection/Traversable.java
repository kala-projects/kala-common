package asia.kala.collection;

import asia.kala.collection.immutable.IArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Traversable<E> extends TraversableOnce<E> {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> Traversable<E> narrow(Traversable<? extends E> traversable) {
        return (Traversable<E>) traversable;
    }

    @NotNull
    default View<E> view() {
        return new Views.Of<>(this);
    }

    default String className() {
        return "Traversable";
    }

    @Override
    default boolean isTraversableAgain() {
        return true;
    }

    @NotNull
    default <U> CollectionFactory<U, ?, ? extends Traversable<U>> iterableFactory() {
        return IArray.factory(); //TODO
    }
}
