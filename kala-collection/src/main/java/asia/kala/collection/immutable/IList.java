package asia.kala.collection.immutable;

import asia.kala.Option;
import asia.kala.OptionContainer;
import asia.kala.Tuple2;
import asia.kala.collection.Enumerator;
import asia.kala.collection.SeqOps;
import asia.kala.collection.TraversableOnce;
import asia.kala.collection.TraversableOps;
import asia.kala.collection.mutable.CollectionBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class IList<E> implements ISeq<E>, Serializable {
    public static final class Builder<E> implements CollectionBuilder<E, IList<E>> {
        private MCons<E> first = null;
        private MCons<E> last = null;

        @Override
        public final void add(E element) {
            MCons<E> i = new MCons<>(element, IList.nil());
            if (last == null) {
                first = i;
            } else {
                last.tail = i;
            }
            last = i;
        }

        @Override
        public final void clear() {
            first = last = null;
        }

        @Override
        public final IList<E> build() {
            return first;
        }
    }

    IList() {
    }

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    public static <E> IList<E> narrow(IList<? extends E> list) {
        return (IList<E>) list;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <E> IList<E> nil() {
        return (IList<E>) Nil.INSTANCE;
    }

    public abstract E head();

    public abstract Option<E> headOption();

    @NotNull
    public abstract IList<E> tail();

    @NotNull
    public abstract Option<IList<E>> tailOption();

    //
    // -- Seq
    //

    @NotNull
    @Override
    public abstract IList<E> drop(int n);

    @NotNull
    @Override
    public abstract IList<E> dropWhile(@NotNull Predicate<? super E> predicate);

    @NotNull
    @Override
    public abstract IList<E> concat(@NotNull TraversableOnce<? extends E> traversable);

    @NotNull
    @Override
    public abstract <U> IList<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper);

    //
    // -- Traversable
    //

    @NotNull
    @Override
    public final <U> IList.Builder<U> newBuilder() {
        return new IList.Builder<>();
    }

    @NotNull
    @Override
    public abstract Tuple2<? extends IList<E>, ? extends IList<E>> span(@NotNull Predicate<? super E> predicate);

    //
    // -- TraversableOnce
    //

    @Override
    public final String stringPrefix() {
        return "IList";
    }

    @NotNull
    @Override
    public abstract <U> IList<U> map(@NotNull Function<? super E, ? extends U> mapper);

    @NotNull
    @Override
    public abstract IList<E> filter(@NotNull Predicate<? super E> predicate);

    @NotNull
    @Override
    public abstract IList<E> filterNot(@NotNull Predicate<? super E> predicate);

    public static final class Nil extends IList<Object> {
        private static final long serialVersionUID = -7963313933036451568L;

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
        // -- Seq
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

        @NotNull
        @Override
        public final IList<Object> drop(int n) {
            return IList.nil();
        }

        @NotNull
        @Override
        public final IList<Object> dropWhile(@NotNull Predicate<? super Object> predicate) {
            return IList.nil();
        }

        @NotNull
        @Override
        @SuppressWarnings("unchecked")
        public final IList<Object> concat(@NotNull TraversableOnce<?> traversable) {
            if (traversable instanceof IList<?>) {
                return (IList<Object>) traversable;
            }

            Builder<Object> builder = new Builder<>();
            builder.addAll(traversable);

            return builder.build();
        }

        //
        // -- Traversable
        //

        @NotNull
        @Override
        public final Tuple2<? extends IList<Object>, ? extends IList<Object>> span(@NotNull Predicate<? super Object> predicate) {
            return new Tuple2<>(IList.nil(), IList.nil());
        }


        //
        // -- TraversableOnce
        //

        @Override
        public final boolean isEmpty() {
            return true;
        }

        @NotNull
        @Override
        public <U> IList<U> map(@NotNull Function<? super Object, ? extends U> mapper) {
            return IList.nil();
        }

        @NotNull
        @Override
        public <U> IList<U> flatMap(@NotNull Function<? super Object, ? extends TraversableOnce<? extends U>> mapper) {
            return IList.nil();
        }

        @NotNull
        @Override
        public final IList<Object> filter(@NotNull Predicate<? super Object> predicate) {
            return IList.nil();
        }

        @NotNull
        @Override
        public IList<Object> filterNot(@NotNull Predicate<? super Object> predicate) {
            return IList.nil();
        }

        @NotNull
        @Override
        public final Enumerator<Object> iterator() {
            return Enumerator.empty();
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
        // -- Seq
        //
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
        public final IList<E> concat(@NotNull TraversableOnce<? extends E> traversable) {
            return SeqOps.concat(this, traversable, newBuilder());
        }

        //
        // -- Traversable
        //

        @NotNull
        @Override
        public final Tuple2<? extends IList<E>, ? extends IList<E>> span(@NotNull Predicate<? super E> predicate) {
            return TraversableOps.span(this, predicate, newBuilder(), newBuilder());
        }

        //
        // -- TraversableOnce
        //

        @Override
        public boolean isEmpty() {
            return false;
        }

        @NotNull
        @Override
        public <U> IList<U> map(@NotNull Function<? super E, ? extends U> mapper) {
            return TraversableOps.map(this, mapper, newBuilder());
        }

        @NotNull
        @Override
        public <U> IList<U> flatMap(@NotNull Function<? super E, ? extends TraversableOnce<? extends U>> mapper) {
            return TraversableOps.flatMap(this, mapper, newBuilder());
        }

        @NotNull
        @Override
        public IList<E> filter(@NotNull Predicate<? super E> predicate) {
            return TraversableOps.filter(this, predicate, newBuilder());
        }

        @NotNull
        @Override
        public IList<E> filterNot(@NotNull Predicate<? super E> predicate) {
            return TraversableOps.filterNot(this, predicate, newBuilder());
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return new IListIterator<>(this);
        }

        @Override
        public final boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public final String toString() {
            return joinToString(", ", "IList[", "]");
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
}
