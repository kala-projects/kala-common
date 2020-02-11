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
        assert seq != null;
        assert builder != null;

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
        assert seq != null;
        assert builder != null;

        Objects.requireNonNull(predicate);

        builder.addAll(seq.iterator().dropWhile(predicate));

        return builder.build();
    }

    public static <E, T extends Seq<? extends E>> T concat(
            @NotNull Seq<? extends E> seq,
            @NotNull TraversableOnce<? extends E> traversable,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        assert seq != null;
        assert builder != null;

        Objects.requireNonNull(traversable);

        builder.sizeHint(seq);
        builder.addAll(seq);

        builder.sizeHint(traversable);
        builder.addAll(traversable);

        return builder.build();
    }

    public static <E, T> T prepended(
            @NotNull Seq<? extends E> seq,
            E element,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        assert seq != null;
        assert builder != null;

        builder.sizeHint(seq, 1);

        builder.add(element);
        builder.addAll(seq);

        return builder.build();
    }

    public static <E, T> T prependedAll(
            @NotNull Seq<? extends E> seq,
            @NotNull TraversableOnce<? extends E> prefix,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        assert seq != null;
        assert builder != null;

        Objects.requireNonNull(prefix);

        builder.sizeHint(prefix);
        builder.addAll(prefix);

        builder.sizeHint(seq);
        builder.addAll(seq);

        return builder.build();
    }

    public static <E, T> T appended(
            @NotNull Seq<? extends E> seq,
            E element,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        assert seq != null;
        assert builder != null;

        builder.sizeHint(seq, 1);

        builder.addAll(seq);
        builder.add(element);

        return builder.build();
    }

    public static <E, T> T appendedAll(
            @NotNull Seq<? extends E> seq,
            @NotNull TraversableOnce<? extends E> postfix,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        assert seq != null;
        assert builder != null;

        Objects.requireNonNull(postfix);

        builder.sizeHint(seq);
        builder.addAll(seq);

        builder.sizeHint(postfix);
        builder.addAll(postfix);

        return builder.build();
    }
}
