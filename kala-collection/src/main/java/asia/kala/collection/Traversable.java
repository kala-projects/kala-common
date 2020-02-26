package asia.kala.collection;

import asia.kala.collection.immutable.IArray;
import asia.kala.collection.immutable.ICollection;
import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Traversable<E> extends TraversableOnce<E> {

    @NotNull
    @Contract(pure = true)
    static <E> CollectionFactory<E, ?, ? extends Traversable<E>> factory() {
        return ICollection.factory();
    }

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> Traversable<E> narrow(Traversable<? extends E> traversable) {
        return (Traversable<E>) traversable;
    }

    @NotNull
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(iterator(), size(), 0);
    }

    @NotNull
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @NotNull
    default Stream<E> parallelStream() {
        return stream().parallel();
    }

    @NotNull
    default View<E> view() {
        return new Views.Of<>(this);
    }

    default String className() {
        return "Traversable";
    }

    @Override
    default boolean isTraversableAgain() {
        return true;
    }

    @NotNull
    @ReadOnly
    @Override
    default Iterable<E> asJava() {
        return this;
    }

    @NotNull
    default <U> CollectionFactory<U, ?, ? extends Traversable<U>> iterableFactory() {
        return factory();
    }
}
