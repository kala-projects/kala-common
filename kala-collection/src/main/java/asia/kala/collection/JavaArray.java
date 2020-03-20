package asia.kala.collection;

import asia.kala.control.Option;
import asia.kala.annotations.Covariant;
import asia.kala.annotations.StaticClass;
import asia.kala.collection.mutable.ArrayBuffer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Predicate;

@StaticClass
@SuppressWarnings("unchecked")
public final class JavaArray {
    private JavaArray() {
    }

    @NotNull
    @Contract(pure = true)
    public static <E> IntFunction<E[]> generator(@NotNull Class<E> type) {
        Objects.requireNonNull(type);

        return length -> (E[]) Array.newInstance(type, length);
    }

    @NotNull
    @Contract(pure = true)
    public static <E> CollectionFactory<E, ?, E[]> factory(@NotNull Class<E> type) {
        assert type != null;
        return factory(generator(type));
    }

    @NotNull
    @Contract(pure = true)
    public static <E> CollectionFactory<E, ?, E[]> factory(@NotNull IntFunction<? extends E[]> generator) {
        Objects.requireNonNull(generator);

        return new JavaArray.Factory<>(generator);
    }

    @NotNull
    public static <E> E[] of(E... elements) {
        Objects.requireNonNull(elements);
        return elements.clone();
    }

    @NotNull
    public static <E> E[] from(E @NotNull [] elements) {
        Objects.requireNonNull(elements);
        return elements.clone();
    }

    @NotNull
    public static <E> E[] from(@NotNull Iterable<? extends E> elements, @NotNull IntFunction<? extends E[]> generator) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(generator);

        if (elements instanceof TraversableOnce<?>) {
            return ((TraversableOnce<?>) elements).toArray(generator);
        }

        if (elements instanceof Collection<?>) {
            Collection<E> collection = (Collection<E>) elements;
            int size = collection.size();
            E[] arr = generator.apply(size);
            int idx = 0;
            for (E e : collection) {
                arr[idx++] = e;
            }
            return arr;
        }

