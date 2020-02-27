package asia.kala.collection.mutable;

import asia.kala.collection.*;
import kotlin.annotations.jvm.Mutable;
import kotlin.annotations.jvm.ReadOnly;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Predicate;

public interface Buffer<E> extends MSeq<E> {

    @NotNull
    static <E> CollectionFactory<E, ?, ? extends Buffer<E>> factory() {
        return ArrayBuffer.factory();
    }

    @NotNull
    @SafeVarargs
    static <E> Buffer<E> of(E... elements) {
        return Buffer.<E>factory().from(elements);
    }

    @NotNull
    static <E> Buffer<E> from(@NotNull E[] elements) {
        return Buffer.<E>factory().from(elements);
    }

    @NotNull
    static <E> Buffer<E> from(@NotNull Iterable<? extends E> iterable) {
        return Buffer.<E>factory().from(iterable);
    }

    @NotNull
    @Contract("_ -> new")
    static <E> Buffer<E> wrapJava(@NotNull @Mutable List<E> list) {
        Objects.requireNonNull(list);
        if (list instanceof RandomAccess) {
            return new JDKConverters.RandomAccessResizableListWrapper<>(list);
        }
        return new JDKConverters.ResizableListWrapper<>(list);
    }

    @Contract(mutates = "this")
    void append(E value);

    @Contract(mutates = "this")
    default void appendAll(@NotNull @ReadOnly Iterable<? extends E> collection) {
        Objects.requireNonNull(collection);
        for (E e : collection) {
            this.append(e);
        }
    }

    @Contract(mutates = "this")
    default void appendAll(@NotNull E[] elements) {
        this.appendAll(MArray.wrap(elements));
    }

    @Contract(mutates = "this")
    void prepend(E value);

    @Contract(mutates = "this")
    @SuppressWarnings("unchecked")
    default void prependAll(@NotNull @ReadOnly Iterable<? extends E> collection) {
        Objects.requireNonNull(collection);
        if (collection instanceof Seq<?>) {
            Enumerator<?> iterator = ((Seq<?>) collection).reverseIterator();
            while (iterator.hasNext()) {
                this.prepend((E) iterator.next());
            }
            return;
        }

        if (collection instanceof List<?> && collection instanceof RandomAccess) {
            List<?> seq = (List<?>) collection;
            int s = seq.size();
            for (int i = s - 1; i >= 0; i--) {
                prepend((E) seq.get(i));
            }
            return;
        }

        Object[] cv = KalaCollectionUtils.asArray(collection);

        for (int i = cv.length - 1; i >= 0; i--) {
            prepend((E) cv[i]);
        }
    }

    @Contract(mutates = "this")
    default void prependAll(@NotNull E[] elements) {
        this.prependAll(MArray.wrap(elements));
    }

    @Contract(mutates = "this")
    void insert(int index, E element);

    @Contract(mutates = "this")
    default void insertAll(int index, @NotNull @ReadOnly Iterable<? extends E> elements) {
        Objects.requireNonNull(elements);

        for (E e : elements) {
            insert(index++, e);
        }
    }

    @Contract(mutates = "this")
    default void insertAll(int index, @NotNull E[] elements) {
        insertAll(index, MArray.wrap(elements));
    }

    @Contract(mutates = "this")
    @Flow(sourceIsContainer = true)
    E remove(@Range(from = 0, to = Integer.MAX_VALUE) int index);

    @Contract(mutates = "this")
    default void remove(int index, int count) {
        for (int i = 0; i < count; i++) {
            remove(index);
        }
    }

    @Contract(mutates = "this")
    void clear();

    @Contract(mutates = "this")
    default void dropInPlace(int n) {
        if (n <= 0) {
            return;
        }
        remove(0, Integer.min(n, size()));
    }

    @Contract(mutates = "this")
    default void dropWhileInPlace(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        int idx = indexWhere(predicate.negate());
        if (idx < 0) {
            clear();
        } else {
            dropInPlace(idx);
        }
    }

    @Contract(mutates = "this")
    default void takeInPlace(int n) {
        int size = this.size();
        if (n >= size) {
            return;
        }
        remove(n, size - n);
    }

    @Contract(mutates = "this")
    default void takeWhileInPlace(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        int idx = indexWhere(predicate.negate());
        if (idx >= 0) {
            takeInPlace(idx);
        }
    }

    //
    // -- MCollection
    //

    @Override
    default String className() {
        return "Buffer";
    }

    @Override
    @NotNull
    default <U> CollectionFactory<U, ?, ? extends Buffer<U>> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    default BufferEditor<E, ? extends Buffer<E>> edit() {
        return new BufferEditor<>(this);
    }

    @NotNull
    @Mutable
    @Override
    default List<E> asJava() {
        if (this instanceof IndexedSeq<?>) {
            return new JDKConverters.IndexedBufferAsJava<>((Buffer<E> & IndexedSeq<E>) this);
        }
        return new JDKConverters.BufferAsJava<>(this);
    }
}
