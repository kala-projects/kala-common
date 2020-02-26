package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.collection.*;
import asia.kala.collection.mutable.ArrayBuffer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
public final class IVector<E> extends AbstractISeq<E> implements IndexedSeq<E>, Serializable {
    private static final long serialVersionUID = -4395603284341829523L;
    private static final int hashMagic = -104040935;

    static final int BRANCHING_BASE = 5;
    static final int BRANCHING_FACTOR = 32;
    static final int BRANCHING_MASK = 31;

    public static final IVector<?> EMPTY = new IVector<>(IArray.empty(), 0, 0, 0);

    public static final IVector.Factory<?> FACTORY = new Factory<>();

    private final Object array;
    private final int offset, length;
    private final int depthShift;

    IVector(@NotNull Object array, int offset, int length, int depthShift) {
        this.array = array;
        this.offset = offset;
        this.length = length;
        this.depthShift = depthShift;
    }

    @NotNull
    public static <E> IVector.Factory<E> factory() {
        return (IVector.Factory<E>) FACTORY;
    }

    @NotNull
    public static <E> IVector<E> empty() {
        return (IVector<E>) EMPTY;
    }

    @NotNull
    public static <E> IVector<E> of() {
        return empty();
    }

    @NotNull
    public static <E> IVector<E> of(E... elements) {
        return from(elements);
    }

    @NotNull
    @Contract("_ -> new")
    public static <E> IVector<E> from(@NotNull E[] elements) {
        Objects.requireNonNull(elements);

        int shift = 0;
        Object[] arr = elements;
        while (arr.length > BRANCHING_FACTOR) {
            arr = ArrayUtils.spilt(arr, BRANCHING_FACTOR);
            shift += BRANCHING_BASE;
        }
        return new IVector<>(arr, 0, elements.length, shift);
    }

    public static <E> IVector<E> from(@NotNull Iterable<? extends E> elements) {
        if (elements instanceof IVector<?>) {
            return (IVector<E>) elements;
        }
        if (elements instanceof ArrayBuffer<?>) {
            Builder<E> builder = new Builder<>();
            builder.values = (ArrayBuffer<E>) elements;
            return builder.build();
        }
        return IVector.<E>factory().from(elements);
    }

    private static int treeSize(int branchCount, int depthShift) {
        final int fullBranchSize = 1 << depthShift;
        return branchCount * fullBranchSize;
    }

    static int firstDigit(int num, int depthShift) {
        return num >> depthShift;
    }

    static int digit(int num, int depthShift) {
        return lastDigit(firstDigit(num, depthShift));
    }

    static int lastDigit(int num) {
        return num & BRANCHING_MASK;
    }

    private Object modify(Object root, int depthShift, int index, NodeModifier node, NodeModifier leaf) {
        return (depthShift == 0)
                ? leaf.apply(root, index)
                : modifyNonLeaf(root, depthShift, index, node, leaf);
    }

    private Object modifyNonLeaf(Object root, int depthShift, int index, NodeModifier node, NodeModifier leaf) {
        int previousIndex = firstDigit(index, depthShift);
        root = node.apply(root, previousIndex);

        Object array = root;
        for (int shift = depthShift - BRANCHING_BASE; shift >= BRANCHING_BASE; shift -= BRANCHING_BASE) {
            final int prev = previousIndex;
            previousIndex = digit(index, shift);
            array = setNewNode(node, prev, array, previousIndex);
        }

        final Object newLeaf = leaf.apply(Array.get(array, previousIndex), lastDigit(index));
        Array.set(array, previousIndex, newLeaf);
        return root;
    }

    private Object setNewNode(NodeModifier node, int previousIndex, Object array, int offset) {
        final Object previous = Array.get(array, previousIndex);
        final Object newNode = node.apply(previous, offset);
        Array.set(array, previousIndex, newNode);
        return newNode;
    }

    //
    // -- ISeq
    //

    @Override
    public final int size() {
        return length;
    }

    public final E get(int index) {
        final Object leaf = getLeaf(index);
        final int leafIndex = lastDigit(offset + index);
        return (E) Array.get(leaf, leafIndex);
    }

    private Object getLeaf(int index) {
        if (depthShift == 0) {
            return array;
        } else {
            return getLeafGeneral(index);
        }
    }

