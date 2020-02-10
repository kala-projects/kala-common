package asia.kala.collection;

import asia.kala.annotations.StaticClass;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

@StaticClass
@SuppressWarnings("unchecked")
public final class SeqOps {
    private SeqOps() {
    }

    public static <E, T extends Seq<? extends E>> T drop(
            @NotNull Seq<? extends E> seq,
            int n,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        Objects.requireNonNull(seq);
        Objects.requireNonNull(builder);

        if (n <= 0) {
            return (T) seq;
        }

        int s = seq.knownSize();
        if (s != -1) {
            builder.sizeHint(Integer.max(s - n, 0));
        }

        for (E e : seq.iterator().drop(n)) {
            builder.add(e);
        }
        return builder.build();
    }

    public static <E, T extends Seq<? extends E>> T dropWhile(
            @NotNull Seq<? extends E> seq,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        Objects.requireNonNull(seq);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(builder);

        builder.addAll(seq.iterator().dropWhile(predicate));

        return builder.build();
    }

    public static <E, T extends Seq<? extends E>> T concat(
            @NotNull Seq<? extends E> seq,
            @NotNull TraversableOnce<? extends E> traversable,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        Objects.requireNonNull(seq);
        Objects.requireNonNull(traversable);
        Objects.requireNonNull(builder);

        builder.sizeHint(seq);
        builder.addAll(seq);

        builder.sizeHint(traversable);
        builder.addAll(traversable);

        return builder.build();
    }
}
