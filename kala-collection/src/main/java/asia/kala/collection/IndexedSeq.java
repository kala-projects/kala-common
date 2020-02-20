package asia.kala.collection;

import asia.kala.Option;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.RandomAccess;

public interface IndexedSeq<E> extends Seq<E>, RandomAccess {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> IndexedSeq<E> narrow(IndexedSeq<? extends E> seq) {
        return (IndexedSeq<E>) seq;
    }

    @Override
    E get(int index);

    @NotNull
    @Override
    default Option<E> getOption(int index) {
        int size = size();
        if (index < 0 || index >= size) {
            return Option.none();
        }
        return Option.some(get(index));
    }

    @Override
    int size();

    @Override
    default int knownSize() {
        return size();
    }

    //
    // -- Traversable
    //

    @Override
    @NotNull
    default IndexedSeqView<E> view() {
        return new IndexedSeqViews.Of<>(this);
    }
}
