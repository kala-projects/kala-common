package asia.kala;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Value<T> extends Functor<T>, Foldable<T> {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <T> Value<T> narrow(Value<? extends T> value) {
        return (Value<T>) value;
    }

    default boolean isReady() {
        return true;
    }


    T get();

    //
    // -- Functor
    //

    @Override
    @NotNull <U> Value<U> map(@NotNull Function<? super T, ? extends U> mapper);

    //
    // -- Foldable
    //
    default <U> U foldLeft(U zero, @NotNull BiFunction<? super U, ? super T, ? extends U> op) {
        Objects.requireNonNull(op);
        return op.apply(zero, get());
    }

    @Override
    default <U> U foldRight(U zero, @NotNull BiFunction<? super T, ? super U, ? extends U> op) {
        Objects.requireNonNull(op);
        return op.apply(get(), zero);
    }

    @Override
    default T reduceLeft(@NotNull BiFunction<? super T, ? super T, ? extends T> op) {
        return get();
    }

    @Override
    default T reduceRight(@NotNull BiFunction<? super T, ? super T, ? extends T> op) {
        return get();
    }

    @Override
    @NotNull
    default Option<T> reduceLeftOption(@NotNull BiFunction<? super T, ? super T, ? extends T> op) {
        return Option.some(get());
    }

    @Override
    @NotNull
    default Option<T> reduceRightOption(@NotNull BiFunction<? super T, ? super T, ? extends T> op) {
        return Option.some(get());
    }

    @Override
    default int size() {
        return 1;
    }

    @Override
    default boolean forall(@NotNull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return predicate.test(get());
    }

    @Override
    default boolean exists(@NotNull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return predicate.test(get());
    }

    @Override
    default boolean contains(Object v) {
        return Objects.equals(get(), v);
    }

    @Override
    default int count(@NotNull Predicate<? super T> predicate) {
        return predicate.test(get()) ? 1 : 0;
    }

    @Override
    @NotNull
    default Option<T> find(@NotNull Predicate<? super T> predicate) {
        T value = get();
        if (predicate.test(value)) {
            return Option.some(value);
        }
        return Option.none();
    }
}
