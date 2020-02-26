package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.collection.CollectionFactory;
import asia.kala.collection.Seq;
import asia.kala.collection.TraversableOnce;
import asia.kala.collection.mutable.ArrayBuffer;
import asia.kala.function.IndexedFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ISeq<E> extends ICollection<E>, Seq<E> {
    @ApiStatus.Internal
    ISeq.Factory<?> FACTORY = new Factory<>();

    @NotNull
    @SuppressWarnings("unchecked")
    static <E> CollectionFactory<E, ?, ? extends ISeq<E>> factory() {
        return (CollectionFactory<E, ?, ? extends ISeq<E>>) FACTORY;
    }

    static <E> ISeq<E> empty() {
        return ISeq.<E>factory().empty();
    }

    @SafeVarargs
    static <E> ISeq<E> of(E... elements) {
        return ISeq.<E>factory().from(elements);
    }

    static <E> ISeq<E> from(@NotNull E[] elements) {
        return ISeq.<E>factory().from(elements);
    }

    static <E> ISeq<E> from(@NotNull Iterable<? extends E> iterable) {
        return ISeq.<E>factory().from(iterable);
    }

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> ISeq<E> narrow(ISeq<? extends E> seq) {
        return (ISeq<E>) seq;
    }

    @NotNull
    default ISeq<E> updated(int index, E newValue) {
        return AbstractISeq.updated(this, index, newValue, iterableFactory());
    }

    @NotNull
    default ISeq<E> drop(int n) {
        return AbstractISeq.drop(this, n, iterableFactory());
    }

    @NotNull
    default ISeq<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        return AbstractISeq.dropWhile(this, predicate, iterableFactory());
    }

    @NotNull
    default ISeq<E> take(int n) {
        return AbstractISeq.take(this, n, iterableFactory());
    }

    @NotNull
    default ISeq<E> takeWhile(@NotNull Predicate<? super E> predicate) {
        return AbstractISeq.takeWhile(this, predicate, iterableFactory());
    }

    @NotNull
    default ISeq<E> concat(@NotNull Seq<? extends E> other) {
        return AbstractISeq.concat(this, other, iterableFactory());
    }

    @NotNull
    default ISeq<E> prepended(E element) {
        return AbstractISeq.prepended(this, element, iterableFactory());
    }

    @NotNull
    default ISeq<E> prependedAll(@NotNull Iterable<? extends E> prefix) {
        return AbstractISeq.prependedAll(this, prefix, iterableFactory());
    }

    @NotNull
    default ISeq<E> appended(E element) {
        return AbstractISeq.appended(this, element, iterableFactory());
    }

    @NotNull
    default ISeq<E> appendedAll(@NotNull Iterable<? extends E> postfix) {
        return AbstractISeq.prependedAll(this, postfix, iterableFactory());
    }

    @NotNull
    default <U> ISeq<U> mapIndexed(@NotNull IndexedFunction<? super E, ? extends U> mapper) {
        return AbstractISeq.mapIndexed(this, mapper, this.<U>iterableFactory());
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
    default <U> CollectionFactory<U, ?, ? extends ISeq<U>> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    default <U> ISeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return AbstractICollection.map(this, mapper, this.<U>iterableFactory());
    }

    @NotNull
    @Override
    default ISeq<E> filter(@NotNull Predicate<? super E> predicate) {
        return AbstractICollection.filter(this, predicate, iterableFactory());
    }

    @NotNull
    @Override
    default ISeq<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return AbstractICollection.filterNot(this, predicate, iterableFactory());
    }

    @NotNull
    @Override
    default <U> ISeq<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return AbstractICollection.flatMap(this, mapper, iterableFactory());
    }

    @NotNull
    @Override
    default Tuple2<? extends ISeq<E>, ? extends ISeq<E>> span(@NotNull Predicate<? super E> predicate) {
        return AbstractICollection.span(this, predicate, iterableFactory());
    }

    class Factory<E> implements CollectionFactory<E, ArrayBuffer<E>, ISeq<E>> {
        Factory() {
        }

        @Override
        public final ArrayBuffer<E> newBuilder() {
            return new ArrayBuffer<>();
        }

        @Override
        public final void addToBuilder(@NotNull ArrayBuffer<E> buffer, E value) {
            buffer.append(value);
        }

        @Override
        public final ArrayBuffer<E> mergeBuilder(@NotNull ArrayBuffer<E> builder1, @NotNull ArrayBuffer<E> builder2) {
            builder1.appendAll(builder2);
            return builder1;
        }

        @Override
        public final void sizeHint(@NotNull ArrayBuffer<E> buffer, int size) {
            buffer.sizeHint(size);
        }

        @Override
        public final ISeq<E> build(@NotNull ArrayBuffer<E> buffer) {
            switch (buffer.size()) {
                case 0:
                    return ISeq0.instance();
                case 1:
                    return new ISeq1<>(buffer.get(0));
                case 2:
                    return new ISeq2<>(buffer.get(0), buffer.get(1));
                case 3:
                    return new ISeq3<>(buffer.get(0), buffer.get(1), buffer.get(2));
                case 4:
                    return new ISeq4<>(buffer.get(0), buffer.get(1), buffer.get(2), buffer.get(3));
                case 5:
                    return new ISeq5<>(buffer.get(0), buffer.get(1), buffer.get(2), buffer.get(3), buffer.get(4));
                default:
                    return IVector.from(buffer);
            }
        }
    }
}
