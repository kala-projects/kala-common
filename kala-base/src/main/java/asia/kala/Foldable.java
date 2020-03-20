package asia.kala;

import asia.kala.annotations.Covariant;
import asia.kala.control.Option;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * A {@code Foldable} is a data structure that can be folded.
 *
 * @param <T> the type of value
 * @author Glavo
 */
public interface Foldable<@Covariant T> {

    /**
     * Folds this elements by apply {@code op}, starting with {@code zero}, unknown folding order.
     * Because the implementation can specify the folding order freely,
     * the {@code fold} function is usually more efficient than {@link #foldLeft} and {@link #foldRight}.
     *
     * @param zero the start value
     * @param op   the binary operator
     * @return the folded value
     * @implSpec The default implementation used {@link #foldLeft}, if there exists more efficient folding order,
     * please override the default implementation.
     */
    default T fold(T zero, @NotNull BiFunction<? super T, ? super T, ? extends T> op) {
        Objects.requireNonNull(op);
        return foldLeft(zero, op);
    }

    /**
     * Folds this elements by apply {@code op}, starting with {@code zero}, going left to right.
     *
     * <p>If this is a sequential container, use {@code $N} to refer to the Nth element,
     * the return value of the function is {@code op(...op(z, $1), $2, ..., $N)}.
     *
     * @param zero the start value
     * @param op   the binary operator
     * @param <U>  the result type of the binary operator
     * @return the folded value
     */
    <U> U foldLeft(U zero, @NotNull BiFunction<? super U, ? super T, ? extends U> op);

    /**
     * Folds this elements by apply {@code op}, starting with {@code zero}, going right to left.
     *
     * <p>If this is a sequential container, use {@code $N} to refer to the Nth element,
     * the return value of the function is {@code op($1, op($2, ... op($n, z)...))}.
     *
     * @param zero the start value
     * @param op   the binary operator
     * @param <U>  the result type of the binary operator
     * @return the folded value
     */
    <U> U foldRight(U zero, @NotNull BiFunction<? super T, ? super U, ? extends U> op);

    /**
     * Reduces this elements by apply {@code op}.
     *
     * @param op the binary operator
     * @return the reduced value
     * @throws NoSuchElementException if this {@code Foldable} is empty
     * @implSpec The default implementation used {@link #reduceLeft}, if there exists more efficient folding order,
     * please override the default implementation.
     */
    default T reduce(@NotNull BiFunction<? super T, ? super T, ? extends T> op) throws NoSuchElementException {
        Objects.requireNonNull(op);
        return reduceLeft(op);
    }

    /**
     * Reduces this elements by apply {@code op}, going left to right.
     *
     * <p>If this is a sequential container, use {@code $N} to refer to the Nth element,
     * the return value of the function is {@code op( op( ... op($1, $2) ..., ${n-1}), $n)}.
     *
     * @param op the binary operator
     * @return the reduced value
     * @throws NoSuchElementException if this {@code Foldable} is empty
     */
    default T reduceLeft(@NotNull BiFunction<? super T, ? super T, ? extends T> op) throws NoSuchElementException {
        Option<T> opt = reduceLeftOption(op);
        if (opt.isDefined()) {
            return opt.get();
        }
        throw new NoSuchElementException("Foldable.reduceLeft");
    }

    /**
     * Reduces this elements by apply {@code op}, going right to left.
     *
     * <p>If this is a sequential container, use {@code $N} to refer to the Nth element,
     * the return value of the function is {@code op($1, op($2, ..., op(${n-1}, $n)...))}.
     *
     * @param op the binary operator
     * @return the reduced value
     * @throws NoSuchElementException if this {@code Foldable} is empty
     */
    default T reduceRight(@NotNull BiFunction<? super T, ? super T, ? extends T> op) throws NoSuchElementException {
        Option<T> opt = reduceRightOption(op);
        if (opt.isDefined()) {
            return opt.get();
        }
        throw new NoSuchElementException("Foldable.reduceRight");
    }

    /**
     * Reduces this elements by apply {@code op}.
     *
     * @param op the binary operator
     * @return an {@code Option} contain the reduced value or a empty {@code Option} if the {@code Foldable} is empty
     * @implSpec The default implementation used {@link #reduceLeft}, if there exists more efficient folding order,
     * please override the default implementation.
     */
    @NotNull
    default Option<T> reduceOption(@NotNull BiFunction<? super T, ? super T, ? extends T> op) {
        Objects.requireNonNull(op);
        return reduceLeftOption(op);
    }

    /**
     * Reduces this elements by apply {@code op}, going left to right.
     *
     * <p>If this is a sequential container, use {@code $N} to refer to the Nth element,
     * the return value of the function is {@code Option.some(op( op( ... op($1, $2) ..., ${n-1}), $n))}.
     *
     * @param op the binary operator
     * @return an {@code Option} contain the reduced value or a empty {@code Option} if the {@code Foldable} is empty
     */
    @NotNull
    Option<T> reduceLeftOption(@NotNull BiFunction<? super T, ? super T, ? extends T> op);

    /**
     * Reduces this elements by apply {@code op}, going right to left.
     *
     * <p>If this is a sequential container, use {@code $N} to refer to the Nth element,
     * the return value of the function is {@code Option.some(op($1, op($2, ..., op(${n-1}, $n)...)))}.
     *
     * @param op the binary operator
     * @return an {@code Option} contain the reduced value or a empty {@code Option} if the {@code Foldable} is empty
     */
    @NotNull
    Option<T> reduceRightOption(@NotNull BiFunction<? super T, ? super T, ? extends T> op);

    /**
     * Returns the size of this {@code Foldable} if is a finite structure.
     *
     * @return the number of elements in this {@link Foldable}
     * @apiNote when this {@code Foldable} is a infinite structure,
     * the method may never return or throws {@link UnsupportedOperationException}
     */
    default int size() {
        return foldLeft(0, (a, b) -> a + 1);
    }

    /**
     * Tests whether all elements of this {@code Foldable} match the {@code predicate}.
     *
     * @return {@code true} if either all elements of this {@code Foldable} match the {@code predicate} or
     * the {@code Foldable} is empty, otherwise {@code false}
     * @implNote the default implementation will always iterate over all elements of {@code Foldable},
     * most {@code Foldable} implementations need to override it
     */
    default boolean forall(@NotNull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return this.foldLeft(true, (l, r) -> l && predicate.test(r));
    }

    /**
     * Tests whether any element of this {@code Foldable} match the {@code predicate}.
     *
     * @return {@code true} if either any element of this {@code Foldable} match the {@code predicate},
     * otherwise {@code false}
     * @implNote the default implementation will always iterate over all elements of {@code Foldable},
     * most {@code Foldable} implementations need to override it
     */
    default boolean exists(@NotNull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return this.foldLeft(false, (l, r) -> l || predicate.test(r));
    }

    default boolean contains(Object v) {
        return exists(Predicate.isEqual(v));
    }

    default int count(@NotNull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return this.foldLeft(0, (c, v) -> predicate.test(v) ? c + 1 : c);
    }

    @NotNull
    default Option<T> find(@NotNull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (this instanceof Iterable<?>) {
            @SuppressWarnings("unchecked") Iterable<T> self = (Iterable<T>) this;
            for (T t : self) {
                if (predicate.test(t)) {
                    return Option.some(t);
                }
            }
            return Option.none();
        }
        return this.foldLeft(Option.none(), (o, v) -> o.isEmpty() && predicate.test(v) ? Option.some(v) : o);
    }
}
