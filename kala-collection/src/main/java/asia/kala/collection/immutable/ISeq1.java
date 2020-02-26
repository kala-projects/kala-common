package asia.kala.collection.immutable;

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
}
