package asia.kala.collection.mutable;

import asia.kala.collection.CollectionFactory;
import asia.kala.collection.IndexedSeq;
import asia.kala.collection.Seq;
import kotlin.annotations.jvm.Mutable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public interface MutableSeq<E> extends MutableCollection<E>, Seq<E> {

    @NotNull
    static <E> CollectionFactory<E, ?, ? extends MutableSeq<E>> factory() {
        return MutableArray.factory();
    }

    @NotNull
    @SafeVarargs
    static <E> MutableSeq<E> of(E... elements) {
        return MutableSeq.<E>factory().from(elements);
    }

    @NotNull
    static <E> MutableSeq<E> from(@NotNull E[] elements) {
        return MutableSeq.<E>factory().from(elements);
    }

    @NotNull
    static <E> MutableSeq<E> from(@NotNull Iterable<? extends E> iterable) {
        return MutableSeq.<E>factory().from(iterable);
    }

    @NotNull
    @Contract("_ -> new")
    static <E> MutableSeq<E> wrapJava(@NotNull @Mutable List<E> list) {
        Objects.requireNonNull(list);
        if (list instanceof RandomAccess) {
            return new JDKConverters.RandomAccessListWrapper<>(list);
        }
        return new JDKConverters.ListWrapper<>(list);
    }

    void set(int index, E newValue);

    default void mapInPlace(@NotNull Function<? super E, ? extends E> mapper) {
        Objects.requireNonNull(mapper);

        int size = size();
        for (int i = 0; i < size; i++) {
            this.set(i, mapper.apply(this.get(i)));
        }
    }

    @SuppressWarnings("unchecked")
    default void sort() {
        sort((Comparator<? super E>) Comparator.naturalOrder());
    }

    @SuppressWarnings("unchecked")
    default void sort(@NotNull Comparator<? super E> comparator) {
        Objects.requireNonNull(comparator);
        Object[] values = toObjectArray();
        Arrays.sort(values, (Comparator<? super Object>) comparator);

        for (int i = 0; i < values.length; i++) {
            this.set(i, (E) values[i]);
        }
    }

    //
    // -- MutableCollection
    //

    @Override
    default String className() {
        return "MutableSeq";
    }

    @NotNull
    @Override
    default <U> CollectionFactory<U, ?, ? extends MutableSeq<U>> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    default MutableSeqEditor<E, ? extends MutableSeq<E>> edit() {
        return new MutableSeqEditor<>(this);
    }

    @NotNull
    @Mutable
    @Override
    default List<E> asJava() {
        if (this instanceof IndexedSeq<?>) {
            return new JDKConverters.MIndexedSeqAsJava<>((MutableSeq<E> & IndexedSeq<E>) this);
        }
        return new JDKConverters.MutableSeqAsJava<>(this);
    }
}
