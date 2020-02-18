package asia.kala.collection;

import org.jetbrains.annotations.NotNull;

public interface Traversable<E> extends TraversableOnce<E> {
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
}
