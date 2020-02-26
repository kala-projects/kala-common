package asia.kala.collection.immutable;

import asia.kala.collection.Enumerator;
import org.jetbrains.annotations.NotNull;

final class ISeq0<E> extends ISeqN<E> {
    private static final long serialVersionUID = 0L;

    private static final ISeq0<?> INSTANCE = new ISeq0<>();

    private ISeq0() {
    }

    @SuppressWarnings("unchecked")
    static <E> ISeq0<E> instance() {
        return (ISeq0<E>) INSTANCE;
    }

    @Override
    public final int size() {
        return 0;
    }

    @Override
    public final E get(int index) {
        throw new IndexOutOfBoundsException("Index out of range: " + index);
    }

    @NotNull
    @Override
    public final Enumerator<E> iterator() {
        return Enumerator.empty();
    }

    //
    // -- Serializable
    //

    private Object readResolve() {
        return INSTANCE;
    }
}
