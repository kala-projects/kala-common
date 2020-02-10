package asia.kala.collection;

import asia.kala.Foldable;
import asia.kala.Functor;
import asia.kala.Option;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface TraversableOnce<E> extends Iterable<E>, Foldable<E>, Functor<E> {

    default boolean isTraversableAgain() {
        return false;
    }

    @NotNull
    TraversableOnce<E> filter(@NotNull Predicate<? super E> predicate);

    @NotNull
    default TraversableOnce<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return filter(predicate.negate());
    }

    default E max() {
        return iterator().max();
    }

    @NotNull
    default Option<E> maxOption() {
        return iterator().maxOption();
    }

    default E maxBy(@NotNull Comparator<? super E> comparator) {
        return iterator().maxBy(comparator);
    }

    @NotNull
    default Option<E> maxByOption(@NotNull Comparator<? super E> comparator) {
        return iterator().maxByOption(comparator);
    }


    default E min() {
        return iterator().min();
    }

    @NotNull
    default Option<E> minOption() {
        return iterator().minOption();
    }

    default E minBy(@NotNull Comparator<? super E> comparator) {
        return iterator().minBy(comparator);
    }

    @NotNull
    default Option<E> minByOption(@NotNull Comparator<? super E> comparator) {
        return iterator().minByOption(comparator);
    }


    default int knownSize() {
        return -1;
    }

    //
    // -- Functor
    //

    @Override
    @NotNull <U> TraversableOnce<U> map(@NotNull Function<? super E, ? extends U> mapper);


    //
    // -- Foldable
    //

    /**
     * {@inheritDoc}
     */
    @Override
    default <U> U foldLeft(U zero, @NotNull BiFunction<? super U, ? super E, ? extends U> op) {
        return iterator().foldLeft(zero, op);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <U> U foldRight(U zero, @NotNull BiFunction<? super E, ? super U, ? extends U> op) {
        return iterator().foldRight(zero, op);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> reduceLeftOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        return iterator().reduceLeftOption(op);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> reduceRightOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        return iterator().reduceRightOption(op);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default int size() {
        return iterator().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean forall(@NotNull Predicate<? super E> predicate) {
        return iterator().forall(predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean exists(@NotNull Predicate<? super E> predicate) {
        return iterator().exists(predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean contains(Object v) {
        return iterator().contains(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default int count(@NotNull Predicate<? super E> predicate) {
        return iterator().count(predicate);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> find(@NotNull Predicate<? super E> predicate) {
        return iterator().find(predicate);
    }

    //
    // -- Iterable
    //

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    Enumerator<E> iterator();

    /**
     * {@inheritDoc}
     */
    @Override
    default void forEach(@NotNull Consumer<? super E> action) {
        iterator().forEach(action);
    }
}
