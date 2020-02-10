package asia.kala.collection;

import asia.kala.Option;
import asia.kala.collection.mutable.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.RandomAccess;
import java.util.function.Function;
import java.util.function.Predicate;

public interface IndexedSeq<E> extends Seq<E>, RandomAccess {

    E get(int index);

    //
    // -- Seq
    //

    @Override
    default Option<E> getOption(int index) {
        if (index < 0 || index >= size()) {
            return Option.none();
        }
        return Option.some(get(index));
    }

    //
    // -- Traversable
    //

    @NotNull
    @Override
    <U> Builder<U, ? extends IndexedSeq<U>> newBuilder();

    //
    // -- TraversableOnce
    //


    @NotNull
    @Override
    default IndexedSeq<E> filter(@NotNull Predicate<? super E> predicate) {
        Builder<E, ? extends IndexedSeq<E>> builder = newBuilder();
        builder.sizeHint(this);
        return Traversable.filter(this, predicate, builder);
    }

    @NotNull
    @Override
    default IndexedSeq<E> filterNot(@NotNull Predicate<? super E> predicate) {
        Builder<E, ? extends IndexedSeq<E>> builder = newBuilder();
        builder.sizeHint(this);
        return Traversable.filterNot(this, predicate, builder);
    }

    @NotNull
    @Override
    default <U> IndexedSeq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return Traversable.map(this, mapper, this.<U>newBuilder());
    }
}
