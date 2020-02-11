package asia.kala.collection.mutable;

import asia.kala.collection.Traversable;
import asia.kala.collection.TraversableOnce;
import asia.kala.collection.TraversableOps;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public interface MTraversable<E> extends Traversable<E> {
    //
    // -- Traversable
    //

    @NotNull
    @Override
    <U> CollectionBuilder<U, ? extends MTraversable<U>> newBuilder();

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
    default MTraversable<E> filter(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filter(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default MTraversable<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return TraversableOps.filterNot(this, predicate, newBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default <U> MTraversable<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return TraversableOps.flatMap(this, mapper, this.<U>newBuilder());
    }

    @NotNull
    @Override
    <U> MTraversable<U> map(@NotNull Function<? super E, ? extends U> mapper);
}
