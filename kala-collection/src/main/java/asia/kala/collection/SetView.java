package asia.kala.collection;

import asia.kala.annotations.Covariant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface SetView<@Covariant E> extends View<E>, Set<E> {

    //
    // -- View
    //


    @NotNull
    @Override
    @Contract(value = "-> this", pure = true)
    default SetView<E> view() {
        return this;
    }

    @Override
    default String className() {
        return "SetView";
    }
}
