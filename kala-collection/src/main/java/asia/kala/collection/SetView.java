package asia.kala.collection;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface SetView<E> extends View<E>, Set<E> {

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
