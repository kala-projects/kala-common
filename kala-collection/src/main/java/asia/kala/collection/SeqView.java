package asia.kala.collection;

import asia.kala.Tuple2;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface SeqView<E> extends Seq<E>, View<E> {

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
