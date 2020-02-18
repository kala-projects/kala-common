package asia.kala.collection.mutable;

public interface Buffer<E> extends MSeq<E> {

    void append(E value);

    //
    // -- MCollection
    //

    @Override
    default String className() {
        return "Buffer";
    }
}
