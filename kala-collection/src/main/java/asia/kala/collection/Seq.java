package asia.kala.collection;

import asia.kala.Option;
import asia.kala.function.IndexedConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
        return iterator().indexOf(element);
    }

    default int indexOf(Object element, int from) {
        return iterator().indexOf(element, from);
    }

    default int indexWhere(@NotNull Predicate<? super E> predicate) {
        return iterator().indexWhere(predicate);
    }

    default int indexWhere(@NotNull Predicate<? super E> predicate, int from) {
        return iterator().indexWhere(predicate, from);
    }

    default void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
        iterator().forEachIndexed(action);
    }

    //
    // -- Traversable
    //


    @Override
    default String className() {
        return "Seq";
    }
}
