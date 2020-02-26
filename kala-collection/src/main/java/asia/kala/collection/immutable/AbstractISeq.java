package asia.kala.collection.immutable;

import asia.kala.collection.CollectionFactory;
import asia.kala.collection.Seq;
import asia.kala.collection.TraversableOnce;
import asia.kala.function.IndexedFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public abstract class AbstractISeq<E> extends AbstractICollection<E> implements ISeq<E> {
    static <E, T, Builder> T updated(
            @NotNull ISeq<? extends E> seq,
            int index,
            E newValue,
            @NotNull CollectionFactory<? super E, Builder, ? extends T> factory
    ) {
        assert seq != null;
        assert factory != null;

        int s = seq.size();

        if (index < 0 || index >= s) {
            throw new IndexOutOfBoundsException();
        }

        Builder builder = factory.newBuilder();

        factory.sizeHint(builder, s);

        for (E e : seq) {
            if (index-- == 0) {
                factory.addToBuilder(builder, newValue);
            } else {
                factory.addToBuilder(builder, e);
            }
        }

        return factory.build(builder);
    }

    static <E, T extends ISeq<? extends E>, Builder> T drop(
            @NotNull ISeq<? extends E> seq,
            int n,
            @NotNull CollectionFactory<? super E, Builder, ? extends T> factory
    ) {
        assert seq != null;
        assert factory != null;

        Builder builder = factory.newBuilder();

        int s = seq.knownSize();
        if (s != -1) {
            factory.sizeHint(builder, Integer.max(s - n, 0));
        }

        for (E e : seq.iterator().drop(n)) {
            factory.addToBuilder(builder, e);
        }
        return factory.build(builder);
    }

    static <E, T extends ISeq<? extends E>, Builder> T dropWhile(
            @NotNull ISeq<? extends E> seq,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionFactory<? super E, Builder, ? extends T> factory
    ) {
        assert seq != null;
        assert factory != null;

        Objects.requireNonNull(predicate);

        Builder builder = factory.newBuilder();

        factory.addAllToBuilder(builder, seq.iterator().dropWhile(predicate));

        return factory.build(builder);
    }

    static <E, T extends ISeq<? extends E>, Builder> T take(
            @NotNull ISeq<? extends E> seq,
            int n,
            @NotNull CollectionFactory<? super E, Builder, ? extends T> factory
    ) {
        assert seq != null;
        assert factory != null;

        if (n <= 0) {
            return factory.empty();
        }

        Builder builder = factory.newBuilder();

        int s = seq.knownSize();
        if (s != -1) {
            factory.sizeHint(builder, Integer.min(s, n));
        }

        int count = 0;
        for (E e : seq) {
            if (++count > n) {
                break;
            }
            factory.addToBuilder(builder, e);
        }

        return factory.build(builder);
    }

    static <E, T extends ISeq<? extends E>, Builder> T takeWhile(
            @NotNull ISeq<? extends E> seq,
            @NotNull Predicate<? super E> predicate,
            @NotNull CollectionFactory<? super E, Builder, ? extends T> factory
    ) {
        assert seq != null;
        assert factory != null;

        Objects.requireNonNull(predicate);

        Builder builder = factory.newBuilder();

        for (E e : seq) {
            if (!predicate.test(e)) {
                break;
            }
            factory.addToBuilder(builder, e);
        }

        return factory.build(builder);
    }

    static <E, T extends ISeq<? extends E>, Builder> T concat(
            @NotNull ISeq<? extends E> seq,
            @NotNull Seq<? extends E> other,
            @NotNull CollectionFactory<? super E, Builder, ? extends T> factory
    ) {
        assert seq != null;
        assert factory != null;

        Objects.requireNonNull(other);

        Builder builder = factory.newBuilder();

        factory.sizeHint(builder, seq);
        factory.addAllToBuilder(builder, seq);

        factory.sizeHint(builder, other);
        factory.addAllToBuilder(builder, other);

        return factory.build(builder);
    }

    static <E, T, Builder> T prepended(
            @NotNull ISeq<? extends E> seq,
            E element,
            @NotNull CollectionFactory<? super E, Builder, ? extends T> factory
    ) {
        assert seq != null;
        assert factory != null;

        Builder builder = factory.newBuilder();

        factory.sizeHint(builder, seq, 1);

        factory.addToBuilder(builder, element);
        factory.addAllToBuilder(builder, seq);

        return factory.build(builder);
    }

    static <E, T, Builder> T prependedAll(
            @NotNull ISeq<? extends E> seq,
            @NotNull Iterable<? extends E> prefix,
            @NotNull CollectionFactory<? super E, Builder, ? extends T> factory
    ) {
        assert seq != null;
        assert factory != null;
        Objects.requireNonNull(prefix);

        Builder builder = factory.newBuilder();

        factory.sizeHint(builder, prefix);
        factory.addAllToBuilder(builder, prefix);

        factory.sizeHint(builder, seq);
        factory.addAllToBuilder(builder, seq);

        return factory.build(builder);
    }

    static <E, T, Builder> T appended(
            @NotNull ISeq<? extends E> seq,
            E element,
            @NotNull CollectionFactory<? super E, Builder, ? extends T> factory
    ) {
        assert seq != null;
        assert factory != null;

        Builder builder = factory.newBuilder();

        factory.sizeHint(builder, seq, 1);

        factory.addAllToBuilder(builder, seq);
        factory.addToBuilder(builder, element);


        return factory.build(builder);
    }

    static <E, T, Builder> T appendedAll(
            @NotNull ISeq<? extends E> seq,
            @NotNull Iterable<? extends E> postfix,
            @NotNull CollectionFactory<? super E, Builder, ? extends T> factory
    ) {
        assert seq != null;
        assert factory != null;
        Objects.requireNonNull(postfix);

        Builder builder = factory.newBuilder();

        factory.sizeHint(builder, seq);
        factory.addAllToBuilder(builder, seq);

        factory.sizeHint(builder, postfix);
        factory.addAllToBuilder(builder, postfix);

        return factory.build(builder);
    }

    static <E, U, T, Builder> T mapIndexed(
            @NotNull ISeq<? extends E> Seq,
            @NotNull IndexedFunction<? super E, ? extends U> mapper,
            @NotNull CollectionFactory<? super U, Builder, ? extends T> factory
    ) {
        assert Seq != null;
        assert factory != null;
        Objects.requireNonNull(mapper);

        Builder builder = factory.newBuilder();

        factory.sizeHint(builder, Seq);

        int idx = 0;
        for (E e : Seq) {
            factory.addToBuilder(builder, mapper.apply(idx++, e));
        }
        return factory.build(builder);
    }

    @NotNull
    protected final <To extends ISeq<E>> To updatedImpl(int index, E newValue) {
        return (To) AbstractISeq.updated(this, index, newValue, iterableFactory());
    }

    @NotNull
    protected final <To extends ISeq<E>> To dropImpl(int n) {
        return (To) AbstractISeq.drop(this, n, iterableFactory());
    }

    @NotNull
    protected final <To extends ISeq<E>> To dropWhileImpl(@NotNull Predicate<? super E> predicate) {
        return (To) AbstractISeq.dropWhile(this, predicate, iterableFactory());
    }

    @NotNull
    protected final <To extends ISeq<E>> To takeImpl(int n) {
        return (To) AbstractISeq.take(this, n, iterableFactory());
    }

    @NotNull
    protected final <To extends ISeq<E>> To takeWhileImpl(@NotNull Predicate<? super E> predicate) {
        return (To) AbstractISeq.takeWhile(this, predicate, iterableFactory());
    }

    @NotNull
    protected final <To extends ISeq<E>> To concatImpl(@NotNull Seq<? extends E> other) {
        return (To) AbstractISeq.concat(this, other, iterableFactory());
    }

    @NotNull
    protected final <To extends ISeq<E>> To prependedImpl(E element) {
        return (To) AbstractISeq.prepended(this, element, iterableFactory());
    }

    @NotNull
    protected final <To extends ISeq<E>> To prependedAllImpl(@NotNull Iterable<? extends E> prefix) {
        return (To) AbstractISeq.prependedAll(this, prefix, iterableFactory());
    }

    @NotNull
    protected final <To extends ISeq<E>> To appendedImpl(E element) {
        return (To) AbstractISeq.prepended(this, element, iterableFactory());
    }

    @NotNull
    protected final <To extends ISeq<E>> To appendedAllImpl(@NotNull Iterable<? extends E> postfix) {
        return (To) AbstractISeq.appendedAll(this, postfix, iterableFactory());
    }

    @NotNull
    protected final <U, To extends ISeq<U>> To mapIndexedImpl(@NotNull IndexedFunction<? super E, ? extends U> mapper) {
        return (To) AbstractISeq.mapIndexed(this, mapper, iterableFactory());
    }
}
