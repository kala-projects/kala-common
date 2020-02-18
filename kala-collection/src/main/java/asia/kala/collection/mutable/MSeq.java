package asia.kala.collection.mutable;

import asia.kala.collection.Seq;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public interface MSeq<E> extends MCollection<E>, Seq<E> {

    void set(int index, E newValue);

    default void mapInPlace(@NotNull Function<? super E, ? extends E> mapper) {
        Objects.requireNonNull(mapper);

        int size = size();
        for (int i = 0; i < size; i++) {
            this.set(i, mapper.apply(this.get(i)));
        }
    }

    //
    // -- MCollection
    //

    @Override
    default String className() {
        return "MSeq";
    }
}
