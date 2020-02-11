package asia.kala.collection.mutable;

import asia.kala.Tuple2;
import asia.kala.collection.Seq;
import asia.kala.collection.SeqOps;
import asia.kala.collection.TraversableOnce;
import asia.kala.collection.TraversableOps;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MSeq<E> extends MTraversable<E>, Seq<E> {

    @Contract(mutates = "this")
    void set(int index, E newValue);

    //
    // -- Seq
    //

    /**
     * {@inheritDoc}
     */
    @NotNull
    default MSeq<E> drop(int n) {
        return SeqOps.drop(this, n, this.<E>newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    default MSeq<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        return SeqOps.dropWhile(this, predicate, this.<E>newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    default MSeq<E> concat(@NotNull TraversableOnce<? extends E> traversable) {
        Objects.requireNonNull(traversable);
        return SeqOps.concat(this, traversable, this.<E>newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default MSeq<E> prepended(E element) {
        return SeqOps.prepended(this, element, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default MSeq<E> prependedAll(@NotNull TraversableOnce<? extends E> prefix) {
        return SeqOps.prependedAll(this, prefix, newBuilder());
    }

    @NotNull
    @Override
    default MSeq<E> appended(E element) {
        return SeqOps.appended(this, element, newBuilder());
    }

    @NotNull
    @Override
    default MSeq<E> appendedAll(@NotNull TraversableOnce<? extends E> prefix) {
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
    <U> CollectionBuilder<U, ? extends MSeq<U>> newBuilder();

    /**
     * {@inheritDoc}
     */
    @NotNull
    default Tuple2<? extends MSeq<E>, ? extends MSeq<E>> span(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.span(this, predicate, this.<E>newBuilder(), this.<E>newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default MSeq<E> filter(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filter(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default MSeq<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filterNot(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default <U> MSeq<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
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
    default <U> MSeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return TraversableOps.map(this, mapper, this.<U>newBuilder());
    }
}
