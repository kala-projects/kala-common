package asia.kala.collection.immutable;

import asia.kala.Option;
import asia.kala.Tuple2;
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

public abstract class IList<E> extends AbstractISeq<E> implements ISeq<E>, Serializable {
    IList() {
    }

    public static final IList.Factory<?> FACTORY = new Factory<>();

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    public static <E> IList<E> narrow(IList<? extends E> list) {
        return (IList<E>) list;
    }

    @SuppressWarnings("unchecked")
    public static <E> IList.Factory<E> factory() {
        return (Factory<E>) FACTORY;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <E> IList<E> nil() {
        return (IList<E>) Nil.INSTANCE;
    }

    @NotNull
    public static <E> IList<E> empty() {
        return nil();
    }

    @NotNull
    public static <E> IList<E> of() {
        return nil();
    }

    @NotNull
    @SafeVarargs
    public static <E> IList<E> of(E... elements) {
        return from(elements);
    }

    @NotNull
    public static <E> IList<E> from(@NotNull E[] elements) {
        Objects.requireNonNull(elements);
        IList<E> list = nil();

        for (int i = elements.length - 1; i >= 0; i--) {
            list = list.cons(elements[i]);
        }

        return list;
    }

    public static <E> IList<E> from(@NotNull Iterable<? extends E> iterable) {
        Objects.requireNonNull(iterable);
        return IList.<E>factory().from(iterable);
    }

    public abstract E head();

    public abstract Option<E> headOption();

    @NotNull
    public abstract IList<E> tail();

    @NotNull
    public abstract Option<IList<E>> tailOption();

    @NotNull
    @Contract("_ -> new")
    public final IList<E> cons(E element) {
        return new ICons<>(element, this);
    }


    //
    // -- ISeq
    //

    @NotNull
    @Override
    public final IList<E> updated(int index, E newValue) {
        return updatedImpl(index, newValue);
    }

    @NotNull
    @Override
    public final IList<E> prepended(E element) {
        return cons(element);
    }

    @NotNull
    @Override
    public final IList<E> prependedAll(@NotNull Iterable<? extends E> prefix) {
        IndexedSeq<E> s = KalaCollectionUtils.tryToIndexedSeq(prefix);
        if (s == null) {
            return prependedAllImpl(prefix);
        }

        IList<E> result = this;
        for (E e : s.reverseIterator()) {
            result = result.prepended(e);
        }
        return result;
    }

    @NotNull
    @Override
    public final IList<E> prependedAll(@NotNull E[] prefix) {
        Objects.requireNonNull(prefix);

        IList<E> result = this;
        for (int i = prefix.length - 1; i >= 0; i--) {
            result = result.cons(prefix[i]);
        }
        return result;
    }

    @NotNull
    @Override
    public final IList<E> appended(E element) {
        return appendedImpl(element);
    }

    @NotNull
    @Override
    public final IList<E> appendedAll(@NotNull Iterable<? extends E> postfix) {
        if (KalaCollectionUtils.knowSize(postfix) == 0) {
            return this;
        }
        return appendedAllImpl(postfix);
    }

    @NotNull
    @Override
    public final IList<E> appendedAll(@NotNull E[] postfix) {
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
    public final IList<E> drop(int n) {
        IList<E> list = this;
        while (list != Nil.INSTANCE && n-- > 0) {
            list = list.tail();
        }
        return list;
    }


    @NotNull
    @Override
    public final IList<E> dropWhile(@NotNull Predicate<? super E> predicate) {
        IList<E> list = this;
        while (list != Nil.INSTANCE && predicate.test(list.head())) {
            list = list.tail();
        }
        return list;
    }

    @NotNull
    @Override
    public final IList<E> take(int n) {
        return takeImpl(n);
    }

    @NotNull
    @Override
    public final ISeq<E> takeWhile(@NotNull Predicate<? super E> predicate) {
        return takeWhileImpl(predicate);
    }

    @NotNull
    @Override
    public final IList<E> concat(@NotNull Seq<? extends E> other) {
        return concatImpl(other);
    }

    @NotNull
    @Override
    public final <U> IList<U> flatMap(@NotNull Function<? super E, ? extends Iterable<? extends U>> mapper) {
        return flatMapImpl(mapper);
    }

    @NotNull
    @Override
    public final IList<E> sorted() {
        return sortedImpl();
    }

    @NotNull
    @Override
    public final IList<E> sorted(@NotNull Comparator<? super E> comparator) {
        return sortedImpl();
    }

    @NotNull
    @Override
    public final <U> IList<U> mapIndexed(@NotNull IndexedFunction<? super E, ? extends U> mapper) {
        return mapIndexedImpl(mapper);
    }


    //
    // -- ICollection
    //

    @Override
    public final String className() {
        return "IList";
    }

    @NotNull
    @Override
    public <U> IList.Factory<U> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    public abstract Tuple2<IList<E>, IList<E>> span(@NotNull Predicate<? super E> predicate);

    @NotNull
    @Override
    public final <U> IList<U> map(@NotNull Function<? super E, ? extends U> mapper) {
        return mapImpl(mapper);
    }

    @NotNull
    @Override
    public final IList<E> filter(@NotNull Predicate<? super E> predicate) {
        return filterImpl(predicate);
    }

    @NotNull
    @Override
    public final IList<E> filterNot(@NotNull Predicate<? super E> predicate) {
        return filterNotImpl(predicate);
    }


    @NotNull
    @Override
    public final IList<E> toIList() {
        return this;
    }

    public static final class Nil extends IList<Object> {
        private static final long serialVersionUID = -7963313933036451568L;
        private static final int hashMagic = 251090697;

        static final Nil INSTANCE = new Nil();

        @Override
        public final Object head() {
            throw new NoSuchElementException("IList.Nil.head()");
        }

        @Override
        public final Option<Object> headOption() {
            return Option.none();
        }

        @NotNull
        @Override
        public final IList<Object> tail() {
            throw new NoSuchElementException("IList.Nil.tail()");
        }

        @NotNull
        @Override
        public final Option<IList<Object>> tailOption() {
            return Option.none();
        }

        //
        // -- ISeq
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

        //
        // -- Traversable
        //

        @NotNull
        @Override
        public final Tuple2<IList<Object>, IList<Object>> span(@NotNull Predicate<? super Object> predicate) {
            return new Tuple2<>(IList.nil(), IList.nil());
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
        public final int hashCode() {
            return hashMagic;
        }

        @Override
        public final String toString() {
            return "IList[]";
        }


        //
        // -- Serializable
        //

        private Object readResolve() {
            return INSTANCE;
        }
    }

    public static abstract class Cons<E> extends IList<E> {
        Cons() {
        }

        @Override
        public final Option<E> headOption() {
            return Option.some(head());
        }

        @NotNull
        @Override
        public final Option<IList<E>> tailOption() {
            return Option.some(tail());
        }

        //
        // -- ICollection
        //

        @NotNull
        @Override
        public final Tuple2<IList<E>, IList<E>> span(@NotNull Predicate<? super E> predicate) {
            return spanImpl(predicate);
        }

        @Override
        public final boolean isEmpty() {
            return false;
        }


        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return new IListIterator<>(this);
        }

        @Override
        public final int hashCode() {
            int hash = 0;
            IList<E> list = this;
            while (list != Nil.INSTANCE) {
                hash = 31 * hash + Objects.hashCode(list.head());
                list = list.tail();
            }
            hash += Nil.INSTANCE.hashCode();
            return hash;
        }

        @Override
        @SuppressWarnings("unchecked")
        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Cons<?>)) {
                return false;
            }

            IList<E> list1 = this;
            IList<E> list2 = (IList<E>) obj;

            while (list1 != Nil.INSTANCE && list2 != Nil.INSTANCE) {
                if (!Objects.equals(list1.head(), list1.head())) {
                    return false;
                }
                list1 = list1.tail();
                list2 = list2.tail();
            }
            return list1 == list2;
        }
    }

    static final class MCons<E> extends Cons<E> {
        private static final long serialVersionUID = 3721401019662509067L;

        E head;

        @NotNull
        IList<? extends E> tail;

        MCons(E head, @NotNull IList<? extends E> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public final E head() {
            return head;
        }

        @NotNull
        @Override
        public final IList<E> tail() {
            return IList.narrow(tail);
        }
    }

    static final class ICons<E> extends Cons<E> {
        private static final long serialVersionUID = -1987307027661389715L;

        final E head;

        @NotNull
        final IList<? extends E> tail;

        ICons(E head, @NotNull IList<? extends E> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public final E head() {
            return head;
        }

        @NotNull
        @Override
        public final IList<E> tail() {
            return IList.narrow(tail);
        }
    }

    public static final class Factory<E> implements CollectionFactory<E, LinkedBuffer<E>, IList<E>> {
        Factory() {
        }

        @Override
        public final IList<E> empty() {
            return IList.empty();
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
            if (((Internal.LinkedBufferImpl<E>) builder2).first != null) {
                for (E e : ((Internal.LinkedBufferImpl<E>) builder2).first) {
                    builder1.append(e);
                }
            }
            return builder1;
        }

        @Override
        public final IList<E> build(@NotNull LinkedBuffer<E> builder) {
            return builder.toIList();
        }
    }
}
