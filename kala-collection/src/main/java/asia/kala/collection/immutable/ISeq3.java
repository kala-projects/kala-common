package asia.kala.collection.immutable;

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
}
