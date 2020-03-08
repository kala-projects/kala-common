package asia.kala.collection;

import asia.kala.Option;
import asia.kala.annotations.Covariant;
import asia.kala.collection.immutable.*;
import asia.kala.function.IndexedConsumer;
import kotlin.annotations.jvm.ReadOnly;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public interface Seq<@Covariant E> extends Traversable<E> {

    //region Narrow method

    @Contract(value = "_ -> param1", pure = true)
    @SuppressWarnings("unchecked")
    static <E> Seq<E> narrow(Seq<? extends E> seq) {
        return (Seq<E>) seq;
    }

    //endregion

    //region Factory methods

    @NotNull
    static <E> CollectionFactory<E, ?, ? extends Seq<E>> factory() {
        return ImmutableSeq.factory();
    }

    @NotNull
    static <E> Seq<E> of() {
        return ImmutableSeq.of();
    }

    @NotNull
    static <E> Seq<E> of(E value1) {
        return ImmutableSeq.of(value1);
    }

    @NotNull
    static <E> Seq<E> of(E value1, E values2) {
        return ImmutableSeq.of(value1, values2);
    }

    @NotNull
    static <E> Seq<E> of(E value1, E values2, E value3) {
        return ImmutableSeq.of(value1, values2, value3);
    }

    @NotNull
    static <E> Seq<E> of(E value1, E values2, E value3, E value4) {
        return ImmutableSeq.of(value1, values2, value3, value4);
    }

    @NotNull
    static <E> Seq<E> of(E value1, E values2, E value3, E value4, E value5) {
        return ImmutableSeq.of(value1, values2, value3, value4, value5);
    }

    @NotNull
    @SafeVarargs
    static <E> Seq<E> of(E... values) {
        return ImmutableSeq.from(values);
    }

    @NotNull
    static <E> Seq<E> from(E @NotNull [] values) {
        return ImmutableSeq.from(values);
    }

    @NotNull
    static <E> Seq<E> from(@NotNull Iterable<? extends E> values) {
        return ImmutableSeq.from(values);
    }

    //endregion

    @Contract(pure = true)
    @Flow(sourceIsContainer = true)
    default E get(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        return getOption(index).getOrThrow(IndexOutOfBoundsException::new);
    }

    @NotNull
    @Contract(pure = true)
    @Flow(sourceIsContainer = true, targetIsContainer = true)
    default Option<E> getOption(int index) {
        if (index < 0) {
            return Option.none();
        }

        int s = knownSize();
        if (s >= 0 && index >= s) {
            return Option.none();
        }

        int i = index;

        for (E e : this) {
            if (i-- == 0) {
                return Option.some(e);
            }
        }
        return Option.none();
    }

    @Nullable
    @Contract(pure = true)
    default E getOrNull(int index) {
        return getOption(index).getOrNull();
    }

    @Contract(pure = true)
    default boolean isDefinedAt(int index) {
        return index >= 0 && index < size();
    }

    @Contract(pure = true)
    default int indexOf(Object value) {
        int idx = 0;
        for (E e : this) {
            if (Objects.equals(e, value)) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    @Contract(pure = true)
    default int indexOf(Object value, int from) {
        int idx = 0;
        for (E e : this) {
            if (idx >= from && Objects.equals(e, value)) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    @Contract(pure = true)
    default int indexWhere(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        int idx = 0;
        for (E e : this) {
            if (predicate.test(e)) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    @Contract(pure = true)
    default int indexWhere(@NotNull Predicate<? super E> predicate, int from) {
        Objects.requireNonNull(predicate);

        int idx = 0;
        for (E e : this) {
            if (idx >= from && predicate.test(e)) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    @Contract(pure = true)
    default int lastIndexOf(Object value) {
        int idx = size() - 1;
        Enumerator<E> it = reverseIterator();
        while (it.hasNext()) {
            if (Objects.equals(value, it.next())) {
                return idx;
            }
            --idx;
        }
        return -1;
    }

    @Contract(pure = true)
    default int lastIndexOf(Object value, int end) {
        int idx = size() - 1;
        Enumerator<E> it = reverseIterator();
        while (it.hasNext()) {
            if (idx <= end && Objects.equals(value, it.next())) {
                return idx;
            }
            --idx;
        }
        return -1;
    }

    @Contract(pure = true)
    default int lastIndexWhere(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        int idx = size() - 1;
        Enumerator<E> it = reverseIterator();
        while (it.hasNext()) {
            if (predicate.test(it.next())) {
                return idx;
            }
            --idx;
        }
        return -1;
    }

    @Contract(pure = true)
    default int lastIndexWhere(@NotNull Predicate<? super E> predicate, int end) {
        Objects.requireNonNull(predicate);

        int idx = size() - 1;
        Enumerator<E> it = reverseIterator();
        while (it.hasNext()) {
            if (idx <= end && predicate.test(it.next())) {
                return idx;
            }
            --idx;
        }
        return -1;
    }

    @Contract(pure = true)
    @Flow(sourceIsContainer = true, target = "array", targetIsContainer = true)
    default int copyToArray(Object @NotNull [] array) {
        return copyToArray(array, 0);
    }

    @Contract(pure = true)
    @Flow(sourceIsContainer = true, target = "array", targetIsContainer = true)
    default int copyToArray(Object @NotNull [] array, int start) {
        int arrayLength = array.length;
        Enumerator<E> it = iterator();

        int i = start;
        while (i < arrayLength && it.hasNext()) {
            array[i++] = it.next();
        }
        return i - start;
    }

    @Contract(pure = true)
    @Flow(sourceIsContainer = true, target = "array", targetIsContainer = true)
    default int copyToArray(Object @NotNull [] array, int start, int length) {
        Enumerator<E> it = iterator();
        int i = start;
        int end = start + Math.min(length, array.length - start);
        while (i < end && it.hasNext()) {
            array[i++] = it.next();
        }
        return i - start;
    }

    @Contract(pure = true)
    default void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
        iterator().forEachIndexed(action);
    }

    @NotNull
    default Enumerator<E> reverseIterator() {
        ImmutableList<E> l = ImmutableList.nil();
        for (E e : this) {
            l = l.cons(e);
        }

        return l.iterator();
    }

    //region Traversable members

    @Override
    default String className() {
        return "Seq";
    }

    @NotNull
    @Override
    default SeqView<E> view() {
        return new SeqViews.Of<>(this);
    }

    @NotNull
    @Override
    default <U> CollectionFactory<U, ?, ? extends Seq<U>> iterableFactory() {
        return factory();
    }

    @Override
    default boolean canEqual(Object other) {
        return other instanceof Seq<?>;
    }

    @NotNull
    @ReadOnly
    @Override
    default List<E> asJava() {
        if (this instanceof IndexedSeq<?>) {
            return new JDKConverters.IndexedSeqAsJava<>((IndexedSeq<E>) this);
        }
        return new JDKConverters.SeqAsJava<>(this);
    }

    //endregion
}
