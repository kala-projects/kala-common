package asia.kala.collection;

import asia.kala.Option;
import asia.kala.Tuple;
import asia.kala.Tuple2;
import asia.kala.collection.mutable.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Seq<E> extends Traversable<E> {

    default E get(int index) {
        return getOption(index).getOrThrow(() -> new IndexOutOfBoundsException(Integer.toString(index)));
    }

    default Option<E> getOption(int index) {
        if (index < 0) {
            return Option.none();
        }

        int s = knownSize();
        if (s >= 0 && index >= s) {
            return Option.none();
        }

        int i = index;

        for (E e : this) {
            if (i-- == 0) {
                return Option.some(e);
            }
        }
        return Option.none();
    }

    default Seq<E> drop(int n) {
        if (n <= 0) {
            return this;
        }

        Builder<E, ? extends Seq<E>> builder = newBuilder();
        int s = knownSize();
        if (s != -1) {
            builder.sizeHint(Integer.max(s - n, 0));
        }

        for (E e : iterator().drop(n)) {
            builder.addOne(e);
        }
        return builder.build();
    }


    //
    // -- Traversable
    //

    @NotNull
    @Override
    <U> Builder<U, ? extends Seq<U>> newBuilder();

    default Tuple2<? extends Seq<E>, ? extends Seq<E>> span(@NotNull Predicate<? super E> predicate){
        Objects.requireNonNull(predicate);
        Builder<E, ? extends Seq<E>> builder1 = newBuilder();
        Builder<E, ? extends Seq<E>> builder2 = newBuilder();
        for(E e : this) {
            if(predicate.test(e)) {
                builder1.addOne(e);
            } else {
                builder2.addOne(e);
            }
        }
        return new Tuple2<>(builder1.build(), builder2.build());
    }

    //
    // -- TraversableOnce
    //

    @NotNull
    @Override
    default Seq<E> filter(@NotNull Predicate<? super E> predicate) {
        return Traversable.filter(this, predicate, newBuilder());
    }

    @NotNull
    @Override
    default Seq<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return Traversable.filterNot(this, predicate, newBuilder());
    }


    //
    // -- Functor
    //

    @NotNull
    @Override
    default <U> Seq<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return Traversable.map(this, mapper, this.<U>newBuilder());
    }
}
