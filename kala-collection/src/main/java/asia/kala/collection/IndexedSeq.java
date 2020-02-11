package asia.kala.collection;

import asia.kala.Option;
import asia.kala.Tuple2;
import asia.kala.collection.mutable.CollectionBuilder;
import asia.kala.collection.mutable.MSeq;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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

    @NotNull
    @Override
    default IndexedSeq<E> drop(int n) {
        return SeqOps.drop(this, n, this.<E>newBuilder());
    }

    @NotNull
    @Override
    default IndexedSeq<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        return SeqOps.dropWhile(this, predicate, this.<E>newBuilder());
    }

    @NotNull
    @Override
    default IndexedSeq<E> concat(@NotNull TraversableOnce<? extends E> traversable) {
        Objects.requireNonNull(traversable);
        return SeqOps.concat(this, traversable, this.<E>newBuilder());
    }

    @NotNull
    @Override
    default IndexedSeq<E> prepended(E element) {
        return SeqOps.prepended(this, element, newBuilder());
    }

    @NotNull
    @Override
    default IndexedSeq<E> prependedAll(@NotNull TraversableOnce<? extends E> prefix) {
        return SeqOps.prependedAll(this, prefix, newBuilder());
    }

    @NotNull
    @Override
    default IndexedSeq<E> appended(E element) {
        return SeqOps.appended(this, element, newBuilder());
    }

    @NotNull
    @Override
    default IndexedSeq<E> appendedAll(@NotNull TraversableOnce<? extends E> prefix) {
        return SeqOps.prependedAll(this, prefix, newBuilder());
    }

    //
    // -- Traversable
    //

    @NotNull
    @Override
    <U> CollectionBuilder<U, ? extends IndexedSeq<U>> newBuilder();

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Tuple2<? extends IndexedSeq<E>, ? extends IndexedSeq<E>> span(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.span(this, predicate, this.<E>newBuilder(), this.<E>newBuilder());
    }

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
        return TraversableOps.filter(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default IndexedSeq<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filterNot(this, predicate, newBuilder());
    }

    @Override
    @NotNull
    default <U> IndexedSeq<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return TraversableOps.flatMap(this, mapper, newBuilder());
    }

    @Override
    default int knownSize() {
        return size();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default <U> IndexedSeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return TraversableOps.map(this, mapper, this.<U>newBuilder());
    }
}
