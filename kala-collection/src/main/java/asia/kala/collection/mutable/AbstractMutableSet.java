package asia.kala.collection.mutable;

import asia.kala.collection.*;
import asia.kala.util.Iterators;

public abstract class AbstractMutableSet<E> extends AbstractCollection<E> implements MutableSet<E> {
    @Override
    public int hashCode() {
        return Iterators.hash(iterator()) + Collection.SET_HASH_MAGIC;
    }
}
