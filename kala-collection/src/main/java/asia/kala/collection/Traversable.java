package asia.kala.collection;

import asia.kala.annotations.Covariant;
import asia.kala.collection.immutable.ImmutableCollection;
import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Traversable<@Covariant E> extends TraversableOnce<E> {

    int SEQ_HASH_MAGIC = -1140647423;

    int SET_HASH_MAGIC = 1045751549;

    @NotNull
    @Contract(pure = true)
    static <E> CollectionFactory<E, ?, ? extends Traversable<E>> factory() {
        return ImmutableCollection.factory();
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
    default Collection<E> asJava() {
        return new JDKConverters.TraversableAsJava<>(this);
    }

    @NotNull
    default <U> CollectionFactory<U, ?, ? extends Traversable<U>> iterableFactory() {
        return factory();
    }
}
