package asia.kala.collection.mutable;

import asia.kala.collection.CollectionFactory;
import asia.kala.collection.Enumerator;
import asia.kala.collection.IndexedSeq;
import asia.kala.collection.immutable.ImmutableArray;
import asia.kala.function.IndexedConsumer;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public final class MutableArray<E> extends AbstractMutableSeq<E> implements IndexedSeq<E>, Serializable {
    private static final long serialVersionUID = 6278999671163491762L;

    public static final Object[] EMPTY_ARRAY = ImmutableArray.EMPTY_ARRAY;
    public static final MutableArray<?> EMPTY = new MutableArray<>(EMPTY_ARRAY);

    public static final MutableArray.Factory<?> FACTORY = new Factory<>();

    @NotNull
    private final Object[] values;
    private final boolean isChecked;

    MutableArray(@NotNull Object[] values) {
        this(values, false);
    }

    MutableArray(@NotNull Object[] values, boolean isChecked) {
        this.values = values;
        this.isChecked = isChecked;
    }

    public static <E> MutableArray.Factory<E> factory() {
        return (Factory<E>) FACTORY;
    }

    public static <E> MutableArray<E> empty() {
        return (MutableArray<E>) EMPTY;
    }

    public static <E> MutableArray<E> of() {
        return (MutableArray<E>) EMPTY;
    }

    @NotNull
    public static <E> MutableArray<E> of(@NotNull E... values) {
        Objects.requireNonNull(values);
        if (values.length == 0) {
            return empty();
        }

        Object[] newValues = new Object[values.length];
        System.arraycopy(values, 0, newValues, 0, values.length);
        return new MutableArray<>(newValues);
    }

    @NotNull
    public static <E> MutableArray<E> wrap(@NotNull E[] array) {
        Objects.requireNonNull(array);
        return new MutableArray<>(array, true);
    }

    public final Object[] getArray() {
        return values;
    }

    public final boolean isChecked() {
        return isChecked;
    }

    //
    // -- MutableSeq
    //

    public final E get(int index) {
        return (E) values[index];
    }

    @Override
    public final void set(int index, E newValue) {
        values[index] = newValue;
    }

    @Override
    public final int size() {
        return values.length;
    }

    @Override
    public final void mapInPlace(@NotNull Function<? super E, ? extends E> mapper) {
        for (int i = 0; i < values.length; i++) {
            values[i] = mapper.apply((E) values[i]);
        }
    }

    @Override
    public final void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
        Objects.requireNonNull(action);
        for (int i = 0; i < values.length; i++) {
            action.accept(i, (E) values[i]);
        }
    }

    @Override
    public final void sort(@NotNull Comparator<? super E> comparator) {
        Objects.requireNonNull(comparator);
        Arrays.sort(values, (Comparator<? super Object>) comparator);
    }

    //
    // -- MutableCollection
    //

    @Override
    public final String className() {
        return "MutableArray";
    }

    @NotNull
    @Override
    public final <U> MutableArray.Factory<U> iterableFactory() {
        return factory();
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
        return Spliterators.spliterator(values, 0);
    }

    @NotNull
    @Override
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public final <U> U[] toArray(@NotNull IntFunction<? extends U[]> generator) {
        U[] newValues = generator.apply(values.length);
        System.arraycopy(values, 0, newValues, 0, values.length);
        return newValues;
    }

    @Override
    public final void forEach(@NotNull Consumer<? super E> action) {
        Objects.requireNonNull(action);

        for (Object value : values) {
            action.accept((E) value);
        }
    }

    public static final class Factory<E> implements CollectionFactory<E, ArrayBuffer<E>, MutableArray<E>> {
        Factory() {
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
        public ArrayBuffer<E> mergeBuilder(@NotNull ArrayBuffer<E> builder1, @NotNull ArrayBuffer<E> builder2) {
            builder1.appendAll(builder2);
            return builder1;
        }

        @Override
        public MutableArray<E> build(@NotNull ArrayBuffer<E> buffer) {
            return new MutableArray<>(buffer.toArray(Object[]::new));
        }
    }
}
