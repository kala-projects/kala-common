package asia.kala.collection.immutable;

import asia.kala.collection.IndexedSeq;
import asia.kala.collection.KalaCollectionUtils;

import java.io.Serializable;

abstract class ISeqN<E> extends AbstractISeq<E> implements IndexedSeq<E>, Serializable {
    private static final int hashMagic = 7320712;

    @Override
    public abstract int size();

    @Override
    public abstract E get(int index);

    @Override
    public final String className() {
        return "ISeq";
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        ISeqN<?> other = (ISeqN<?>) obj;

        return size() == other.size() && this.sameElements(other);
    }

    @Override
    public final int hashCode() {
        return KalaCollectionUtils.hash(iterator()) + hashMagic;
    }
}
