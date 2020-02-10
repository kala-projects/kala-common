package asia.kala.collection;

import asia.kala.Option;
import asia.kala.Tuple2;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Seq<E> extends Traversable<E> {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> Seq<E> narrow(Seq<? extends E> seq) {
        return (Seq<E>) seq;
    }

    default E get(int index) {
        return getOption(index).getOrThrow(() -> new IndexOutOfBoundsException(Integer.toString(index)));
    }

    default boolean isDefinedAt(int index) {
        return index >= 0 && index < size();
    }

    @NotNull
    default Option<E> getOption(int index) {
        if (index < 0) {
            return Option.none();
        }

        int s = knownSize();
        if (s >= 0 && index >= s) {
            return Option.none();
        }

        int i = index;

        for (E e : this) {
            if (i-- == 0) {
                return Option.some(e);
            }
        }
        return Option.none();
    }

    default Seq<E> drop(int n) {
        return SeqOps.drop(this, n, this.<E>newBuilder());
    }

    default Seq<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        return SeqOps.dropWhile(this, predicate, this.<E>newBuilder());
    }

    default Seq<E> concat(@NotNull TraversableOnce<? extends E> traversable) {
        Objects.requireNonNull(traversable);
        return SeqOps.concat(this, traversable, this.<E>newBuilder());
    }

    //
    // -- Traversable
    //

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    <U> CollectionBuilder<U, ? extends Seq<U>> newBuilder();

    /**
     * {@inheritDoc}
     */
    @NotNull
    default Tuple2<? extends Seq<E>, ? extends Seq<E>> span(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.span(this, predicate, this.<E>newBuilder(), this.<E>newBuilder());
    }

    //
    // -- TraversableOnce
    //

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Seq<E> filter(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filter(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Seq<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filterNot(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    default <U> Seq<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return TraversableOps.flatMap(this, mapper, this.<U>newBuilder());
    }

    //
    // -- Functor
    //

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default <U> Seq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return TraversableOps.map(this, mapper, this.<U>newBuilder());
    }
}
