package asia.kala.collection.immutable;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

final class ISeq4<E> extends ISeqN<E> {
    private static final long serialVersionUID = 0L;

    private final E $0;
    private final E $1;
    private final E $2;
    private final E $3;

    ISeq4(E $0, E $1, E $2, E $3) {
        this.$0 = $0;
        this.$1 = $1;
        this.$2 = $2;
        this.$3 = $3;
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public final E get(int index) {
        switch (index) {
            case 0:
                return $0;
            case 1:
                return $1;
            case 2:
                return $2;
            case 3:
                return $3;
        }
        throw new IndexOutOfBoundsException("Index out of range: " + index);
    }

    @NotNull
    @Override
    public final ISeq<E> appended(E element) {
        return new ISeq5<>($0, $1, $2, $3, element);
    }

    @NotNull
    @Override
    public final ISeq<E> prepended(E element) {
        return new ISeq5<>(element, $0, $1, $2, $3);
    }

    @NotNull
    @Override
    public final <U> ISeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return new ISeq4<>(mapper.apply($0), mapper.apply($1), mapper.apply($2), mapper.apply($3));
    }
}
