package asia.kala.collection.immutable;

import asia.kala.Option;
import asia.kala.Tuple2;
import asia.kala.annotations.Covariant;
import asia.kala.collection.*;
import asia.kala.collection.mutable.LinkedBuffer;
import asia.kala.function.IndexedFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ImmutableList<@Covariant E> extends AbstractImmutableSeq<E> implements ImmutableSeq<E>, Serializable {
    ImmutableList() {
    }

    private static final ImmutableList.Factory<?> FACTORY = new Factory<>();

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    public static <E> ImmutableList<E> narrow(ImmutableList<? extends E> list) {
        return (ImmutableList<E>) list;
    }

    @SuppressWarnings("unchecked")
    public static <E> CollectionFactory<E, ?, ImmutableList<E>> factory() {
        return (Factory<E>) FACTORY;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <E> ImmutableList<E> nil() {
        return (ImmutableList<E>) Nil.INSTANCE;
    }

    @NotNull
    public static <E> ImmutableList<E> empty() {
        return nil();
    }

    @NotNull
    public static <E> ImmutableList<E> of() {
        return nil();
    }

    @NotNull
    @SafeVarargs
    public static <E> ImmutableList<E> of(E... elements) {
        return from(elements);
    }

    @NotNull
    public static <E> ImmutableList<E> from(E @NotNull [] elements) {
        Objects.requireNonNull(elements);
        ImmutableList<E> list = nil();

        for (int i = elements.length - 1; i >= 0; i--) {
            list = list.cons(elements[i]);
        }

        return list;
    }

    public static <E> ImmutableList<E> from(@NotNull Iterable<? extends E> iterable) {
        Objects.requireNonNull(iterable);
        return ImmutableList.<E>factory().from(iterable);
    }

    public abstract E head();

    public abstract Option<E> headOption();

    @NotNull
    public abstract ImmutableList<E> tail();

    @NotNull
    public abstract Option<ImmutableList<E>> tailOption();

    @NotNull
    @Contract("_ -> new")
    public final ImmutableList<E> cons(E element) {
        return new ImmutableCons<>(element, this);
    }


    //
    // -- ImmutableSeq
    //

    @NotNull
    @Override
    public final ImmutableList<E> updated(int index, E newValue) {
        return updatedImpl(index, newValue);
    }

    @NotNull
    @Override
    public final ImmutableList<E> prepended(E element) {
        return cons(element);
    }

    @NotNull
    @Override
    public final ImmutableList<E> prependedAll(@NotNull Iterable<? extends E> prefix) {
        IndexedSeq<E> s = KalaCollectionUtils.tryToIndexedSeq(prefix);
        if (s == null) {
            return prependedAllImpl(prefix);
        }

        ImmutableList<E> result = this;
        for (E e : s.reverseIterator()) {
            result = result.prepended(e);
        }
        return result;
    }

    @NotNull
    @Override
    public final ImmutableList<E> prependedAll(E @NotNull [] prefix) {
        Objects.requireNonNull(prefix);

        ImmutableList<E> result = this;
        for (int i = prefix.length - 1; i >= 0; i--) {
            result = result.cons(prefix[i]);
        }
        return result;
    }

    @NotNull
    @Override
    public final ImmutableList<E> appended(E element) {
        return appendedImpl(element);
    }

    @NotNull
    @Override
    public final ImmutableList<E> appendedAll(@NotNull Iterable<? extends E> postfix) {
        if (KalaCollectionUtils.knowSize(postfix) == 0) {
            return this;
        }
        return appendedAllImpl(postfix);
    }

    @NotNull
    @Override
    public final ImmutableList<E> appendedAll(E @NotNull [] postfix) {
        Objects.requireNonNull(postfix);
        if (postfix.length == 0) {
            return this;
        }
        if (isEmpty()) {
            return from(postfix);
        }
        return appendedAllImpl(postfix);
    }

    @NotNull
    @Override
    public final ImmutableList<E> drop(int n) {
        ImmutableList<E> list = this;
        while (list != Nil.INSTANCE && n-- > 0) {
            list = list.tail();
        }
        return list;
    }


    @NotNull
    @Override
    public final ImmutableList<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        ImmutableList<E> list = this;
        while (list != Nil.INSTANCE && predicate.test(list.head())) {
            list = list.tail();
        }
        return list;
    }

    @NotNull
    @Override
    public final ImmutableList<E> take(int n) {
        return takeImpl(n);
    }

    @NotNull
    @Override
    public final ImmutableSeq<E> takeWhile(@NotNull Predicate<? super E> predicate) {
        return takeWhileImpl(predicate);
    }

    @NotNull
    @Override
    public final ImmutableList<E> concat(@NotNull Seq<? extends E> other) {
        return concatImpl(other);
    }

    @NotNull
    @Override
    public final <U> ImmutableList<U> flatMap(@NotNull Function<? super E, ? extends Iterable<? extends U>> mapper) {
        return flatMapImpl(mapper);
    }

    @NotNull
    @Override
    public final ImmutableList<E> sorted() {
        return sortedImpl();
    }

    @NotNull
    @Override
    public final ImmutableList<E> sorted(@NotNull Comparator<? super E> comparator) {
        return sortedImpl();
    }

    @NotNull
    @Override
    public final <U> ImmutableList<U> mapIndexed(@NotNull IndexedFunction<? super E, ? extends U> mapper) {
        return mapIndexedImpl(mapper);
    }


    //
    // -- ImmutableCollection
    //

    @Override
    public final String className() {
        return "ImmutableList";
    }

    @NotNull
    @Override
    public <U> CollectionFactory<U, ?, ImmutableList<U>> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    public final <U> ImmutableList<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return mapImpl(mapper);
    }

    @NotNull
    @Override
    public final ImmutableList<E> filter(@NotNull Predicate<? super E> predicate) {
        return filterImpl(predicate);
    }

    @NotNull
    @Override
    public final ImmutableList<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return filterNotImpl(predicate);
    }

    @NotNull
    @Override
    public final ImmutableList<@NotNull E> filterNotNull() {
        return this.filter(Objects::nonNull);
    }

    @NotNull
    @Override
    public abstract Tuple2<ImmutableList<E>, ImmutableList<E>> span(@NotNull Predicate<? super E> predicate);

    @NotNull
    @Override
    public final ImmutableList<E> toImmutableList() {
        return this;
    }

    public static final class Nil extends ImmutableList<Object> {
        private static final long serialVersionUID = -7963313933036451568L;

        static final Nil INSTANCE = new Nil();

        @Override
        public final Object head() {
            throw new NoSuchElementException("ImmutableList.Nil.head()");
        }

        @Override
        public final Option<Object> headOption() {
            return Option.none();
        }

        @NotNull
        @Override
        public final ImmutableList<Object> tail() {
            throw new NoSuchElementException("ImmutableList.Nil.tail()");
        }

        @NotNull
        @Override
        public final Option<ImmutableList<Object>> tailOption() {
            return Option.none();
        }

        //
        // -- ImmutableSeq
        //

        @Override
        public final Object get(int index) {
            throw new NoSuchElementException();
        }

        @NotNull
        @Override
        public final Option<Object> getOption(int index) {
            return Option.none();
        }

        //region Traversable members

        @NotNull
        @Override
        public final Tuple2<ImmutableList<Object>, ImmutableList<Object>> span(@NotNull Predicate<? super Object> predicate) {
            return new Tuple2<>(ImmutableList.nil(), ImmutableList.nil());
        }

        @Override
        public final boolean isEmpty() {
            return true;
        }

        @Override
        public final int knownSize() {
            return 0;
        }

        @NotNull
        @Override
        public final Enumerator<Object> iterator() {
            return Enumerator.empty();
        }

        @Override
        public final String toString() {
            return "ImmutableList[]";
        }

        //endregion

        //
        // -- Serializable
        //

        private Object readResolve() {
            return INSTANCE;
        }
    }

    public static abstract class Cons<E> extends ImmutableList<E> {
        Cons() {
        }

        @Override
        public final Option<E> headOption() {
            return Option.some(head());
        }

        @NotNull
        @Override
        public final Option<ImmutableList<E>> tailOption() {
            return Option.some(tail());
        }

        //
        // -- ImmutableCollection
        //

        @NotNull
        @Override
        public final Tuple2<ImmutableList<E>, ImmutableList<E>> span(@NotNull Predicate<? super E> predicate) {
            return spanImpl(predicate);
        }

        @Override
        public final boolean isEmpty() {
            return false;
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return new ImmutableListIterator<>(this);
        }
    }

    static final class MutableCons<E> extends Cons<E> {
        private static final long serialVersionUID = 3721401019662509067L;

        E head;

        @NotNull
        ImmutableList<? extends E> tail;

        MutableCons(E head, @NotNull ImmutableList<? extends E> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public final E head() {
            return head;
        }

        @NotNull
        @Override
        public final ImmutableList<E> tail() {
            return ImmutableList.narrow(tail);
        }
    }

    static final class ImmutableCons<E> extends Cons<E> {
        private static final long serialVersionUID = -1987307027661389715L;

        final E head;

        @NotNull
        final ImmutableList<? extends E> tail;

        ImmutableCons(E head, @NotNull ImmutableList<? extends E> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public final E head() {
            return head;
        }

        @NotNull
        @Override
        public final ImmutableList<E> tail() {
            return ImmutableList.narrow(tail);
        }
    }

    private static final class Factory<E> implements CollectionFactory<E, LinkedBuffer<E>, ImmutableList<E>> {
        Factory() {
        }

        @Override
        public final ImmutableList<E> empty() {
            return ImmutableList.empty();
        }

        @Override
        public final LinkedBuffer<E> newBuilder() {
            return new LinkedBuffer<>();
        }

        @Override
        public final void addToBuilder(@NotNull LinkedBuffer<E> builder, E value) {
            builder.append(value);
        }

        @Override
        public final LinkedBuffer<E> mergeBuilder(@NotNull LinkedBuffer<E> builder1, @NotNull LinkedBuffer<E> builder2) {
            if (((ImmutableInternal.LinkedBufferImpl<E>) builder2).first != null) {
                for (E e : ((ImmutableInternal.LinkedBufferImpl<E>) builder2).first) {
                    builder1.append(e);
                }
            }
            return builder1;
        }

        @Override
        public final ImmutableList<E> build(@NotNull LinkedBuffer<E> builder) {
            return builder.toImmutableList();
        }
    }
}
