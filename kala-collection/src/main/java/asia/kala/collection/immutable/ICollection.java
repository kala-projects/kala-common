package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.collection.Transformable;
import asia.kala.collection.Traversable;
import asia.kala.collection.TraversableOnce;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ICollection<E> extends Traversable<E>, Transformable<E> {
    @Override
    default String className() {
        return "ICollection";
    }

    @NotNull <U> CollectionBuilder<U, ? extends ICollection<U>> newBuilder();

    @NotNull
    @Override
    default <U> ICollection<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return AbstractICollection.map(this, mapper, this.<U>newBuilder());
    }

    @NotNull
    @Override
    default ICollection<E> filter(@NotNull Predicate<? super E> predicate) {
        return AbstractICollection.filter(this, predicate, newBuilder());
    }

    @NotNull
    @Override
    default ICollection<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return AbstractICollection.filterNot(this, predicate, newBuilder());
    }

    @NotNull
    @Override
    default <U> ICollection<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return AbstractICollection.flatMap(this, mapper, this.newBuilder());
    }

    @NotNull
    @Override
    default Tuple2<? extends ICollection<E>, ? extends ICollection<E>> span(@NotNull Predicate<? super E> predicate) {
        return AbstractICollection.span(this, predicate, newBuilder(), newBuilder());
    }
}
