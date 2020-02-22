package asia.kala.collection.mutable;

import asia.kala.collection.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.IntFunction;

@SuppressWarnings("unchecked")
public final class ArrayBuffer<E> extends AbstractBuffer<E> implements IndexedSeq<E>, Serializable {
    private static final long serialVersionUID = 6323541372807433607L;
    private static final int hashMagic = -1142720889;

    private static final int DEFAULT_CAPACITY = 16;

    public static final ArrayBuffer.Factory<?> FACTORY = new Factory<>();

    @NotNull
    private Object[] elements;
    private int size;

    ArrayBuffer(@NotNull Object[] elements, int size) {
        assert elements != null;

        this.elements = elements;
        this.size = size;
    }

    public ArrayBuffer() {
        this(MArray.EMPTY_ARRAY, 0);
    }

    public ArrayBuffer(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("illegal initialCapacity: " + initialCapacity);
        }

        this.elements = initialCapacity == 0 ? MArray.EMPTY_ARRAY : new Object[initialCapacity];
        this.size = 0;
    }

    public static <E> ArrayBuffer.Factory<E> factory() {
        return (Factory<E>) FACTORY;
    }

    @NotNull
    @Contract("_ -> new")
    public static <E> ArrayBuffer<E> of(E... elements) {
        return from(elements);
    }

    @NotNull
    @Contract("_ -> new")
    public static <E> ArrayBuffer<E> from(@NotNull E[] elements) {
        Objects.requireNonNull(elements);

        int length = elements.length;
        if (length == 0) {
            return new ArrayBuffer<>(MArray.EMPTY_ARRAY, 0);
        }
        Object[] newValues = new Object[length];
        System.arraycopy(elements, 0, newValues, 0, length);
        return new ArrayBuffer<>(newValues, length);
    }

    @NotNull
    public static <E> ArrayBuffer<E> from(@NotNull Iterable<? extends E> iterable) {
        ArrayBuffer<E> buffer = new ArrayBuffer<>();
        buffer.appendAll(iterable);
        return buffer;
    }

    private void grow() {
        grow(size + 1);
    }

    private void grow(int minCapacity) {
        Object[] newArray = growArray(minCapacity);
        if (elements.length != 0) {
            System.arraycopy(elements, 0, newArray, 0, size);
        }
        elements = newArray;
    }

    private Object[] growArray(int minCapacity) {
        int oldCapacity = elements.length;
        if (oldCapacity == 0) {
            return new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }

        int newCapacity = Math.max(Math.max(oldCapacity, minCapacity), oldCapacity + (oldCapacity >> 1));
        return new Object[newCapacity];
    }

    private void checkInBound(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
    }

    public void sizeHint(int s) {
        int len = elements.length;
        int size = this.size;

        if (s > 0 && s + size > len) {
            grow(size + s);
        }
    }

    //
    // -- Buffer
    //

    @Override
    public final void append(E value) {
        if (size == elements.length) {
            grow();
        }
        elements[size++] = value;
    }

    @Override
    public final void appendAll(@NotNull Iterable<? extends E> collection) {
        Objects.requireNonNull(collection);

        if (collection instanceof TraversableOnce<?>) {
            int ks = ((TraversableOnce<?>) collection).knownSize();
            if (ks > 0 && size + ks > elements.length) {
                grow(size + ks);
            }
        } else if (collection instanceof List<?> && collection instanceof RandomAccess) {
            int s = ((List<?>) collection).size();
            if (size + s > elements.length) {
                grow(size + s);
            }
        }
        for (E e : collection) {
            this.append(e);
        }
    }

    @Override
    public final void prepend(E value) {
        Object[] values = elements;
        if (size == values.length) {
            values = growArray(size + 1);
        }
        System.arraycopy(elements, 0, values, 1, size);
        values[0] = value;
        this.elements = values;
        ++size;
    }

    @Override
    public final void prependAll(@NotNull Iterable<? extends E> collection) {
        Objects.requireNonNull(collection);
        if (collection instanceof IndexedSeq<?>) {
            IndexedSeq<?> seq = (IndexedSeq<?>) collection;
            int s = seq.size();
            Object[] values = elements;
            if (values.length < size + s) {
                values = growArray(size + s);
            }
            System.arraycopy(elements, 0, values, s, size);
            for (int i = 0; i < s; i++) {
                values[i] = seq.get(i);
            }
            elements = values;
            size += s;
            return;
        }


        Object[] cv = KalaCollectionUtils.asArray(collection);
        if (cv.length == 0) {
            return;
        }

        Object[] values = elements;
        if (values.length <= size + cv.length) {
            values = growArray(size + cv.length);
        }

        System.arraycopy(elements, 0, values, cv.length, size);
        System.arraycopy(cv, 0, values, 0, cv.length);
        elements = values;
        size += cv.length;
    }

    @Override
    public final void insert(int index, E element) {
        int size = this.size;
        if (index < 0 && index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == size) {
            append(element);
        }
        if (elements.length == size) {
            grow();
        }

        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        ++this.size;
    }

    @Override
    public final E remove(int index) {
        checkInBound(index);
        E v = (E) elements[index];
        System.arraycopy(elements, index + 1, elements, index, size - index);
        --size;
        return v;
    }

    @Override
    public final void remove(int index, int count) {
        if (index < 0 || index > size - count) {
            throw new IndexOutOfBoundsException(String.format("index: %d, count: %d", index, count));
        }

        System.arraycopy(elements, index + count, elements, index, size - (index + count));
        size -= count;
    }

    @Override
    public final void clear() {
        Arrays.fill(elements, null);
        size = 0;
    }

    //
    // -- MSeq
    //

    @Override
    public final E get(int index) {
        checkInBound(index);
        return (E) elements[index];
    }

    @Override
    public final void set(int index, E newValue) {
        checkInBound(index);
        elements[index] = newValue;
    }


    //
    // -- MCollection
    //

    @Override
    public final String className() {
        return "ArrayBuffer";
    }

    @NotNull
    @Override
    public final <U> ArrayBuffer.Factory<U> iterableFactory() {
        return factory();
    }

    @Override
    public int size() {
        return size;
    }

    @NotNull
    @Override
    public final Enumerator<E> iterator() {
        return (Enumerator<E>) Enumerator.ofArray(elements, 0, size);
    }

    @Override
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public final <U> U[] toArray(@NotNull IntFunction<? extends U[]> generator) {
        Objects.requireNonNull(generator);
        U[] arr = generator.apply(size);
        System.arraycopy(elements, 0, arr, 0, size);
        return arr;
    }

    //
    // -- Serializable
    //

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.write(size);
        if (size != 0) {
            Object[] values = elements.length == size ? elements : Arrays.copyOf(elements, size);
            out.writeObject(values);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.size = in.readInt();
        if (size == 0) {
            elements = MArray.EMPTY_ARRAY;
        } else {
            elements = (Object[]) in.readObject();
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
        if (!(o instanceof ArrayBuffer<?>)) {
            return false;
        }

        ArrayBuffer<?> other = (ArrayBuffer<?>) o;

        if (size != other.size) {
            return false;
        }
        return Arrays.equals(elements, other.elements);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(elements) + hashMagic;
    }

    public static final class Factory<E> implements CollectionFactory<E, ArrayBuffer<E>, ArrayBuffer<E>> {

        @Override
        public final ArrayBuffer<E> newBuilder() {
            return new ArrayBuffer<>();
        }

        @Override
        public final void addToBuilder(@NotNull ArrayBuffer<E> buffer, E value) {
            buffer.append(value);
        }

        @Override
        public void sizeHint(@NotNull ArrayBuffer<E> buffer, int size) {
            buffer.sizeHint(size);
        }

        @Override
        public final ArrayBuffer<E> mergeBuilder(@NotNull ArrayBuffer<E> builder1, @NotNull ArrayBuffer<E> builder2) {
            builder1.appendAll(builder2);
            return builder1;
        }

        @Override
        public final ArrayBuffer<E> build(@NotNull ArrayBuffer<E> buffer) {
            return buffer;
        }
    }
}