    private Object getLeafGeneral(int index) {
        index += offset;
        Object leaf = Array.get(array, firstDigit(index, depthShift));
        for (int shift = depthShift - BRANCHING_BASE; shift > 0; shift -= BRANCHING_BASE) {
            leaf = Array.get(leaf, digit(index, shift));
        }
        return leaf;
    }

    @NotNull
    @Override
    public final IVector<E> updated(int index, E newValue) {
        final Object root = modify(array, depthShift, offset + index, NodeModifier.COPY_NODE, updateLeafWith(newValue));
        return new IVector<>(root, offset, length, depthShift);
    }

    private NodeModifier updateLeafWith(E element) {
        return (a, i) -> {
            Object arr = Arrays.copyOf((Object[]) a, Math.max(i + 1, Array.getLength(a)));
            Array.set(arr, i, element);
            return arr;
        };
    }

    @NotNull
    @Override
    public final IVector<E> prepended(E element) {
        return prependedAll(new ISeq1<>(element));
    }

    @NotNull
    @Override
    public final IVector<E> prependedAll(@NotNull Iterable<? extends E> prefix) {
        Objects.requireNonNull(prefix);

        IndexedSeq<E> seq = KalaCollectionUtils.asIndexedSeq(prefix);

        Iterator<? extends E> iterator = seq.reverseIterator();
        int size = seq.size();

        IVector<E> result = this;
        while (size > 0) {
            Object array = result.array;
            int shift = result.depthShift, offset = result.offset;
            if (result.isFullLeft()) {
                Object arr = Array.newInstance(array.getClass(), BRANCHING_FACTOR);
                Array.set(arr, BRANCHING_FACTOR - 1, array);
                array = arr;
                shift += BRANCHING_BASE;
                offset = treeSize(BRANCHING_FACTOR - 1, shift);
            }

            final int index = offset - 1;
            final int delta = Math.min(size, lastDigit(index) + 1);
            size -= delta;

            array = result.modify(array, shift, index, NodeModifier.COPY_NODE, prependToLeaf(iterator));
            result = new IVector<>(array, offset - delta, result.length + delta, shift);
        }
        return result;
    }

    private boolean isFullLeft() {
        return offset == 0;
    }

    private NodeModifier prependToLeaf(java.util.Iterator<? extends E> iterator) {
        return (array, index) -> {
            final Object copy =
                    Arrays.copyOf(((Object[]) array), Math.max(((Object[]) array).length, BRANCHING_FACTOR));
            while (iterator.hasNext() && index >= 0) {
                Array.set(copy, index--, iterator.next());
            }
            return copy;
        };
    }

    @NotNull
    @Override
    public final IVector<E> appended(E element) {
        return appendedAll(new ISeq1<>(element));
    }

    @NotNull
    @Override
    public final IVector<E> appendedAll(@NotNull Iterable<? extends E> postfix) {
        Objects.requireNonNull(postfix);

        int size = KalaCollectionUtils.knowSize(postfix);
        Iterator<? extends E> iterator;
        if (size < 0) {
            IndexedSeq<E> seq = KalaCollectionUtils.asIndexedSeq(postfix);
            iterator = seq.iterator();
            size = seq.size();
        } else {
            iterator = postfix.iterator();
        }

        IVector<E> result = this;
        while (size > 0) {
            Object array = result.array;
            int shift = result.depthShift;
            if (result.isFullRight()) {
                array = ArrayUtils.wrapInArray(array);
                shift += BRANCHING_BASE;
            }

            final int index = offset + result.length;
            final int leafSpace = lastDigit(index);
            final int delta = Math.min(size, BRANCHING_FACTOR - leafSpace);
            size -= delta;

            array = result.modify(array, shift, index, NodeModifier.COPY_NODE, appendToLeaf(iterator, leafSpace + delta));
            result = new IVector<>(array, offset, result.length + delta, shift);
        }
        return result;
    }

    private boolean isFullRight() {
        return (offset + length + 1) > treeSize(BRANCHING_FACTOR, depthShift);
    }

    private NodeModifier appendToLeaf(Iterator<? extends E> iterator, int leafSize) {
        return (array, index) -> {
            final Object copy =
                    Arrays.copyOf((Object[]) array, Math.max(((Object[]) array).length, leafSize));
            while (iterator.hasNext() && index < leafSize) {
                Array.set(copy, index++, iterator.next());
            }
            return copy;
        };
    }

