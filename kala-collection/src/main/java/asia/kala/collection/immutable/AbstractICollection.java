package asia.kala.collection.immutable;

import asia.kala.Tuple2;
import asia.kala.collection.AbstractTraversable;
import asia.kala.collection.TraversableOnce;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public abstract class AbstractICollection<E> extends AbstractTraversable<E> implements ICollection<E> {
    static <E, U, T> T map(
            @NotNull ICollection<? extends E> collection,
            @NotNull Function<? super E, ? extends U> mapper,
            @NotNull CollectionBuilder<? super U, ? extends T> builder
    ) {
        assert collection != null;
        assert builder != null;

        Objects.requireNonNull(mapper);

        builder.sizeHint(collection);

        for (E e : collection) {
            builder.add(mapper.apply(e));
        }
        return builder.build();
    }

    static <E, T> T filter(
            @NotNull ICollection<? extends E> collection,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionBuilder<? super E, ? extends T> builder) {
        assert collection != null;
        assert builder != null;

        Objects.requireNonNull(predicate);

        for (E e : collection) {
            if (predicate.test(e)) {
                builder.add(e);
            }
        }

        return builder.build();
    }

    static <E, T> T filterNot(
            @NotNull ICollection<? extends E> collection,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionBuilder<? super E, ? extends T> builder) {
        assert collection != null;
        assert builder != null;

        Objects.requireNonNull(predicate);

        for (E e : collection) {
            if (!predicate.test(e)) {
                builder.add(e);
            }
        }

        return builder.build();
    }

    static <E, U, T> T flatMap(
            @NotNull ICollection<? extends E> collection,
            @NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper,
            @NotNull CollectionBuilder<? super U, ? extends T> builder
    ) {
        assert collection != null;
        assert builder != null;

        Objects.requireNonNull(mapper);

        for (E e : collection) {
            TraversableOnce<? extends U> us = mapper.apply(e);
            builder.sizeHint(us);
            for (U u : us) {
                builder.add(u);
            }
        }

        return builder.build();
    }

    static <E, T> Tuple2<T, T> span(
            @NotNull ICollection<? extends E> collection,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionBuilder<? super E, ? extends T> builder1,
            @NotNull CollectionBuilder<? super E, ? extends T> builder2) {
        assert collection != null;
        assert builder1 != null;
        assert builder2 != null;

        Objects.requireNonNull(predicate);

        for (E e : collection) {
            if (predicate.test(e)) {
                builder1.add(e);
            } else {
                builder2.add(e);
            }
        }
        return new Tuple2<>(builder1.build(), builder2.build());
    }


    @NotNull
    protected final <U, To extends ICollection<U>> To mapImpl(@NotNull Function<? super E, ? extends U> mapper) {
        return (To) AbstractICollection.map(this, mapper, this.<U>newBuilder());
    }

    @NotNull
    protected final <To extends ICollection<E>> To filterImpl(@NotNull Predicate<? super E> predicate) {
        return (To) AbstractICollection.filter(this, predicate, newBuilder());
    }

    @NotNull
    protected final <To extends ICollection<E>> To filterNotImpl(@NotNull Predicate<? super E> predicate) {
        return (To) AbstractICollection.filterNot(this, predicate, newBuilder());
    }

    @NotNull
    protected final <U, To extends ICollection<U>> To flatMapImpl(
            @NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
        return (To) AbstractICollection.flatMap(this, mapper, newBuilder());
    }

    @SuppressWarnings("rawtypes")
    protected final <To extends ICollection<E>> Tuple2<To, To> spanImpl(@NotNull Predicate<? super E> predicate) {
        return (Tuple2) AbstractICollection.span(this, predicate, newBuilder(), newBuilder());
    }
}
