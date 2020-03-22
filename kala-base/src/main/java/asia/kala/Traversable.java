package asia.kala;

import asia.kala.annotations.Covariant;
import asia.kala.control.Option;
import asia.kala.factory.CollectionFactory;
import asia.kala.util.Iterators;
import asia.kala.util.JavaArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;

@SuppressWarnings("unchecked")
public interface Traversable<@Covariant E>
        extends Iterable<E>, Foldable<E> {

    @SuppressWarnings("unchecked")
    @Contract(value = "_ -> param1", pure = true)
    static <E> Traversable<E> narrow(Traversable<? extends E> traversable) {
        return (Traversable<E>) traversable;
    }

    @NotNull
    Iterator<E> iterator();

    default boolean isEmpty() {
        final int size = knownSize();
        if (size < 0) {
            return !iterator().hasNext();
        } else {
            return size == 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default int size() {
        return Iterators.size(iterator());
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
        final int os;
        if (other instanceof Traversable<?>) {
            os = ((Traversable<?>) other).knownSize();
        } else if (other instanceof java.util.Collection<?>) {
            os = ((Collection<?>) other).size();
        } else {
            os = -1;
        }

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

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean contains(E value) {
        return Iterators.contains(iterator(), value);
    }

    default boolean containsAll(E @NotNull [] values) {
        for (E value : values) {
            if (!contains(value)) {
                return false;
            }
        }
        return true;
    }

    default boolean containsAll(@NotNull Iterable<? extends E> values) {
        for (E value : values) {
            if (!contains(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean forall(@NotNull Predicate<? super E> predicate) {
        return Iterators.forall(iterator(), predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean exists(@NotNull Predicate<? super E> predicate) {
        return Iterators.exists(iterator(), predicate);
    }

    @NotNull
    @Override
    default Option<E> find(@NotNull Predicate<? super E> predicate) {
        for (E e : this) {
            if (predicate.test(e)) {
                return Option.some(e);
            }
        }
        return Option.none();
    }

    default boolean sameElements(@NotNull Iterable<?> other) {
        return Iterators.sameElements(iterator(), other.iterator());
    }

    default E max() {
        return max((Comparator<E>) Comparator.naturalOrder());
    }

    default E max(@NotNull Comparator<? super E> comparator) {
        return maxOption(comparator).getOrThrow(NoSuchElementException::new);
    }

    @Nullable
    default E maxOrNull() {
        return maxOrNull((Comparator<E>) Comparator.naturalOrder());
    }

    @Nullable
    default E maxOrNull(@NotNull Comparator<? super E> comparator) {
        return maxOption(comparator).getOrNull();
    }

    @NotNull
    default Option<E> maxOption() {
        return maxOption((Comparator<E>) Comparator.naturalOrder());
    }

    @NotNull
    default Option<E> maxOption(@NotNull Comparator<? super E> comparator) {
        return Iterators.maxOption(iterator(), comparator);
    }

    default E min() {
        return min((Comparator<E>) Comparator.naturalOrder());
    }

    default E min(@NotNull Comparator<? super E> comparator) {
        return minOption(comparator).getOrThrow(NoSuchElementException::new);
    }

    @Nullable
    default E minOrNull() {
        return minOrNull((Comparator<E>) Comparator.naturalOrder());
    }

    @Nullable
    default E minOrNull(@NotNull Comparator<? super E> comparator) {
        return minOption(comparator).getOrNull();
    }

    @NotNull
    default Option<E> minOption() {
        return minOption((Comparator<E>) Comparator.naturalOrder());
    }

    @NotNull
    default Option<E> minOption(@NotNull Comparator<? super E> comparator) {
        return Iterators.minOption(iterator(), comparator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <U> U foldLeft(U zero, @NotNull BiFunction<? super U, ? super E, ? extends U> op) {
        return Iterators.foldLeft(iterator(), zero, op);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <U> U foldRight(U zero, @NotNull BiFunction<? super E, ? super U, ? extends U> op) {
        return Iterators.foldRight(iterator(), zero, op);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> reduceLeftOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        return Iterators.reduceLeftOption(iterator(), op);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> reduceRightOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        return Iterators.reduceRightOption(iterator(), op);
    }

    default <R> R collectTo(@NotNull Collector<? super E, ?, ? extends R> collector) {
        return Iterators.collectTo(iterator(), collector);
    }

    default <R> R collectTo(@NotNull CollectionFactory<? super E, ?, ? extends R> factory) {
        return Iterators.collectTo(iterator(), factory);
    }

    @NotNull
    default <U /*super E*/> U[] toArray(@NotNull Class<U> type) {
        return toArray(JavaArray.generator(type));
    }

    @NotNull
    default <U /*super E*/> U[] toArray(@NotNull IntFunction<U[]> generator) {
        int s = knownSize();
        if (s == 0) {
            return generator.apply(0);
        } else if (s > 0) {
            U[] arr = generator.apply(s);
            int i = 0;
            for (E e : this) {
                arr[i++] = (U) e;
            }
            return arr;
        } else {
            return Iterators.toArray((Iterator<U>) iterator(), generator);
        }
    }

    @NotNull
    default Object[] toObjectArray() {
        return toArray(Object[]::new);
    }

    default <A extends Appendable> A joinTo(@NotNull A buffer) {
        return joinTo(buffer, ", ");
    }

    default <A extends Appendable> A joinTo(@NotNull A buffer, CharSequence separator) {
        return joinTo(buffer, separator, "", "");
    }

    @NotNull
    @Contract(value = "_, _, _, _ -> param1", mutates = "param1")
    default <A extends Appendable> A joinTo(
            @NotNull A buffer,
            CharSequence separator, CharSequence prefix, CharSequence postfix
    ) {
        return Iterators.joinTo(iterator(), buffer, separator, prefix, postfix);
    }

    default String joinToString() {
        return joinTo(new StringBuilder()).toString();
    }

    default String joinToString(CharSequence separator) {
        return joinTo(new StringBuilder(), separator).toString();
    }

    default String joinToString(CharSequence separator, CharSequence prefix, CharSequence postfix) {
        return joinTo(new StringBuilder(), separator, prefix, postfix).toString();
    }

    @Override
    default void forEach(@NotNull Consumer<? super E> action) {
        for (E e : this) {
            action.accept(e);
        }
    }
}
