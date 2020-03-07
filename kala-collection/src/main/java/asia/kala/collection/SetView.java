package asia.kala.collection;

import asia.kala.annotations.Covariant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

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

    @NotNull
    @Override
    default SetView<E> filter(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new SetViews.Filter<>(this, predicate);
    }

    @NotNull
    @Override
    default SetView<E> filterNot(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new SetViews.Filter<>(this, predicate.negate());
    }
}
