package asia.kala.collection.mutable;

import asia.kala.collection.Traversable;
import org.jetbrains.annotations.NotNull;

public interface MCollection<E> extends Traversable<E> {
    @Override
    default String className() {
        return "MCollection";
    }

    @NotNull
    default MCollectionEditor<E, ? extends MCollection<E>> edit() {
        return new MCollectionEditor<>(this);
    }
}
