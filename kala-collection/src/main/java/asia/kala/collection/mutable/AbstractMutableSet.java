package asia.kala.collection.mutable;

import asia.kala.collection.*;

public abstract class AbstractMutableSet<E> extends AbstractTraversable<E> implements MutableSet<E> {
    @Override
    public int hashCode() {
        return Enumerator.hash(iterator()) + Traversable.SET_HASH_MAGIC;
    }
}
