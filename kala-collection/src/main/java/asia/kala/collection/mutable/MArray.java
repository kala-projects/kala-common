package asia.kala.collection.mutable;

import asia.kala.collection.CollectionFactory;
import asia.kala.collection.Enumerator;
import asia.kala.collection.IndexedSeq;
import asia.kala.collection.immutable.IArray;
import asia.kala.function.IndexedConsumer;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

@SuppressWarnings("unchecked")
public final class MArray<E> extends AbstractMSeq<E> implements IndexedSeq<E>, Serializable {
    private static final long serialVersionUID = 6278999671163491762L;
    private static final int hashMagic = -822992626;

    public static final Object[] EMPTY_ARRAY = IArray.EMPTY_ARRAY;
    public static final MArray<?> EMPTY = new MArray<>(EMPTY_ARRAY);

    public static final MArray.Factory<?> FACTORY = new Factory<>();

    @NotNull
    private final Object[] values;
    private final boolean isChecked;

    MArray(@NotNull Object[] values) {
        this(values, false);
    }

    MArray(@NotNull Object[] values, boolean isChecked) {
        this.values = values;
        this.isChecked = isChecked;
    }

    public static <E> MArray.Factory<E> factory() {
        return (Factory<E>) FACTORY;
    }

    public static <E> MArray<E> empty() {
        return (MArray<E>) EMPTY;
    }

    public static <E> MArray<E> of() {
        return (MArray<E>) EMPTY;
    }

    @NotNull
    public static <E> MArray<E> of(@NotNull E... values) {
        Objects.requireNonNull(values);
        if (values.length == 0) {
            return empty();
        }

        Object[] newValues = new Object[values.length];
        System.arraycopy(values, 0, newValues, 0, values.length);
        return new MArray<>(newValues);
    }

    @NotNull
    public static <E> MArray<E> wrap(@NotNull E[] array) {
        Objects.requireNonNull(array);
        return new MArray<>(array, true);
    }

    public final Object[] getArray() {
        return values;
    }

    public final boolean isChecked() {
        return isChecked;
    }

    //
    // -- MSeq
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
    public void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
        Objects.requireNonNull(action);

        for (int i = 0; i < values.length; i++) {
            action.accept(i, (E) values[i]);
        }
    }

    //
    // -- Traversable
    //

    @Override
    public final String className() {
        return "MArray";
    }

    @NotNull
    @Override
    public final <U> MArray.Factory<U> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    public final Enumerator<E> iterator() {
        return (Enumerator<E>) Enumerator.ofArray(values);
    }

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
        if (!(o instanceof MArray<?>)) {
            return false;
        }

        return Arrays.equals(values, ((MArray<?>) o).values);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(values) + hashMagic;
    }

    public static final class Factory<E> implements CollectionFactory<E, ArrayBuffer<E>, MArray<E>> {

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
        public MArray<E> build(@NotNull ArrayBuffer<E> buffer) {
            return new MArray<>(buffer.toArray(Object[]::new));
        }
    }
}
