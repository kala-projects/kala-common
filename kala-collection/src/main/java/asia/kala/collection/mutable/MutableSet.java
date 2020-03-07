package asia.kala.collection.mutable;

import asia.kala.collection.*;
import kotlin.annotations.jvm.Mutable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

public interface MutableSet<E> extends MutableCollection<E>, Set<E> {

    @Contract(mutates = "this")
    boolean add(E value);

    @Contract(mutates = "this")
    default boolean addAll(@NotNull Iterable<? extends E> values) {
        boolean m = false;
        for (E value : values) {
            if (this.add(value)) {
                m = true;
            }
        }
        return m;
    }

    @Contract(mutates = "this")
    default boolean addAll(E @NotNull [] values) {
        Objects.requireNonNull(values);

        return addAll(ArraySeq.wrap(values));
    }

    @Contract(mutates = "this")
    boolean remove(E value);

    @Contract(mutates = "this")
    default boolean removeAll(@NotNull Iterable<? extends E> values) {
        Objects.requireNonNull(values);
        boolean m = false;
        for (E value : values) {
            if (remove(value)) {
                m = true;
            }
        }
        return m;
    }

    @Contract(mutates = "this")
    default boolean removeAll(E @NotNull [] values) {
        Objects.requireNonNull(values);
        return removeAll(ArraySeq.wrap(values));
    }

    @Contract(mutates = "this")
    void clear();

    @Contract(mutates = "this")
    default boolean retainIf(@NotNull Predicate<? super E> predicate) {
        int oldSize = size();
        filterInPlace(predicate);
        return size() != oldSize;
    }

    @Contract(mutates = "this")
    @SuppressWarnings("unchecked")
    default boolean retainAll(@NotNull Iterable<? super E> values) {
        Objects.requireNonNull(values);

        if (isEmpty()) {
            return false;
        }

        Traversable<E> t = KalaCollectionUtils.asTraversable(values);
        if (t.isEmpty()) {
            return false;
        }

        Object[] arr = toObjectArray();
        boolean m = false;

        for (Object value : arr) {
            if (!t.contains(value)) {
                this.remove((E) value);
                m = true;
            }
        }
        return m;
    }

    @Contract(mutates = "this")
    default boolean retainAll(E @NotNull [] values) {
        return retainAll(ArraySeq.wrap(values));
    }

    @Contract(mutates = "this")
    @SuppressWarnings("unchecked")
    default void filterInPlace(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        if (isEmpty()) {
            return;
        }

        Object[] arr = toObjectArray();
        for (Object e : arr) {
            if (!predicate.test((E) e)) {
                this.remove((E) e);
            }
        }
    }

    //
    // -- MutableCollection
    //

    @Override
    default String className() {
        return "MutableSet";
    }

    @NotNull
    @Override
    default MutableSetEditor<E, ? extends MutableSet<E>> edit() {
        return new MutableSetEditor<>(this);
    }

    @NotNull
    @Mutable
    @Override
    default java.util.Set<E> asJava() {
        return new JDKConverters.MutableSetAsJava<>(this);
    }
}
