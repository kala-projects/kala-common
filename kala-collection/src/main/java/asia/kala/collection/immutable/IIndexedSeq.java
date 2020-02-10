package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.collection.IndexedSeq;
import asia.kala.collection.SeqOps;
import asia.kala.collection.TraversableOnce;
import asia.kala.collection.TraversableOps;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface IIndexedSeq<E> extends ISeq<E>, IndexedSeq<E> {
    //
    // -- Seq
    //

    @NotNull
    default IIndexedSeq<E> drop(int n) {
        return SeqOps.drop(this, n, this.<E>newBuilder());
    }

    @NotNull
    default IIndexedSeq<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        return SeqOps.dropWhile(this, predicate, this.<E>newBuilder());
    }

    @NotNull
    default IIndexedSeq<E> concat(@NotNull TraversableOnce<? extends E> traversable) {
        Objects.requireNonNull(traversable);
        return SeqOps.concat(this, traversable, this.<E>newBuilder());
    }

    //
    // -- Traversable
    //

    @NotNull
    @Override
    <U> CollectionBuilder<U, ? extends IIndexedSeq<U>> newBuilder();

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Tuple2<? extends IIndexedSeq<E>, ? extends IIndexedSeq<E>> span(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.span(this, predicate, this.<E>newBuilder(), this.<E>newBuilder());
    }

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
    default IIndexedSeq<E> filter(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filter(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default IIndexedSeq<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filterNot(this, predicate, newBuilder());
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
    default <U> IIndexedSeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return TraversableOps.map(this, mapper, this.<U>newBuilder());
    }
}
