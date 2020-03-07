package asia.kala.collection;

import asia.kala.LazyValue;
import asia.kala.Option;
import asia.kala.annotations.Covariant;
import asia.kala.function.IndexedConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.*;

final class SeqViews {
    static class Of<@Covariant E, C extends Seq<E>> extends Views.Of<E, C> implements SeqView<E> {
        Of(@NotNull C collection) {
            super(collection);
        }

        public E get(int index) {
            return collection.get(index);
        }

        @NotNull
        public Option<E> getOption(int index) {
            return collection.getOption(index);
        }

        public boolean isDefinedAt(int index) {
            return collection.isDefinedAt(index);
        }

        public int indexOf(Object value) {
            return collection.indexOf(value);
        }

        public int indexOf(Object value, int from) {
            return collection.indexOf(value, from);
        }

        public int indexWhere(@NotNull Predicate<? super E> predicate) {
            return collection.indexWhere(predicate);
        }

        public int indexWhere(@NotNull Predicate<? super E> predicate, int from) {
            return collection.indexWhere(predicate, from);
        }

        public int lastIndexOf(Object value) {
            return collection.lastIndexOf(value);
        }

        public int lastIndexOf(Object value, int end) {
            return collection.lastIndexOf(value, end);
        }

        public int lastIndexWhere(@NotNull Predicate<? super E> predicate) {
            return collection.lastIndexWhere(predicate);
        }

        public int lastIndexWhere(@NotNull Predicate<? super E> predicate, int end) {
            return collection.lastIndexWhere(predicate, end);
        }

        public void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
            collection.forEachIndexed(action);
        }

