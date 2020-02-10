package asia.kala.collection;

import asia.kala.Tuple2;
import asia.kala.annotations.StaticClass;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@StaticClass
public final class TraversableOps {
    private TraversableOps() {

    }

    public static <E, T> T filter(
            @NotNull Traversable<? extends E> collection,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionBuilder<? super E, ? extends T> builder) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(builder);

        for (E e : collection) {
            if (predicate.test(e)) {
                builder.add(e);
            }
        }

        return builder.build();
    }

    public static <E, T> T filterNot(
            @NotNull Traversable<? extends E> collection,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionBuilder<? super E, ? extends T> builder) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(builder);

        for (E e : collection) {
            if (!predicate.test(e)) {
                builder.add(e);
            }
        }

        return builder.build();
    }

    public static <E, U, T> T map(
            @NotNull Traversable<? extends E> collection,
            @NotNull Function<? super E, ? extends U> mapper,
            @NotNull CollectionBuilder<? super U, ? extends T> builder
    ) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(builder);

        builder.sizeHint(collection);

        for (E e : collection) {
            builder.add(mapper.apply(e));
        }
        return builder.build();
    }

    public static <E, U, T> T flatMap(
            @NotNull Traversable<? extends E> collection,
            @NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper,
            @NotNull CollectionBuilder<? super U, ? extends T> builder
    ) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(builder);

        for (E e : collection) {
            TraversableOnce<? extends U> us = mapper.apply(e);
            builder.sizeHint(us);
            for (U u : us) {
                builder.add(u);
            }
        }

        return builder.build();
    }

    public static <E, T> Tuple2<T, T> span(
            @NotNull Traversable<? extends E> collection,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionBuilder<? super E, ? extends T> builder1,
            @NotNull CollectionBuilder<? super E, ? extends T> builder2) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(builder1);
        Objects.requireNonNull(builder2);

        for (E e : collection) {
            if (predicate.test(e)) {
                builder1.add(e);
            } else {
                builder2.add(e);
            }
        }
        return new Tuple2<>(builder1.build(), builder2.build());
    }

    public static String toString(@NotNull Traversable<?> collection) {
        return collection.joinToString(", ", collection.stringPrefix() + "[", "]");
    }
}
