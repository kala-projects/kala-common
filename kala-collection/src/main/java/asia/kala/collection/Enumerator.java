package asia.kala.collection;

import asia.kala.Option;
import asia.kala.collection.immutable.IList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.*;

@SuppressWarnings("unchecked")
public interface Enumerator<E> extends Iterator<E>, TraversableOnce<E> {

    @NotNull
    static <E> Enumerator<E> fromJava(@NotNull Iterator<E> iterator) {
        Objects.requireNonNull(iterator);
        if (iterator instanceof Enumerator<?>) {
            return (Enumerator<E>) iterator;
        }
        return new Enumerators.IteratorWrapper<>(iterator);
    }

    static <E> Enumerator<E> empty() {
        return (Enumerator<E>) Enumerators.Empty.INSTANCE;
    }

    static <E> Enumerator<E> of() {
        return empty();
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    static <E> Enumerator<E> of(E element) {
        return new Enumerators.Single<>(element);
    }

    @NotNull
    static <E> Enumerator<E> of(@NotNull E... elements) {
        Objects.requireNonNull(elements);
        int l = elements.length;
        if (l == 0) {
            return empty();
        }
        if (l == 1) {
            return new Enumerators.Single<>(elements[0]);
        }
        return new Enumerators.OfArray<>(elements, 0, elements.length);
    }

    /**
     * {@inheritDoc}
     */
    boolean hasNext();

    /**
     * {@inheritDoc}
     */
    E next();

    default Option<E> nextOption() {
        if (hasNext()) {
            return Option.some(next());
        }
        return Option.none();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("-> this")
    default Enumerator<E> iterator() {
        return this;
    }

    @Override
    @Deprecated
    @Contract("-> fail")
    default void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Enumerator.remove");
    }

    @NotNull
    default Enumerator<E> drop(int n) {
        while (n > 0 && hasNext()) {
            next();
            --n;
        }
        return this;
    }

    @NotNull
    default Enumerator<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new Enumerators.DropWhile<>(this, predicate);
    }

    @NotNull
    default Enumerator<E> take(int n) {
        if (this.isEmpty() || n <= 0) {
            return Enumerator.empty();
        }

        return new Enumerators.Take<>(this, n);
    }

    @NotNull
    default Enumerator<E> takeWhile(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new Enumerators.TakeWhile<>(this, predicate);
    }

