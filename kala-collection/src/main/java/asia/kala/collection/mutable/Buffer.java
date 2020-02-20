package asia.kala.collection.mutable;

import asia.kala.collection.IndexedSeq;
import asia.kala.collection.TraversableOnce;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface Buffer<E> extends MSeq<E> {

    void append(E value);

    default void appendAll(@NotNull TraversableOnce<? extends E> collection) {
        Objects.requireNonNull(collection);
        for (E e : collection) {
            this.append(e);
        }
    }

    void prepend(E value);

    @SuppressWarnings("unchecked")
    default void prependAll(@NotNull TraversableOnce<? extends E> collection) {
        Objects.requireNonNull(collection);
        if (collection instanceof IndexedSeq<?>) {
            IndexedSeq<?> seq = (IndexedSeq<?>) collection;
            int s = seq.size();
            for (int i = s - 1; i >= 0; i--) {
                prepend((E) seq.get(i));
            }
            return;
        }

        Object[] cv = collection.toArray(Object[]::new);

        for (int i = cv.length - 1; i >= 0; i--) {
            prepend((E) cv[i]);
        }
    }

    E remove(int index);

    void remove(int index, int count);

    void clear();

    //
    // -- MCollection
    //

    @Override
    default String className() {
        return "Buffer";
    }

    @NotNull
    @Override
    default BufferEditor<E, ? extends Buffer<E>> edit() {
        return new BufferEditor<>(this);
    }
}
