package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.annotations.StaticClass;
import asia.kala.collection.*;
import asia.kala.collection.mutable.ArrayBuffer;
import asia.kala.function.IndexedConsumer;
import asia.kala.function.IndexedFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public final class IArray<E> extends AbstractISeq<E> implements IndexedSeq<E>, Serializable {
    private static final long serialVersionUID = 1845940935381169058L;
    private static final int hashMagic = -1300712527;

    public static final Object[] EMPTY_ARRAY = new Object[0];
    public static final IArray<?> EMPTY = new IArray<>(EMPTY_ARRAY);

    public static final IArray.Factory<?> FACTORY = new Factory<>();

    private final Object[] values;

    IArray(Object[] values) {
        this.values = values;
    }

    @Contract("_ -> param1")
    public static <E> IArray<E> narrow(IArray<? extends E> array) {
        return (IArray<E>) array;
    }

    public static <E> IArray.Factory<E> factory() {
        return (Factory<E>) FACTORY;
    }

    public static <E> IArray<E> empty() {
        return (IArray<E>) EMPTY;
    }

    public static <E> IArray<E> of() {
        return (IArray<E>) EMPTY;
    }

    @NotNull
    @SafeVarargs
    @Contract("_ -> new")
    public static <E> IArray<E> of(E... elements) {
        return from(elements);
    }

    @NotNull
    @Contract("_ -> new")
    public static <E> IArray<E> from(@NotNull E[] elements) {
        Objects.requireNonNull(elements);
        if (elements.length == 0) {
            return empty();
        }
        return new IArray<>(elements.clone());
    }

    public static <E> IArray<E> from(@NotNull Iterable<? extends E> iterable) {
        return IArray.<E>factory().from(iterable);
    }

    //
    // -- ISeq
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
    public final IArray<E> updated(int index, E newValue) {
        if (index < 0 || index >= values.length) {
            throw new IndexOutOfBoundsException();
        }
        Object[] newValues = values.clone();
        newValues[index] = newValue;
        return new IArray<>(newValues);
    }

    @NotNull
    @Override
    public final IArray<E> drop(int n) {
        if (n <= 0) {
            return this;
        }

        if (n >= values.length) {
            return empty();
        }

        return new IArray<>(Arrays.copyOfRange(values, n, values.length));
    }

    @NotNull
    @Override
    public final IArray<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        int idx = 0;
        while (idx < values.length && predicate.test((E) values[idx])) {
            ++idx;
        }

        if (idx >= values.length) {
            return empty();
        }
        return new IArray<>(Arrays.copyOfRange(values, idx, values.length));
    }

    @NotNull
    @Override
    public final IArray<E> take(int n) {
        if (n <= 0) {
            return empty();
        }

        if (n >= values.length) {
            return this;
        }

        Object[] newValues = new Object[n];
        System.arraycopy(values, 0, newValues, 0, n);

        return new IArray<>(newValues);
    }

    @NotNull
    @Override
    public final IArray<E> takeWhile(@NotNull Predicate<? super E> predicate) {
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

        return new IArray<>(Arrays.copyOf(values, count));
    }

    @NotNull
    @Override
    public final IArray<E> concat(@NotNull Seq<? extends E> other) {
        return appendedAll(other);
    }

    @NotNull
    @Override
    public final IArray<E> prepended(E element) {
        Object[] newValues = new Object[values.length + 1];
        newValues[0] = element;
        System.arraycopy(values, 0, newValues, 1, values.length);

        return new IArray<>(newValues);
    }

    @NotNull
    @Override
    public final IArray<E> prependedAll(@NotNull Iterable<? extends E> prefix) {
        Objects.requireNonNull(prefix);

        Object[] data = prefix instanceof IArray<?> ? ((IArray<?>) prefix).values : KalaCollectionUtils.asArray(prefix);
        Object[] newValues = new Object[data.length + values.length];

        System.arraycopy(data, 0, newValues, 0, data.length);
        System.arraycopy(values, 0, newValues, data.length, values.length);

        return new IArray<>(newValues);
    }

    @NotNull
    @Override
    public final IArray<E> appended(E element) {
        Object[] newValues = Arrays.copyOf(values, values.length + 1);
        newValues[values.length] = element;

        return new IArray<>(newValues);
    }

    @NotNull
    @Override
    public final IArray<E> appendedAll(@NotNull Iterable<? extends E> postfix) {
        Objects.requireNonNull(postfix);

        Object[] data = postfix instanceof IArray<?> ? ((IArray<?>) postfix).values : KalaCollectionUtils.asArray(postfix);
        Object[] newValues = new Object[data.length + values.length];

        System.arraycopy(values, 0, newValues, 0, values.length);
        System.arraycopy(data, 0, newValues, values.length, data.length);

        return new IArray<>(newValues);
    }

    @NotNull
    @Override
    public final <U> IArray<U> mapIndexed(@NotNull IndexedFunction<? super E, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        Object[] newValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = mapper.apply(i, (E) values[i]);
        }
        return new IArray<>(newValues);
    }

    @NotNull
    @Override
    public final IArray<E> sorted() {
        return sortedImpl();
    }

    @NotNull
    @Override
    public final IArray<E> sorted(@NotNull Comparator<? super E> comparator) {
        Object[] newValues = values.clone();
        Arrays.sort(newValues, (Comparator<? super Object>) comparator);
        return new IArray<>(newValues);
    }

    @Override
    public void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
        Objects.requireNonNull(action);

        for (int i = 0; i < values.length; i++) {
            action.accept(i, (E) values[i]);
        }
    }

    //
    // -- ICollection
    //

    @Override
    public final String className() {
        return "IArray";
    }

    @NotNull
    @Override
    public final <U> IArray<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        Object[] values = this.values;
        int length = values.length;

        if (length == 0) {
            return empty();
        }

        Object[] newValues = new Object[values.length];

        for (int i = 0; i < length; i++) {
            newValues[i] = mapper.apply((E) values[i]);
        }

        return new IArray<>(newValues);
    }

    @NotNull
    @Override
    public final Tuple2<IArray<E>, IArray<E>> span(@NotNull Predicate<? super E> predicate) {
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

        IArray<E> ia1 = idx1 == 0 ? empty() : new IArray<>(Arrays.copyOf(newArr1, idx1));
        IArray<E> ia2 = idx2 == 0 ? empty() : new IArray<>(Arrays.copyOf(newArr2, idx2));

        return new Tuple2<>(ia1, ia2);
    }

    @NotNull
    @Override
    public <U> IArray.Factory<U> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    public final Enumerator<E> iterator() {
        return (Enumerator<E>) Enumerator.ofArray(values);
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

    //
    // -- Object
    //

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IArray<?>)) {
            return false;
        }

        return Arrays.equals(values, ((IArray<?>) o).values);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(values) + hashMagic;
    }

    public static final class Factory<E> implements CollectionFactory<E, ArrayBuffer<E>, IArray<E>> {
        Factory() {
        }

        @Override
        public IArray<E> empty() {
            return IArray.empty();
        }

        @Override
        public ArrayBuffer<E> newBuilder() {
            return new ArrayBuffer<>();
        }

        @Override
        public void addToBuilder(@NotNull ArrayBuffer<E> buffer, E value) {
            buffer.append(value);
        }

        @Override
        public void sizeHint(@NotNull ArrayBuffer<E> buffer, int size) {
            buffer.sizeHint(size);
        }

        @Override
        public ArrayBuffer<E> mergeBuilder(@NotNull ArrayBuffer<E> buffer1, @NotNull ArrayBuffer<E> buffer2) {
            buffer1.appendAll(buffer2);
            return buffer1;
        }

        @Override
        public IArray<E> build(@NotNull ArrayBuffer<E> buffer) {
            if (buffer.isEmpty()) {
                return empty();
            }
            return new IArray<>(buffer.toArray(Object[]::new));
        }
    }
}
