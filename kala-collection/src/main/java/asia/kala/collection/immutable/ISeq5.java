package asia.kala.collection.immutable;

final class ISeq5<E> extends ISeqN<E> {
    private static final long serialVersionUID = 0L;

    private final E $0;
    private final E $1;
    private final E $2;
    private final E $3;
    private final E $4;

    ISeq5(E $0, E $1, E $2, E $3, E $4) {
        this.$0 = $0;
        this.$1 = $1;
        this.$2 = $2;
        this.$3 = $3;
        this.$4 = $4;
    }

    @Override
    public int size() {
        return 5;
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
            case 4:
                return $4;
        }
        throw new IndexOutOfBoundsException("Index out of range: " + index);
    }
}
