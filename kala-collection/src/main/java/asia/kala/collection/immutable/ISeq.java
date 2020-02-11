package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.collection.*;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ISeq<E> extends ITraversable<E>, Seq<E> {

    //
    // -- Seq
    //

    @NotNull
    @Override
    default ISeq<E> updated(int index, E newValue) {
        return SeqOps.updated(this, index, newValue, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    default ISeq<E> drop(int n) {
        return SeqOps.drop(this, n, this.<E>newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    default ISeq<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        return SeqOps.dropWhile(this, predicate, this.<E>newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    default ISeq<E> concat(@NotNull TraversableOnce<? extends E> traversable) {
        Objects.requireNonNull(traversable);
        return SeqOps.concat(this, traversable, this.<E>newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default ISeq<E> prepended(E element) {
        return SeqOps.prepended(this, element, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default ISeq<E> prependedAll(@NotNull TraversableOnce<? extends E> prefix) {
        return SeqOps.prependedAll(this, prefix, newBuilder());
    }

    @NotNull
    @Override
    default ISeq<E> appended(E element) {
        return SeqOps.appended(this, element, newBuilder());
    }

    @NotNull
    @Override
    default ISeq<E> appendedAll(@NotNull TraversableOnce<? extends E> prefix) {
        return SeqOps.prependedAll(this, prefix, newBuilder());
    }

    //
    // -- Traversable
    //

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    <U> CollectionBuilder<U, ? extends ISeq<U>> newBuilder();

    /**
     * {@inheritDoc}
     */
    @NotNull
    default Tuple2<? extends ISeq<E>, ? extends ISeq<E>> span(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.span(this, predicate, this.<E>newBuilder(), this.<E>newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default ISeq<E> filter(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filter(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default ISeq<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filterNot(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default <U> ISeq<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
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
    default <U> ISeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return TraversableOps.map(this, mapper, this.<U>newBuilder());
    }
}
