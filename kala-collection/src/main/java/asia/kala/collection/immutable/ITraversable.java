package asia.kala.collection.immutable;

import asia.kala.collection.Traversable;
import asia.kala.collection.TraversableOnce;
import asia.kala.collection.TraversableOps;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ITraversable<E> extends Traversable<E> {

    //
    // -- Traversable
    //

    @NotNull
    @Override
    <U> CollectionBuilder<U, ? extends ITraversable<U>> newBuilder();

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
    default ITraversable<E> filter(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filter(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default ITraversable<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filterNot(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default <U> ITraversable<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return TraversableOps.flatMap(this, mapper, this.<U>newBuilder());
    }

    //
    // -- Functor
    //

    @NotNull
    @Override
    <U> ITraversable<U> map(@NotNull Function<? super E, ? extends U> mapper);
}
