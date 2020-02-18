package asia.kala.collection;

import asia.kala.Option;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.*;

final class Views {
    static class Of<E, C extends Traversable<E>> implements View<E> {
        @NotNull
        protected final C collection;

        Of(@NotNull C collection) {
            assert collection != null;
            this.collection = collection;
        }

        @Override
        public final boolean isEmpty() {
            return collection.isEmpty();
        }

        @Override
        public final int knownSize() {
            return collection.knownSize();
        }

        @Override
        public final boolean sameElements(@NotNull TraversableOnce<?> other) {
            return collection.sameElements(other);
        }

        @Override
        public final E max() {
            return collection.max();
        }

        @NotNull
        @Override
        public final Option<E> maxOption() {
            return collection.maxOption();
        }

        @Override
        public final E maxBy(@NotNull Comparator<? super E> comparator) {
            return collection.maxBy(comparator);
        }

        @NotNull
        @Override
        public final Option<E> maxByOption(@NotNull Comparator<? super E> comparator) {
            return collection.maxByOption(comparator);
        }

        @Override
        public final E min() {
            return collection.min();
        }

        @NotNull
        @Override
        public final Option<E> minOption() {
            return collection.minOption();
        }

        @Override
        public final E minBy(@NotNull Comparator<? super E> comparator) {
            return collection.minBy(comparator);
        }

        @NotNull
        @Override
        public final Option<E> minByOption(@NotNull Comparator<? super E> comparator) {
            return collection.minByOption(comparator);
        }

        @Override
        public final <A extends Appendable> A joinTo(@NotNull A buffer) {
            return collection.joinTo(buffer);
        }

        @Override
        public final <A extends Appendable> A joinTo(@NotNull A buffer, @NotNull CharSequence separator) {
            return collection.joinTo(buffer, separator);
        }

        @Override
        public final <A extends Appendable> A joinTo(@NotNull A buffer, @NotNull CharSequence separator, @NotNull CharSequence prefix, @NotNull CharSequence postfix) {
            return collection.joinTo(buffer, separator, prefix, postfix);
        }

        @Override
        public final String joinToString() {
            return collection.joinToString();
        }

        @Override
        public final String joinToString(@NotNull CharSequence separator) {
            return collection.joinToString(separator);
        }

        @Override
        public final String joinToString(@NotNull CharSequence separator, @NotNull CharSequence prefix, @NotNull CharSequence postfix) {
            return collection.joinToString(separator, prefix, postfix);
        }

        @Override
        public final <U> U[] toArray(@NotNull IntFunction<? extends U[]> generator) {
            return collection.toArray(generator);
        }

        @Override
        public final <U> U foldLeft(U zero, @NotNull BiFunction<? super U, ? super E, ? extends U> op) {
            return collection.foldLeft(zero, op);
        }

        @Override
        public final <U> U foldRight(U zero, @NotNull BiFunction<? super E, ? super U, ? extends U> op) {
            return collection.foldRight(zero, op);
        }

        @NotNull
        @Override
        public final Option<E> reduceLeftOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
            return collection.reduceLeftOption(op);
        }

        @NotNull
        @Override
        public final Option<E> reduceRightOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
            return collection.reduceRightOption(op);
        }

        @Override
        public final int size() {
            return collection.size();
        }

        @Override
        public final boolean forall(@NotNull Predicate<? super E> predicate) {
            return collection.forall(predicate);
        }

        @Override
        public final boolean exists(@NotNull Predicate<? super E> predicate) {
            return collection.exists(predicate);
        }

        @Override
        public final boolean contains(Object v) {
            return collection.contains(v);
        }

        @Override
        public final int count(@NotNull Predicate<? super E> predicate) {
            return collection.count(predicate);
        }

        @NotNull
        @Override
        public final Option<E> find(@NotNull Predicate<? super E> predicate) {
            return collection.find(predicate);
        }

        @Override
        public final void forEach(@NotNull Consumer<? super E> action) {
            collection.forEach(action);
        }

        @Override
        public final Spliterator<E> spliterator() {
            return collection.spliterator();
        }

        @Override
        public final E fold(E zero, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
            return collection.fold(zero, op);
        }

        @Override
        public final E reduce(@NotNull BiFunction<? super E, ? super E, ? extends E> op) throws NoSuchElementException {
            return collection.reduce(op);
        }

        @Override
        public final E reduceLeft(@NotNull BiFunction<? super E, ? super E, ? extends E> op) throws NoSuchElementException {
            return collection.reduceLeft(op);
        }

        @Override
        public final E reduceRight(@NotNull BiFunction<? super E, ? super E, ? extends E> op) throws NoSuchElementException {
            return collection.reduceRight(op);
        }

        @NotNull
        @Override
        public final Option<E> reduceOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
            return collection.reduceOption(op);
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return collection.iterator();
        }

        @Override
        public final String toString() {
            return joinToString(", ", className() + "[", "]");
        }
    }

    static final class Mapped<E, T> extends AbstractView<E> {
        @NotNull
        private final View<T> source;

        @NotNull
        private final Function<? super T, ? extends E> mapper;

        public Mapped(@NotNull View<T> source, @NotNull Function<? super T, ? extends E> mapper) {
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

    static final class Filter<E> extends AbstractView<E> {
        @NotNull
        private final View<E> source;

        @NotNull
        private final Predicate<? super E> predicate;

        public Filter(@NotNull View<E> source, @NotNull Predicate<? super E> predicate) {
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

    static final class FlatMapped<E, T> extends AbstractView<E> {
        @NotNull
        private final View<? extends T> source;
        @NotNull
        private final Function<? super T, ? extends TraversableOnce<? extends E>> mapper;

        public FlatMapped(
                @NotNull View<? extends T> source,
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
