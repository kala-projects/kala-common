package asia.kala.collection.mutable;

import asia.kala.collection.CollectionFactory;
import asia.kala.collection.IndexedSeq;
import asia.kala.collection.Seq;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public interface MSeq<E> extends MCollection<E>, Seq<E> {

    static <E> CollectionFactory<E, ?, ? extends MSeq<E>> factory() {
        return MArray.factory();
    }

    @SafeVarargs
    static <E> MSeq<E> of(E... elements) {
        return MSeq.<E>factory().from(elements);
    }

    static <E> MSeq<E> from(@NotNull E[] elements) {
        return MSeq.<E>factory().from(elements);
    }

    static <E> MSeq<E> from(@NotNull Iterable<? extends E> iterable) {
        return MSeq.<E>factory().from(iterable);
    }

    void set(int index, E newValue);

    default void mapInPlace(@NotNull Function<? super E, ? extends E> mapper) {
        Objects.requireNonNull(mapper);

        int size = size();
        for (int i = 0; i < size; i++) {
            this.set(i, mapper.apply(this.get(i)));
        }
    }

    //
    // -- MCollection
    //

    @Override
    default String className() {
        return "MSeq";
    }

    @NotNull
    @Override
    default <U> CollectionFactory<U, ?, ? extends MSeq<U>> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    default MSeqEditor<E, ? extends MSeq<E>> edit() {
        return new MSeqEditor<>(this);
    }

    @Override
    default List<E> asJava() {
        if (this instanceof IndexedSeq<?>) {
            return new JDKConverters.MIndexedSeqAsJava<>((MSeq<E> & IndexedSeq<E>) this);
        }
        return new JDKConverters.MSeqAsJava<>(this);
    }
}
