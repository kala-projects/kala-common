package asia.kala.collection.mutable;

import asia.kala.collection.CollectionFactory;
import asia.kala.collection.JDKConverters;
import asia.kala.collection.Traversable;
import kotlin.annotations.jvm.Mutable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface MutableCollection<E> extends Traversable<E> {
    static <E> CollectionFactory<E, ?, ? extends MutableCollection<E>> factory() {
        return MutableSeq.factory();
    }

    @SafeVarargs
    static <E> MutableCollection<E> of(E... elements) {
        return MutableCollection.<E>factory().from(elements);
    }

    static <E> MutableCollection<E> from(E @NotNull [] elements) {
        return MutableCollection.<E>factory().from(elements);
    }

    static <E> MutableCollection<E> from(@NotNull Iterable<? extends E> iterable) {
        return MutableCollection.<E>factory().from(iterable);
    }

    @Override
    default String className() {
        return "MutableCollection";
    }

    @NotNull
    @Override
    default <U> CollectionFactory<U, ?, ? extends MutableCollection<U>> iterableFactory() {
        return factory();
    }

    @NotNull
    default MutableCollectionEditor<E, ? extends MutableCollection<E>> edit() {
        return new MutableCollectionEditor<>(this);
    }

    @NotNull
    @Mutable
    @Override
    default Collection<E> asJava() {
        return new JDKConverters.MutableCollectionAsJava<>(this);
    }
}
