package asia.kala.collection;

import asia.kala.Functor;
import asia.kala.Tuple2;
import asia.kala.annotations.Covariant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Transformable<@Covariant E> extends Functor<E> {
    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> Transformable<E> narrow(Transformable<? extends E> transformable) {
        return (Transformable<E>) transformable;
    }

    @NotNull
    @Override
    <U> Transformable<U> map(@NotNull Function<? super E, ? extends U> mapper);

    @NotNull
    Transformable<E> filter(@NotNull Predicate<? super E> predicate);

    @NotNull
    Transformable<E> filterNot(@NotNull Predicate<? super E> predicate);

    @NotNull <U> Transformable<U> flatMap(@NotNull Function<? super E, ? extends Iterable<? extends U>> mapper);

    @NotNull
    Tuple2<? extends Transformable<E>, ? extends Transformable<E>> span(@NotNull Predicate<? super E> predicate);
}
