package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.collection.CollectionFactory;
import asia.kala.collection.Transformable;
import asia.kala.collection.Traversable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ImmutableCollection<E> extends Traversable<E>, Transformable<E> {
    static <E> CollectionFactory<E, ?, ? extends ImmutableCollection<E>> factory() {
        return ImmutableSeq.factory();
    }

    @SafeVarargs
    static <E> ImmutableCollection<E> of(E... elements) {
        return ImmutableCollection.<E>factory().from(elements);
    }

    static <E> ImmutableCollection<E> from(@NotNull E[] elements) {
        return ImmutableCollection.<E>factory().from(elements);
    }

    static <E> ImmutableCollection<E> from(@NotNull Iterable<? extends E> iterable) {
        return ImmutableCollection.<E>factory().from(iterable);
    }

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> ImmutableCollection<E> narrow(ImmutableCollection<? extends E> collection) {
        return (ImmutableCollection<E>) collection;
    }

    @Override
    default String className() {
        return "ImmutableCollection";
    }

    @NotNull
    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(iterator(), size(), Spliterator.IMMUTABLE);
    }

    @NotNull
    @Override
    @Contract(pure = true)
    default <U> CollectionFactory<U, ?, ? extends ImmutableCollection<U>> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    @Contract(pure = true)
    default <U> ImmutableCollection<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return AbstractImmutableCollection.map(this, mapper, this.<U>iterableFactory());
    }

    @NotNull
    @Override
    @Contract(pure = true)
    default ImmutableCollection<E> filter(@NotNull Predicate<? super E> predicate) {
        return AbstractImmutableCollection.filter(this, predicate, iterableFactory());
    }

    @NotNull
    @Override
    @Contract(pure = true)
    default ImmutableCollection<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return AbstractImmutableCollection.filterNot(this, predicate, iterableFactory());
    }

    @NotNull
    @Override
    @Contract(pure = true)
    default <U> ImmutableCollection<U> flatMap(@NotNull Function<? super E, ? extends Iterable<? extends U>> mapper) {
        return AbstractImmutableCollection.flatMap(this, mapper, iterableFactory());
    }

    @NotNull
    @Override
    @Contract(pure = true)
    default Tuple2<? extends ImmutableCollection<E>, ? extends ImmutableCollection<E>> span(@NotNull Predicate<? super E> predicate) {
        return AbstractImmutableCollection.span(this, predicate, iterableFactory());
    }
}
