package asia.kala.collection.mutable;

import asia.kala.collection.Traversable;
import asia.kala.collection.TraversableOnce;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public interface CollectionBuilder<E, To> extends Growable<E> {

    @Override
    @Contract(mutates = "this")
    void add(E element);

    @Contract(mutates = "this")
    void clear();

    To build();

    @Contract(mutates = "this")
    default void addAll(@NotNull Iterable<? extends E> iterable) {
        Objects.requireNonNull(iterable);
        for (E e : iterable) {
            this.add(e);
        }
    }

    default void sizeHint(int site) {
    }

    default void sizeHint(@NotNull TraversableOnce<?> t) {
        sizeHint(t, 0);
    }

    default void sizeHint(@NotNull TraversableOnce<?> t, int delta) {
        Objects.requireNonNull(t);
        int s = t.knownSize();
        if (s != -1) {
            this.sizeHint(s + delta);
        }
    }

    default void sizeHintBounded(int size, @NotNull Traversable<?> boundingColl) {
        int c = boundingColl.knownSize();
        if (c != -1) {
            sizeHint(Integer.min(c, size));
        }
    }

    default <NewTo> CollectionBuilder<E, NewTo> mapResult(@NotNull Function<? super To, ? extends NewTo> mapper) {
        Objects.requireNonNull(mapper);
        return new MappedCollectionBuilder<>(this, mapper);
    }
}
