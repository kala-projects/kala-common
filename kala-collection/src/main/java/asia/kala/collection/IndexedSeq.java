package asia.kala.collection;

import asia.kala.Option;
import org.jetbrains.annotations.NotNull;

import java.util.RandomAccess;

public interface IndexedSeq<E> extends Seq<E>, RandomAccess {
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
