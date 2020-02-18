package asia.kala.collection;

import java.util.RandomAccess;

public interface IndexedSeq<E> extends Seq<E>, RandomAccess {
    @Override
    E get(int index);

    @Override
    int size();

    @Override
    default int knownSize() {
        return size();
    }
}
