package asia.kala.util;

import asia.kala.Tuple2;
import asia.kala.annotations.Covariant;
import asia.kala.annotations.StaticClass;
import asia.kala.control.Option;
import asia.kala.factory.CollectionFactory;
import asia.kala.function.IndexedConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

@StaticClass
@SuppressWarnings("unchecked")
public final class Iterators {
    private Iterators() {
    }

    @Contract(value = "_ -> param1", pure = true)
    public static <E> Iterator<E> narrow(Iterator<? extends E> iterator) {
        return (Iterator<E>) iterator;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <E> Iterator<E> empty() {
        return ((Iterator<E>) EMPTY);
    }

    @NotNull
    public static <E> Iterator<E> of() {
        return empty();
    }

    @NotNull
    public static <E> Iterator<E> of(E value) {
        return new Id<>(value);
    }

    @NotNull
    @SafeVarargs
    public static <E> Iterator<E> of(E... values) {
        return JavaArray.iterator(values);
    }

    @NotNull
    public static <E> Iterator<E> from(@NotNull Iterable<? extends E> values) {
        return narrow(values.iterator());
    }

    @NotNull
    public static <E> Iterator<E> from(E @NotNull [] values) {
        return JavaArray.iterator(values);
    }

    @NotNull
    public static <E> Iterator<E> concat(Iterator<? extends E>... its) {
        return new Concat<>(JavaArray.iterator(its));
    }

    @NotNull
    public static <E> Iterator<E> concat(@NotNull Iterable<? extends Iterator<? extends E>> its) {
        return new Concat<>(its.iterator());
    }

    @NotNull
    public static <E> Iterator<E> concat(@NotNull Iterator<? extends Iterator<? extends E>> its) {
        Objects.requireNonNull(its);
        return new Concat<>(its);
    }

    public static int hash(@NotNull Iterator<?> it) {
        assert it != null;

        int ans = 0;
        while (it.hasNext()) {
            ans = ans * 31 + Objects.hashCode(it.next());
        }
        return ans;
    }

    @Contract(value = "_, _ -> param1", mutates = "param1")
    public static <E> Iterator<E> drop(@NotNull Iterator<? extends E> it, int n) {
        while (n > 0 && it.hasNext()) {
            it.next();
            --n;
        }
        return ((Iterator<E>) it);
    }

    @NotNull
    public static <E> Iterator<E> dropWhile(@NotNull Iterator<? extends E> it, @NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        if (!it.hasNext()) {
            return empty();
        }
        return new DropWhile<>(it, predicate);
    }

    @NotNull
    public static <E> Iterator<E> take(@NotNull Iterator<? extends E> it, int n) {
        if (!it.hasNext() || n <= 0) {
            return empty();
        }

        return new Take<>(it, n);
    }

    @NotNull
    public static <E> Iterator<E> takeWhile(@NotNull Iterator<? extends E> it, @NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        if (!it.hasNext()) {
            return empty();
        }
        return new TakeWhile<>(it, predicate);
    }

    @NotNull
    public static <E> Iterator<E> updated(@NotNull Iterator<? extends E> it, int n, E newValue) {
        Objects.requireNonNull(it);
        return new Updated<>(it, n, newValue);
    }

    @NotNull
    public static <E> Iterator<E> prepended(@NotNull Iterator<? extends E> it, E value) {
        Objects.requireNonNull(it);
        return new Prepended<>(it, value);
    }

    @NotNull
    public static <E> Iterator<E> appended(@NotNull Iterator<? extends E> it, E value) {
        Objects.requireNonNull(it);
        return new Appended<>(it, value);
    }

    @NotNull
    public static <E> Iterator<E> filter(@NotNull Iterator<? extends E> it, @NotNull Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        if (!it.hasNext()) {
            return empty();
        }
        return new Filter<>(it, predicate);
    }

    @NotNull
    public static <E> Iterator<E> filterNot(@NotNull Iterator<? extends E> it, @NotNull Predicate<? super E> predicate) {
        if (!it.hasNext()) {
            return empty();
        }
        return new Filter<>(it, predicate.negate());
    }

    @NotNull
    public static <E> Iterator<@NotNull E> filterNotNull(@NotNull Iterator<? extends E> it) {
        if (!it.hasNext()) {
            return empty();
        }
        //noinspection ConstantConditions
        return new Filter<>(it, Objects::nonNull);
    }

    @NotNull
    public static <E, U> Iterator<U> map(
            @NotNull Iterator<? extends E> it,
            @NotNull Function<? super E, ? extends U> mapper
    ) {
        Objects.requireNonNull(mapper);
        if (!it.hasNext()) {
            return empty();
        }
        return new Mapped<>(it, mapper);
    }

    @NotNull
    public static <E, U> Iterator<U> flatMap(
            @NotNull Iterator<? extends E> it,
            @NotNull Function<? super E, ? extends Iterable<? extends U>> mapper
    ) {
        Objects.requireNonNull(mapper);
        if (!it.hasNext()) {
            return empty();
        }

        return new Concat<>(Iterators.map(Iterators.map(it, mapper), Iterable::iterator));
    }

    @NotNull
    public static <E> Tuple2<Iterator<E>, Iterator<E>> span(
            @NotNull Iterator<E> it, @NotNull Predicate<? super E> predicate
    ) {
        if (!it.hasNext()) {
            return new Tuple2<>(empty(), empty());
        }

        LinkedList<E> list = new LinkedList<>();


        while (it.hasNext()) {
            E e = it.next();
            if (predicate.test(e)) {
                list.add(e);
            } else {
                it = prepended(it, e);
                break;
            }
        }

        return new Tuple2<>(list.iterator(), it);
    }

    public static boolean sameElements(@NotNull Iterator<?> it1, @NotNull Iterator<?> it2) {
        while (it1.hasNext() && it2.hasNext()) {
            if (!Objects.equals(it1.next(), it2.next())) {
                return false;
            }
        }
        return it1.hasNext() == it2.hasNext();
    }

    public static int size(@NotNull Iterator<?> it) {
        int i = 0;
        while (it.hasNext()) {
            it.next();
            ++i;
        }
        return i;
    }

    public static boolean contains(Iterator<?> it, Object value) {
        if (value == null) {
            while (it.hasNext()) {
                if (it.next() == null) {
                    return true;
                }
            }
        } else {
            while (it.hasNext()) {
                if (value.equals(it.next())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <E> int count(@NotNull Iterator<? extends E> it, @NotNull Predicate<? super E> predicate) {
        int c = 0;
        while (it.hasNext()) {
            if (predicate.test(it.next())) {
                ++c;
            }
        }
        return c;
    }

    public static <E> boolean exists(@NotNull Iterator<? extends E> it, @NotNull Predicate<? super E> predicate) {
        while (it.hasNext()) {
            if (!predicate.test(it.next())) {
                return false;
            }
        }
        return true;
    }

    public static <E> boolean forall(@NotNull Iterator<? extends E> it, @NotNull Predicate<? super E> predicate) {
        while (it.hasNext()) {
            if (!predicate.test(it.next())) {
                return false;
            }
        }
        return true;
    }

    public static <E extends Comparable<E>> E max(@NotNull Iterator<? extends E> it) {
        if (!it.hasNext()) {
            throw new NoSuchElementException();
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (v.compareTo(e) > 0) {
                e = v;
            }
        }
        return e;
    }

    public static <E> E max(@NotNull Iterator<? extends E> it, @NotNull Comparator<? super E> comparator) {
        if (!it.hasNext()) {
            throw new NoSuchElementException();
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (comparator.compare(v, e) > 0) {
                e = v;
            }
        }
        return e;
    }

    @Nullable
    public static <E extends Comparable<E>> E maxOrNull(@NotNull Iterator<? extends E> it) {
        if (!it.hasNext()) {
            return null;
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (v.compareTo(e) > 0) {
                e = v;
            }
        }
        return e;
    }

    @Nullable
    public static <E> E maxOrNull(@NotNull Iterator<? extends E> it, @NotNull Comparator<? super E> comparator) {
        if (!it.hasNext()) {
            return null;
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (comparator.compare(v, e) > 0) {
                e = v;
            }
        }
        return e;
    }

    public static <E extends Comparable<E>> Option<E> maxOption(@NotNull Iterator<? extends E> it) {
        if (!it.hasNext()) {
            return Option.none();
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (v.compareTo(e) > 0) {
                e = v;
            }
        }
        return Option.some(e);
    }

    public static <E> Option<E> maxOption(@NotNull Iterator<? extends E> it, @NotNull Comparator<? super E> comparator) {
        if (!it.hasNext()) {
            return Option.none();
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (comparator.compare(v, e) > 0) {
                e = v;
            }
        }
        return Option.some(e);
    }

    public static <E extends Comparable<E>> E min(@NotNull Iterator<? extends E> it) {
        if (!it.hasNext()) {
            throw new NoSuchElementException();
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (v.compareTo(e) < 0) {
                e = v;
            }
        }
        return e;
    }

    public static <E> E min(@NotNull Iterator<? extends E> it, @NotNull Comparator<? super E> comparator) {
        if (!it.hasNext()) {
            throw new NoSuchElementException();
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (comparator.compare(v, e) < 0) {
                e = v;
            }
        }
        return e;
    }

    @Nullable
    public static <E extends Comparable<E>> E minOrNull(@NotNull Iterator<? extends E> it) {
        if (!it.hasNext()) {
            return null;
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (v.compareTo(e) < 0) {
                e = v;
            }
        }
        return e;
    }

    @Nullable
    public static <E> E minOrNull(@NotNull Iterator<? extends E> it, @NotNull Comparator<? super E> comparator) {
        if (!it.hasNext()) {
            return null;
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (comparator.compare(v, e) < 0) {
                e = v;
            }
        }
        return e;
    }

    public static <E extends Comparable<E>> Option<E> minOption(@NotNull Iterator<? extends E> it) {
        if (!it.hasNext()) {
            return Option.none();
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (v.compareTo(e) < 0) {
                e = v;
            }
        }
        return Option.some(e);
    }

    public static <E> Option<E> minOption(@NotNull Iterator<? extends E> it, @NotNull Comparator<? super E> comparator) {
        if (!it.hasNext()) {
            return Option.none();
        }
        E e = it.next();
        while (it.hasNext()) {
            E v = it.next();
            if (comparator.compare(v, e) < 0) {
                e = v;
            }
        }
        return Option.some(e);
    }

    @Contract(value = "_, _ -> param2", mutates = "param1, param2")
    public static <A extends Appendable> A joinTo(
            @NotNull Iterator<?> it,
            @NotNull A buffer
    ) {
        return joinTo(it, buffer, ", ", "", "");
    }

    @Contract(value = "_, _, _ -> param2", mutates = "param1, param2")
    public static <A extends Appendable> A joinTo(
            @NotNull Iterator<?> it,
            @NotNull A buffer,
            CharSequence separator
    ) {
        return joinTo(it, buffer, separator, "", "");
    }

    @Contract(value = "_, _, _, _, _ -> param2", mutates = "param1, param2")
    public static <A extends Appendable> A joinTo(
            @NotNull Iterator<?> it,
            @NotNull A buffer,
            CharSequence separator, CharSequence prefix, CharSequence postfix
    ) {
        try {
            buffer.append(prefix);
            if (it.hasNext()) {
                buffer.append(Objects.toString(it.next()));
            }
            while (it.hasNext()) {
                buffer.append(separator).append(Objects.toString(it.next()));
            }
            buffer.append(postfix);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return buffer;
    }

    @NotNull
    public static String joinToString(@NotNull Iterator<?> it) {
        return joinTo(it, new StringBuilder()).toString();
    }

    @NotNull
    public static String joinToString(
            @NotNull Iterator<?> it,
            CharSequence separator
    ) {
        return joinTo(it, new StringBuilder(), separator).toString();
    }

    @NotNull
    public static String joinToString(
            @NotNull Iterator<?> it,
            CharSequence separator, CharSequence prefix, CharSequence postfix
    ) {
        return joinTo(it, new StringBuilder(), separator, prefix, postfix).toString();
    }

    public static <E> E fold(
            @NotNull Iterator<? extends E> it,
            E zero, @NotNull BiFunction<? super E, ? super E, ? extends E> op
    ) {
        while (it.hasNext()) {
            zero = op.apply(zero, it.next());
        }
        return zero;
    }

    public static <E, U> U foldLeft(
            @NotNull Iterator<? extends E> it,
            U zero, @NotNull BiFunction<? super U, ? super E, ? extends U> op
    ) {
        while (it.hasNext()) {
            zero = op.apply(zero, it.next());
        }
        return zero;
    }


    public static <E, U> U foldRight(
            @NotNull Iterator<? extends E> it,
            U zero, @NotNull BiFunction<? super E, ? super U, ? extends U> op
    ) {
        if (!it.hasNext()) {
            return zero;
        }
        LinkedList<E> list = new LinkedList<>();
        while (it.hasNext()) {
            list.addFirst(it.next());
        }
        for (E u : list) {
            zero = op.apply(u, zero);
        }
        return zero;
    }

    public static <E> E reduce(@NotNull Iterator<? extends E> it, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        if (!it.hasNext()) {
            throw new NoSuchElementException();
        }
        E e = it.next();
        while (it.hasNext()) {
            e = op.apply(e, it.next());
        }
        return e;
    }

    public static <E> E reduceLeft(@NotNull Iterator<? extends E> it, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        if (!it.hasNext()) {
            throw new NoSuchElementException();
        }
        E e = it.next();
        while (it.hasNext()) {
            e = op.apply(e, it.next());
        }
        return e;
    }

    public static <E> E reduceRight(@NotNull Iterator<? extends E> it, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        if (!it.hasNext()) {
            throw new NoSuchElementException();
        }
        LinkedList<E> list = new LinkedList<>();
        while (it.hasNext()) {
            list.addFirst(it.next());
        }
        it = list.iterator();
        E e = it.next();
        if (it.hasNext()) {
            e = op.apply(it.next(), e);
        }
        return e;
    }

    public static <E> Option<E> reduceOption(@NotNull Iterator<? extends E> it, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        if (!it.hasNext()) {
            return Option.none();
        }
        E e = it.next();
        while (it.hasNext()) {
            e = op.apply(e, it.next());
        }
        return Option.some(e);
    }

    public static <E> Option<E> reduceLeftOption(@NotNull Iterator<? extends E> it, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        if (!it.hasNext()) {
            return Option.none();
        }
        E e = it.next();
        while (it.hasNext()) {
            e = op.apply(e, it.next());
        }
        return Option.some(e);
    }

    public static <E> Option<E> reduceRightOption(@NotNull Iterator<? extends E> it, @NotNull BiFunction<? super E, ? super E, ? extends E> op) {
        if (!it.hasNext()) {
            return Option.none();
        }
        LinkedList<E> list = new LinkedList<>();
        while (it.hasNext()) {
            list.addFirst(it.next());
        }
        it = list.iterator();
        E e = it.next();
        if (it.hasNext()) {
            e = op.apply(it.next(), e);
        }
        return Option.some(e);
    }

    public static <E> E[] toArray(@NotNull Iterator<? extends E> it, @NotNull IntFunction<E[]> generator) {
        Objects.requireNonNull(generator);
        if (!it.hasNext()) {
            return generator.apply(0);
        }
        ArrayList<E> buffer = new ArrayList<>();
        while (it.hasNext()) {
            buffer.add(it.next());
        }
        return buffer.toArray(generator.apply(buffer.size()));
    }

    public static <E, R> R collectTo(
            @NotNull Iterator<? extends E> it,
            @NotNull CollectionFactory<? super E, ?, ? extends R> factory
    ) {
        return collectToImpl(it, factory);
    }

    static <E, R, Builder> R collectToImpl(
            @NotNull Iterator<? extends E> it,
            @NotNull CollectionFactory<? super E, Builder, ? extends R> factory
    ) {
        if (!it.hasNext()) {
            return factory.empty();
        }
        Builder builder = factory.newBuilder();
        while (it.hasNext()) {
            factory.addToBuilder(builder, it.next());
        }
        return factory.build(builder);
    }

    public static <E, R> R collectTo(
            @NotNull Iterator<? extends E> it,
            @NotNull Collector<? super E, ?, ? extends R> collector
    ) {
        return collectToImpl(it, collector);
    }

    static <E, R, Builder> R collectToImpl(
            @NotNull Iterator<? extends E> it,
            @NotNull Collector<? super E, Builder, ? extends R> collector
    ) {
        Builder builder = collector.supplier().get();
        final Function<Builder, ? extends R> finisher = collector.finisher();
        if (!it.hasNext()) {
            return finisher.apply(builder);
        }

        final BiConsumer<Builder, ? super E> accumulator = collector.accumulator();
        while (it.hasNext()) {
            accumulator.accept(builder, it.next());
        }
        return finisher.apply(builder);
    }

    public static <E> void forEach(@NotNull Iterator<? extends E> it, @NotNull Consumer<? super E> action) {
        while (it.hasNext()) {
            action.accept(it.next());
        }
    }

    public static <E> void forEachIndexed(@NotNull Iterator<? extends E> it, @NotNull IndexedConsumer<? super E> action) {
        int idx = 0;
        while (it.hasNext()) {
            action.accept(idx++, it.next());
        }
    }

    private static final Object TAG = new Object();

    private static final Iterator<?> EMPTY = new Iterator<Object>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }
    };

    static final class Id<@Covariant T> implements Iterator<T> {

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
                throw new NoSuchElementException("Iterators.Single.next");
            }
            @SuppressWarnings("unchecked")
            T v = (T) value;
            value = TAG;
            return v;
        }
    }

    static final class Mapped<@Covariant E, S> implements Iterator<E> {
        @NotNull
        private final Iterator<? extends S> source;

        @NotNull
        private final Function<? super S, ? extends E> mapper;

        Mapped(@NotNull Iterator<? extends S> source, @NotNull Function<? super S, ? extends E> mapper) {
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

    static final class Filter<@Covariant E> implements Iterator<E> {
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

    static final class DropWhile<@Covariant E> implements Iterator<E> {
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

    static final class Take<@Covariant E> implements Iterator<E> {
        private final Iterator<? extends E> source;
        private int n;

        Take(Iterator<? extends E> source, int n) {
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

    static final class TakeWhile<@Covariant E> implements Iterator<E> {
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
                    source = Iterators.empty();
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

    static final class Concat<@Covariant E> implements Iterator<E> {
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

    static final class Updated<@Covariant E> implements Iterator<E> {
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

    static final class Prepended<@Covariant E> implements Iterator<E> {
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

    static final class Appended<@Covariant E> implements Iterator<E> {
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
