package asia.kala.collection;

import asia.kala.Foldable;
import asia.kala.Functor;
import asia.kala.Option;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface TraversableOnce<E> extends Iterable<E>, Foldable<E>, Functor<E> {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> TraversableOnce<E> narrow(TraversableOnce<? extends E> traversable) {
        return (TraversableOnce<E>) traversable;
    }

    default boolean isTraversableAgain() {
        return false;
    }

    default boolean isEmpty() {
        return iterator().isEmpty();
    }

    default int knownSize() {
        return -1;
    }

    default boolean sameElements(@NotNull TraversableOnce<?> other) {
        return this.iterator().sameElements(other);
    }

    @NotNull
    TraversableOnce<E> filter(@NotNull Predicate<? super E> predicate);

    @NotNull
    default TraversableOnce<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return filter(predicate.negate());
    }

    <U> TraversableOnce<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper);

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

    @NotNull
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default <A extends Appendable> A joinTo(@NotNull A buffer) {
        return joinTo(buffer, ", ");
    }

    default <A extends Appendable> A joinTo(@NotNull A buffer, @NotNull CharSequence separator) {
        return joinTo(buffer, separator, "", "");
    }

    default <A extends Appendable> A joinTo(
            @NotNull A buffer,
            @NotNull CharSequence separator,
            @NotNull CharSequence prefix,
            @NotNull CharSequence postfix) {
        return iterator().joinTo(buffer, prefix, separator, postfix);
    }

    default String joinToString() {
        return joinTo(new StringBuilder()).toString();
    }

    default String joinToString(@NotNull CharSequence separator) {
        return joinTo(new StringBuilder(), separator).toString();
    }

    default String joinToString(
            @NotNull CharSequence separator,
            @NotNull CharSequence prefix,
            @NotNull CharSequence postfix) {
        return joinTo(new StringBuilder(), prefix, separator, postfix).toString();
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
