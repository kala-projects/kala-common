package asia.kala.collection.immutable;

import asia.kala.collection.Enumerator;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

final class IListIterator<E> implements Enumerator<E> {
    @NotNull
    private IList<? extends E> list;

    IListIterator(@NotNull IList<? extends E> list) {
        assert list != null;

        this.list = list;
    }

    @Override
    public final boolean hasNext() {
        return list != IList.Nil.INSTANCE;
    }

    @Override
    public final E next() {
        if (list == IList.Nil.INSTANCE) {
            throw new NoSuchElementException("IListIterator.next()");
        }

        E v = list.head();
        list = list.tail();
        return v;
    }
}
