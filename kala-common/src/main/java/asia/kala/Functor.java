package asia.kala;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A {@code Functor} is a data structure that can be {@link #map(Function)} to another {@code Functor}.
 *
 * @param <T> the type of value
 * @author Glavo
 */
public interface Functor<T> {

    /**
     * Returns a container consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * @param <U>    The element type of the new container
     * @param mapper a non-interfering stateless function to apply to each element
     * @return the new container
     */
    @NotNull
    @Contract(pure = true)
    <U> Functor<U> map(@NotNull Function<? super T, ? extends U> mapper);
}