    default int indexOf(Object element) {
        int idx = 0;
        while (hasNext()) {
            if (Objects.equals(next(), element)) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    default int indexOf(Object element, int from) {
        return drop(from).indexOf(element);
    }

    default int indexWhere(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        int idx = 0;
        while (hasNext()) {
            if (predicate.test(next())) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    default int indexWhere(@NotNull Predicate<? super E> predicate, int from) {
        return drop(from).indexWhere(predicate);
    }

    @Override
    default E[] toArray(@NotNull IntFunction<? extends E[]> generator) {
        Objects.requireNonNull(generator);
        ArrayList<E> list = new ArrayList<>(); // TODO: replace with kala collection
        while (hasNext()) {
            list.add(next());
        }
        E[] arr = generator.apply(list.size());
        return list.toArray(arr);
    }

    //
    // -- TraversableOnce
    //

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isTraversableAgain() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isEmpty() {
        return !hasNext();
    }

    @Override
    default boolean sameElements(@NotNull TraversableOnce<?> other) {
        Objects.requireNonNull(other);
        Enumerator<?> it = other.iterator();
        while (this.hasNext() && it.hasNext()) {
            if (!Objects.equals(this.next(), it.next())) {
                return false;
            }
        }

        return this.hasNext() == it.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    default Enumerator<E> filter(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new Enumerators.Filtered<>(this, predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    default Enumerator<E> filterNot(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new Enumerators.Filtered<>(this, predicate.negate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <U> Enumerator<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        Objects.requireNonNull(mapper);

        return new Enumerators.Concat<>(this.map(mapper).map(TraversableOnce::iterator).iterator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default E max() {
        return maxOption().getOrThrow(NoSuchElementException::new);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> maxOption() {
        return maxByOption((Comparator<E>) Comparator.naturalOrder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default E maxBy(@NotNull Comparator<? super E> comparator) {
        return maxByOption(comparator).getOrThrow(NoSuchElementException::new);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> maxByOption(@NotNull Comparator<? super E> comparator) {
        Objects.requireNonNull(comparator);

        if (!hasNext()) {
            return Option.none();
        }

        E e = next();
        while (hasNext()) {
            E nextValue = next();
            if (comparator.compare(e, nextValue) < 0) {
                e = nextValue;
            }
        }

        return Option.some(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default E min() {
        return minOption().getOrThrow(NoSuchElementException::new);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> minOption() {
        return minByOption((Comparator<E>) Comparator.naturalOrder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default E minBy(@NotNull Comparator<? super E> comparator) {
        return minByOption(comparator).getOrThrow(NoSuchElementException::new);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> minByOption(@NotNull Comparator<? super E> comparator) {
        Objects.requireNonNull(comparator);

        if (!hasNext()) {
            return Option.none();
        }

        E e = next();
        while (hasNext()) {
            E nextValue = next();
            if (comparator.compare(e, nextValue) > 0) {
                e = nextValue;
            }
        }

        return Option.some(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <A extends Appendable> A joinTo(
            @NotNull A buffer,
            @NotNull CharSequence separator,
            @NotNull CharSequence prefix,
            @NotNull CharSequence postfix) {
        try {
            buffer.append(prefix);
            if (hasNext()) {
                buffer.append(Objects.toString(next()));
            }
            while (hasNext()) {
                buffer.append(separator).append(Objects.toString(next()));
            }
            buffer.append(postfix);
            return buffer;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    default <U> Enumerator<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return new Enumerators.Mapped<>(this, mapper);
    }

    //
    // -- Foldable
    //

    /**
     * {@inheritDoc}
     */
    @Override
    default <U> U foldLeft(U zero, @NotNull BiFunction<? super U, ? super E, ? extends U> op) {
        while (hasNext()) {
            zero = op.apply(zero, next());
        }
        return zero;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <U> U foldRight(U zero, @NotNull BiFunction<? super E, ? super U, ? extends U> op) {
        IList<E> list = IList.nil();
        while (hasNext()) {
            list = list.cons(next());
        }

        for (E u : list) {
            zero = op.apply(u, zero);
        }
        return zero;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> reduceLeftOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        if (hasNext()) {
            E e = next();
            while (hasNext()) {
                e = op.apply(e, next());
            }
        }
        return Option.none();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> reduceRightOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        if (hasNext()) {
            IList<E> list = IList.nil();
            while (hasNext()) {
                list = list.cons(next());
            }
            Iterator<E> it = list.iterator();
            E e = it.next();
            if (it.hasNext()) {
                e = op.apply(it.next(), e);
            }
            return Option.some(e);
        }
        return Option.none();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default int size() {
        int c = 0;
        while (hasNext()) {
            next();
            ++c;
        }
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean forall(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        while (hasNext()) {
            if (!predicate.test(next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean exists(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        while (hasNext()) {
            if (predicate.test(next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean contains(Object v) {
        while (hasNext()) {
            if (Objects.equals(next(), v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default int count(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        int c = 0;
        while (hasNext()) {
            if (predicate.test(next())) {
                ++c;
            }
        }
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Option<E> find(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        while (hasNext()) {
            E v = next();
            if (predicate.test(v)) {
                return Option.some(v);
            }
        }
        return Option.none();
    }

    //
    // -- Iterable
    //

    /**
     * {@inheritDoc}
     */
    @Override
    default void forEach(@NotNull Consumer<? super E> action) {
        Objects.requireNonNull(action);
        if (hasNext()) {
            action.accept(next());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void forEachRemaining(@NotNull Consumer<? super E> action) {
        this.forEach(action);
    }
}
