package asia.kala.collection.mutable;

import asia.kala.collection.*;

public abstract class AbstractMutableSeq<E> extends AbstractMutableCollection<E> implements MutableSeq<E> {
    @Override
    public int hashCode() {
        return Enumerator.hash(iterator()) + Traversable.SEQ_HASH_MAGIC;
    }
}
