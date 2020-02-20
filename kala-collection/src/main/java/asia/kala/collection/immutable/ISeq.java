package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.collection.Seq;
import asia.kala.collection.TraversableOnce;
import asia.kala.collection.mutable.CollectionBuilder;
import asia.kala.function.IndexedFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ISeq<E> extends ICollection<E>, Seq<E> {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> ISeq<E> narrow(ISeq<? extends E> seq) {
        return (ISeq<E>) seq;
    }

    @NotNull
    default ISeq<E> updated(int index, E newValue) {
        return AbstractISeq.updated(this, index, newValue, newBuilder());
    }

    @NotNull
    default ISeq<E> drop(int n) {
        return AbstractISeq.drop(this, n, this.<E>newBuilder());
    }

    @NotNull
    default ISeq<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        return AbstractISeq.dropWhile(this, predicate, this.<E>newBuilder());
    }

    @NotNull
    default ISeq<E> take(int n) {
        return AbstractISeq.take(this, n, this.<E>newBuilder());
    }

    @NotNull
    default ISeq<E> takeWhile(@NotNull Predicate<? super E> predicate) {
        return AbstractISeq.takeWhile(this, predicate, this.<E>newBuilder());
    }

    @NotNull
    default ISeq<E> concat(@NotNull TraversableOnce<? extends E> traversable) {
        Objects.requireNonNull(traversable);
        return AbstractISeq.concat(this, traversable, this.<E>newBuilder());
    }

    @NotNull
    default ISeq<E> prepended(E element) {
        return AbstractISeq.prepended(this, element, newBuilder());
    }

    @NotNull
    default ISeq<E> prependedAll(@NotNull TraversableOnce<? extends E> prefix) {
        return AbstractISeq.prependedAll(this, prefix, newBuilder());
    }

    @NotNull
    default ISeq<E> appended(E element) {
        return AbstractISeq.appended(this, element, newBuilder());
    }

    @NotNull
    default ISeq<E> appendedAll(@NotNull TraversableOnce<? extends E> postfix) {
        return AbstractISeq.prependedAll(this, postfix, newBuilder());
    }

    @NotNull
    default <U> ISeq<U> mapIndexed(@NotNull IndexedFunction<? super E, ? extends U> mapper) {
        return AbstractISeq.mapIndexed(this, mapper, this.<U>newBuilder());
    }

    //
    // -- ICollection
    //

    @Override
    default String className() {
        return "ISeq";
    }

    @NotNull
    @Override
    <U> CollectionBuilder<U, ? extends ISeq<U>> newBuilder();

    @NotNull
    @Override
    default <U> ISeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return AbstractICollection.map(this, mapper, this.<U>newBuilder());
    }

    @NotNull
    @Override
    default ISeq<E> filter(@NotNull Predicate<? super E> predicate) {
        return AbstractICollection.filter(this, predicate, newBuilder());
    }

    @NotNull
    @Override
    default ISeq<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return AbstractICollection.filterNot(this, predicate, newBuilder());
    }

    @NotNull
    @Override
    default <U> ISeq<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return AbstractICollection.flatMap(this, mapper, this.newBuilder());
    }

    @NotNull
    @Override
    default Tuple2<? extends ISeq<E>, ? extends ISeq<E>> span(@NotNull Predicate<? super E> predicate) {
        return AbstractICollection.span(this, predicate, newBuilder(), newBuilder());
    }
}
