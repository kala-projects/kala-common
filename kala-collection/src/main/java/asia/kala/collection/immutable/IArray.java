package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.annotations.StaticClass;
import asia.kala.collection.*;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.ApiStatus;
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
public final class IArray<E> implements IndexedSeq<E>, Serializable {
    private static final long serialVersionUID = 1845940935381169058L;
    private static final int hashMagic = -1300712527;

    public static final Object[] EMPTY_ARRAY = new Object[0];
    public static final IArray<?> EMPTY = new IArray<>(EMPTY_ARRAY);

    @NotNull
    private final Object[] values;

    IArray(@NotNull Object[] values) {
        assert values != null;

        this.values = values;
    }

    public static <E> IArray<E> empty() {
        return (IArray<E>) EMPTY;
    }

    @NotNull
    @SafeVarargs
    @Contract("_ -> new")
    public static <E> IArray<E> of(E... elements) {
        Objects.requireNonNull(elements);
        return new IArray<>(elements.clone());
    }

    @StaticClass
    @ApiStatus.Internal
    public static final class Unsafe {
        @NotNull
        @Contract("_ -> new")
        public static <E> IArray<E> wrap(@NotNull E[] array) {
            Objects.requireNonNull(array);

            return new IArray<>(array);
        }
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
    public final IArray<E> concat(@NotNull TraversableOnce<? extends E> traversable) {
        return appendedAll(traversable);
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
    public final IArray<E> prependedAll(@NotNull TraversableOnce<? extends E> prefix) {
        Objects.requireNonNull(prefix);

        Object[] data = prefix instanceof IArray<?> ? ((IArray<?>) prefix).values : prefix.toArray(Object[]::new);
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
    public final IArray<E> appendedAll(@NotNull TraversableOnce<? extends E> postfix) {
        Objects.requireNonNull(postfix);

        Object[] data = postfix instanceof IArray<?> ? ((IArray<?>) postfix).values : postfix.toArray(Object[]::new);
        Object[] newValues = new Object[data.length + values.length];

        System.arraycopy(values, 0, newValues, 0, values.length);
        System.arraycopy(data, 0, newValues, values.length, data.length);

        return new IArray<>(newValues);
    }

    //
    // -- Traversable
    //

    @Override
    public final String stringPrefix() {
        return "IArray";
    }

    @NotNull
    @Override
    public final <U> IArray.Builder<U> newBuilder() {
        return new IArray.Builder<>();
    }

    @NotNull
    @Override
    public final Tuple2<? extends IArray<E>, ? extends IArray<E>> span(@NotNull Predicate<? super E> predicate) {
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
    public final Enumerator<E> iterator() {
        return Enumerator.of((E[]) values);
    }

    @NotNull
    @Override
    public final Stream<E> stream() {
        return Stream.of((E[]) values);
    }

    @NotNull
    @Override
    public final IArray<E> filter(@NotNull Predicate<? super E> predicate) {
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
        return new IArray<>(Arrays.copyOf(newArr, idx));
    }

    @NotNull
    @Override
    public final IArray<E> filterNot(@NotNull Predicate<? super E> predicate) {
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
        return new IArray<>(Arrays.copyOf(newArr, idx));
    }

    @NotNull
    @Override
    public final <U> IArray<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return TraversableOps.flatMap(this, mapper, newBuilder());
    }

    @NotNull
    @Override
    public final <U> IArray<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (values.length == 0) {
            return empty();
        }

        Object[] newArr = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newArr[i] = mapper.apply((E) values[i]);
        }
        return new IArray<>(newArr);
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

    @Override
    public final String toString() {
        return TraversableOps.toString(this);
    }

    public static final class Builder<E> implements CollectionBuilder<E, IArray<E>> {
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
        public final IArray<E> build() {
            return new IArray<>(list.toArray());
        }
    }
}
