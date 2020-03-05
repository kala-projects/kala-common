package asia.kala.collection;

import asia.kala.annotations.Covariant;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

final class Enumerators {
    private Enumerators() {

    }

    static final Object TAG = new Object();


    static final class IteratorWrapper<@Covariant E> extends AbstractEnumerator<E> {
        @NotNull
        private final Iterator<? extends E> iterator;

        IteratorWrapper(@NotNull Iterator<? extends E> iterator) {
            assert iterator != null;

            this.iterator = iterator;
        }

        @Override
        public final boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public final E next() {
            return iterator.next();
        }
    }

    static final class Empty extends AbstractEnumerator<Object> {
        static final Empty INSTANCE = new Empty();

        private Empty() {
        }

        @Override
        public final boolean hasNext() {
            return false;
        }

        @Override
        public final Object next() {
            throw new NoSuchElementException("Enumerators.Empty.next()");
        }
    }

    static final class Id<@Covariant T> extends AbstractEnumerator<T> {

        private Object value;

        Id(Object value) {
            this.value = value;
        }

        @Override
        public final boolean hasNext() {
            return value != TAG;
        }

        @Override
        public final T next() {
            if (value == TAG) {
                throw new NoSuchElementException("Enumerators.Single.next");
            }
            @SuppressWarnings("unchecked")
            T v = (T) value;
            value = TAG;
            return v;
        }
    }

    static final class Mapped<@Covariant E, S> extends AbstractEnumerator<E> {
        @NotNull
        private final Enumerator<? extends S> source;

        @NotNull
        private final Function<? super S, ? extends E> mapper;

        Mapped(@NotNull Enumerator<? extends S> source, @NotNull Function<? super S, ? extends E> mapper) {
            assert source != null;
            assert mapper != null;

            this.source = source;
            this.mapper = mapper;
        }

        @Override
        public final boolean hasNext() {
            return source.hasNext();
        }

        @Override
        public final E next() {
            return mapper.apply(source.next());
        }
    }

    static final class Filter<@Covariant E> extends AbstractEnumerator<E> {
        @NotNull
        private final Iterator<? extends E> source;
        @NotNull
        private final Predicate<? super E> predicate;

        private Object nextValue = TAG;

        Filter(@NotNull Iterator<? extends E> source, @NotNull Predicate<? super E> predicate) {
            assert source != null;
            assert predicate != null;

            this.source = source;
            this.predicate = predicate;
        }

        private void next0() {
            while (source.hasNext()) {
                E e = source.next();
                if (predicate.test(e)) {
                    nextValue = e;
                    return;
                }
            }
            nextValue = TAG;
        }

        @Override
        public final boolean hasNext() {
            if (nextValue == TAG) {
                next0();
            }
            return nextValue != TAG;
        }

        @Override

        public final E next() {
            if (nextValue == TAG) {
                next0();
            }

            if (nextValue == TAG) {
                throw new NoSuchElementException(this + ".next");
            }

            @SuppressWarnings("unchecked")
            E n = (E) nextValue;
            next0();
            return n;
        }
    }

    static final class DropWhile<@Covariant E> extends AbstractEnumerator<E> {
        @NotNull
        private final Iterator<? extends E> source;

        private Predicate<? super E> predicate;

        private Object nextValue = TAG;

        DropWhile(@NotNull Iterator<? extends E> source, @NotNull Predicate<? super E> predicate) {
            assert source != null;
            assert predicate != null;

            this.source = source;
            this.predicate = predicate;
        }

        @Override
        public final boolean hasNext() {
            if (predicate == null) {
                return source.hasNext();
            }
            if (nextValue != TAG) {
                return true;
            }

            while (source.hasNext()) {
                E e = source.next();
                if (!predicate.test(e)) {
                    nextValue = e;
                    return true;
                }
            }
            predicate = null;
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public final E next() {
            if (hasNext()) {
                if (predicate == null) {
                    return source.next();
                }
                predicate = null;
                return (E) nextValue;
            }
            throw new NoSuchElementException(this + ".next()");
        }
    }