        return ArrayBuffer.from(elements).toArray(generator);
    }

    @NotNull
    public static <E> E[] wrapInArray(E element) {
        Class<?> cls = element == null ? Object.class : element.getClass();
        Object arr = Array.newInstance(cls, 1);
        Array.set(arr, 0, element);
        return (E[]) arr;
    }

    @NotNull
    public static <E> E[][] grouped(E @NotNull [] array, int size) {
        Objects.requireNonNull(array);
        if (size <= 0) {
            throw new IllegalArgumentException();
        }

        int length = array.length;
        if (length == 0) {
            return (E[][]) Array.newInstance(array.getClass(), 0);
        }

        int x = length / size;
        int r = length % size;

        E[][] res = (E[][]) Array.newInstance(array.getClass(), r == 0 ? x : x + 1);

        for (int i = 0; i < x; i++) {
            res[i] = Arrays.copyOfRange(array, i * size, (i + 1) * size);
        }
        if (r != 0) {
            res[x] = Arrays.copyOfRange(array, x * size, x * size + r);
        }

        return res;
    }

    @Contract(pure = true)
    public static int indexOf(Object @NotNull [] array, Object value) {
        assert array != null;

        final int length = array.length;

        if (value == null) {
            for (int i = 0; i < length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                if (value.equals(array[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Contract(pure = true)
    public static int indexOf(Object @NotNull [] array, Object value, int from) {
        assert array != null;
        final int length = array.length;

        if (from >= length) {
            return -1;
        }

        if (value == null) {
            for (int i = Math.max(from, 0); i < length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = Math.max(from, 0); i < length; i++) {
                if (value.equals(array[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Contract(pure = true)
    public static <E> int indexWhere(E @NotNull [] array, @NotNull Predicate<? super E> predicate) {
        assert array != null;
        assert predicate != null;

        final int length = array.length;
        for (int i = 0; i < length; i++) {
            if (predicate.test(array[i])) {
                return i;
            }
        }
        return -1;
    }

    @Contract(pure = true)
    public static <E> int indexWhere(E @NotNull [] array, @NotNull Predicate<? super E> predicate, int from) {
        assert array != null;
        assert predicate != null;

        final int length = array.length;

        if (from >= length) {
            return -1;
        }

        for (int i = Math.max(from, 0); i < length; i++) {
            if (predicate.test(array[i])) {
                return i;
            }
        }
        return -1;
    }

    @Contract(pure = true)
    public static int lastIndexOf(Object @NotNull [] array, Object value) {
        assert array != null;

        if (value == null) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value.equals(array[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Contract(pure = true)
    public static int lastIndexOf(Object @NotNull [] array, Object value, int end) {
        assert array != null;

        if (end < 0) {
            return -1;
        }

        if (value == null) {
            for (int i = end; i >= 0; i--) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = end; i >= 0; i--) {
                if (value.equals(array[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Contract(pure = true)
    public static <E> int lastIndexWhere(E @NotNull [] array, @NotNull Predicate<? super E> predicate) {
        assert array != null;
        assert predicate != null;

        for (int i = array.length - 1; i >= 0; i--) {
            if (predicate.test(array[i])) {
                return i;
            }
        }

        return -1;
    }

    @Contract(pure = true)
    public static <E> int lastIndexWhere(E @NotNull [] array, @NotNull Predicate<? super E> predicate, int end) {
        assert array != null;
        assert predicate != null;

        if (end < 0) {
            return -1;
        }

        for (int i = end; i >= 0; i--) {
            if (predicate.test(array[i])) {
                return i;
            }
        }

        return -1;
    }

    static Object maxImpl(Object @NotNull [] array) {
        assert array != null;

        final int length = array.length;
        if (length == 0) {
            throw new NoSuchElementException();
        }

        Object e = array[0];
        for (int i = 1; i < length; i++) {
            Object v = array[i];
            if (((Comparable<Object>) e).compareTo(v) < 0) {
                e = v;
            }
        }
        return e;
    }

    @Contract(pure = true)
    public static <E extends Comparable<E>> E max(E @NotNull [] array) {
        return (E) maxImpl(array);
    }

    @Contract(pure = true)
    public static <E> E maxBy(E @NotNull [] array, @NotNull Comparator<? super E> comparator) {
        assert array != null;
        assert comparator != null;

        final int length = array.length;
        E e = array[0];
        for (int i = 1; i < length; i++) {
            E v = array[i];
            if (comparator.compare(e, v) < 0) {
                e = v;
            }
        }
        return e;
    }

    @NotNull
    @Contract(pure = true)
    public static <E extends Comparable<E>> Option<E> maxOption(E @NotNull [] array) {
        if (array.length == 0) {
            return Option.none();
        }
        return Option.some((E) maxImpl(array));
    }

    @NotNull
    @Contract(pure = true)
    public static <E> Option<E> maxByOption(E @NotNull [] array, @NotNull Comparator<? super E> comparator) {
        if (array.length == 0) {
            return Option.none();
        }
        return Option.some(maxBy(array, comparator));
    }

    static Object minImpl(Object @NotNull [] array) {
        assert array != null;

        final int length = array.length;
        if (length == 0) {
            throw new NoSuchElementException();
        }

        Object e = array[0];
        for (int i = 1; i < length; i++) {
            Object v = array[i];
            if (((Comparable<Object>) e).compareTo(v) > 0) {
                e = v;
            }
        }
        return e;
    }

    @Contract(pure = true)
    public static <E extends Comparable<E>> E min(E @NotNull [] array) {
        return (E) minImpl(array);
    }

    @Contract(pure = true)
    public static <E> E minBy(E @NotNull [] array, @NotNull Comparator<? super E> comparator) {
        assert array != null;
        assert comparator != null;

        final int length = array.length;
        E e = array[0];
        for (int i = 1; i < length; i++) {
            E v = array[i];
            if (comparator.compare(e, v) > 0) {
                e = v;
            }
        }
        return e;
    }

    @NotNull
    @Contract(pure = true)
    public static <E extends Comparable<E>> Option<E> minOption(E @NotNull [] array) {
        if (array.length == 0) {
            return Option.none();
        }
        return Option.some((E) minImpl(array));
    }

    @NotNull
    @Contract(pure = true)
    public static <E> Option<E> minByOption(E @NotNull [] array, @NotNull Comparator<? super E> comparator) {
        if (array.length == 0) {
            return Option.none();
        }
        return Option.some(minBy(array, comparator));
    }

    @Contract(pure = true)
    public static <E> E fold(
            E @NotNull [] array, E zero, @NotNull BiFunction<? super E, ? super E, ? extends E> op
    ) {
        return foldLeft(array, zero, op);
    }

    @Contract(pure = true)
    public static <E, U> U foldLeft(
            E @NotNull [] array, U zero, @NotNull BiFunction<? super U, ? super E, ? extends U> op
    ) {
        assert array != null;
        assert op != null;

        for (E e : array) {
            zero = op.apply(zero, e);
        }
        return zero;
    }

    @Contract(pure = true)
    public static <E, U> U foldRight(
            E @NotNull [] array, U zero, @NotNull BiFunction<? super E, ? super U, ? extends U> op
    ) {
        assert array != null;
        assert op != null;

        for (int i = array.length - 1; i >= 0; i--) {
            zero = op.apply(array[i], zero);
        }
        return zero;
    }

    @Contract(pure = true)
    public static <E> E reduce(E @NotNull [] array, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        return reduceLeft(array, op);
    }

    @Contract(pure = true)
    public static <E> E reduceLeft(E @NotNull [] array, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        assert array != null;

        final int length = array.length;

        if (length == 0) {
            throw new NoSuchElementException();
        }

        E e = array[0];
        for (int i = 1; i < length; i++) {
            e = op.apply(e, array[i]);
        }
        return e;
    }

    @Contract(pure = true)
    public static <E> E reduceRight(E @NotNull [] array, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        assert array != null;

        final int length = array.length;

        if (length == 0) {
            throw new NoSuchElementException();
        }

        E e = array[length - 1];
        for (int i = length - 2; i >= 0; i--) {
            e = op.apply(array[i], e);
        }
        return e;
    }

    @NotNull
    @Contract(pure = true)
    public static <E> Option<E> reduceOption(E @NotNull [] array, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        return reduceLeftOption(array, op);
    }

    @NotNull
    @Contract(pure = true)
    public static <E> Option<E> reduceLeftOption(E @NotNull [] array, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        assert array != null;

        final int length = array.length;

        if (length == 0) {
            return Option.none();
        }

        E e = array[0];
        for (int i = 1; i < length; i++) {
            e = op.apply(e, array[i]);
        }
        return Option.some(e);
    }

    @NotNull
    @Contract(pure = true)
    public static <E> Option<E> reduceRightOption(E @NotNull [] array, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        assert array != null;

        final int length = array.length;

        if (length == 0) {
            return Option.none();
        }

        E e = array[length - 1];
        for (int i = length - 2; i >= 0; i--) {
            e = op.apply(array[i], e);
        }
        return Option.some(e);
    }

    public static <E> boolean forall(E @NotNull [] array, @NotNull Predicate<? super E> predicate) {
        assert array != null;
        assert predicate != null;

        for (Object e : array) {
            if (!predicate.test((E) e)) {
                return false;
            }
        }
        return true;
    }

    public static <E> boolean exists(E @NotNull [] array, @NotNull Predicate<? super E> predicate) {
        assert array != null;
        assert predicate != null;

        for (Object e : array) {
            if (predicate.test((E) e)) {
                return true;
            }
        }
        return false;
    }

    public static <A extends Appendable> A joinTo(
            @NotNull Object[] array,
            @NotNull A buffer
    ) {
        return joinTo(array, buffer, ", ", "", "");
    }

    public static <A extends Appendable> A joinTo(
            @NotNull Object[] array,
            @NotNull A buffer,
            CharSequence separator
    ) {
        return joinTo(array, buffer, separator, "", "");
    }

    public static <A extends Appendable> A joinTo(
            @NotNull Object[] array,
            @NotNull A buffer,
            CharSequence separator, CharSequence prefix, CharSequence postfix
    ) {
        final int length = array.length;
        try {
            if (length == 0) {
                buffer.append(prefix).append(postfix);
                return buffer;
            }
            buffer.append(prefix).append(Objects.toString(array[0]));
            for (int i = 1; i < length; i++) {
                buffer.append(separator).append(Objects.toString(array[i]));
            }
            buffer.append(postfix);
            return buffer;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String joinToString(
            @NotNull Object[] array
    ) {
        return joinTo(array, new StringBuilder()).toString();
    }

    public static String joinToString(
            @NotNull Object[] array,
            CharSequence separator
    ) {
        return joinTo(array, new StringBuilder(), separator).toString();
    }

    public static String joinToString(
            @NotNull Object[] array,
            CharSequence separator, CharSequence prefix, CharSequence postfix
    ) {
        return joinTo(array, new StringBuilder(), separator, prefix, postfix).toString();
    }

    @NotNull
    public static <E> Enumerator<E> iterator(E @NotNull [] array) {
        assert array != null;
        int l = array.length;
        if (l == 0) {
            return Enumerator.empty();
        }
        if (l == 1) {
            return new Enumerators.Id<>(array[0]);
        }
        return new Itr<>(array, 0, array.length);
    }

    @NotNull
    public static <E> Enumerator<E> reverseIterator(E @NotNull [] array) {
        assert array != null;

        final int length = array.length;
        if (length == 0) {
            return Enumerator.empty();
        }
        if (length == 1) {
            return Enumerator.of(array[0]);
        }
        return new ReverseItr<>(array, length - 1);
    }

    private static final class Factory<E> implements CollectionFactory<E, ArrayBuffer<E>, E[]> {
        @NotNull
        private final IntFunction<? extends E[]> generator;

        private final E[] empty;

        public Factory(@NotNull IntFunction<? extends E[]> generator) {
            assert generator != null;

            this.generator = generator;
            this.empty = generator.apply(0);
        }


        @Override
        public final E[] empty() {
            return empty;
        }

        @Override
        public final ArrayBuffer<E> newBuilder() {
            return new ArrayBuffer<>();
        }

        @Override
        public final void addToBuilder(@NotNull ArrayBuffer<E> buffer, E value) {
            buffer.append(value);
        }

        @Override
        public final ArrayBuffer<E> mergeBuilder(@NotNull ArrayBuffer<E> builder1, @NotNull ArrayBuffer<E> builder2) {
            builder1.appendAll(builder2);
            return builder1;
        }

        @Override
        public final void sizeHint(@NotNull ArrayBuffer<E> buffer, int size) {
            buffer.sizeHint(size);
        }

        @Override
        public final E[] build(@NotNull ArrayBuffer<E> buffer) {
            if (buffer.isEmpty()) {
                return empty;
            }

            return buffer.toArray(generator);
        }
    }

    static final class Itr<@Covariant E> extends AbstractEnumerator<E> {
        @NotNull
        private final E[] array;
        private final int end;

        private int index;

        Itr(E @NotNull [] array, int start, int end) {
            assert array != null;

            this.array = array;
            this.index = start;
            this.end = end;
        }


        @Override
        public final boolean hasNext() {
            return index < end;
        }

        @Override
        public final E next() {
            if (index >= end) {
                throw new NoSuchElementException(this + ".next()");
            }
            return array[index++];
        }
    }

    static final class ReverseItr<@Covariant E> extends AbstractEnumerator<E> {
        @NotNull
        private final E[] array;

        private int index;

        ReverseItr(E @NotNull [] array, int index) {
            this.array = array;
            this.index = index;
        }

        ReverseItr(E @NotNull [] array) {
            this(array, array.length - 1);
        }


        @Override
        public final boolean hasNext() {
            return index >= 0;
        }

        @Override
        public final E next() {
            return array[index--];
        }
    }
}