        @NotNull
        @Override
        public Enumerator<E> reverseIterator() {
            return collection.reverseIterator();
        }
    }

    static class Updated<@Covariant E> extends AbstractSeqView<E> {
        @NotNull
        private final SeqView<E> source;

        private final int index;

        private final E newValue;

        Updated(@NotNull SeqView<E> source, int index, E newValue) {
            assert source != null;

            this.source = source;
            this.index = index;
            this.newValue = newValue;
        }

        @Override
        public final int size() {
            return source.size();
        }

        @Override
        public final E get(int index) {
            if (index == this.index) {
                return newValue;
            }
            return source.get(index);
        }

        @NotNull
        @Override
        public final Option<E> getOption(int index) {
            if (index == this.index) {
                return Option.some(newValue);
            }
            return source.getOption(index);
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return new AbstractEnumerator<E>() {
                private final Enumerator<E> it = source.iterator();
                private int i = 0;

                @Override
                public final boolean hasNext() {
                    if (it.hasNext()) {
                        return true;
                    }
                    if (index >= i) {
                        throw new IndexOutOfBoundsException();
                    }
                    return false;
                }

                @Override
                public final E next() {
                    E value = it.next();
                    if (i++ == index) {
                        value = newValue;
                    }

                    return value;
                }
            };
        }
    }

    static class Drop<@Covariant E> extends AbstractSeqView<E> {
        @NotNull
        protected final SeqView<E> source;

        protected final int n;

        Drop(@NotNull SeqView<E> source, int n) {
            assert source != null;

            this.source = source;
            this.n = n;
        }

        @Override
        public final int size() {
            int s = source.size();
            if (n <= 0) {
                return s;
            }
            if (n >= s) {
                return 0;
            }
            return s - n;
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return source.iterator().drop(n);
        }
    }

    static class DropWhile<@Covariant E> extends AbstractSeqView<E> {
        @NotNull
        private final SeqView<E> source;

        @NotNull
        private final Predicate<? super E> predicate;

        DropWhile(@NotNull SeqView<E> source, @NotNull Predicate<? super E> predicate) {
            assert source != null;
            assert predicate != null;

            this.source = source;
            this.predicate = predicate;
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return source.iterator().dropWhile(predicate);
        }
    }

    static class Take<@Covariant E> extends AbstractSeqView<E> {
        @NotNull
        protected final SeqView<E> source;

        private final int n;

        Take(@NotNull SeqView<E> source, int n) {
            assert source != null;

            this.source = source;
            this.n = n;
        }

        @Override
        public final int size() {
            if (n <= 0) {
                return 0;
            }

            return Integer.min(n, source.size());
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return source.iterator().take(n);
        }
    }

    static class TakeWhile<@Covariant E> extends AbstractSeqView<E> {
        @NotNull
        private final SeqView<E> source;

        @NotNull
        private final Predicate<? super E> predicate;

        TakeWhile(@NotNull SeqView<E> source, @NotNull Predicate<? super E> predicate) {
            assert source != null;
            assert predicate != null;

            this.source = source;
            this.predicate = predicate;
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return source.iterator().takeWhile(predicate);
        }
    }

    static class Concat<@Covariant E> extends AbstractSeqView<E> {
        @NotNull
        private final Seq<E> seq1;

        @NotNull
        private final Seq<E> seq2;

        Concat(@NotNull Seq<E> seq1, @NotNull Seq<E> seq2) {
            assert seq1 != null;
            assert seq2 != null;


            this.seq1 = seq1;
            this.seq2 = seq2;
        }

        @Override
        public final int size() {
            return seq1.size() + seq2.size();
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return Enumerator.concat(seq1.iterator(), seq1.iterator());
        }
    }

    static class Prepended<@Covariant E> extends AbstractSeqView<E> {
        @NotNull
        private final SeqView<E> source;

        private final E value;

        Prepended(@NotNull SeqView<E> source, E value) {
            assert source != null;

            this.source = source;
            this.value = value;
        }

        @Override
        public final E get(int index) {
            if (index == 0) {
                return value;
            }
            return source.get(index + 1);
        }

        @NotNull
        @Override
        public final Option<E> getOption(int index) {
            if (index == 0) {
                return Option.some(value);
            }
            return source.getOption(index + 1);
        }

        @Override
        public final int size() {
            return source.size() + 1;
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return source.iterator().prepended(value);
        }
    }

    static class Appended<@Covariant E> extends AbstractSeqView<E> {
        @NotNull
        protected final SeqView<E> source;

        protected final E value;

        Appended(@NotNull SeqView<E> source, E value) {
            assert source != null;

            this.source = source;
            this.value = value;
        }

        @Override
        public final int size() {
            return source.size() + 1;
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return source.iterator().appended(value);
        }
    }

    static class Mapped<@Covariant E, T> extends AbstractSeqView<E> {
        @NotNull
        private final SeqView<T> source;

        @NotNull
        private final Function<? super T, ? extends E> mapper;

        public Mapped(@NotNull SeqView<T> source, @NotNull Function<? super T, ? extends E> mapper) {
            assert source != null;
            assert mapper != null;

            this.source = source;
            this.mapper = mapper;
        }

        @Override
        public final int size() {
            return source.size();
        }

        @Override
        public final E get(int index) {
            return mapper.apply(source.get(index));
        }

        @NotNull
        @Override
        public final Option<E> getOption(int index) {
            return source.getOption(index).map(mapper);
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return source.iterator().map(mapper);
        }
    }

    static final class Filter<@Covariant E> extends AbstractSeqView<E> {
        @NotNull
        private final SeqView<E> source;

        @NotNull
        private final Predicate<? super E> predicate;

        public Filter(@NotNull SeqView<E> source, @NotNull Predicate<? super E> predicate) {
            assert source != null;
            assert predicate != null;

            this.source = source;
            this.predicate = predicate;
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return source.iterator().filter(predicate);
        }
    }

    static final class FlatMapped<@Covariant E, T> extends AbstractSeqView<E> {
        @NotNull
        private final SeqView<? extends T> source;
        @NotNull
        private final Function<? super T, ? extends Iterable<? extends E>> mapper;

        public FlatMapped(
                @NotNull SeqView<? extends T> source,
                @NotNull Function<? super T, ? extends Iterable<? extends E>> mapper) {
            assert source != null;
            assert mapper != null;

            this.source = source;
            this.mapper = mapper;
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return new Enumerators.Concat<>(source.map(it -> mapper.apply(it).iterator()).iterator());
        }
    }

    static final class Sorted<@Covariant E> extends AbstractSeqView<E> {
        private final SeqView<E> source;
        private final LazyValue<Seq<E>> sortedSeq;

        @SuppressWarnings("unchecked")
        Sorted(@NotNull SeqView<E> source, @NotNull Comparator<? super E> comparator) {
            assert source != null;
            assert comparator != null;

            this.source = source;
            this.sortedSeq = LazyValue.of(() -> {
                Object[] arr = source.toObjectArray();
                Arrays.sort(arr, (Comparator<? super Object>) comparator);
                return ArraySeq.wrap((E[]) arr);
            });
        }

        @Override
        public final E get(int index) {
            return sortedSeq.get().get(index);
        }

        @NotNull
        @Override
        public final Option<E> getOption(int index) {
            return sortedSeq.get().getOption(index);
        }

        @Override
        public final int size() {
            if (sortedSeq.isReady()) {
                return sortedSeq.get().size();
            }
            return source.size();
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return sortedSeq.get().iterator();
        }
    }
}
