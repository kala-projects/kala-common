package asia.kala.collection;

import asia.kala.Option;
import asia.kala.function.IndexedConsumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.*;

final class SeqViews {
    static class Of<E, C extends Seq<E>> extends Views.Of<E, C> implements SeqView<E> {
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

        public int indexOf(Object element) {
            return collection.indexOf(element);
        }

        public int indexOf(Object element, int from) {
            return collection.indexOf(element, from);
        }

        public int indexWhere(@NotNull Predicate<? super E> predicate) {
            return collection.indexWhere(predicate);
        }

        public int indexWhere(@NotNull Predicate<? super E> predicate, int from) {
            return collection.indexWhere(predicate, from);
        }

        public void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
            collection.forEachIndexed(action);
        }
    }

    static final class Mapped<E, T> extends AbstractSeqView<E> {
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

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return source.iterator().map(mapper);
        }
    }

    static final class Filter<E> extends AbstractSeqView<E> {
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

    static final class FlatMapped<E, T> extends AbstractSeqView<E> {
        @NotNull
        private final SeqView<? extends T> source;
        @NotNull
        private final Function<? super T, ? extends TraversableOnce<? extends E>> mapper;

        public FlatMapped(
                @NotNull SeqView<? extends T> source,
                @NotNull Function<? super T, ? extends TraversableOnce<? extends E>> mapper) {
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

}
