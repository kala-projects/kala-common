package asia.kala.collection;

import asia.kala.collection.mutable.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Traversable<E> extends TraversableOnce<E> {
    static <E, T> T filter(
            @NotNull Traversable<? extends E> collection,
            @NotNull Predicate<? super E> predicate,
            @NotNull Builder<? super E, ? extends T> builder) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(builder);

        for (E e : collection) {
            if (predicate.test(e)) {
                builder.addOne(e);
            }
        }

        return builder.build();
    }

    static <E, T> T filterNot(
            @NotNull Traversable<? extends E> collection,
            @NotNull Predicate<? super E> predicate,
            @NotNull Builder<? super E, ? extends T> builder) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(builder);

        for (E e : collection) {
            if (!predicate.test(e)) {
                builder.addOne(e);
            }
        }

        return builder.build();
    }

    static <E, U, T> T map(
            @NotNull Traversable<? extends E> collection,
            @NotNull Function<? super E, ? extends U> mapper,
            @NotNull Builder<? super U, ? extends T> builder
    ) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(builder);

        builder.sizeHint(collection);

        for (E e : collection) {
            builder.addOne(mapper.apply(e));
        }
        return builder.build();
    }

    @NotNull <U> Builder<U, ? extends Traversable<U>> newBuilder();

    //
    // -- TraversableOnce
    //


    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isTraversableAgain() {
        return true;
    }

    @NotNull
    @Override
    default Traversable<E> filter(@NotNull Predicate<? super E> predicate) {
        return Traversable.filter(this, predicate, newBuilder());
    }

    @NotNull
    @Override
    default Traversable<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return Traversable.filterNot(this, predicate, newBuilder());
    }

    @Override
    default int knownSize() {
        return size();
    }

    //
    // -- Functor
    //

    @NotNull
    @Override
    <U> Traversable<U> map(@NotNull Function<? super E, ? extends U> mapper);
}
