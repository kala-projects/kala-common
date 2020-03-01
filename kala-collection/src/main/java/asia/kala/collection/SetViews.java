package asia.kala.collection;

import asia.kala.annotations.Covariant;
import org.jetbrains.annotations.NotNull;

final class SetViews {
    static class Of<@Covariant E, C extends Set<E>> extends Views.Of<E, C> implements SetView<E> {
        Of(@NotNull C collection) {
            super(collection);
        }
    }
}