    static final class Take<@Covariant E> extends AbstractEnumerator<E> {
        private final Enumerator<? extends E> source;
        private int n;

        Take(Enumerator<? extends E> source, int n) {
            assert source != null;

            this.source = source;
            this.n = n;
        }

        @Override
        public final boolean hasNext() {
            return n > 0 && source.hasNext();
        }

        @Override
        public final E next() {
            if (hasNext()) {
                --n;
                return source.next();
            }
            throw new NoSuchElementException(this + ".next()");
        }
    }

    static final class TakeWhile<@Covariant E> extends AbstractEnumerator<E> {
        @NotNull
        private Iterator<? extends E> source;

        private Predicate<? super E> predicate;

        private Object nextValue = TAG;

        TakeWhile(@NotNull Iterator<? extends E> source, Predicate<? super E> predicate) {
            assert source != null;

            this.source = source;
            this.predicate = predicate;
        }

        @Override
        public final boolean hasNext() {
            if (nextValue != TAG) {
                return true;
            }
            if (source.hasNext()) {
                E e = source.next();
                if (predicate.test(e)) {
                    nextValue = e;
                    return true;
                } else {
                    source = Enumerator.empty();
                }
            }
            return false;
        }

        @Override
        public final E next() {
            if (hasNext()) {
                @SuppressWarnings("unchecked")
                E e = (E) nextValue;
                nextValue = TAG;
                return e;
            }
            throw new NoSuchElementException(this + ".next()");
        }
    }

    static final class Concat<@Covariant E> extends AbstractEnumerator<E> {
        @NotNull
        private final Iterator<? extends Iterator<? extends E>> iterators;

        private Iterator<? extends E> current = null;

        Concat(@NotNull Iterator<? extends Iterator<? extends E>> iterators) {
            this.iterators = iterators;
        }

        @Override
        public final boolean hasNext() {
            while ((current == null || !current.hasNext()) && iterators.hasNext()) {
                current = iterators.next();
            }
            return current != null && current.hasNext();
        }

        @Override
        public final E next() {
            if (hasNext()) {
                return current.next();
            }
            throw new NoSuchElementException(this + ".next()");
        }
    }

    static final class Updated<@Covariant E> extends AbstractEnumerator<E> {
        @NotNull
        private final Iterator<? extends E> source;

        private final int n;
        private final E newValue;

        private int idx = 0;

        Updated(@NotNull Iterator<? extends E> source, int n, E newValue) {
            this.source = source;
            this.n = n;
            this.newValue = newValue;
        }

        @Override
        public final boolean hasNext() {
            return source.hasNext();
        }

        @Override
        public final E next() {
            if (idx++ == n) {
                source.next();
                return newValue;
            }
            return source.next();
        }
    }

    static final class Prepended<@Covariant E> extends AbstractEnumerator<E> {
        @NotNull
        private final Iterator<? extends E> source;

        private final E value;

        private boolean flag = true;

        Prepended(@NotNull Iterator<? extends E> source, E value) {
            this.source = source;
            this.value = value;
        }

        @Override
        public final boolean hasNext() {
            return flag || source.hasNext();
        }

        @Override
        public final E next() {
            if (flag) {
                flag = false;
                return value;
            }

            return source.next();
        }
    }

    static final class Appended<@Covariant E> extends AbstractEnumerator<E> {
        @NotNull
        private final Iterator<? extends E> source;

        private final E value;

        private boolean flag = true;

        Appended(@NotNull Iterator<? extends E> source, E value) {
            this.source = source;
            this.value = value;
        }

        @Override
        public final boolean hasNext() {
            return source.hasNext() || flag;
        }

        @Override
        public final E next() {
            if (source.hasNext()) {
                return source.next();
            }
            if (flag) {
                flag = false;
                return value;
            }
            throw new NoSuchElementException();
        }
    }

}
