package asia.kala.collection.mutable;

import asia.kala.collection.ArraySeq;
import asia.kala.collection.CollectionFactory;
import asia.kala.collection.IndexedSeq;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class MutableArray<E> extends ArraySeq<E> implements MutableSeq<E>, IndexedSeq<E>, Serializable {
    private static final long serialVersionUID = 6278999671163491762L;

    public static final Object[] EMPTY_ARRAY = ArraySeq.EMPTY_ARRAY;
    public static final MutableArray<?> EMPTY = new MutableArray<>(EMPTY_ARRAY);

    private static final MutableArray.Factory<?> FACTORY = new Factory<>();

    private final boolean isChecked;

    MutableArray(@NotNull Object[] array) {
        this(array, false);
    }

    MutableArray(@NotNull Object[] array, boolean isChecked) {
        super(array);
        this.isChecked = isChecked;
    }

    @NotNull
    public static <E> CollectionFactory<E, ?, MutableArray<E>> factory() {
        return (Factory<E>) FACTORY;
    }

    @NotNull
    public static <E> MutableArray<E> empty() {
        return (MutableArray<E>) EMPTY;
    }

    @NotNull
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
    public static <E> MutableArray<E> wrap(E @NotNull [] array) {
        Objects.requireNonNull(array);
        return new MutableArray<>(array, true);
    }

    public final Object[] getArray() {
        return array;
    }

    public final boolean isChecked() {
        return isChecked;
    }

    //
    // -- MutableSeq
    //

    @Override
    public final void set(int index, E newValue) {
        array[index] = newValue;
    }

    @Override
    public final void mapInPlace(@NotNull Function<? super E, ? extends E> mapper) {
        for (int i = 0; i < array.length; i++) {
            array[i] = mapper.apply((E) array[i]);
        }
    }

    @Override
    public final void sort(@NotNull Comparator<? super E> comparator) {
        Objects.requireNonNull(comparator);
        Arrays.sort(array, (Comparator<? super Object>) comparator);
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
    public final <U> CollectionFactory<U, ?, MutableArray<U>> iterableFactory() {
        return factory();
    }

    private static final class Factory<E> implements CollectionFactory<E, ArrayBuffer<E>, MutableArray<E>> {
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
