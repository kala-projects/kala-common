package asia.kala.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

final class Enumerators {
    private Enumerators() {

    }

    static final Object TAG = new Object();

    static final class Empty implements Enumerator<Object> {
        static final Enumerators.Empty INSTANCE = new Enumerators.Empty();

        private Empty() {
        }

        @Override
        public final boolean hasNext() {
            return false;
        }

        @Override
        public final Object next() {
            throw new NoSuchElementException("Enumerators.Empty.next");
        }

        @Override
        public final String toString() {
            return "Enumerators.Empty";
        }
    }

    static final class Single<T> implements Enumerator<T> {

        private Object value;

        Single(Object value) {
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

    static final class Mapped<E, S> implements Enumerator<E> {
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

    static final class Filtered<E> implements Enumerator<E> {
        @NotNull
        private final Iterator<? extends E> source;
        @NotNull
        private final Predicate<? super E> predicate;

        private Object nextValue = TAG;

        Filtered(@NotNull Iterator<? extends E> source, @NotNull Predicate<? super E> predicate) {
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

    static final class Dropped<E> implements Enumerator<E> {
        @NotNull
        private final Iterator<? extends E> source;

        private Predicate<? super E> predicate;

        private Object nextValue = TAG;

        Dropped(@NotNull Iterator<? extends E> source, @NotNull Predicate<? super E> predicate) {
            assert source != null;
            assert predicate != null;

            this.source = source;
            this.predicate = predicate;
        }

        @Override
        public final boolean hasNext() {
            if(predicate == null) {
                return source.hasNext();
            }
            if(nextValue != TAG) {
                return true;
            }

            while (source.hasNext()) {
                E e = source.next();
                if(!predicate.test(e)) {
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
            if(hasNext()) {
                if(predicate == null) {
                    return source.next();
                }
                predicate = null;
                return (E) nextValue;
            }
            throw new NoSuchElementException(this + ".next");
        }
    }
}
