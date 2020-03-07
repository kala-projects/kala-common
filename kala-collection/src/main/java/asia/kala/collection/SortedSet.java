package asia.kala.collection;

import asia.kala.annotations.Covariant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public interface SortedSet<@Covariant E> extends Set<E> {

    @NotNull
    default Comparator<? super E> comparator() {
        @SuppressWarnings("unchecked") final Comparator<? super E> comparator =
                (Comparator<? super E>) Comparator.naturalOrder();
        return comparator;
    }

    @Contract(pure = true)
    default E first() {
        return iterator().next();
    }

    @Contract(pure = true)
    default E last() {
        Enumerator<E> iterator = iterator();
        E res = iterator.next();
        while (iterator.hasNext()) {
            res = iterator.next();
        }
        return res;
    }

    //
}
