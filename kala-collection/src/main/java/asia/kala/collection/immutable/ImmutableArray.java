package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.annotations.Covariant;
import asia.kala.annotations.StaticClass;
import asia.kala.collection.*;
import asia.kala.collection.mutable.ArrayBuffer;
import asia.kala.function.IndexedConsumer;
import asia.kala.function.IndexedFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public final class ImmutableArray<@Covariant E> extends AbstractImmutableSeq<E> implements IndexedSeq<E>, Serializable {
    private static final long serialVersionUID = 1845940935381169058L;

    public static final Object[] EMPTY_ARRAY = new Object[0];
    public static final ImmutableArray<?> EMPTY = new ImmutableArray<>(EMPTY_ARRAY);

    public static final ImmutableArray.Factory<?> FACTORY = new Factory<>();

    private final Object[] values;

    ImmutableArray(Object[] values) {
        this.values = values;
    }

    @Contract("_ -> param1")
    public static <E> ImmutableArray<E> narrow(ImmutableArray<? extends E> array) {
        return (ImmutableArray<E>) array;
    }

    @NotNull
    public static <E> ImmutableArray.Factory<E> factory() {
        return (Factory<E>) FACTORY;
    }

    @NotNull
    public static <E> ImmutableArray<E> empty() {
        return (ImmutableArray<E>) EMPTY;
    }

    @NotNull
    public static <E> ImmutableArray<E> of() {
        return (ImmutableArray<E>) EMPTY;
    }

    @NotNull
    @SafeVarargs
    @Contract("_ -> new")
    public static <E> ImmutableArray<E> of(E... elements) {
        return from(elements);
    }

    @NotNull
    @Contract("_ -> new")
    public static <E> ImmutableArray<E> from(@NotNull E[] elements) {
        Objects.requireNonNull(elements);
        if (elements.length == 0) {
            return empty();
        }
        return new ImmutableArray<>(elements.clone());
    }

    @NotNull
    public static <E> ImmutableArray<E> from(@NotNull Iterable<? extends E> iterable) {
        return ImmutableArray.<E>factory().from(iterable);
    }

    @StaticClass
    public static class Unsafe {
        @NotNull
        @Contract("_ -> new")
        public static <E> ImmutableArray<E> wrap(@NotNull E[] array) {
            Objects.requireNonNull(array);
            return new ImmutableArray<>(array);
        }
    }

    //
    // -- ImmutableSeq
    //

    @Override
    public final E get(int index) {
        return (E) values[index];
    }

    @Override
    public final int size() {
        return values.length;
    }

    @NotNull
    @Override
    public final ImmutableArray<E> updated(int index, E newValue) {
        if (index < 0 || index >= values.length) {
            throw new IndexOutOfBoundsException();
        }
        Object[] newValues = values.clone();
        newValues[index] = newValue;
        return new ImmutableArray<>(newValues);
    }

    @NotNull
    @Override
    public final ImmutableArray<E> drop(int n) {
        if (n <= 0) {
            return this;
        }

        if (n >= values.length) {
            return empty();
        }

        return new ImmutableArray<>(Arrays.copyOfRange(values, n, values.length));
    }

    @NotNull
    @Override
    public final ImmutableArray<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        int idx = 0;
        while (idx < values.length && predicate.test((E) values[idx])) {
            ++idx;
        }

        if (idx >= values.length) {
            return empty();
        }
        return new ImmutableArray<>(Arrays.copyOfRange(values, idx, values.length));
    }

    @NotNull
    @Override
    public final ImmutableArray<E> take(int n) {
        if (n <= 0) {
            return empty();
        }

        if (n >= values.length) {
            return this;
        }

        Object[] newValues = new Object[n];
        System.arraycopy(values, 0, newValues, 0, n);

        return new ImmutableArray<>(newValues);
    }

    @NotNull
    @Override
    public final ImmutableArray<E> takeWhile(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        if (values.length == 0) {
            return empty();
        }

        int count = 0;
        while (count < values.length && predicate.test((E) values[count])) {
            ++count;
        }

        if (count == 0) {
            return empty();
        }

        return new ImmutableArray<>(Arrays.copyOf(values, count));
    }

    @NotNull
    @Override
    public final ImmutableArray<E> concat(@NotNull Seq<? extends E> other) {
        return appendedAll(other);
    }

    @NotNull
    @Override
    public final ImmutableArray<E> prepended(E element) {
        Object[] newValues = new Object[values.length + 1];
        newValues[0] = element;
        System.arraycopy(values, 0, newValues, 1, values.length);

        return new ImmutableArray<>(newValues);
    }

    @NotNull
    @Override
    public final ImmutableArray<E> prependedAll(@NotNull Iterable<? extends E> prefix) {
        Objects.requireNonNull(prefix);

        Object[] data = prefix instanceof ImmutableArray<?> ? ((ImmutableArray<?>) prefix).values : KalaCollectionUtils.asArray(prefix);
        Object[] newValues = new Object[data.length + values.length];

        System.arraycopy(data, 0, newValues, 0, data.length);
        System.arraycopy(values, 0, newValues, data.length, values.length);

        return new ImmutableArray<>(newValues);
    }

    @NotNull
    @Override
    public final ImmutableArray<E> prependedAll(@NotNull E[] prefix) {
        Objects.requireNonNull(prefix);

        Object[] newValues = new Object[prefix.length + values.length];
        System.arraycopy(prefix, 0, newValues, 0, prefix.length);
        System.arraycopy(values, 0, newValues, prefix.length, values.length);

        return new ImmutableArray<>(newValues);
    }

    @NotNull
    @Override
    public final ImmutableArray<E> appended(E element) {
        Object[] newValues = Arrays.copyOf(values, values.length + 1);
        newValues[values.length] = element;

        return new ImmutableArray<>(newValues);
    }

    @NotNull
    @Override
    public final ImmutableArray<E> appendedAll(@NotNull Iterable<? extends E> postfix) {
        Objects.requireNonNull(postfix);

        Object[] data = postfix instanceof ImmutableArray<?> ? ((ImmutableArray<?>) postfix).values : KalaCollectionUtils.asArray(postfix);
        Object[] newValues = new Object[data.length + values.length];

        System.arraycopy(values, 0, newValues, 0, values.length);
        System.arraycopy(data, 0, newValues, values.length, data.length);

        return new ImmutableArray<>(newValues);
    }

    @NotNull
    @Override
    public final ImmutableArray<E> appendedAll(@NotNull E[] postfix) {
        Objects.requireNonNull(postfix);

        Object[] newValues = new Object[postfix.length + values.length];

        System.arraycopy(values, 0, newValues, 0, values.length);
        System.arraycopy(postfix, 0, newValues, values.length, postfix.length);

        return new ImmutableArray<>(newValues);
    }

    @NotNull
    @Override
    public final <U> ImmutableArray<U> mapIndexed(@NotNull IndexedFunction<? super E, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        Object[] newValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = mapper.apply(i, (E) values[i]);
        }
        return new ImmutableArray<>(newValues);
    }

    @NotNull
    @Override
    public final ImmutableArray<E> sorted() {
        return sortedImpl();
    }

    @NotNull
    @Override
    public final ImmutableArray<E> sorted(@NotNull Comparator<? super E> comparator) {
        Object[] newValues = values.clone();
        Arrays.sort(newValues, (Comparator<? super Object>) comparator);
        return new ImmutableArray<>(newValues);
    }

    @Override
    public void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
        Objects.requireNonNull(action);

        for (int i = 0; i < values.length; i++) {
            action.accept(i, (E) values[i]);
        }
    }

    //
    // -- ImmutableCollection
    //

    @Override
    public final String className() {
        return "ImmutableArray";
    }

    @NotNull
    @Override
    public final Enumerator<E> iterator() {
        return (Enumerator<E>) Enumerator.ofArray(values);
    }

    @NotNull
    @Override
    public final Stream<E> stream() {
        return Arrays.stream((E[]) values);
    }

    @NotNull
    @Override
    public final Stream<E> parallelStream() {
        return stream().parallel();
    }

    @NotNull
    @Override
    public final Spliterator<E> spliterator() {
        return Spliterators.spliterator(values, Spliterator.IMMUTABLE);
    }

    @NotNull
    @Override
    public final <U> ImmutableArray<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        Object[] values = this.values;
        int length = values.length;

        if (length == 0) {
            return empty();
        }

        Object[] newValues = new Object[values.length];

        for (int i = 0; i < length; i++) {
            newValues[i] = mapper.apply((E) values[i]);
        }

        return new ImmutableArray<>(newValues);
    }

    @NotNull
    @Override
    public final Tuple2<ImmutableArray<E>, ImmutableArray<E>> span(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        if (values.length == 0) {
            return new Tuple2<>(empty(), empty());
        }

        Object[] newArr1 = new Object[values.length];
        Object[] newArr2 = new Object[values.length];
        int idx1 = 0;
        int idx2 = 0;

        for (Object value : values) {
            if (predicate.test((E) value)) {
                newArr1[idx1++] = value;
            } else {
                newArr2[idx2++] = value;
            }
        }

        ImmutableArray<E> ia1 = idx1 == 0 ? empty() : new ImmutableArray<>(Arrays.copyOf(newArr1, idx1));
        ImmutableArray<E> ia2 = idx2 == 0 ? empty() : new ImmutableArray<>(Arrays.copyOf(newArr2, idx2));

        return new Tuple2<>(ia1, ia2);
    }

    @NotNull
    @Override
    public <U> ImmutableArray.Factory<U> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public <U> U[] toArray(@NotNull IntFunction<? extends U[]> generator) {
        U[] newValues = generator.apply(values.length);
        System.arraycopy(values, 0, newValues, 0, values.length);
        return newValues;
    }

    @Override
    public void forEach(@NotNull Consumer<? super E> action) {
        Objects.requireNonNull(action);

        for (Object value : values) {
            action.accept((E) value);
        }
    }

    public static final class Factory<E> implements CollectionFactory<E, ArrayBuffer<E>, ImmutableArray<E>> {
        Factory() {
        }

        @Override
        public final ImmutableArray<E> empty() {
            return ImmutableArray.empty();
        }

        @Override
        public final ImmutableArray<E> from(@NotNull E[] elements) {
            return ImmutableArray.from(elements);
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
        public final void sizeHint(@NotNull ArrayBuffer<E> buffer, int size) {
            buffer.sizeHint(size);
        }

        @Override
        public final ArrayBuffer<E> mergeBuilder(@NotNull ArrayBuffer<E> buffer1, @NotNull ArrayBuffer<E> buffer2) {
            buffer1.appendAll(buffer2);
            return buffer1;
        }

        @Override
        public final ImmutableArray<E> build(@NotNull ArrayBuffer<E> buffer) {
            if (buffer.isEmpty()) {
                return empty();
            }
            return new ImmutableArray<>(buffer.toArray(Object[]::new));
        }

    }
}
