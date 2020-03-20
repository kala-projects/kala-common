package asia.kala.collection;

import asia.kala.Foldable;
import asia.kala.control.Option;
import asia.kala.annotations.Covariant;
import asia.kala.collection.immutable.ImmutableArray;
import asia.kala.collection.immutable.ImmutableList;
import asia.kala.collection.immutable.ImmutableSeq;
import asia.kala.collection.immutable.ImmutableVector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.*;
import java.util.stream.Collector;

@SuppressWarnings("unchecked")
public interface TraversableOnce<@Covariant E> extends Iterable<E>, Foldable<E> {

    //region Narrow method

    @Contract(value = "_ -> param1", pure = true)
    @SuppressWarnings("unchecked")
    static <E> TraversableOnce<E> narrow(TraversableOnce<? extends E> traversable) {
        return (TraversableOnce<E>) traversable;
    }

    //endregion

    @NotNull
    @Override
    Enumerator<E> iterator();

    @NotNull
    default Object asJava() {
        return this;
    }

    default boolean isTraversableAgain() {
        return false;
    }

    default boolean isEmpty() {
        return iterator().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default int size() {
        return iterator().size();
    }

    @Range(from = -1, to = Integer.MAX_VALUE)
    default int knownSize() {
        return -1;
    }

    //region Size compare operators

    default int sizeCompare(int otherSize) {
        if (otherSize < 0) {
            return 1;
        }
        final int knownSize = knownSize();
        if (knownSize >= 0) {
            return Integer.compare(knownSize, otherSize);
        }
        int i = 0;
        for (E e : this) {
            if (i == otherSize) {
                return 1;
            }
            ++i;
        }
        return i - otherSize;
    }

    default int sizeCompare(@NotNull Iterable<?> other) {
        final int os = KalaCollectionUtils.knowSize(other);
        if (os >= 0) {
            return sizeCompare(os);
        }
        int ks = this.knownSize();
        if (ks == 0) {
            return other.iterator().hasNext() ? -1 : 0;
        } else if (ks > 0) {
            Iterator<?> it = other.iterator();
            while (it.hasNext()) {
                it.next();
                --ks;
                if (ks == 0) {
                    return it.hasNext() ? -1 : 0;
                }
            }
            return 1;
        }

        Iterator<?> it1 = this.iterator();
        Iterator<?> it2 = other.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            it1.next();
            it2.next();
        }

        if (it1.hasNext()) {
            return 1;
        }
        if (it2.hasNext()) {
            return -1;
        }
        return 0;
    }

    default boolean sizeIs(int otherSize) {
        return sizeCompare(otherSize) == 0;
    }

    default boolean sizeEquals(int otherSize) {
        return sizeIs(otherSize);
    }

    default boolean sizeEquals(@NotNull Iterable<?> other) {
        return sizeCompare(other) == 0;
    }

    default boolean sizeLessThan(int otherSize) {
        return sizeCompare(otherSize) < 0;
    }

    default boolean sizeLessThan(@NotNull Iterable<?> other) {
        return sizeCompare(other) < 0;
    }

    default boolean sizeLessThanOrEquals(int otherSize) {
        return sizeCompare(otherSize) <= 0;
    }

    default boolean sizeLessThanOrEquals(@NotNull Iterable<?> other) {
        return sizeCompare(other) <= 0;
    }

    default boolean sizeGreaterThan(int otherSize) {
        return sizeCompare(otherSize) > 0;
    }

    default boolean sizeGreaterThan(@NotNull Iterable<?> other) {
        return sizeCompare(other) > 0;
    }

    default boolean sizeGreaterThanOrEquals(int otherSize) {
        return sizeCompare(otherSize) >= 0;
    }

    default boolean sizeGreaterThanOrEquals(@NotNull Iterable<?> other) {
        return sizeCompare(other) >= 0;
    }

    //endregion

    default boolean sameElements(@NotNull Iterable<?> other) {
        return iterator().sameElements(other);
    }

    /**
     * {@inheritDoc}
     */
    default E max() {
        return maxBy((Comparator<E>) Comparator.naturalOrder());
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
    default Option<E> maxOption() {
        return maxByOption((Comparator<E>) Comparator.naturalOrder());
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
        return minBy((Comparator<E>) Comparator.naturalOrder());
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
    default Option<E> minOption() {
        return minByOption((Comparator<E>) Comparator.naturalOrder());
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

    @NotNull
    @Contract(value = "_, _, _, _ -> param1", mutates = "param1")
    default <A extends Appendable> A joinTo(
            @NotNull A buffer,
            CharSequence separator, CharSequence prefix, CharSequence postfix
    ) {
        return iterator().joinTo(buffer, prefix, separator, postfix);
    }

    default String joinToString() {
        return joinTo(new StringBuilder()).toString();
    }

    default String joinToString(@NotNull CharSequence separator) {
        return joinTo(new StringBuilder(), separator).toString();
    }

    default String joinToString(CharSequence separator, CharSequence prefix, CharSequence postfix) {
        return joinTo(new StringBuilder(), prefix, separator, postfix).toString();
    }

    default <R> R collectTo(@NotNull Collector<? super E, ?, ? extends R> collector) {
        return AbstractTraversable.collectTo(this, collector);
    }

    default <R> R collectTo(@NotNull CollectionFactory<? super E, ?, ? extends R> factory) {
        return AbstractTraversable.collectTo(this, factory);
    }

    @Override
    default void forEach(@NotNull Consumer<? super E> action) {
        iterator().forEach(action);
    }

    @NotNull
    default Object[] toObjectArray() {
        return toArray(Object[]::new);
    }

    @NotNull
    default E[] toArray(@NotNull Class<E> type) {
        return toArray(JavaArray.generator(type));
    }

    @NotNull
    default <U> U[] toArray(@NotNull IntFunction<? extends U[]> generator) {
        int s = knownSize();
        if (s > 0) {
            U[] arr = generator.apply(s);
            int i = 0;
            for (E e : this) {
                arr[i] = (U) e;
            }
            return arr;
        }
        return iterator().toArray(generator);
    }

    @NotNull
    default Seq<E> toSeq() {
        return toImmutableSeq();
    }

    @NotNull
    default ImmutableSeq<E> toImmutableSeq() {
        return toImmutableVector();
    }

    @NotNull
    default ImmutableArray<E> toImmutableArray() {
        return (ImmutableArray<E>) ImmutableArray.Unsafe.wrap(toObjectArray());
    }

    @NotNull
    default ImmutableList<E> toImmutableList() {
        return ImmutableList.from(this);
    }

    @NotNull
    default ImmutableVector<E> toImmutableVector() {
        return ImmutableVector.from(this);
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
    default boolean contains(Object value) {
        return iterator().contains(value);
    }

    default boolean containsAll(@NotNull Iterable<?> values) {
        Objects.requireNonNull(values);
        TraversableOnce<?> t = this;
        if (!isTraversableAgain()) {
            t = this.toImmutableVector();
        }

        for (Object value : values) {
            if (!t.contains(value)) {
                return false;
            }
        }
        return true;
    }

    default boolean containsAll(@NotNull Object[] values) {
        Objects.requireNonNull(values);
        return containsAll(ArraySeq.wrap(values));
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
