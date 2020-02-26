package asia.kala.collection.immutable;

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
}
