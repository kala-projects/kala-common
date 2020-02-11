package asia.kala.collection;

import asia.kala.Tuple2;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Traversable<E> extends TraversableOnce<E> {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> Traversable<E> narrow(Traversable<? extends E> traversable) {
        return (Traversable<E>) traversable;
    }

    @NotNull <U> CollectionBuilder<U, ? extends Traversable<U>> newBuilder();

    @NotNull
    default Tuple2<? extends Traversable<E>, ? extends Traversable<E>> span(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.span(this, predicate, newBuilder(), newBuilder());
    }

    default String stringPrefix() {
        return this.getClass().getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isTraversableAgain() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Traversable<E> filter(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filter(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Traversable<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filterNot(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default <U> TraversableOnce<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return TraversableOps.flatMap(this, mapper, this.<U>newBuilder());
    }

    @NotNull
    @Override
    <U> Traversable<U> map(@NotNull Function<? super E, ? extends U> mapper);
}
