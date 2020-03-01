package asia.kala.collection.immutable;

import asia.kala.annotations.Covariant;
import asia.kala.collection.AbstractEnumerator;
import asia.kala.collection.Enumerator;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

final class ImmutableListIterator<@Covariant E> extends AbstractEnumerator<E> implements Enumerator<E> {
    @NotNull
    private ImmutableList<? extends E> list;

    ImmutableListIterator(@NotNull ImmutableList<? extends E> list) {
        assert list != null;

        this.list = list;
    }

    @Override
    public final boolean hasNext() {
        return list != ImmutableList.Nil.INSTANCE;
    }

    @Override
    public final E next() {
        if (list == ImmutableList.Nil.INSTANCE) {
            throw new NoSuchElementException("ImmutableListIterator.next()");
        }

        E v = list.head();
        list = list.tail();
        return v;
    }
}