    @NotNull
    @Override
    public final IVector<E> take(int n) {
        if (n <= 0) {
            return empty();
        }
        if (n >= length) {
            return this;
        }

        final int index = n - 1;
        final Object root = arePointingToSameLeaf(index, length - 1)
                ? array
                : modify(array, depthShift, offset + index,
                (arr, idx) -> {
                    int len = Array.getLength(arr);
                    Object newArr = Array.newInstance(arr.getClass().getComponentType(), len);
                    System.arraycopy(arr, 0, newArr, 0, idx + 1);
                    return newArr;
                },
                NodeModifier.IDENTITY);
        return collapsed(root, offset, n, depthShift);
    }

    @NotNull
    @Override
    public final IVector<E> takeWhile(@NotNull Predicate<? super E> predicate) {
        if (this.isEmpty()) {
            return empty();
        }

        int count = 0;
        for (E e : this) {
            if (!predicate.test(e)) {
                break;
            }
            ++count;
        }

        return take(count);
    }

    @NotNull
    @Override
    public final IVector<E> drop(int n) {
        if (n <= 0) {
            return this;
        }
        if (n >= length) {
            return empty();
        }

        final int index = offset + n;
        final Object root = arePointingToSameLeaf(0, n)
                ? array
                : modify(array, depthShift, index,
                (arr, idx) -> {
                    int len = Array.getLength(arr);
                    Object newArr = Array.newInstance(arr.getClass().getComponentType(), len);
                    System.arraycopy(arr, idx, newArr, idx, len - idx);
                    return newArr;
                },
                NodeModifier.IDENTITY);
        return collapsed(root, index, length - n, depthShift);
    }

    @NotNull
    @Override
    public final IVector<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        if (this.isEmpty()) {
            return empty();
        }

        int count = 0;
        for (E e : this) {
            if (!predicate.test(e)) {
                break;
            }
            ++count;
        }

        return drop(count);
    }

    private boolean arePointingToSameLeaf(int i, int j) {
        return firstDigit(offset + i, BRANCHING_BASE) == firstDigit(offset + j, BRANCHING_BASE);
    }

    private static <T> IVector<T> collapsed(Object array, int offset, int length, int shift) {
        for (; shift > 0; shift -= BRANCHING_BASE) {
            final int skippedElements = Array.getLength(array) - 1;
            if (skippedElements != digit(offset, shift)) {
                break;
            }
            array = Array.get(array, skippedElements);
            offset -= treeSize(skippedElements, shift);
        }
        return new IVector<>(array, offset, length, shift);
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
        if (o == this) {
            return true;
        }
        if (!(o instanceof IVector<?>)) {
            return false;
        }

        IVector<?> other = (IVector<?>) o;

        if (other.length != this.length) {
            return false;
        }

        return sameElements(other);
    }

    @Override
    public final int hashCode() {
        return KalaCollectionUtils.hash(iterator()) + hashMagic;
    }

    public static final class Builder<E> {
        ArrayBuffer<E> values;

        public Builder() {
            values = new ArrayBuffer<>();
        }

        public final void append(E value) {
            values.append(value);
        }

        public final void clear() {
            values.clear();
        }

        @NotNull
        public final IVector<E> build() {
            if (values.isEmpty()) {
                return empty();
            }
            int shift = 0;
            Object[] arr = values.toArray(Object[]::new);
            while (arr.length > BRANCHING_FACTOR) {
                arr = ArrayUtils.spilt(arr, BRANCHING_FACTOR);
                shift += BRANCHING_BASE;
            }
            return new IVector<>(arr, 0, values.size(), shift);
        }
    }

    public static final class Factory<E> implements CollectionFactory<E, Builder<E>, IVector<E>> {
        Factory() {
        }

        @Override
        public final IVector<E> empty() {
            return IVector.empty();
        }

        @NotNull
        @Override
        public final IVector<E> from(@NotNull E[] elements) {
            return IVector.from(elements);
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
            builder1.values.appendAll(builder2.values);
            return builder1;
        }

        @Override
        public final IVector<E> build(@NotNull Builder<E> builder) {
            return builder.build();
        }
    }

    @FunctionalInterface
    interface NodeModifier {
        Object apply(Object array, int index);

        NodeModifier COPY_NODE = (o, i) -> Arrays.copyOf((Object[]) o, Math.max(i + 1, Array.getLength(o)));
        NodeModifier IDENTITY = (o, i) -> o;
    }
}
