package asia.kala.collection.mutable;

import asia.kala.collection.CollectionFactory;
import asia.kala.collection.Traversable;
import org.jetbrains.annotations.NotNull;

public interface MCollection<E> extends Traversable<E> {
    @Override
    default String className() {
        return "MCollection";
    }

    @NotNull
    @Override
    default <U> CollectionFactory<U, ?, ? extends MCollection<U>> iterableFactory() {
        return MArray.factory();
    }

    @NotNull
    default MCollectionEditor<E, ? extends MCollection<E>> edit() {
        return new MCollectionEditor<>(this);
    }
}
