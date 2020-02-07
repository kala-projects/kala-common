package asia.kala;

import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A container object which may or may not contain a value.
 *
 * <p>{@code Option} is not the same as {@link Optional}.
 * {@code Option} is a container that supports collection operations, supports serialization,
 * and distinguishes between a {@code Option} instance that contains null as the value and the empty Option.
 * {@link Optional} does not support serialization and cannot store null as its value.
 * Use the {@code Option} in situations where {@link Optional} is not suitable.
 *
 * @param <T> the type of value
 * @author Glavo
 * @see Optional
 */
public final class Option<T> implements OptionContainer<T>, Iterable<T>, Serializable {
    private static final long serialVersionUID = -4962768465676381896L;

    private static final int hashMagic = -1623337737;

    /**
     * The single instance of empty {@code Option}.
     */
    public static final Option<?> NONE = new Option<>(InternalEmptyTag.INSTANCE);

    /**
     * The value if this {@code Option} is not empty, otherwise {@link InternalEmptyTag#INSTANCE}.
     */
    private final T value;

    private Option(T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Contract(value = "_ -> param1", pure = true)
    public static <T> Option<T> narrow(Option<? extends T> option) {
        return (Option<T>) option;
    }

    /**
     * Returns the single instance of empty {@code Option}.
     *
     * @param <T> the type of value
     * @return the single instance of empty {@code Option}.
     * @see Option#NONE
     */
    @SuppressWarnings("unchecked")
    public static <T> Option<T> none() {
        return (Option<T>) NONE;
    }

    /**
     * Returns a new {@code Option} contain the {@code value}.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return a new {@code Option} contain the {@code value}
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <T> Option<T> some(T value) {
        return new Option<>(value);
    }

    /**
     * Returns {@code Option.some(value)} if value is not null, otherwise returns {@code Option.none()}.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return {@code Option.some(value)} if value is not null, otherwise {@code Option.none()}
     */
    @SuppressWarnings("unchecked")
    public static <T> Option<T> of(@Nullable T value) {
        if (value == null) {
            return (Option<T>) NONE;
        }
        return new Option<>(value);
    }

    /**
     * Convert {@link Optional} to {@code Option}.
     *
     * @param optional a {@link Optional} value
     * @param <T>      the type of the value
     * @return {@code Option.some(optional.get())} if {@code optional} is present, otherwise {@code Option.none()}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Option<T> fromJava(@NotNull Optional<? extends T> optional) {
        Objects.requireNonNull(optional);
        return of(optional.orElse(null));
    }

    //
    // -- OptionContainer
    //

    /**
     * Returns {@code true} if the {@code Option} contain a value, otherwise return {@code false}.
     *
     * @return {@code true} if the {@code Option} contain a value, otherwise {@code false}
     * @apiNote return {@code false} if and only if {@code this == Option.NONE}
     */
    public final boolean isDefined() {
        return this != NONE;
    }

    /**
     * Returns the {@code Option}'s value.
     *
     * @return the {@code Option}'s value
     * @throws NoSuchElementException if the {@code Option} is empty
     */
    @Flow(sourceIsContainer = true)
    public final T get() {
        if (isEmpty()) {
            throw new NoSuchElementException("Option.None");
        }
        return value;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final Option<T> getOption() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public final <U> Option<U> map(@NotNull Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return isDefined() ? some(mapper.apply(value)) : none();
    }

    @NotNull
    public final Option<T> filter(@NotNull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        return isDefined() && predicate.test(value) ? this : none();
    }

    //
    // -- Foldable
    //

    /**
     * {@inheritDoc}
     */
    @Override
    @Contract(pure = true)
    public final boolean contains(T v) {
        return Objects.equals(value, v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public final Option<T> find(@NotNull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        return isDefined() && predicate.test(value) ? this : none();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int size() {
        return isEmpty() ? 0 : 1;
    }


    /**
     * Convert this {@code Option} to {@link Optional}.
     *
     * @return {@code Optional.of(get())} if the {@code Option} contain a value, otherwise {@link Optional#empty()}
     * @throws NullPointerException if {@link #isDefined()} but value is {@code null}
     */
    @NotNull
    public final Optional<T> asJava() {
        if (isEmpty()) {
            return Optional.empty();
        }
        Objects.requireNonNull(value, "value is null");
        return Optional.of(value);
    }


    //
    // -- Iterable
    //

    @NotNull
    @Override
    public final Iterator<T> iterator() {
        return new OptionContainerIterator<>(value);
    }

    @NotNull
    @Override
    public final Spliterator<T> spliterator() {
        return new OptionContainerIterator<>(value);
    }

    //
    // -- Object
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Option<?>)) {
            return false;
        }
        Option<?> option = (Option<?>) o;
        return Objects.equals(value, option.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(value) + hashMagic;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Contract(pure = true)
    @Override
    public final String toString() {
        if (this == NONE) {
            return "Option.None";
        }
        return "Option[" + value + "]";
    }

    //
    // -- Serializable
    //

    private Object readResolve() {
        if (value == InternalEmptyTag.INSTANCE) {
            return NONE;
        }
        return this;
    }

}

