package asia.kala.collection.mutable;

import org.jetbrains.annotations.NotNull;

public class MutableCollectionEditor<E, C extends MutableCollection<E>> {
    @NotNull
    protected final C source;

    public MutableCollectionEditor(@NotNull C source) {
        assert source != null;

        this.source = source;
    }

    @NotNull
    public C done() {
        return source;
    }
}
