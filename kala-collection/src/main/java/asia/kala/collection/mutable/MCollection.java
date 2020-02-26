package asia.kala.collection.mutable;

import asia.kala.collection.CollectionFactory;
import asia.kala.collection.Traversable;
import asia.kala.collection.TraversableOnce;
import kotlin.annotations.jvm.Mutable;
import org.jetbrains.annotations.NotNull;

public interface MCollection<E> extends Traversable<E> {
    static <E> CollectionFactory<E, ?, ? extends MCollection<E>> factory() {
        return MSeq.factory();
    }

    @SafeVarargs
    static <E> MCollection<E> of(E... elements) {
        return MCollection.<E>factory().from(elements);
    }

    static <E> MCollection<E> from(@NotNull E[] elements) {
        return MCollection.<E>factory().from(elements);
    }

    static <E> MCollection<E> from(@NotNull Iterable<? extends E> iterable) {
        return MCollection.<E>factory().from(iterable);
    }

    @Override
    default String className() {
        return "MCollection";
    }

    @NotNull
    @Override
    default <U> CollectionFactory<U, ?, ? extends MCollection<U>> iterableFactory() {
        return factory();
    }

    @NotNull
    default MCollectionEditor<E, ? extends MCollection<E>> edit() {
        return new MCollectionEditor<>(this);
    }

    @NotNull
    @Mutable
    @Override
    default Iterable<E> asJava() {
        return this;
    }
}
