package asia.kala.collection;

import asia.kala.annotations.Covariant;
import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public interface CollectionFactory<E, Builder, @Covariant R> extends Collector<E, Builder, R> {

    Builder newBuilder();

    void addToBuilder(@NotNull Builder builder, E value);

    default void addAllToBuilder(@NotNull Builder builder, @NotNull Iterable<? extends E> values) {
        Objects.requireNonNull(values);
        for (E value : values) {
            addToBuilder(builder, value);
        }
    }

    Builder mergeBuilder(@NotNull Builder builder1, @NotNull Builder builder2);

    R build(@NotNull Builder builder);

    default void sizeHint(@NotNull Builder builder, int size) {

    }

    default void sizeHint(@NotNull Builder builder, @NotNull Iterable<?> it) {
        this.sizeHint(builder, it, 0);
    }

    default void sizeHint(@NotNull Builder builder, @NotNull Iterable<?> it, int delta) {
        Objects.requireNonNull(it);

        if (it instanceof TraversableOnce<?>) {
            int s = ((TraversableOnce<?>) it).knownSize();
            if (s != -1) {
                this.sizeHint(builder, s + delta);
            }
        } else if (it instanceof Collection<?>) {
            int s = ((Collection<?>) it).size();
            this.sizeHint(builder, s + delta);
        }
    }

    default R empty() {
        return build(newBuilder());
    }

    default R from(@NotNull @ReadOnly Iterable<? extends E> iterable) {
        Builder builder = newBuilder();
        sizeHint(builder, iterable);

        for (E e : iterable) {
            addToBuilder(builder, e);
        }
        return build(builder);
    }

    default R from(@NotNull E[] elements) {
        Objects.requireNonNull(elements);
        if (elements.length == 0) {
            return empty();
        }

        Builder builder = newBuilder();
        sizeHint(builder, elements.length);
        for (E element : elements) {
            addToBuilder(builder, element);
        }
        return build(builder);
    }

    default <U> CollectionFactory<E, Builder, U> mapResult(@NotNull Function<? super R, ? extends U> mapper) {
        CollectionFactory<E, Builder, R> source = this;
        return new CollectionFactory<E, Builder, U>() {
            @Override
            public U empty() {
                return mapper.apply(source.empty());
            }

            @Override
            public Builder newBuilder() {
                return source.newBuilder();
            }

            @Override
            public void addToBuilder(@NotNull Builder builder, E value) {
                source.addToBuilder(builder, value);
            }

            @Override
            public Builder mergeBuilder(@NotNull Builder builder1, @NotNull Builder builder2) {
                return source.mergeBuilder(builder1, builder2);
            }

            @Override
            public U build(@NotNull Builder builder) {
                return mapper.apply(source.build(builder));
            }

            @Override
            public Set<Characteristics> characteristics() {
                return source.characteristics();
            }
        };
    }

    @Override
    default Supplier<Builder> supplier() {
        return this::newBuilder;
    }

    @Override
    default BiConsumer<Builder, E> accumulator() {
        return this::addToBuilder;
    }

    @Override
    default Function<Builder, R> finisher() {
        return this::build;
    }

    @Override
    default BinaryOperator<Builder> combiner() {
        return this::mergeBuilder;
    }

    @Override
    default Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
