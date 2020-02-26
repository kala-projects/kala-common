package asia.kala.collection;

import asia.kala.Tuple2;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface View<E> extends Traversable<E>, Transformable<E> {

    @NotNull
    @Override
    default View<E> view() {
        return this;
    }

    @Override
    default String className() {
        return "View";
    }

    @NotNull
    @Override
    default <U> View<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        Objects.requireNonNull(mapper);

        return new Views.Mapped<>(this, mapper);
    }

    @Override
    @NotNull
    default View<E> filter(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new Views.Filter<>(this, predicate);
    }

    @Override
    @NotNull
    default View<E> filterNot(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new Views.Filter<>(this, predicate.negate());
    }

    @Override
    @NotNull
    default <U> View<U> flatMap(@NotNull Function<? super E, ? extends Iterable<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        return new Views.FlatMapped<>(this, mapper);
    }

    @NotNull
    @Override
    default Tuple2<? extends View<E>, ? extends View<E>> span(@NotNull Predicate<? super E> predicate) {
        return new Tuple2<>(this.filter(predicate), this.filterNot(predicate));
    }
}
