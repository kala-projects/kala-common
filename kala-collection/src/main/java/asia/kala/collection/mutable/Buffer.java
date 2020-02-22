package asia.kala.collection.mutable;

import asia.kala.collection.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;

public interface Buffer<E> extends MSeq<E> {

    static <E> CollectionFactory<E, ?, ? extends Buffer<E>> factory() {
        return ArrayBuffer.factory();
    }

    @SafeVarargs
    static <E> Buffer<E> of(E... elements) {
        return Buffer.<E>factory().from(elements);
    }

    static <E> Buffer<E> from(@NotNull E[] elements) {
        return Buffer.<E>factory().from(elements);
    }

    static <E> Buffer<E> from(@NotNull Iterable<? extends E> iterable) {
        return Buffer.<E>factory().from(iterable);
    }

    void append(E value);

    default void appendAll(@NotNull Iterable<? extends E> collection) {
        Objects.requireNonNull(collection);
        for (E e : collection) {
            this.append(e);
        }
    }

    void prepend(E value);

    @SuppressWarnings("unchecked")
    default void prependAll(@NotNull Iterable<? extends E> collection) {
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

    void insert(int index, E element);

    default void insertAll(int index, @NotNull Iterable<? extends E> elements) {
        Objects.requireNonNull(elements);

        for (E e : elements) {
            insert(index++, e);
        }
    }

    E remove(int index);

    default void remove(int index, int count) {
        for (int i = 0; i < count; i++) {
            remove(index);
        }
    }

    void clear();

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

    @Override
    default List<E> asJava() {
        if (this instanceof IndexedSeq<?>) {
            return new JDKConverters.IndexedBufferAsJava<>((Buffer<E> & IndexedSeq<E>) this);
        }
        return new JDKConverters.BufferAsJava<>(this);
    }
}
