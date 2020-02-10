package asia.kala.collection.mutable;

import asia.kala.collection.Traversable;
import asia.kala.collection.TraversableOnce;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public interface Builder<E, To> extends Growable<E> {
    @Override
    void append(E element);

    void clear();

    To build();

    @Contract("_ -> this")
    default Builder<E, To> addAll(@NotNull Iterable<? extends E> iterable) {
        Objects.requireNonNull(iterable);
        for (E e : iterable) {
            this.append(e);
        }
        return this;
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

    default <NewTo> Builder<E, NewTo> mapResult(@NotNull Function<? super To, ? extends NewTo> mapper) {
        Objects.requireNonNull(mapper);
        return new MappedBuilder<>(this, mapper);
    }
}
