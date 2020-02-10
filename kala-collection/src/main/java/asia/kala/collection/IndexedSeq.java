package asia.kala.collection;

import asia.kala.Option;
import asia.kala.collection.mutable.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.RandomAccess;
import java.util.function.Function;
import java.util.function.Predicate;

public interface IndexedSeq<E> extends Seq<E>, RandomAccess {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> IndexedSeq<E> narrow(IndexedSeq<? extends E> seq) {
        return (IndexedSeq<E>) seq;
    }

    E get(int index);

    @Override
    int size();

    //
    // -- Seq
    //

    @NotNull
    @Override
    default Option<E> getOption(int index) {
        if (index < 0 || index >= size()) {
            return Option.none();
        }
        return Option.some(get(index));
    }

    //
    // -- Traversable
    //

    @NotNull
    @Override
    <U> Builder<U, ? extends IndexedSeq<U>> newBuilder();

    //
    // -- TraversableOnce
    //

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default IndexedSeq<E> filter(@NotNull Predicate<? super E> predicate) {
        Builder<E, ? extends IndexedSeq<E>> builder = newBuilder();
        builder.sizeHint(this);
        return TraversableOps.filter(this, predicate, builder);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default IndexedSeq<E> filterNot(@NotNull Predicate<? super E> predicate) {
        Builder<E, ? extends IndexedSeq<E>> builder = newBuilder();
        builder.sizeHint(this);
        return TraversableOps.filterNot(this, predicate, builder);
    }

    @Override
    default int knownSize() {
        return size();
    }

    //
    // -- Functor
    //

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default <U> IndexedSeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return TraversableOps.map(this, mapper, this.<U>newBuilder());
    }
}
