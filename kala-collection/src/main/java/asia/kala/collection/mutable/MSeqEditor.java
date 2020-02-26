package asia.kala.collection.mutable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Function;

public class MSeqEditor<E, C extends MSeq<E>> extends MCollectionEditor<E, C> {
    public MSeqEditor(@NotNull C source) {
        super(source);
    }

    @NotNull
    @Contract("_, _ -> this")
    public MSeqEditor<E, C> set(int index, E newValue) {
        source.set(index, newValue);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public MSeqEditor<E, C> mapInPlace(@NotNull Function<? super E, ? extends E> mapper) {
        source.mapInPlace(mapper);
        return this;
    }

    @NotNull
    @Contract("-> this")
    public MSeqEditor<E, C> sort() {
        source.sort();
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public MSeqEditor<E, C> sort(@NotNull Comparator<? super E> comparator) {
        source.sort(comparator);
        return this;
    }
}
