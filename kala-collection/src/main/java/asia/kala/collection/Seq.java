package asia.kala.collection;

import asia.kala.Option;
import asia.kala.collection.immutable.IList;
import asia.kala.function.IndexedConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public interface Seq<E> extends Traversable<E> {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> Seq<E> narrow(Seq<? extends E> seq) {
        return (Seq<E>) seq;
    }

    default E get(int index) {
        return getOption(index).getOrThrow(IndexOutOfBoundsException::new);
    }

    @NotNull
    default Option<E> getOption(int index) {
        if (index < 0) {
            return Option.none();
        }

        int s = knownSize();
        if (s >= 0 && index >= s) {
            return Option.none();
        }

        int i = index;

        for (E e : this) {
            if (i-- == 0) {
                return Option.some(e);
            }
        }
        return Option.none();
    }

    default boolean isDefinedAt(int index) {
        return index >= 0 && index < size();
    }

    default int indexOf(Object element) {
        int idx = 0;
        for (E e : this) {
            if (Objects.equals(e, element)) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    default int indexOf(Object element, int from) {
        int idx = 0;
        for (E e : this) {
            if (idx >= from && Objects.equals(e, element)) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    default int indexWhere(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        int idx = 0;
        for (E e : this) {
            if (predicate.test(e)) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    default int indexWhere(@NotNull Predicate<? super E> predicate, int from) {
        Objects.requireNonNull(predicate);

        int idx = 0;
        for (E e : this) {
            if (idx >= from && predicate.test(e)) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    default int lastIndexOf(Object element) {
        int idx = size() - 1;
        Enumerator<E> it = reverseIterator();
        while (it.hasNext()) {
            if (Objects.equals(element, it.next())) {
                return idx;
            }
            --idx;
        }
        return -1;
    }

    default int lastIndexOf(Object element, int end) {
        int idx = size() - 1;
        Enumerator<E> it = reverseIterator();
        while (it.hasNext()) {
            if (idx <= end && Objects.equals(element, it.next())) {
                return idx;
            }
            --idx;
        }
        return -1;
    }

    default int lastIndexWhere(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        int idx = size() - 1;
        Enumerator<E> it = reverseIterator();
        while (it.hasNext()) {
            if (predicate.test(it.next())) {
                return idx;
            }
            --idx;
        }
        return -1;
    }

    default int lastIndexWhere(@NotNull Predicate<? super E> predicate, int end) {
        Objects.requireNonNull(predicate);

        int idx = size() - 1;
        Enumerator<E> it = reverseIterator();
        while (it.hasNext()) {
            if (idx <= end && predicate.test(it.next())) {
                return idx;
            }
            --idx;
        }
        return -1;
    }

    default void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
        iterator().forEachIndexed(action);
    }

    default Enumerator<E> reverseIterator() {
        IList<E> l = IList.nil();
        for (E e : this) {
            l = l.cons(e);
        }

        return l.iterator();
    }

    //
    // -- Traversable
    //


    @Override
    default String className() {
        return "Seq";
    }

    @NotNull
    @Override
    default SeqView<E> view() {
        return new SeqViews.Of<>(this);
    }

    @Override
    default List<E> asJava() {
        if (this instanceof IndexedSeq<?>) {
            return new JDKConverters.IndexedSeqAsJava<>((IndexedSeq<E>) this);
        }
        return new JDKConverters.SeqAsJava<>(this);
    }
}
