package asia.kala.collection;

import asia.kala.collection.mutable.Builder;
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

    @NotNull <U> Builder<U, ? extends Traversable<U>> newBuilder();

    default String stringPrefix() {
        return this.getClass().getSimpleName();
    }

    //
    // -- TraversableOnce
    //

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

    //
    // -- Functor
    //

    @NotNull
    @Override
    <U> Traversable<U> map(@NotNull Function<? super E, ? extends U> mapper);
}
