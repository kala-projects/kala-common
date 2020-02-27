package asia.kala.collection.immutable;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

final class ISeq3<E> extends ISeqN<E> {
    private static final long serialVersionUID = 0L;

    private final E $0;
    private final E $1;
    private final E $2;

    ISeq3(E $0, E $1, E $2) {
        this.$0 = $0;
        this.$1 = $1;
        this.$2 = $2;
    }

    @Override
    public int size() {
        return 3;
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
        }
        throw new IndexOutOfBoundsException("Index out of range: " + index);
    }

    @NotNull
    @Override
    public final ISeq<E> appended(E element) {
        return new ISeq4<>($0, $1, $2, element);
    }

    @NotNull
    @Override
    public final ISeq<E> prepended(E element) {
        return new ISeq4<>(element, $0, $1, $2);
    }


    @NotNull
    @Override
    public final <U> ISeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return new ISeq3<>(mapper.apply($0), mapper.apply($1), mapper.apply($2));
    }
}
