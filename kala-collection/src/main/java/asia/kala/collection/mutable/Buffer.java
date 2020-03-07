package asia.kala.collection.mutable;

import asia.kala.collection.*;
import asia.kala.collection.JDKConverters;
import kotlin.annotations.jvm.Mutable;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Predicate;

public interface Buffer<E> extends MutableSeq<E> {

    //region Factory methods

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
    static <E> Buffer<E> from(E @NotNull [] elements) {
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
        return new asia.kala.collection.JDKConverters.ResizableListWrapper<>(list);
    }

    //endregion

    @Contract(mutates = "this")
    void append(@Flow(targetIsContainer = true) E value);

    @Contract(mutates = "this")
    default void appendAll(
            @NotNull @Flow(sourceIsContainer = true, targetIsContainer = true) Iterable<? extends E> collection
    ) {
        Objects.requireNonNull(collection);
        for (E e : collection) {
            this.append(e);
        }
    }

    @Contract(mutates = "this")
    default void appendAll(@Flow(sourceIsContainer = true, targetIsContainer = true) E @NotNull [] elements) {
        this.appendAll(ArraySeq.wrap(elements));
    }

    @Contract(mutates = "this")
    void prepend(E value);

    @Contract(mutates = "this")
    @SuppressWarnings("unchecked")
    default void prependAll(
            @NotNull @Flow(sourceIsContainer = true, targetIsContainer = true) Iterable<? extends E> collection
    ) {
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
    default void prependAll(@Flow(sourceIsContainer = true, targetIsContainer = true) E @NotNull [] elements) {
        this.prependAll(ArraySeq.wrap(elements));
    }

    @Contract(mutates = "this")
    void insert(int index, @Flow(targetIsContainer = true) E element);

    @Contract(mutates = "this")
    default void insertAll(
            int index,
            @NotNull @Flow(sourceIsContainer = true, targetIsContainer = true) Iterable<? extends E> elements
    ) {
        Objects.requireNonNull(elements);

        for (E e : elements) {
            insert(index++, e);
        }
    }

    @Contract(mutates = "this")
    default void insertAll(
            int index,
            @Flow(sourceIsContainer = true, targetIsContainer = true) E @NotNull [] elements) {
        insertAll(index, ArraySeq.wrap(elements));
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

    //region MutableCollection members

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
            return new asia.kala.collection.JDKConverters.IndexedBufferAsJava<>((Buffer<E> & IndexedSeq<E>) this);
        }
        return new asia.kala.collection.JDKConverters.BufferAsJava<>(this);
    }

    //endregion
}
