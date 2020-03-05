package asia.kala.collection;

import asia.kala.Option;
import asia.kala.Tuple2;
import asia.kala.annotations.Covariant;
import asia.kala.collection.mutable.ArrayBuffer;
import asia.kala.collection.mutable.LinkedBuffer;
import asia.kala.function.IndexedConsumer;
import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.*;

@SuppressWarnings("unchecked")
public interface Enumerator<@Covariant E> extends Iterator<E>, TraversableOnce<E>, Transformable<E> {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> Enumerator<E> narrow(Enumerator<? extends E> enumerator) {
        return (Enumerator<E>) enumerator;
    }

    @NotNull
    static <E> Enumerator<E> fromJava(@NotNull @ReadOnly Iterator<? extends E> iterator) {
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
        return new Enumerators.Id<>(element);
    }

    @NotNull
    static <E> Enumerator<E> of(E... elements) {
        return JavaArray.iterator(elements);
    }

    @NotNull
    static <E> Enumerator<E> ofArray(E @NotNull [] elements) {
        return JavaArray.iterator(elements);
    }

    @NotNull
    static <E> Enumerator<E> ofArray(E @NotNull [] elements, int start, int length) {
        Objects.requireNonNull(elements);
        if (start < 0 || start >= elements.length) {
            throw new IndexOutOfBoundsException("Index: " + start);
        }
        if (length < 0 || length + start > elements.length) {
            throw new IndexOutOfBoundsException("Index: " + length + start);
        }
        return new JavaArray.Iterator<>(elements, start, start + length);
    }

    @NotNull
    @SafeVarargs
    static <E> Enumerator<E> concat(@NotNull Enumerator<? extends E>... enumerators) {
        Objects.requireNonNull(enumerators);

        if (enumerators.length == 0) {
            return empty();
        }

        if (enumerators.length == 1) {
            return narrow(enumerators[0]);
        }

        return new Enumerators.Concat<>(ofArray(enumerators));
    }

    static int hash(@NotNull Iterator<?> it) {
        assert it != null;

        int ans = 0;
        while (it.hasNext()) {
            ans = ans * 31 + Objects.hashCode(it.next());
        }
        return ans;
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

    @NotNull
    default Enumerator<E> updated(int n, E newValue) {
        return new Enumerators.Updated<>(this, n, newValue);
    }

    default Enumerator<E> prepended(E value) {
        return new Enumerators.Prepended<>(this, value);
    }

    default Enumerator<E> appended(E value) {
        return new Enumerators.Appended<>(this, value);
    }

    @NotNull
    @Override
    default <U> U[] toArray(@NotNull IntFunction<? extends U[]> generator) {
        Objects.requireNonNull(generator);

        ArrayBuffer<E> buffer = new ArrayBuffer<>();
        while (hasNext()) {
            buffer.append(next());
        }

        return buffer.toArray(generator);
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

    @NotNull
    @ReadOnly
    @Override
    default Iterator<E> asJava() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isEmpty() {
        return !hasNext();
    }

    @Override
    default boolean sameElements(@NotNull Iterable<?> other) {
        Objects.requireNonNull(other);
        Iterator<?> it = other.iterator();
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
    @NotNull
    @Override
    default Enumerator<E> filter(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new Enumerators.Filter<>(this, predicate);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default Enumerator<E> filterNot(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new Enumerators.Filter<>(this, predicate.negate());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    default <U> Enumerator<U> flatMap(@NotNull Function<? super E, ? extends Iterable<? extends U>> mapper) {
        Objects.requireNonNull(mapper);

        return new Enumerators.Concat<>(this.map(mapper).map(Iterable::iterator).iterator());
    }

    @NotNull
    @Override
    default Tuple2<Enumerator<E>, Enumerator<E>> span(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        LinkedBuffer<E> buffer1 = new LinkedBuffer<>();
        LinkedBuffer<E> buffer2 = new LinkedBuffer<>();

        while (hasNext()) {
            E e = next();
            if (predicate.test(e)) {
                buffer1.append(e);
            } else {
                buffer2.append(e);
            }
        }

        return new Tuple2<>(buffer1.iterator(), buffer2.iterator());
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

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return buffer;
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
        LinkedBuffer<E> list = new LinkedBuffer<>();
        while (hasNext()) {
            list.prepend(next());
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
            LinkedBuffer<E> list = new LinkedBuffer<>();
            while (hasNext()) {
                list.prepend(next());
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

    default void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
        Objects.requireNonNull(action);
        int idx = 0;
        while (hasNext()) {
            action.accept(idx++, next());
        }
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
        while (hasNext()) {
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
