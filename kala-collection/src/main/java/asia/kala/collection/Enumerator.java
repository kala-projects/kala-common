package asia.kala.collection;

import asia.kala.Option;
import asia.kala.Tuple;
import asia.kala.Tuple2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public interface Enumerator<E> extends Iterator<E>, TraversableOnce<E> {

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
        return new Enumerators.Dropped<>(this, predicate);
    }

    //
    // -- TraversableOnce
    //

    @Override
    default boolean isTraversableAgain() {
        return false;
    }

    @Override
    @NotNull
    default Enumerator<E> filter(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new Enumerators.Filtered<>(this, predicate);
    }

    @Override
    @NotNull
    default Enumerator<E> filterNot(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new Enumerators.Filtered<>(this, predicate.negate());
    }

    @Override
    default E max() {
        return maxOption().getOrThrow(NoSuchElementException::new);
    }

    @NotNull
    @Override
    default Option<E> maxOption() {
        return maxByOption((Comparator<E>)Comparator.naturalOrder());
    }

    @Override
    default E maxBy(@NotNull Comparator<? super E> comparator) {
        return maxByOption(comparator).getOrThrow(NoSuchElementException::new);
    }

    @NotNull
    @Override
    default Option<E> maxByOption(@NotNull Comparator<? super E> comparator) {
        Objects.requireNonNull(comparator);

        if(!hasNext()) {
            return Option.none();
        }

        E e = next();
        while (hasNext()) {
            E nextValue = next();
            if(comparator.compare(e, nextValue) < 0) {
                e = nextValue;
            }
        }

        return Option.some(e);
    }

    @Override
    default E min() {
        return minOption().getOrThrow(NoSuchElementException::new);
    }

    @NotNull
    @Override
    default Option<E> minOption() {
        return minByOption((Comparator<E>)Comparator.naturalOrder());
    }

    @Override
    default E minBy(@NotNull Comparator<? super E> comparator) {
        return minByOption(comparator).getOrThrow(NoSuchElementException::new);
    }

    @NotNull
    @Override
    default Option<E> minByOption(@NotNull Comparator<? super E> comparator) {
        Objects.requireNonNull(comparator);

        if(!hasNext()) {
            return Option.none();
        }

        E e = next();
        while (hasNext()) {
            E nextValue = next();
            if(comparator.compare(e, nextValue) > 0) {
                e = nextValue;
            }
        }

        return Option.some(e);
    }

    //
    // -- Functor
    //

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
        LinkedList<E> us = new LinkedList<>(); // TODO: replace with kala collection
        while (hasNext()) {
            us.addFirst(next());
        }

        for (E u : us) {
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
            LinkedList<E> us = new LinkedList<>(); // TODO: replace with kala collection
            while (hasNext()) {
                us.addFirst(next());
            }
            Iterator<E> it = us.iterator();
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
