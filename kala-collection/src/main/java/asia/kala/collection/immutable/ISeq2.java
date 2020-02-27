package asia.kala.collection.immutable;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

final class ISeq2<E> extends ISeqN<E> {
    private static final long serialVersionUID = 0L;

    private final E $0;
    private final E $1;

    ISeq2(E $0, E $1) {
        this.$0 = $0;
        this.$1 = $1;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public final E get(int index) {
        switch (index) {
            case 0:
                return $0;
            case 1:
                return $1;
        }
        throw new IndexOutOfBoundsException("Index out of range: " + index);
    }

    @NotNull
    @Override
    public final ISeq<E> appended(E element) {
        return new ISeq3<>($0, $1, element);
    }

    @NotNull
    @Override
    public final ISeq<E> prepended(E element) {
        return new ISeq3<>(element, $0, $1);
    }


    @NotNull
    @Override
    public final <U> ISeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return new ISeq2<>(mapper.apply($0), mapper.apply($1));
    }
}
