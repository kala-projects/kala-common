package asia.kala.collection.mutable;

import asia.kala.Tuple2;
import asia.kala.collection.*;
import asia.kala.collection.immutable.IArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public final class MArray<E> implements MIndexedSeq<E>, Serializable {
    private static final long serialVersionUID = 6278999671163491762L;
    private static final int hashMagic = -822992626;

    public static final Object[] EMPTY_ARRAY = IArray.EMPTY_ARRAY;
    public static final MArray<?> EMPTY = new MArray<>(EMPTY_ARRAY);

    @NotNull
    private final Object[] values;

    private final boolean isChecked;

    MArray(@NotNull Object[] values) {
        this(values, false);
    }

    MArray(@NotNull Object[] values, boolean isChecked) {
        assert values != null;

        this.values = values;
        this.isChecked = isChecked;
    }

    public static <E> MArray<E> empty() {
        return (MArray<E>) EMPTY;
    }

    @NotNull
    @SafeVarargs
    @Contract("_ -> new")
    public static <E> MArray<E> of(E... elements) {
        Objects.requireNonNull(elements);

        return new MArray<>(elements.clone());
    }

    @NotNull
    @Contract("_ -> new")
    public static <E> MArray<E> wrap(@NotNull E[] array) {
        Objects.requireNonNull(array);

        return new MArray<>(array, true);
    }

    public final boolean isChecked() {
        return isChecked;
    }

    //
    // -- MSeq
    //

    @Override
    public final void set(int index, E newValue) {
        values[index] = newValue;
    }


    //
    // -- Seq
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
    public final MArray<E> drop(int n) {
        if (n <= 0) {
            return this;
        }

        if (n >= values.length) {
            return empty();
        }

        return new MArray<>(Arrays.copyOfRange(values, n, values.length));
    }

    @NotNull
    @Override
    public final MArray<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        int idx = 0;
        while (idx < values.length && predicate.test((E) values[idx])) {
            ++idx;
        }

        if (idx >= values.length) {
            return empty();
        }
        return new MArray<>(Arrays.copyOfRange(values, idx, values.length));
    }

    @NotNull
    @Override
    public final MArray<E> concat(@NotNull TraversableOnce<? extends E> traversable) {
        return SeqOps.concat(this, traversable, newBuilder());
    }

    //
    // -- Traversable
    //

    @NotNull
    @Override
    public final <U> MArray.Builder<U> newBuilder() {
        return new MArray.Builder<>();
    }

    @NotNull
    @Override
    public final Tuple2<? extends MArray<E>, ? extends MArray<E>> span(@NotNull Predicate<? super E> predicate) {
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

        MArray<E> ia1 = idx1 == 0 ? empty() : new MArray<>(Arrays.copyOf(newArr1, idx1));
        MArray<E> ia2 = idx2 == 0 ? empty() : new MArray<>(Arrays.copyOf(newArr2, idx2));

        return new Tuple2<>(ia1, ia2);
    }

    @Override
    public final String stringPrefix() {
        return "MArray";
    }

    @NotNull
    @Override
    public final Enumerator<E> iterator() {
        return Enumerator.of((E[]) values);
    }


    @NotNull
    @Override
    public final MArray<E> filter(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        if (values.length == 0) {
            return empty();
        }

        Object[] newArr = new Object[values.length];
        int idx = 0;

        for (Object e : values) {
            if (predicate.test((E) e)) {
                newArr[idx++] = e;
            }
        }

        if (idx == 0) {
            return empty();
        }
        return new MArray<>(Arrays.copyOf(newArr, idx));
    }

    @NotNull
    @Override
    public final MArray<E> filterNot(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        if (values.length == 0) {
            return empty();
        }

        Object[] newArr = new Object[values.length];
        int idx = 0;

        for (Object e : values) {
            if (!predicate.test((E) e)) {
                newArr[idx++] = e;
            }
        }

        if (idx == 0) {
            return empty();
        }
        return new MArray<>(Arrays.copyOf(newArr, idx));
    }

    @NotNull
    @Override
    public final <U> MArray<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return TraversableOps.flatMap(this, mapper, newBuilder());
    }

    //
    // -- Functor
    //

    @NotNull
    @Override
    public final <U> MArray<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (values.length == 0) {
            return empty();
        }

        Object[] newArr = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newArr[i] = mapper.apply((E) values[i]);
        }
        return new MArray<>(newArr);
    }

    @NotNull
    @Override
    public final Stream<E> stream() {
        return Stream.of((E[]) values);
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

    @Override
    public final String toString() {
        return TraversableOps.toString(this);
    }

    public static final class Builder<E> implements CollectionBuilder<E, MArray<E>> {
        private final ArrayList<E> list = new ArrayList<>(); // TODO: use kala collection

        @Override
        public final void add(E element) {
            list.add(element);
        }

        @Override
        public final void clear() {
            list.clear();
        }

        @Override
        public final MArray<E> build() {
            return new MArray<>(list.toArray());
        }
    }
}
