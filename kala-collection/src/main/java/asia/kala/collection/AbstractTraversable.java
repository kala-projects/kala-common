package asia.kala.collection;

import asia.kala.annotations.Covariant;
import asia.kala.factory.CollectionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

public abstract class AbstractTraversable<@Covariant E> implements Traversable<E> {
    static <E, R, Builder> R collectTo(
            @NotNull TraversableOnce<? extends E> collection,
            @NotNull Collector<? super E, Builder, ? extends R> collector
    ) {
        assert collection != null;
        Objects.requireNonNull(collector);

        final BiConsumer<Builder, ? super E> accumulator = collector.accumulator();

        Builder builder = collector.supplier().get();

        for (E e : collection) {
            accumulator.accept(builder, e);
        }

        return collector.finisher().apply(builder);
    }

    static <E, R, Builder> R collectTo(
            @NotNull TraversableOnce<? extends E> collection,
            @NotNull CollectionFactory<? super E, Builder, ? extends R> factory
    ) {
        assert collection != null;
        Objects.requireNonNull(factory);

        if (collection.isEmpty()) {
            return factory.empty();
        }

        Builder builder = factory.newBuilder();

        for (E e : collection) {
            factory.addToBuilder(builder, e);
        }

        return factory.build(builder);
    }

    @Override
    public int hashCode() {
        return Enumerator.hash(iterator());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Traversable<?>)
                || !(canEqual(obj))
                || !(((Traversable<?>) obj).canEqual(this))) {
            return false;
        }
        return sameElements(((Traversable<?>) obj));
    }

    @Override
    public String toString() {
        return joinToString(", ", className() + "[", "]");
    }
}
