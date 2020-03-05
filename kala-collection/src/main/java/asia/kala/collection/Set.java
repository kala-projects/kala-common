package asia.kala.collection;

import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;

public interface Set<E> extends Traversable<E>, Predicate<E> {
    //
    // -- Predicate
    //

    @Override
    default boolean test(E e) {
        return contains(e);
    }

    //
    // -- Traversable
    //

    @Override
    default String className() {
        return "Set";
    }

    @NotNull
    @Override
    default SetView<E> view() {
        return new SetViews.Of<>(this);
    }

    @NotNull
    @ReadOnly
    @Override
    default java.util.Set<E> asJava() {
        return new JDKConverters.SetAsJava<>(this);
    }

    @NotNull
    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(iterator(), size(), Spliterator.DISTINCT);
    }
}
