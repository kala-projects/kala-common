package asia.kala.collection.mutable;

import org.jetbrains.annotations.NotNull;

public class MCollectionEditor<E, C extends MCollection<E>> {
    @NotNull
    protected final C source;

    public MCollectionEditor(@NotNull C source) {
        assert source != null;

        this.source = source;
    }

    @NotNull
    public C done() {
        return source;
    }
}
