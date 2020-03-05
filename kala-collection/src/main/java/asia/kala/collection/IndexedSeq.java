package asia.kala.collection;

import asia.kala.Option;
import asia.kala.annotations.Covariant;
import asia.kala.collection.immutable.ImmutableArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.IntFunction;

public interface IndexedSeq<@Covariant E> extends Seq<E>, RandomAccess {

    static <E> CollectionFactory<E, ?, ? extends IndexedSeq<E>> factory() {
        return ImmutableArray.factory();
    }

    @SafeVarargs
    static <E> IndexedSeq<E> of(E... elements) {
        return IndexedSeq.<E>factory().from(elements);
    }

    static <E> IndexedSeq<E> from(E @NotNull [] elements) {
        return IndexedSeq.<E>factory().from(elements);
    }

    static <E> IndexedSeq<E> from(@NotNull Iterable<? extends E> iterable) {
        return IndexedSeq.<E>factory().from(iterable);
    }

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> IndexedSeq<E> narrow(IndexedSeq<? extends E> seq) {
        return (IndexedSeq<E>) seq;
    }

    @Override
    E get(int index);

    @NotNull
    @Override
    default Option<E> getOption(int index) {
        int size = size();
        if (index < 0 || index >= size) {
            return Option.none();
        }
        return Option.some(get(index));
    }

    @Override
    int size();

    @Override
    default int knownSize() {
        return size();
    }

    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    @NotNull
    @Override
    default Enumerator<E> iterator() {
        int size = size();
        if (size == 0) {
            return Enumerator.empty();
        }

        return new AbstractEnumerator<E>() {
            private int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < size;
            }

            @Override
            public E next() {
                if (idx >= size) {
                    throw new NoSuchElementException();
                }
                return get(idx++);
            }
        };
    }

    @NotNull
    @Override
    default Enumerator<E> reverseIterator() {
        return new AbstractEnumerator<E>() {
            private int idx = size() - 1;

            @Override
            public final boolean hasNext() {
                return idx >= 0;
            }

            @Override
            public final E next() {
                if (idx < 0) {
                    throw new NoSuchElementException();
                }
                return get(idx--);
            }
        };
    }

    //
    // -- Traversable
    //

    @Override
    @NotNull
    default IndexedSeqView<E> view() {
        return new IndexedSeqViews.Of<>(this);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <U> U[] toArray(@NotNull IntFunction<? extends U[]> generator) {
        Objects.requireNonNull(generator);

        int size = size();
        U[] arr = generator.apply(size);
        for (int i = 0; i < size; i++) {
            arr[i] = (U) get(i);
        }
        return arr;
    }
}
