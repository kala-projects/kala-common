package asia.kala.collection;

import asia.kala.annotations.StaticClass;
import asia.kala.collection.mutable.Builder;
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
            @NotNull Builder<? super E, ? extends T> builder
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
            builder.append(e);
        }
        return builder.build();
    }

    public static <E, T extends Seq<? extends E>> T dropWhile(
            @NotNull Seq<? extends E> seq,
            @NotNull Predicate<? super E> predicate,
            @NotNull Builder<? super E, ? extends T> builder
    ) {
        Objects.requireNonNull(seq);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(builder);

        for (E e : seq.iterator().dropWhile(predicate)) {
            builder.append(e);
        }

        return builder.build();
    }
}
