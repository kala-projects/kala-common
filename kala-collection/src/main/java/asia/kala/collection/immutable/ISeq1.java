package asia.kala.collection.immutable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

final class ISeq1<E> extends ISeqN<E> {
    private static final long serialVersionUID = 0L;

    private final E $0;

    ISeq1(E $0) {
        this.$0 = $0;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public final E get(int index) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (index) {
            case 0:
                return $0;
        }
        throw new IndexOutOfBoundsException("Index out of range: " + index);
    }

    @NotNull
    @Override
    public final ISeq<E> appended(E element) {
        return new ISeq2<>($0, element);
    }

    @NotNull
    @Override
    public final ISeq<E> prepended(E element) {
        return new ISeq2<>(element, $0);
    }

    @NotNull
    @Override
    public final <U> ISeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return new ISeq1<>(mapper.apply($0));
    }
}
