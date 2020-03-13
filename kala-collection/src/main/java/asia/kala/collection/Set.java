package asia.kala.collection;

import asia.kala.collection.immutable.ImmutableSeq;
import asia.kala.collection.immutable.ImmutableSet;
import org.jetbrains.annotations.NotNull;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;

public interface Set<E> extends Traversable<E> {

    static <E> CollectionFactory<E, ?, ? extends Set<E>> factory() {
        return ImmutableSet.factory();
    }

    @Override
    default boolean contains(Object value) {
        return iterator().contains(value);
    }

    default Predicate<E> asPredicate() {
        return this::contains;
    }

    //region Traversable members

    @Override
    default String className() {
        return "Set";
    }

    @NotNull
    @Override
    default <U> CollectionFactory<U, ?, ? extends Set<U>> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    default SetView<E> view() {
        return new SetViews.Of<>(this);
    }

    @Override
    default boolean canEqual(Object other) {
        return other instanceof Set<?>;
    }

    @NotNull
    @Override
    default java.util.Set<E> asJava() {
        return new JDKConverters.SetAsJava<>(this);
    }

    @NotNull
    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(iterator(), size(), Spliterator.DISTINCT);
    }

    //endregion
}
