package asia.kala.collection;

import asia.kala.Foldable;
import asia.kala.Option;
import asia.kala.collection.immutable.IList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public interface TraversableOnce<E> extends Iterable<E>, Foldable<E> {
    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> TraversableOnce<E> narrow(TraversableOnce<? extends E> traversable) {
        return (TraversableOnce<E>) traversable;
    }

    @NotNull
    @Override
    Enumerator<E> iterator();

    default Iterable<E> asJava() {
        return this;
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
        return iterator().sameElements(other);
    }

    /**
     * {@inheritDoc}
     */
    default E max() {
        return maxOption().getOrThrow(NoSuchElementException::new);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    default Option<E> maxOption() {
        return maxByOption((Comparator<E>) Comparator.naturalOrder());
    }

    /**
     * {@inheritDoc}
     */
    default E maxBy(@NotNull Comparator<? super E> comparator) {
        return maxByOption(comparator).getOrThrow(NoSuchElementException::new);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    default Option<E> maxByOption(@NotNull Comparator<? super E> comparator) {
        return iterator().maxByOption(comparator);
    }

    /**
     * {@inheritDoc}
     */
    default E min() {
        return minOption().getOrThrow(NoSuchElementException::new);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    default Option<E> minOption() {
        return minByOption((Comparator<E>) Comparator.naturalOrder());
    }

    /**
     * {@inheritDoc}
     */
    default E minBy(@NotNull Comparator<? super E> comparator) {
        return minByOption(comparator).getOrThrow(NoSuchElementException::new);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    default Option<E> minByOption(@NotNull Comparator<? super E> comparator) {
        return iterator().maxByOption(comparator);
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

    default <U> U[] toArray(@NotNull IntFunction<? extends U[]> generator) {
        return iterator().toArray(generator);
    }

    @Override
    default void forEach(@NotNull Consumer<? super E> action) {
        iterator().forEach(action);
    }

    default IList<E> toIList() {
        return IList.from(this);
    }

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
}
