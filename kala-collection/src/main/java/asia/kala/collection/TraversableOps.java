package asia.kala.collection;

import asia.kala.annotations.StaticClass;
import asia.kala.collection.mutable.Builder;
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
            @NotNull Builder<? super E, ? extends T> builder) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(builder);

        for (E e : collection) {
            if (predicate.test(e)) {
                builder.append(e);
            }
        }

        return builder.build();
    }

    public static <E, T> T filterNot(
            @NotNull Traversable<? extends E> collection,
            @NotNull Predicate<? super E> predicate,
            @NotNull Builder<? super E, ? extends T> builder) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(builder);

        for (E e : collection) {
            if (!predicate.test(e)) {
                builder.append(e);
            }
        }

        return builder.build();
    }

    public static <E, U, T> T map(
            @NotNull Traversable<? extends E> collection,
            @NotNull Function<? super E, ? extends U> mapper,
            @NotNull Builder<? super U, ? extends T> builder
    ) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(builder);

        builder.sizeHint(collection);

        for (E e : collection) {
            builder.append(mapper.apply(e));
        }
        return builder.build();
    }

    public static <E, U, T> T flatMap(
            @NotNull Traversable<? extends E> collection,
            @NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper,
            @NotNull Builder<? super U, ? extends T> builder
    ) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(builder);

        for (E e : collection) {
            TraversableOnce<? extends U> us = mapper.apply(e);
            builder.sizeHint(us);
            for (U u : us) {
                builder.append(u);
            }
        }

        return builder.build();
    }

    public static String toString(@NotNull Traversable<?> collection) {
        return collection.joinToString(", ", collection.stringPrefix() + "[", "]");
    }
}
