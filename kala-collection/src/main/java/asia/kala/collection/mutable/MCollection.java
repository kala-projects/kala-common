package asia.kala.collection.mutable;

import asia.kala.collection.Traversable;

public interface MCollection<E> extends Traversable<E> {
    @Override
    default String className() {
        return "MCollection";
    }
}
