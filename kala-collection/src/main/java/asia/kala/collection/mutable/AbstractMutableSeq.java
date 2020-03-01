package asia.kala.collection.mutable;

import asia.kala.collection.*;

public abstract class AbstractMutableSeq<E> extends AbstractMutableCollection<E> implements MutableSeq<E> {
    @Override
    public int hashCode() {
        return Enumerator.hash(iterator()) + Traversable.SEQ_HASH_MAGIC;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Seq<?>) || obj instanceof View<?>) {
            return false;
        }

        return this.sameElements((Seq<?>) obj);
    }
}
