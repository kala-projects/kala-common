package asia.kala.collection.immutable;

import asia.kala.collection.TraversableOnce;
import asia.kala.collection.mutable.CollectionBuilder;
import asia.kala.function.IndexedFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public abstract class AbstractISeq<E> extends AbstractICollection<E> implements ISeq<E> {
    static <E, T> T updated(
            @NotNull ISeq<? extends E> seq,
            int index,
            E newValue,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        assert seq != null;
        assert builder != null;

        if (index < 0 || index >= seq.size()) {
            throw new IndexOutOfBoundsException();
        }

        builder.sizeHint(seq);

        for (E e : seq) {
            if (index-- == 0) {
                builder.add(newValue);
            } else {
                builder.add(e);
            }
        }

        return builder.build();
    }

    static <E, T extends ISeq<? extends E>> T drop(
            @NotNull ISeq<? extends E> seq,
            int n,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        assert seq != null;
        assert builder != null;

        int s = seq.knownSize();
        if (s != -1) {
            builder.sizeHint(Integer.max(s - n, 0));
        }

        for (E e : seq.iterator().drop(n)) {
            builder.add(e);
        }
        return builder.build();
    }

    static <E, T extends ISeq<? extends E>> T dropWhile(
            @NotNull ISeq<? extends E> seq,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        assert seq != null;
        assert builder != null;

        Objects.requireNonNull(predicate);

        builder.addAll(seq.iterator().dropWhile(predicate));

        return builder.build();
    }

    static <E, T extends ISeq<? extends E>> T take(
            @NotNull ISeq<? extends E> seq,
            int n,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        assert seq != null;
        assert builder != null;

        int s = seq.knownSize();
        if (s != -1) {
            builder.sizeHint(Integer.min(s, n));
        }

        int count = 0;
        for (E e : seq) {
            if (++count > n) {
                break;
            }
            builder.add(e);
        }

        return builder.build();
    }

    static <E, T extends ISeq<? extends E>> T takeWhile(
            @NotNull ISeq<? extends E> seq,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionBuilder<? super E, ? extends T> builder
    ) {
        assert seq != null;
        assert builder != null;

        Objects.requireNonNull(predicate);

        for (E e : seq) {
            if (!predicate.test(e)) {
                break;
            }
            builder.add(e);
        }

        return builder.build();
    }

    static <E, T extends ISeq<? extends E>> T concat(
            @NotNull ISeq<? extends E> seq,
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

    static <E, T> T prepended(
            @NotNull ISeq<? extends E> seq,
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

    static <E, T> T prependedAll(
            @NotNull ISeq<? extends E> seq,
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

    static <E, T> T appended(
            @NotNull ISeq<? extends E> seq,
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

    static <E, T> T appendedAll(
            @NotNull ISeq<? extends E> seq,
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

    static <E, U, T> T mapIndexed(
            @NotNull ISeq<? extends E> Seq,
            @NotNull IndexedFunction<? super E, ? extends U> mapper,
            @NotNull CollectionBuilder<? super U, ? extends T> builder
    ) {
        assert Seq != null;
        assert builder != null;

        Objects.requireNonNull(mapper);

        builder.sizeHint(Seq);

        int idx = 0;
        for (E e : Seq) {
            builder.add(mapper.apply(idx++, e));
        }
        return builder.build();
    }

    @NotNull
    protected final <To extends ISeq<E>> To updatedImpl(int index, E newValue) {
        return (To) AbstractISeq.updated(this, index, newValue, newBuilder());
    }

    @NotNull
    protected final <To extends ISeq<E>> To dropImpl(int n) {
        return (To) AbstractISeq.drop(this, n, newBuilder());
    }

    @NotNull
    protected final <To extends ISeq<E>> To dropWhileImpl(@NotNull Predicate<? super E> predicate) {
        return (To) AbstractISeq.dropWhile(this, predicate, newBuilder());
    }

    @NotNull
    protected final <To extends ISeq<E>> To takeImpl(int n) {
        return (To) AbstractISeq.take(this, n, newBuilder());
    }

    @NotNull
    protected final <To extends ISeq<E>> To takeWhileImpl(@NotNull Predicate<? super E> predicate) {
        return (To) AbstractISeq.takeWhile(this, predicate, newBuilder());
    }

    @NotNull
    protected final <To extends ISeq<E>> To concatImpl(@NotNull TraversableOnce<? extends E> traversable) {
        return (To) AbstractISeq.concat(this, traversable, newBuilder());
    }

    @NotNull
    protected final <To extends ISeq<E>> To prependedImpl(E element) {
        return (To) AbstractISeq.prepended(this, element, newBuilder());
    }

    @NotNull
    protected final <To extends ISeq<E>> To prependedAllImpl(@NotNull TraversableOnce<? extends E> prefix) {
        return (To) AbstractISeq.prependedAll(this, prefix, newBuilder());
    }

    @NotNull
    protected final <To extends ISeq<E>> To appendedImpl(E element) {
        return (To) AbstractISeq.prepended(this, element, newBuilder());
    }

    @NotNull
    protected final <To extends ISeq<E>> To appendedAllImpl(@NotNull TraversableOnce<? extends E> postfix) {
        return (To) AbstractISeq.appendedAll(this, postfix, newBuilder());
    }

    @NotNull
    protected final <U, To extends ISeq<U>> To mapIndexedImpl(@NotNull IndexedFunction<? super E, ? extends U> mapper) {
        return (To) AbstractISeq.mapIndexed(this, mapper, this.<U>newBuilder());
    }
}
