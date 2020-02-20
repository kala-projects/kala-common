package asia.kala.collection;

import asia.kala.Tuple2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface SeqView<E> extends Seq<E>, View<E> {

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    static <E> SeqView<E> narrow(SeqView<? extends E> view) {
        return (SeqView<E>) view;
    }

    @NotNull
    default SeqView<E> updated(int index, E newValue) {
        return new SeqViews.Updated<>(this, index, newValue);
    }

    @NotNull
    default SeqView<E> drop(int n) {
        return new SeqViews.Drop<>(this, n);
    }

    @NotNull
    default SeqView<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        return new SeqViews.DropWhile<>(this, predicate);
    }

    @NotNull
    default SeqView<E> take(int n) {
        return new SeqViews.Take<>(this, n);
    }

    @NotNull
    default SeqView<E> takeWhile(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);

        return new SeqViews.TakeWhile<>(this, predicate);
    }

    default SeqView<E> concat(@NotNull Seq<? extends E> other) {
        Objects.requireNonNull(other);

        return new SeqViews.Concat<>(this, narrow(other.view()));
    }

    default SeqView<E> prepended(E value) {
        return new SeqViews.Prepended<>(this, value);
    }

    default SeqView<E> appended(E value) {
        return new SeqViews.Appended<>(this, value);
    }

    //
    // -- View
    //

    @NotNull
    @Override
    default SeqView<E> view() {
        return this;
    }

    @Override
    default String className() {
        return "SeqView";
    }

    @NotNull
    @Override
    default <U> SeqView<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        Objects.requireNonNull(mapper);

        return new SeqViews.Mapped<>(this, mapper);
    }

    @Override
    @NotNull
    default SeqView<E> filter(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new SeqViews.Filter<>(this, predicate);
    }

    @Override
    @NotNull
    default SeqView<E> filterNot(@NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        return new SeqViews.Filter<>(this, predicate.negate());
    }

    @Override
    @NotNull
    default <U> SeqView<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        return new SeqViews.FlatMapped<>(this, mapper);
    }

    @NotNull
    @Override
    default Tuple2<? extends SeqView<E>, ? extends SeqView<E>> span(@NotNull Predicate<? super E> predicate) {
        return new Tuple2<>(this.filter(predicate), this.filterNot(predicate));
    }
}
