package asia.kala.collection.immutable;

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
}
