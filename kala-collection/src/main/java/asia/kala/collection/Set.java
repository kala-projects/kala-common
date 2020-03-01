package asia.kala.collection;

import asia.kala.annotations.Covariant;
import org.jetbrains.annotations.NotNull;

public interface Set<@Covariant E> extends Traversable<E> {

    //
    // -- Traversable
    //

    @Override
    default String className() {
        return "Set";
    }

    @NotNull
    @Override
    default View<E> view() {
        return new SetViews.Of<>(this);
    }
}
