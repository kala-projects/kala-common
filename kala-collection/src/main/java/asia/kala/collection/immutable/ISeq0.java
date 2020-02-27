package asia.kala.collection.immutable;

import asia.kala.collection.Enumerator;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

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
    public final ISeq<E> appended(E element) {
        return new ISeq1<>(element);
    }

    @NotNull
    @Override
    public final ISeq<E> prepended(E element) {
        return new ISeq1<>(element);
    }

    @NotNull
    @Override
    public final Enumerator<E> iterator() {
        return Enumerator.empty();
    }

    @NotNull
    @Override
    public final <U> ISeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return instance();
    }

    //
    // -- Serializable
    //

    private Object readResolve() {
        return INSTANCE;
    }
}
