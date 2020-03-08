package asia.kala.collection;

import asia.kala.Option;
import asia.kala.collection.immutable.ImmutableArray;
import asia.kala.collection.immutable.ImmutableSeq;
import asia.kala.collection.mutable.ArrayBuffer;
import asia.kala.function.IndexedConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class ArraySeq<E> implements Seq<E>, IndexedSeq<E>, Serializable {
    private static final long serialVersionUID = 4981379062449237945L;

    public static final Object[] EMPTY_ARRAY = new Object[0];
    public static final ArraySeq<?> EMPTY = new ArraySeq<>(EMPTY_ARRAY);

    private static final ArraySeq.Factory<?> FACTORY = new ArraySeq.Factory<>();

    @NotNull
    protected final Object[] array;

    protected ArraySeq(@NotNull Object[] array) {
        this.array = array;
    }

    //region Factory methods

    @NotNull
    public static <E> CollectionFactory<E, ?, ? extends ArraySeq<E>> factory() {
        return (Factory<E>) FACTORY;
    }

    @NotNull
    @Contract("_ -> new")
    public static <E> ArraySeq<E> wrap(@NotNull E[] array) {
        Objects.requireNonNull(array);
        return new ArraySeq<>(array);
    }

    @NotNull
    public static <E> ArraySeq<E> empty() {
        return (ArraySeq<E>) EMPTY;
    }

    @NotNull
    public static <E> ArraySeq<E> of() {
        return empty();
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <E> ArraySeq<E> of(E value1) {
        return new ArraySeq<>(new Object[]{value1});
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static <E> ArraySeq<E> of(E value1, E value2) {
        return new ArraySeq<>(new Object[]{value1, value2});
    }

    @NotNull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static <E> ArraySeq<E> of(E value1, E value2, E value3) {
        return new ArraySeq<>(new Object[]{value1, value2, value3});
    }

    @NotNull
    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static <E> ArraySeq<E> of(E value1, E value2, E value3, E value4) {
        return new ArraySeq<>(new Object[]{value1, value2, value3, value4});
    }

    @NotNull
    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    public static <E> ArraySeq<E> of(E value1, E value2, E value3, E value4, E value5) {
        return new ArraySeq<>(new Object[]{value1, value2, value3, value4, value5});
    }

    @NotNull
    @SafeVarargs
    @Contract(pure = true)
    public static <E> ArraySeq<E> of(E... values) {
        return from(values);
    }

    @NotNull
    @Contract(pure = true)
    public static <E> ArraySeq<E> from(E @NotNull [] values) {
        assert values != null;
        if (values.length == 0) {
            return empty();
        }
        return new ArraySeq<>(values.clone());
    }

    @NotNull
    public static <E> ArraySeq<E> from(@NotNull TraversableOnce<? extends E> values) {
        if (values instanceof ImmutableArray<?> || values.getClass() == ArraySeq.class) {
            return (ArraySeq<E>) values;
        }

        if (values.knownSize() == 0) {
            return empty();
        }

        Object[] arr = values.toObjectArray();
        if (arr.length == 0) {
            return empty();
        }
        return new ArraySeq<>(arr);
    }

    @NotNull
    public static <E> ArraySeq<E> from(@NotNull java.util.Collection<? extends E> values) {
        if (values.size() == 0) {
            return empty();
        }
        return new ArraySeq<>(values.toArray());
    }

    @NotNull
    public static <E> ArraySeq<E> from(@NotNull Iterable<? extends E> values) {
        Objects.requireNonNull(values);

        if (values instanceof TraversableOnce<?>) {
            return from((TraversableOnce<E>) values);
        }
        if (values instanceof java.util.Collection<?>) {
            return from(((java.util.Collection<E>) values));
        }

        return ArrayBuffer.<E>from(values).toImmutableArray();
    }

    //endregion

    public final E get(int index) {
        return (E) array[index];
    }

    @NotNull
    @Override
    public final Option<E> getOption(int index) {
        if (index < 0 || index >= array.length) {
            return Option.none();
        }
        return Option.some((E) array[index]);
    }

    @Override
    public final int size() {
        return array.length;
    }

    @Override
    public final boolean isEmpty() {
        return array.length == 0;
    }

    @Override
    public final boolean isDefinedAt(int index) {
        return index >= 0 && index < array.length;
    }

    @Override
    public final int indexOf(Object value) {
        return JavaArray.indexOf(array, value);
    }

    @Override
    public final int indexOf(Object value, int from) {
        return JavaArray.indexOf(array, value, from);
    }

    @Override
    public final int indexWhere(@NotNull Predicate<? super E> predicate) {
        return JavaArray.indexWhere(array, (Predicate<Object>) predicate);
    }

    @Override
    public final int indexWhere(@NotNull Predicate<? super E> predicate, int from) {
        return JavaArray.indexWhere(array, (Predicate<Object>) predicate, from);
    }

    @Override
    public final int lastIndexOf(Object value) {
        return JavaArray.lastIndexOf(array, value);
    }

    @Override
    public final int lastIndexOf(Object value, int end) {
        return JavaArray.lastIndexOf(array, value, end);
    }

    @Override
    public final int lastIndexWhere(@NotNull Predicate<? super E> predicate) {
        return JavaArray.lastIndexWhere(array, (Predicate<Object>) predicate);
    }

    @Override
    public final int lastIndexWhere(@NotNull Predicate<? super E> predicate, int end) {
        return JavaArray.lastIndexWhere(array, (Predicate<Object>) predicate, end);
    }

    @Override
    public final E max() {
        return (E) JavaArray.maxImpl(array);
    }

    @Override
    public final E maxBy(@NotNull Comparator<? super E> comparator) {
        return (E) JavaArray.maxBy(array, (Comparator<Object>) comparator);
    }

    @NotNull
    @Override
    public final Option<E> maxOption() {
        final Object[] array = this.array;
        if (array.length == 0) {
            return Option.none();
        }
        return Option.some((E) JavaArray.maxImpl(array));
    }

    @NotNull
    @Override
    public final Option<E> maxByOption(@NotNull Comparator<? super E> comparator) {
        if (array.length == 0) {
            return Option.none();
        }
        return Option.some(maxBy(comparator));
    }

    @Override
    public final E min() {
        return (E) JavaArray.minImpl(array);
    }

    @Override
    public final E minBy(@NotNull Comparator<? super E> comparator) {
        return (E) JavaArray.minBy(array, (Comparator<Object>) comparator);
    }

    @NotNull
    @Override
    public final Option<E> minOption() {
        final Object[] array = this.array;
        if (array.length == 0) {
            return Option.none();
        }
        return Option.some((E) JavaArray.minImpl(array));
    }

    @NotNull
    @Override
    public final Option<E> minByOption(@NotNull Comparator<? super E> comparator) {
        if (array.length == 0) {
            return Option.none();
        }
        return Option.some(minBy(comparator));
    }

    @Override
    public final E fold(E zero, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        return (E) JavaArray.fold(array, zero, (BiFunction<Object, Object, ?>) op);
    }

    @Override
    public final <U> U foldLeft(U zero, @NotNull BiFunction<? super U, ? super E, ? extends U> op) {
        return JavaArray.foldLeft(array, zero, (BiFunction<U, Object, U>) op);
    }

    @Override
    public final <U> U foldRight(U zero, @NotNull BiFunction<? super E, ? super U, ? extends U> op) {
        return JavaArray.foldRight(array, zero, (BiFunction<Object, U, U>) op);
    }

    @Override
    public final E reduce(@NotNull BiFunction<? super E, ? super E, ? extends E> op) throws NoSuchElementException {
        return (E) JavaArray.reduce(array, (BiFunction<Object, Object, ?>) op);
    }

    @Override
    public final E reduceLeft(@NotNull BiFunction<? super E, ? super E, ? extends E> op) throws NoSuchElementException {
        return (E) JavaArray.reduceLeft(array, (BiFunction<Object, Object, ?>) op);
    }

    @Override
    public final E reduceRight(@NotNull BiFunction<? super E, ? super E, ? extends E> op) throws NoSuchElementException {
        return (E) JavaArray.reduceRight(array, (BiFunction<Object, Object, ?>) op);
    }

    @NotNull
    @Override
    public final Option<E> reduceOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        return (Option<E>) JavaArray.reduceOption(array, (BiFunction<Object, Object, ?>) op);
    }

    @NotNull
    @Override
    public final Option<E> reduceLeftOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        return (Option<E>) JavaArray.reduceLeftOption(array, (BiFunction<Object, Object, ?>) op);
    }

    @NotNull
    @Override
    public final Option<E> reduceRightOption(@NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        return (Option<E>) JavaArray.reduceRightOption(array, (BiFunction<Object, Object, ?>) op);
    }

    @Override
    public final boolean forall(@NotNull Predicate<? super E> predicate) {
        assert predicate != null;

        for (Object e : array) {
            if (!predicate.test((E) e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public final boolean exists(@NotNull Predicate<? super E> predicate) {
        assert predicate != null;

        for (Object e : array) {
            if (predicate.test((E) e)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean contains(Object value) {
        final Object[] array = this.array;

        if (array.length == 0) {
            return false;
        }
        if (value == null) {
            for (Object e : array) {
                if (e == null) {
                    return true;
                }
            }
        } else {
            for (Object e : array) {
                if (value.equals(e)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public final boolean containsAll(@NotNull Iterable<?> values) {
        assert values != null;

        for (Object v : values) {
            if (!contains(v)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public final int count(@NotNull Predicate<? super E> predicate) {
        assert predicate != null;

        int c = 0;

        for (Object e : this.array) {
            if (predicate.test(((E) e))) {
                ++c;
            }
        }

        return c;
    }

    @NotNull
    @Override
    public final Option<E> find(@NotNull Predicate<? super E> predicate) {
        assert predicate != null;

        for (Object e : array) {
            if (predicate.test((E) e)) {
                return Option.some((E) e);
            }
        }
        return Option.none();
    }

    @NotNull
    @Override
    public final <A extends Appendable> A joinTo(
            @NotNull A buffer,
            CharSequence separator, CharSequence prefix, CharSequence postfix
    ) {
        final Object[] array = this.array;
        final int length = array.length;

        try {
            buffer.append(prefix);
            if (length > 0) {
                buffer.append(Objects.toString(array[0]));
                for (int i = 1; i < length; i++) {
                    buffer.append(separator);
                    buffer.append(Objects.toString(array[i]));
                }
            }
            buffer.append(postfix);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return buffer;
    }

    private <R, Builder> R collectToImpl(@NotNull Collector<? super E, Builder, ? extends R> collector) {
        if (collector instanceof CollectionFactory<?, ?, ?>) {
            return collectToImpl((CollectionFactory<? super E, Builder, ? extends R>) collector);
        }

        final Object[] array = this.array;

        Builder builder = collector.supplier().get();
        if (array.length == 0) {
            return collector.finisher().apply(builder);
        }
        final BiConsumer<Builder, ? super E> accumulator = collector.accumulator();
        for (Object o : array) {
            accumulator.accept(builder, (E) o);
        }
        return collector.finisher().apply(builder);
    }

    @Override
    public final <R> R collectTo(@NotNull Collector<? super E, ?, ? extends R> collector) {
        return collectToImpl(collector);
    }

    private <R, Builder> R collectToImpl(@NotNull CollectionFactory<? super E, Builder, ? extends R> factory) {
        assert factory != null;

        final Object[] array = this.array;
        final int length = array.length;

        if (length == 0) {
            return factory.empty();
        }

        Builder builder = factory.newBuilder();
        factory.sizeHint(builder, length);
        for (Object e : array) {
            factory.addToBuilder(builder, (E) e);
        }
        return factory.build(builder);
    }

    @Override
    public final <R> R collectTo(@NotNull CollectionFactory<? super E, ?, ? extends R> factory) {
        return collectToImpl(factory);
    }

    @NotNull
    @Override
    public final Object[] toObjectArray() {
        final Object[] array = this.array;
        final int length = array.length;

        Object[] res = new Object[length];
        if (length != 0) {
            System.arraycopy(array, 0, res, 0, length);
        }
        return res;
    }

    @NotNull
    @Override
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public final <U> U[] toArray(@NotNull IntFunction<? extends U[]> generator) {
        assert generator != null;

        final Object[] array = this.array;
        final int length = array.length;

        U[] res = generator.apply(length);
        if (length != 0) {
            System.arraycopy(array, 0, res, 0, length);
        }
        return res;
    }

    @Override
    public String className() {
        return "ArraySeq";
    }

    @NotNull
    @Override
    public final Enumerator<E> iterator() {
        return (Enumerator<E>) JavaArray.iterator(array);
    }

    @NotNull
    @Override
    public final Enumerator<E> reverseIterator() {
        return (Enumerator<E>) JavaArray.reverseIterator(array);
    }

    @NotNull
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(array, 0);
    }

    @NotNull
    @Override
    public final Stream<E> stream() {
        return (Stream<E>) Arrays.stream(array);
    }

    @NotNull
    @Override
    public final Stream<E> parallelStream() {
        return (Stream<E>) Arrays.stream(array).parallel();
    }

    @NotNull
    @Override
    public <U> CollectionFactory<U, ?, ? extends ArraySeq<U>> iterableFactory() {
        return factory();
    }

    @Override
    public final void forEach(@NotNull Consumer<? super E> action) {
        assert action != null;

        final Object[] array = this.array;
        for (Object e : array) {
            action.accept((E) e);
        }
    }

    @Override
    public final void forEachIndexed(@NotNull IndexedConsumer<? super E> action) {
        assert action != null;

        final Object[] array = this.array;
        final int length = array.length;
        for (int i = 0; i < length; i++) {
            action.accept(i, (E) array[i]);
        }
    }

    private static final class Factory<E> implements CollectionFactory<E, ArrayBuffer<E>, ArraySeq<E>> {

        @Override
        public final ArraySeq<E> empty() {
            return ArraySeq.empty();
        }

        @Override
        public final ArraySeq<E> from(E @NotNull [] values) {
            if (values.length == 0) {
                return empty();
            }
            return new ArraySeq<>(values.clone());
        }

        @Override
        public final ArrayBuffer<E> newBuilder() {
            return new ArrayBuffer<>();
        }

        @Override
        public final void addToBuilder(@NotNull ArrayBuffer<E> buffer, E value) {
            buffer.append(value);
        }

        @Override
        public final ArrayBuffer<E> mergeBuilder(@NotNull ArrayBuffer<E> builder1, @NotNull ArrayBuffer<E> builder2) {
            builder1.appendAll(builder2);
            return builder1;
        }

        @Override
        public final void sizeHint(@NotNull ArrayBuffer<E> buffer, int size) {
            buffer.sizeHint(size);
        }

        @Override
        public final ArraySeq<E> build(@NotNull ArrayBuffer<E> buffer) {
            return new ArraySeq<>(buffer.toObjectArray());
        }
    }
}
