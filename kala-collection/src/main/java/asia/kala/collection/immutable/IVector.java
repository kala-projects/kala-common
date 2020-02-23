package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.collection.ArrayUtils;
import asia.kala.collection.CollectionFactory;
import asia.kala.collection.IndexedSeq;
import asia.kala.collection.KalaCollectionUtils;
import asia.kala.collection.mutable.ArrayBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public final class IVector<E> extends AbstractISeq<E> implements IndexedSeq<E>, Serializable {
    private static final long serialVersionUID = -4395603284341829523L;
    private static final int hashMagic = -104040935;

    static final int VECTOR_SHIFT = 5;
    static final int VECTOR_FACTOR = 32;
    static final int VECTOR_MASK = 31;

    public static final IVector<?> EMPTY = new IVector<>(IArray.empty(), 0, 0, 0);

    public static final IVector.Factory<?> FACTORY = new Factory<>();

    private final Object array;

    @Range(from = 0, to = VECTOR_SHIFT)
    private final int depth;

    @Range(from = 0, to = 31)
    private final int offset;
    private final int length;

    IVector(@NotNull Object array, int depth, int offset, int length) {
        this.array = array;
        this.depth = depth;
        this.offset = offset;
        this.length = length;
    }

    public static <E> IVector.Factory<E> factory() {
        return (IVector.Factory<E>) FACTORY;
    }

    public static <E> IVector<E> empty() {
        return (IVector<E>) EMPTY;
    }

    public static <E> IVector<E> of() {
        return empty();
    }

    public static <E> IVector<E> of(E... elements) {
        return IVector.<E>factory().from(elements);
    }

    public static <E> IVector<E> from(@NotNull E[] elements) {
        return IVector.<E>factory().from(elements);
    }

    public static <E> IVector<E> from(@NotNull Iterable<? extends E> elements) {
        if (elements instanceof IVector<?>) {
            return (IVector<E>) elements;
        }
        return IVector.<E>factory().from(elements);
    }

    private E getElem(int index) {
        switch (depth) {
            case 0:
                return (E) ((Object[]) array)
                        [index & VECTOR_MASK];
            case 1:
                return (E) ((Object[][]) array)
                        [(index >>> 5) & VECTOR_MASK]
                        [index & VECTOR_MASK];
            case 2:
                return (E) ((Object[][][]) array)
                        [(index >>> 10) & VECTOR_MASK]
                        [(index >>> 5) & VECTOR_MASK]
                        [index & VECTOR_MASK];
            case 3:
                return (E) ((Object[][][][]) array)
                        [(index >>> 15) & VECTOR_MASK]
                        [(index >>> 10) & VECTOR_MASK]
                        [(index >>> 5) & VECTOR_MASK]
                        [index & VECTOR_MASK];
            case 4:
                return (E) ((Object[][][][][]) array)
                        [(index >>> 20) & VECTOR_MASK]
                        [(index >>> 15) & VECTOR_MASK]
                        [(index >>> 10) & VECTOR_MASK]
                        [(index >>> 5) & VECTOR_MASK]
                        [index & VECTOR_MASK];
            case 5:
                return (E) ((Object[][][][][][]) array)
                        [(index >>> 25) & VECTOR_MASK]
                        [(index >>> 20) & VECTOR_MASK]
                        [(index >>> 15) & VECTOR_MASK]
                        [(index >>> 10) & VECTOR_MASK]
                        [(index >>> 5) & VECTOR_MASK]
                        [index & VECTOR_MASK];
        }
        throw new AssertionError("illegal depth: " + depth);
    }

    private void checkInBound(int index) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
    }

    //
    // -- ISeq
    //

    public final E get(int index) {
        checkInBound(index);

        return getElem(index + offset);
    }

    @Override
    public final int size() {
        return length;
    }

    //
    // -- ICollection
    //

    @Override
    public final String className() {
        return "IVector";
    }

    @NotNull
    @Override
    public final <U> IVector.Factory<U> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    public final <U> IVector<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return mapImpl(mapper);
    }

    @NotNull
    @Override
    public final IVector<E> filter(@NotNull Predicate<? super E> predicate) {
        return filterImpl(predicate);
    }

    @NotNull
    @Override
    public final IVector<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return filterNotImpl(predicate);
    }

    @NotNull
    @Override
    public final Tuple2<IVector<E>, IVector<E>> span(@NotNull Predicate<? super E> predicate) {
        return spanImpl(predicate);
    }

    @Override
    public final boolean equals(Object o) {
        return o instanceof IVector<?> && this.sameElements((IVector<?>) o);
    }

    @Override
    public final int hashCode() {
        return KalaCollectionUtils.hash(iterator()) + hashMagic;
    }

    public static final class Builder<E> {
        private ArrayBuffer<Object[]> blocks;
        private Object[] current;

        private int length = 0;
        private int currentLength = 0;

        public Builder() {
            this.blocks = new ArrayBuffer<>();
            this.current = new Object[VECTOR_FACTOR];

            blocks.append(current);
        }

        public final void append(E value) {
            if (currentLength >= VECTOR_FACTOR) {
                current = new Object[VECTOR_FACTOR];
                blocks.append(current);
                currentLength = 0;
            }

            current[currentLength++] = value;
            ++length;
        }

        public final void clear() {
            blocks = null;
            current = null;
            length = currentLength = -1;
        }

        public IVector<E> build() {
            IVector<E> ans = buildImpl();
            clear();
            return ans;
        }

        private IVector<E> buildImpl() {
            ArrayBuffer<Object[]> blocks = this.blocks;
            int blockCount = blocks.size();
            if (blockCount == 1) {
                if (length == 0) {
                    return IVector.empty();
                }
                Object[] arr = length == VECTOR_FACTOR
                        ? blocks.get(0)
                        : Arrays.copyOf(blocks.get(0), length);
                return new IVector<>(arr, 0, 0, length);
            }

            blocks.set(
                    blockCount - 1,
                    Arrays.copyOf(blocks.get(blockCount - 1), currentLength)
            );

            int depth = 1;
            Object bcs = blocks.toArray(Object[][]::new);

            while (depth <= VECTOR_SHIFT) {
                if (Array.getLength(bcs) <= VECTOR_FACTOR) {
                    return new IVector<>(bcs, depth, 0, length);
                }
                bcs = ArrayUtils.spilt((Object[]) bcs, VECTOR_FACTOR);
                ++depth;
            }

            throw new AssertionError();
        }

    }

    public static final class Factory<E> implements CollectionFactory<E, Builder<E>, IVector<E>> {
        @Override
        public final IVector<E> empty() {
            return IVector.empty();
        }

        @Override
        public final Builder<E> newBuilder() {
            return new Builder<>();
        }

        @Override
        public void addToBuilder(@NotNull Builder<E> builder, E value) {
            builder.append(value);
        }

        @Override
        public final Builder<E> mergeBuilder(@NotNull Builder<E> builder1, @NotNull Builder<E> builder2) {
            addAllToBuilder(builder1, builder2.build());
            return builder1;
        }

        @Override
        public final IVector<E> build(@NotNull Builder<E> builder) {
            return builder.build();
        }
    }
}
